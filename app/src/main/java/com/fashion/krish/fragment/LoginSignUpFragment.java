/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fashion.krish.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.activity.DashboardActivity;
import com.fashion.krish.utility.AppPreferences;
import com.fashion.krish.utility.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import io.fabric.sdk.android.Fabric;

public class LoginSignUpFragment extends Fragment implements TextWatcher,GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	private static final String ARG_POSITION = "position";
	private int position;

	private MaterialDialog dialog,errorDialog;
	Utility util;
	Activity activity;

	private EditText etUsername,etPassword;
	private EditText etFname,etLname,etEmail,etRPassword,etCPassword;

	private LinearLayout layFb,layGPlus,layTwitter;

	String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

	private Button btnLogin,btnRegister;
	private AppPreferences preferences;

	CallbackManager callbackManager;
	public static boolean isBackFromFacebook = false;
	public static LoginResult loginResult;
	public static FacebookException fbException;
	public static String flag;
	//String accessToken,appId;

	/* Request code used to invoke sign in user interactions. */
	public static final int RC_SIGN_IN = 0;

	/* Client used to interact with Google APIs. */
	public static  GoogleApiClient mGoogleApiClient;

	/* A flag indicating that a PendingIntent is in progress and prevents
       * us from starting further intents.
       */
	public static  boolean mIntentInProgress;

	//public static TwitterAuthClient mTwitterAuthClient;
	public static int SOCIAL_INDEX,FACEBOOK=1,TWITTER=2,GPLUS=3;

	public static LoginSignUpFragment newInstance(int position) {
		LoginSignUpFragment f = new LoginSignUpFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		position = getArguments().getInt(ARG_POSITION);

		// Build GoogleApiClient with access to basic profile
		mGoogleApiClient = new GoogleApiClient.Builder(activity)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API)
				.addScope(new Scope(Scopes.PROFILE))
				.build();
		/*
		if(AppController.TWITTER_DETAILS.get("secretKey").length()>0){
			TwitterAuthConfig authConfig = new TwitterAuthConfig(AppController.TWITTER_DETAILS.get("apiKey"),
					AppController.TWITTER_DETAILS.get("secretKey"));
			Fabric.with(activity, new Crashlytics(), new Twitter(authConfig));
		}*/


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		dialog = new MaterialDialog.Builder(activity)
				.content("Please Wait")
				.progress(true, 0)
				.build();


		util = new Utility(activity);
		preferences = new AppPreferences(activity);

		View baseView = inflater.inflate(R.layout.login_fragment_child, null);

		LinearLayout productListLay = (LinearLayout) baseView.findViewById(R.id.layout_login_container);

		if(position == 0){
			View innerView = infalInflater.inflate(R.layout.layout_login, null);
			productListLay.addView(innerView);
			handleLoginView(innerView);

		}else{
			View innerView = infalInflater.inflate(R.layout.layout_signup, null);
			productListLay.addView(innerView);
			handleRegisterView(innerView);

		}

		DashboardActivity.animateToggle(0, 1);

		return baseView;
	}

	public void handleLoginView(final View view){
		etUsername = (EditText) view.findViewById(R.id.edt_username);
		etUsername.applyTheme(Color.parseColor(AppController.PRIMARY_COLOR),Color.parseColor(AppController.SECONDARY_COLOR));
		etUsername.addTextChangedListener(this);
		etPassword = (EditText) view.findViewById(R.id.edt_password);
		etPassword.addTextChangedListener(this);
		etPassword.applyTheme(Color.parseColor(AppController.PRIMARY_COLOR), Color.parseColor(AppController.SECONDARY_COLOR));
		((LinearLayout)view.findViewById(R.id.lay_login_btn)).setBackgroundColor(Color.parseColor(AppController.SECONDARY_COLOR));
		btnLogin =(Button) view.findViewById(R.id.btn_login);
		btnLogin.setBackgroundDrawable(util.getSecondaryRippleDrawable());

		layFb = (LinearLayout) view.findViewById(R.id.lay_facebook);
		layFb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FacebookSdk.setApplicationId(AppController.FACEBOOK_DETAILS.get("appID"));
				LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "user_friends"));
				SOCIAL_INDEX = FACEBOOK;
			}
		});

		layGPlus = (LinearLayout) view.findViewById(R.id.lay_gplus);

		layGPlus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// User clicked the sign-in button, so begin the sign-in process and automatically
				// attempt to resolve any errors that occur.
				if(mGoogleApiClient!=null){
					mGoogleApiClient.disconnect();
				}

				mGoogleApiClient = new GoogleApiClient.Builder(activity)
						.addConnectionCallbacks(LoginSignUpFragment.this)
						.addOnConnectionFailedListener(LoginSignUpFragment.this)
						.addApi(Plus.API)
						.addScope(Plus.SCOPE_PLUS_LOGIN)
						.addScope(Plus.SCOPE_PLUS_PROFILE)
						.build();
				mGoogleApiClient.connect();
				SOCIAL_INDEX = GPLUS;
				SOCIAL_INDEX = GPLUS;
			}
		});

		layTwitter = (LinearLayout) view.findViewById(R.id.lay_twitter);
		//mTwitterAuthClient= new TwitterAuthClient();
		/*layTwitter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// User clicked the sign-in button, so begin the sign-in process and automatically
				// attempt to resolve any errors that occur.
				SOCIAL_INDEX = TWITTER;
				mTwitterAuthClient.authorize(activity, new com.twitter.sdk.android.core.Callback<TwitterSession>() {

					@Override
					public void success(Result<TwitterSession> twitterSessionResult) {
						// Success
						Log.d("Tag", "Success");

					}

					@Override
					public void failure(TwitterException e) {
						e.printStackTrace();
					}
				});
			}
		});*/

		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (validateLogin()) {
					String username = etUsername.getText().toString();
					String password = etPassword.getText().toString();
					performLogin(username, password);
				}

			}
		});
	}

	public void handleRegisterView(final View view){

		etFname = (EditText) view.findViewById(R.id.edt_fname);
		etFname.applyTheme(Color.parseColor(AppController.PRIMARY_COLOR),Color.parseColor(AppController.SECONDARY_COLOR));
		etFname.addTextChangedListener(this);
		etLname = (EditText) view.findViewById(R.id.edt_lname);
		etLname.applyTheme(Color.parseColor(AppController.PRIMARY_COLOR), Color.parseColor(AppController.SECONDARY_COLOR));
		etLname.addTextChangedListener(this);
		etEmail = (EditText) view.findViewById(R.id.edt_email);
		etEmail.applyTheme(Color.parseColor(AppController.PRIMARY_COLOR),Color.parseColor(AppController.SECONDARY_COLOR));
		etEmail.addTextChangedListener(this);
		etRPassword = (EditText) view.findViewById(R.id.edt_password);
		etRPassword.applyTheme(Color.parseColor(AppController.PRIMARY_COLOR), Color.parseColor(AppController.SECONDARY_COLOR));
		etRPassword.addTextChangedListener(this);
		etCPassword = (EditText) view.findViewById(R.id.edt_cpassword);
		etCPassword.applyTheme(Color.parseColor(AppController.PRIMARY_COLOR),Color.parseColor(AppController.SECONDARY_COLOR));
		etCPassword.addTextChangedListener(this);
		btnRegister =(Button) view.findViewById(R.id.btn_register);
		btnRegister.setBackgroundDrawable(util.getSecondaryRippleDrawable());
		((LinearLayout)view.findViewById(R.id.lay_login_btn)).setBackgroundColor(Color.parseColor(AppController.SECONDARY_COLOR));

		final ArrayList<String> regParams = new ArrayList<>();

		btnRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (validateRegistration()) {
					regParams.clear();
					String fname = etFname.getText().toString();
					regParams.add(fname);
					String lname = etLname.getText().toString();
					regParams.add(lname);
					String email = etEmail.getText().toString();
					regParams.add(email);
					String password = etRPassword.getText().toString();
					regParams.add(password);
					String cpassword = etCPassword.getText().toString();
					regParams.add(cpassword);
					regParams.add("1");
					regParams.add("1");

					performRegister(regParams);
				}

			}
		});
	}

	private void performLogin(final String username, final String password){

		dialog.show();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				final String response = new RestClient(activity).login(username, password);

				try{
					final JSONObject jsonObject = new JSONObject(response);
					if(!jsonObject.get("status").equals(null)){

						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dialog.dismiss();
								try {
									if (jsonObject.get("status").toString().equals("error")) {
										errorDialog = new MaterialDialog.Builder(activity)
												.content(jsonObject.get("text").toString())
												.positiveText("Ok")
												.show();
									} else {
										preferences.setIsLoggedIn("1");
										setAccountDetails(jsonObject.getJSONObject("account"));
										DashboardActivity.setPopUpMenuItems();
										MyAccountFragment myAccountFragment = new MyAccountFragment();
										updateFragment(myAccountFragment);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}else {
						Toast.makeText(activity, "Some error has been occurred. Please try again later. ", Toast.LENGTH_SHORT).show();
					}
				}catch (JSONException e){
					e.printStackTrace();
					new MaterialDialog.Builder(activity)
							.content("Some error has been occured. Please try again later.")
							.positiveText("Ok").show();
				}

			}
		});
		if(RestClient.isNetworkAvailable(activity, util))
		{
			t.start();
		}
	}

	private void performRegister(final ArrayList<String> regParams){

		dialog.show();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				final String response = new RestClient(activity).register(regParams);

				try{
					final JSONObject jsonObject = new JSONObject(response);
					if(!jsonObject.get("status").equals(null)){

						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dialog.dismiss();
								try {
									if (jsonObject.get("status").toString().equals("error")) {
										errorDialog = new MaterialDialog.Builder(activity)
												.content(jsonObject.get("text").toString())
												.positiveText("Ok")
												.show();
									} else {
										preferences.setIsLoggedIn("1");
										DashboardActivity.setPopUpMenuItems();
										MyAccountFragment myAccountFragment = new MyAccountFragment();
										updateFragment(myAccountFragment);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}else {
						new MaterialDialog.Builder(activity)
								.content("Some error has been occured. Please try again later.")
								.positiveText("Ok").show();
					}
				}catch (JSONException e){
					e.printStackTrace();
					new MaterialDialog.Builder(activity)
							.content("Some error has been occured. Please try again later.")
							.positiveText("Ok").show();
				}

			}
		});
		if(RestClient.isNetworkAvailable(activity, util))
		{
			t.start();
		}
	}

	public void updateFragment(Fragment fragment) {
		FragmentManager fragmentManager = getFragmentManager();
		DashboardActivity.current_flag = fragment;
		AppController.fragmentStack.pop();
		String backStateName = fragment.getClass().getName();

		if(AppController.fragmentStack.contains(backStateName)){
			AppController.fragmentStack.remove(AppController.fragmentStack.indexOf(backStateName));
		}
		AppController.fragmentStack.push(backStateName);
		//Replace fragment
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.replace(R.id.content_frame, fragment);
		ft.commit();
	}

	public boolean validateRegistration(){
		boolean validated = false;
		if(etFname.getText().toString().trim().length()==0){
			etFname.setHelper("This is required");
			validated = false;
			return validated;
		}else{
			etFname.setHelper("");
			validated = true;
		}

		if(etLname.getText().toString().trim().length()==0){
			etLname.setHelper("This is required");
			validated = false;
			return validated;
		}else{
			etLname.setHelper("");
			validated = true;
		}

		if(etEmail.getText().toString().trim().length()==0){
			etEmail.setHelper("This is required");
			validated = false;
			return validated;
		}else if(!etEmail.getText().toString().matches(emailPattern)){
			etEmail.setHelper("Please enter a valid email address.");
			validated = false;
			return validated;
		}else{
			etEmail.setHelper("");
			validated = true;
		}

		if(etRPassword.getText().toString().trim().length()==0){
			etRPassword.requestFocus();
			etRPassword.setHelper("This is required");
			etCPassword.setHelper("This is required");
			validated = false;
			return validated;
		}else if(etRPassword.getText().toString().trim().length() > 0 && etRPassword.getText().toString().trim().length() < 6){
			etRPassword.setHelper("The minimum password length is 6 characters.");
			etCPassword.setHelper("This is required");
			validated = false;
			return validated;
		}else if(etCPassword.getText().toString().trim().length()==0){
			etCPassword.requestFocus();
			etCPassword.setHelper("This is required");
			etRPassword.setHelper("");
			validated = false;
			return validated;
		}else if(!etCPassword.getText().toString().equals(etRPassword.getText().toString())){
			etCPassword.setHelper("Passwords does not match.");
			etRPassword.setHelper("");
			validated = false;
			return validated;
		}else{
			etRPassword.setHelper("");
			etCPassword.setHelper("");
			validated = true;
		}

		return validated;

	}

	public boolean validateLogin(){
		boolean validated = false;
		if(etUsername.getText().toString().trim().length()==0){
			etUsername.setHelper("This is required");
			validated = false;
			return validated;
		}else if(!etUsername.getText().toString().matches(emailPattern)){
			etUsername.setHelper("Please enter a valid email address.");
			validated = false;
			return validated;
		}else{
			etUsername.setHelper("");
			validated = true;
		}

		if(etPassword.getText().toString().trim().length()==0){
			etPassword.requestFocus();
			etPassword.setHelper("This is required");
			validated = false;
			return validated;
		}else{
			etPassword.setHelper("");
			validated = true;

		}
		return validated;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

		if(position == 0){
			if(etUsername.getText().hashCode() == s.hashCode()){
				if(s.length() == 0)
					etUsername.setHelper("This is required");
				else if(s.length() >0 && !s.toString().matches(emailPattern))
					etUsername.setHelper("Please enter a valid email address.");
				else
					etUsername.setHelper("");

			}else if(etPassword.getText().hashCode() == s.hashCode()){
				if(s.length() == 0)
					etPassword.setHelper("This is required");
				else
					etPassword.setHelper("");
			}
		}else{

			if(etFname.getText().hashCode() == s.hashCode()){
				if(s.length() == 0)
					etFname.setHelper("This is required");
				else
					etFname.setHelper("");

			}else if(etLname.getText().hashCode() == s.hashCode()){
				if(s.length() == 0)
					etLname.setHelper("This is required");
				else
					etLname.setHelper("");

			} else if (etEmail.getText().hashCode() == s.hashCode()){
				if(s.length() == 0)
					etEmail.setHelper("This is required");
				else if(s.length() >0 && !s.toString().matches(emailPattern))
					etEmail.setHelper("Please enter a valid email address.");
				else
					etEmail.setHelper("");

			}else if(etCPassword.getText().hashCode() == s.hashCode()){

				if(s.length() == 0)
					etCPassword.setHelper("This is required");
				else if(s.length() > 0 && !s.toString().equals(etRPassword.getText().toString()))
					etCPassword.setHelper("Passwords does not match.");
				else
					etCPassword.setHelper("");

			}else if(etRPassword.getText().hashCode() == s.hashCode()){
				if(s.length() == 0)
					etRPassword.setHelper("This is required");
				else if(s.length() >0 && s.toString().length() < 6)
					etRPassword.setHelper("The minimum password length is 6 characters.");
				else
					etRPassword.setHelper("");
			}
		}

	}

	@Override
	public void afterTextChanged(Editable s) {}

	public void setAccountDetails(JSONObject jsonObject){

		try {
			preferences.setFirstName(jsonObject.get("firstname").toString());
			preferences.setLastName(jsonObject.get("lastname").toString());
			preferences.setEmail(jsonObject.get("email").toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void facebookCallBack(String flag,LoginResult result,FacebookException fbException){

		if(flag.equals("Success")){
			String accessToken = result.getAccessToken().getToken().toString();
			String appId = result.getAccessToken().getApplicationId().toString();
			performFBLogin(accessToken,appId);
			isBackFromFacebook = false;
			//Log.d("Access Token",accessToken);
			//Log.d("User Id", result.getAccessToken().getUserId().toString());
			//Log.d("Application Id", result.getAccessToken().getApplicationId().toString());

			//dialog.show();

		}else{
			Log.d("Exception",fbException.toString());
			errorDialog = new MaterialDialog.Builder(activity)
					.content("Some Error has been occurred. Please try again.")
					.positiveText("Ok")
					.show();
		}
	}

	public void performFBLogin(final String accessToken, final String applicationId){

		//Utility util1 = new Utility(activity);
		util.showLoadingDialog("Please Wait");
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				final String response = new RestClient(activity).loginViaFacebook(accessToken,applicationId);

				try{
					final JSONObject jsonObject = new JSONObject(response);
					if(!jsonObject.get("status").equals(null)){

						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								util.hideLoadingDialog();
								try {
									if (jsonObject.get("status").toString().equals("error")) {
										util.showErrorDialog(jsonObject.get("text").toString(), "Ok", "");

									} else {
										preferences.setIsLoggedIn("1");
										isBackFromFacebook = false;
										/*setAccountDetails(jsonObject.getJSONObject("account"));
										util.setPopUpMenuItems();
										DashboardFragment dashboardFragment = new DashboardFragment();
										updateFragment(dashboardFragment);*/
										util.showErrorDialog(jsonObject.get("text").toString(), "Ok", "");
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}else {
						util.showErrorDialog("Some error has been occurred. Please try again.", "Ok", "");

					}
				}catch (JSONException e){
					e.printStackTrace();
					util.showErrorDialog("Some error has been occurred. Please try again.", "Ok", "");
				}

			}
		});
		if(RestClient.isNetworkAvailable(activity, util))
		{
			t.start();
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity=activity;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(isBackFromFacebook){
			//performFBLogin(accessToken, appId);
			//util.showLoadingDialog("Please Wait");
			facebookCallBack(flag,loginResult,fbException);
		}
	}


	@Override
	public void onConnected(Bundle bundle) {
		Log.d("GPLUS", "onConnected:" + bundle);
		/* This Line is the key */
		//Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);

		// After that  fetch data
		if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
			Person currentPerson = Plus.PeopleApi
					.getCurrentPerson(mGoogleApiClient);

			String personName = currentPerson.getDisplayName();
			Log.i("personName", personName);

		}
	}

	@Override
	public void onConnectionSuspended(int i) {
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Could not connect to Google Play Services.  The user needs to select an account,
		// grant permissions or resolve an error in order to sign in. Refer to the javadoc for
		// ConnectionResult to see possible error codes.
		Log.d("GPLUS", "onConnectionFailed:" + result);

		if (!mIntentInProgress && result.hasResolution()) {
			try {
				mIntentInProgress = true;
				activity.startIntentSenderForResult(result.getResolution().getIntentSender(),
						RC_SIGN_IN, null, 0, 0, 0);
			} catch (IntentSender.SendIntentException e) {
				// The intent was canceled before it was sent.  Return to the default
				// state and attempt to connect to get an updated ConnectionResult.
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}



}
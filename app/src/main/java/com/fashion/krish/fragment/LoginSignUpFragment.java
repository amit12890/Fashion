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

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.adapter.ProductAdapter;
import com.fashion.krish.utility.AppPreferences;
import com.fashion.krish.utility.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rey.material.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginSignUpFragment extends Fragment implements TextWatcher {

	private static final String ARG_POSITION = "position";
	private int position;

	private MaterialDialog dialog,errorDialog;
	Utility util;

	private EditText etUsername,etPassword;
	private EditText etFname,etLname,etEmail,etRPassword,etCPassword;

	String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

	private Button btnLogin,btnRegister;
	private AppPreferences preferences;

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

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		LayoutInflater infalInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		dialog = new MaterialDialog.Builder(getActivity())
				.content("Please Wait")
				.progress(true, 0)
				.build();


		util = new Utility(getActivity());
		preferences = new AppPreferences(getActivity());

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

		return baseView;
	}

	public void handleLoginView(final View view){
		etUsername = (EditText) view.findViewById(R.id.edt_username);
		etUsername.addTextChangedListener(this);
		etPassword = (EditText) view.findViewById(R.id.edt_password);
		etPassword.addTextChangedListener(this);
		btnLogin =(Button) view.findViewById(R.id.btn_login);

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
		etFname.addTextChangedListener(this);
		etLname = (EditText) view.findViewById(R.id.edt_lname);
		etLname.addTextChangedListener(this);
		etEmail = (EditText) view.findViewById(R.id.edt_email);
		etEmail.addTextChangedListener(this);
		etRPassword = (EditText) view.findViewById(R.id.edt_password);
		etRPassword.addTextChangedListener(this);
		etCPassword = (EditText) view.findViewById(R.id.edt_cpassword);
		etCPassword.addTextChangedListener(this);
		btnRegister =(Button) view.findViewById(R.id.btn_register);

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
				final String response = new RestClient(getActivity()).login(username, password);

				try{
					final JSONObject jsonObject = new JSONObject(response);
					if(!jsonObject.get("status").equals(null)){

						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dialog.dismiss();
								try {
									if(jsonObject.get("status").toString().equals("error")){
										errorDialog = new MaterialDialog.Builder(getActivity())
												.content(jsonObject.get("text").toString())
												.positiveText("Ok")
												.show();
                                    }else{
										preferences.setIsLoggedIn("1");
										util.setPopUpMenuItems();
										DashboardFragment dashboardFragment = new DashboardFragment();
										updateFragment(dashboardFragment);
                                    }
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}else {
						Toast.makeText(getActivity(), "Some error has been occurred. Please try again later. ", Toast.LENGTH_SHORT).show();
					}
				}catch (JSONException e){
					e.printStackTrace();
					new MaterialDialog.Builder(getActivity())
							.content("Some error has been occured. Please try again later.")
							.positiveText("Ok").show();
				}

			}
		});
		if(RestClient.isNetworkAvailable(getActivity(), util))
		{
			t.start();
		}
	}

	private void performRegister(final ArrayList<String> regParams){

		dialog.show();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				final String response = new RestClient(getActivity()).register(regParams);

				try{
					final JSONObject jsonObject = new JSONObject(response);
					if(!jsonObject.get("status").equals(null)){

						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dialog.dismiss();
								try {
									if(jsonObject.get("status").toString().equals("error")){
										errorDialog = new MaterialDialog.Builder(getActivity())
												.content(jsonObject.get("text").toString())
												.positiveText("Ok")
												.show();
									}else{
										preferences.setIsLoggedIn("1");
										util.setPopUpMenuItems();
										DashboardFragment dashboardFragment = new DashboardFragment();
										updateFragment(dashboardFragment);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}else {
						new MaterialDialog.Builder(getActivity())
								.content("Some error has been occured. Please try again later.")
								.positiveText("Ok").show();
					}
				}catch (JSONException e){
					e.printStackTrace();
					new MaterialDialog.Builder(getActivity())
							.content("Some error has been occured. Please try again later.")
							.positiveText("Ok").show();
				}

			}
		});
		if(RestClient.isNetworkAvailable(getActivity(), util))
		{
			t.start();
		}
	}

	public void updateFragment(Fragment fragment) {
		FragmentManager fragmentManager = getFragmentManager();

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
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

		if(position == 0){
			if(etUsername.getText().hashCode() == s.hashCode()){
				if(count == 0)
					etUsername.setHelper("This is required");
				else if(count >0 && !s.toString().matches(emailPattern))
					etUsername.setHelper("Please enter a valid email address.");
				else
					etUsername.setHelper("");

			}else if(etPassword.getText().hashCode() == s.hashCode()){
				if(count == 0)
					etPassword.setHelper("This is required");
				else
					etPassword.setHelper("");
			}
		}else{

			if(etFname.getText().hashCode() == s.hashCode()){
				if(count == 0)
					etFname.setHelper("This is required");
				else
					etFname.setHelper("");

			}else if(etLname.getText().hashCode() == s.hashCode()){
				if(count == 0)
					etLname.setHelper("This is required");
				else
					etLname.setHelper("");

			} else if (etEmail.getText().hashCode() == s.hashCode()){
				if(count == 0)
					etEmail.setHelper("This is required");
				else if(count >0 && !s.toString().matches(emailPattern))
					etEmail.setHelper("Please enter a valid email address.");
				else
					etEmail.setHelper("");

			}else if(etCPassword.getText().hashCode() == s.hashCode()){

				if(count == 0)
					etCPassword.setHelper("This is required");
				else if(count > 0 && !s.toString().equals(etRPassword.getText().toString()))
					etCPassword.setHelper("Passwords does not match.");
				else
					etCPassword.setHelper("");

			}else if(etRPassword.getText().hashCode() == s.hashCode()){
				if(count == 0)
					etRPassword.setHelper("This is required");
				else if(count >0 && s.toString().length() < 6)
					etRPassword.setHelper("The minimum password length is 6 characters.");
				else
					etRPassword.setHelper("");
			}
		}

	}

	@Override
	public void afterTextChanged(Editable s) {

	}
}
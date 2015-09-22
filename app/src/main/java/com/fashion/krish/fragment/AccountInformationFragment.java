package com.fashion.krish.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.activity.DashboardActivity;
import com.fashion.krish.utility.AppPreferences;
import com.fashion.krish.utility.Utility;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AccountInformationFragment extends Fragment implements TextWatcher {

    Utility util;
    private EditText et_fname,et_lname,et_email,et_password,et_re_password,et_c_password;
    private CheckBox cb_change_pwd;
    private LinearLayout lay_password,lay_back;
    private Button btn_submit;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private AppPreferences preferences;
    ArrayList<String> accountInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_account_info, container, false);
        util = new Utility(getActivity());
        Utility.changeStatusBarColor(getActivity());
        preferences = new AppPreferences(getActivity());
        accountInfo = new ArrayList<>();

        init(rootView);


        return rootView;
    }

    private void init(View rootView){

        ((RelativeLayout)rootView.findViewById(R.id.lay_title)).setBackgroundColor(Color.parseColor(AppController.PRIMARY_COLOR));
        et_fname = (EditText) rootView.findViewById(R.id.edt_fname);
        et_fname.applyTheme(Color.parseColor(AppController.PRIMARY_COLOR), Color.parseColor(AppController.SECONDARY_COLOR));
        et_fname.addTextChangedListener(this);
        et_lname = (EditText) rootView.findViewById(R.id.edt_lname);
        et_lname.applyTheme(Color.parseColor(AppController.PRIMARY_COLOR), Color.parseColor(AppController.SECONDARY_COLOR));
        et_lname.addTextChangedListener(this);
        et_email = (EditText) rootView.findViewById(R.id.edt_email);
        et_email.applyTheme(Color.parseColor(AppController.PRIMARY_COLOR), Color.parseColor(AppController.SECONDARY_COLOR));
        et_email.addTextChangedListener(this);
        et_password = (EditText) rootView.findViewById(R.id.edt_password);
        et_password.applyTheme(Color.parseColor(AppController.PRIMARY_COLOR), Color.parseColor(AppController.SECONDARY_COLOR));
        et_password.addTextChangedListener(this);
        et_re_password = (EditText) rootView.findViewById(R.id.edt__new_password);
        et_re_password.applyTheme(Color.parseColor(AppController.PRIMARY_COLOR), Color.parseColor(AppController.SECONDARY_COLOR));
        et_re_password.addTextChangedListener(this);
        et_c_password = (EditText) rootView.findViewById(R.id.edt_cpassword);
        et_c_password.applyTheme(Color.parseColor(AppController.PRIMARY_COLOR), Color.parseColor(AppController.SECONDARY_COLOR));
        et_c_password.addTextChangedListener(this);


        lay_password = (LinearLayout) rootView.findViewById(R.id.lay_change_password);

        LinearLayout layPwd = (LinearLayout) rootView.findViewById(R.id.lay_change_pwd);
        //layPwd.setBackgroundColor(Color.parseColor(AppController.PRIMARY_COLOR));
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cb_change_pwd = new CheckBox(getActivity(),preferences.getSecondaryColor());
        cb_change_pwd.setText("Change Password");
        cb_change_pwd.setGravity(Gravity.CENTER_VERTICAL);
        cb_change_pwd.setLayoutParams(layParams);
        layPwd.addView(cb_change_pwd);
        //cb_change_pwd = (CheckBox) findViewById(R.id.cb_change_password);
        cb_change_pwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    lay_password.setVisibility(View.VISIBLE);
                else
                    lay_password.setVisibility(View.GONE);
            }
        });

        LinearLayout submitLay = (LinearLayout) rootView.findViewById(R.id.lay_submit_btn);
        GradientDrawable submitLayDrawable = (GradientDrawable) submitLay.getBackground();
        submitLayDrawable.setColor(preferences.getSecondaryColor());

        btn_submit = (Button) rootView.findViewById(R.id.btn_submit);
        btn_submit.setBackgroundDrawable(util.getSecondaryRippleDrawable());
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountInfo.clear();
                if (validateForm()) {
                    accountInfo.add(0, "0");
                    accountInfo.add(et_fname.getText().toString());
                    accountInfo.add(et_lname.getText().toString());
                    accountInfo.add(et_email.getText().toString());

                    if (cb_change_pwd.isChecked()) {
                        accountInfo.set(0, "1");
                        accountInfo.add(et_password.getText().toString());
                        accountInfo.add(et_re_password.getText().toString());
                        accountInfo.add(et_c_password.getText().toString());
                    }
                    util.showLoadingDialog("Please Wait");
                    setAccountInfo();

                }

            }
        });

        DashboardActivity.animateToggle(0, 1);
        fillData();


    }


    public boolean validateForm(){
        boolean validated = false;
        if(et_fname.getText().toString().trim().length()==0){
            et_fname.setHelper("This is required");
            validated = false;
            return validated;
        }else{
            et_fname.setHelper("");
            validated = true;
        }

        if(et_lname.getText().toString().trim().length()==0){
            et_lname.setHelper("This is required");
            validated = false;
            return validated;
        }else{
            et_lname.setHelper("");
            validated = true;
        }


        if(et_email.getText().toString().trim().length()==0){
            et_email.setHelper("This is required");
            validated = false;
            return validated;
        }else if(!et_email.getText().toString().matches(emailPattern)){
            et_email.setHelper("Please enter a valid email address.");
            validated = false;
            return validated;
        }else{
            et_email.setHelper("");
            validated = true;
        }

        if(cb_change_pwd.isChecked()){
            if(et_password.getText().toString().trim().length()==0){
                et_password.requestFocus();
                et_password.setHelper("This is required");
                et_re_password.setHelper("This is required");
                et_c_password.setHelper("This is required");
                validated = false;
                return validated;
            }else if(et_password.getText().toString().trim().length() > 0 && et_password.getText().toString().trim().length() < 6){
                et_password.setHelper("The minimum password length is 6 characters.");
                et_re_password.setHelper("This is required");
                et_c_password.setHelper("This is required");
                validated = false;
                return validated;
            }else if(et_re_password.getText().toString().trim().length()==0){
                et_password.setHelper("");
                et_re_password.requestFocus();
                et_c_password.setHelper("This is required");
                validated = false;
                return validated;
            }else if(et_re_password.getText().toString().trim().length() > 0 && et_re_password.getText().toString().trim().length() < 6){
                et_re_password.setHelper("The minimum password length is 6 characters.");
                et_c_password.setHelper("This is required");
                validated = false;
                return validated;
            }else if(et_c_password.getText().toString().trim().length()==0){
                et_c_password.requestFocus();
                et_c_password.setHelper("This is required");
                et_re_password.setHelper("");
                et_password.setHelper("");
                validated = false;
                return validated;
            }else if(!et_c_password.getText().toString().equals(et_re_password.getText().toString())){
                et_c_password.setHelper("Passwords does not match.");
                et_re_password.setHelper("");
                et_password.setHelper("");
                validated = false;
                return validated;
            }else{
                et_re_password.setHelper("");
                et_password.setHelper("");
                et_c_password.setHelper("");
                validated = true;
            }
        }



        return validated;

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(et_fname.getText().hashCode() == s.hashCode()){
            if(s.length() == 0)
                et_fname.setHelper("This is required");
            else
                et_fname.setHelper("");

        }else if(et_lname.getText().hashCode() == s.hashCode()){
            if(s.length() == 0)
                et_lname.setHelper("This is required");
            else
                et_lname.setHelper("");

        } else if (et_email.getText().hashCode() == s.hashCode()){
            if(s.length() == 0)
                et_email.setHelper("This is required");
            else if(s.length() >0 && !s.toString().matches(emailPattern))
                et_email.setHelper("Please enter a valid email address.");
            else
                et_email.setHelper("");

        }else if(et_password.getText().hashCode() == s.hashCode()){

            if(s.length() == 0)
                et_password.setHelper("This is required");
            else if(s.length() > 0 && s.toString().length() < 6)
                et_password.setHelper("The minimum password length is 6 characters.");
            else
                et_password.setHelper("");

        }else if(et_re_password.getText().hashCode() == s.hashCode()){

            if(s.length() == 0)
                et_re_password.setHelper("This is required");
            else if(s.length() > 0 && s.toString().length() < 6)
                et_re_password.setHelper("The minimum password length is 6 characters.");
            else
                et_re_password.setHelper("");

        }else if(et_c_password.getText().hashCode() == s.hashCode()){

            if(s.length() == 0)
                et_c_password.setHelper("This is required");
            else if(s.length() > 0 && !s.toString().equals(et_re_password.getText().toString()))
                et_c_password.setHelper("Passwords does not match.");
            else
                et_c_password.setHelper("");

        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void fillData(){
        et_fname.setText(preferences.getFirstName());
        et_lname.setText(preferences.getLastName());
        et_email.setText(preferences.getEmail());
        cb_change_pwd.setChecked(false);
    }

    private void setAccountInfo(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(getActivity()).changeAccountInfo(accountInfo);

                try{
                    final JSONObject jsonObject = new JSONObject(response);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(jsonObject.get("status").toString().equals("error")){
                                    util.hideLoadingDialog();
                                    util.showErrorDialog(jsonObject.get("text").toString(),"Ok","");
                                }else{
                                    util.hideLoadingDialog();
                                    util.showErrorDialog(jsonObject.get("text").toString(),"Ok","");
                                    preferences.setFirstName(et_fname.getText().toString());
                                    preferences.setLastName(et_lname.getText().toString());
                                    preferences.setEmail(et_email.getText().toString());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        });
        if(RestClient.isNetworkAvailable(getActivity(), util))
        {
            t.start();


        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DashboardActivity.animateToggle(1, 0);
    }
}

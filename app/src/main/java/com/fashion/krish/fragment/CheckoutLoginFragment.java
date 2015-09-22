package com.fashion.krish.fragment;

import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.fashion.krish.R;
import com.fashion.krish.activity.CheckoutActivity;
import com.fashion.krish.utility.AppPreferences;
import com.fashion.krish.utility.Utility;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;
import com.rey.material.widget.RadioButton;

public class CheckoutLoginFragment implements TextWatcher,CompoundButton.OnCheckedChangeListener{

    private RadioButton radioGuest,radioRegister;
    private LinearLayout layRegister;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public Button btnLogin,btnRegister;
    private EditText etUsername,etPassword;
    private EditText etFname,etLname,etEmail,etRPassword;
    private AppPreferences preferences;

    private Utility util;
    private CheckoutActivity activity;
    private boolean isRegister = false;

    public CheckoutLoginFragment(CheckoutActivity activity){
        this.activity = activity;
        init();
    }

    private void init(){

        preferences = new AppPreferences(activity);
        util = new Utility(activity);

        radioGuest = (RadioButton) activity.findViewById(R.id.radio_guest);
        radioGuest.setStrokeColor(preferences.getSecondaryColor());
        radioGuest.setOnCheckedChangeListener(this);
        radioRegister = (RadioButton) activity.findViewById(R.id.radio_register);
        radioRegister.setStrokeColor(preferences.getSecondaryColor());
        radioRegister.setOnCheckedChangeListener(this);
        layRegister = (LinearLayout) activity.findViewById(R.id.lay_register);
        layRegister.setVisibility(View.GONE);

        btnRegister =(Button) activity.findViewById(R.id.btn_continue);
        btnRegister.setBackgroundDrawable(util.getSecondaryRippleDrawable());

        handleLoginView();
        handleRegisterView();

    }

    public void show(){

    }

    public void hide(){

    }

    public void handleLoginView(){
        etUsername = (EditText) activity.findViewById(R.id.edt_username);
        applyTheme(etUsername);
        etUsername.addTextChangedListener(this);
        etPassword = (EditText) activity.findViewById(R.id.edt_login_password);
        etPassword.addTextChangedListener(this);
        applyTheme(etPassword);

        LinearLayout layLogin = (LinearLayout) activity.findViewById(R.id.lay_login_btn);
        GradientDrawable layLoginDrawable = (GradientDrawable) layLogin.getBackground();
        layLoginDrawable.setColor(preferences.getSecondaryColor());

        btnLogin =(Button) activity.findViewById(R.id.btn_login);
        btnLogin.setBackgroundDrawable(util.getSecondaryRippleDrawable());

    }

    public void handleRegisterView(){

        etFname = (EditText) activity.findViewById(R.id.edt_fname);
        applyTheme(etFname);
        etFname.addTextChangedListener(this);
        etLname = (EditText) activity.findViewById(R.id.edt_lname);
        applyTheme(etLname);
        etLname.addTextChangedListener(this);
        etEmail = (EditText) activity.findViewById(R.id.edt_email);
        applyTheme(etEmail);
        etEmail.addTextChangedListener(this);
        etRPassword = (EditText) activity.findViewById(R.id.edt_password);
        applyTheme(etRPassword);
        etRPassword.addTextChangedListener(this);


        LinearLayout layContinue = (LinearLayout) activity.findViewById(R.id.lay_continue_btn);
       // GradientDrawable layLoginDrawable = (GradientDrawable) layContinue.getBackground();
        //layLoginDrawable.setColor(preferences.getSecondaryColor());


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
            validated = false;
            return validated;
        }else if(etRPassword.getText().toString().trim().length() > 0 && etRPassword.getText().toString().trim().length() < 6){
            etRPassword.setHelper("The minimum password length is 6 characters.");
            validated = false;
            return validated;
        }else{
            etRPassword.setHelper("");
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

        if(!isRegister){
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
    
    public  void applyTheme(EditText v){
        v.applyTheme(preferences.getPrimaryColor(),preferences.getSecondaryColor());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.radio_guest:
                    if(isChecked){
                        radioRegister.setChecked(false);
                        layRegister.setVisibility(View.GONE);
                        isRegister = false;
                    }else{
                        radioRegister.setChecked(true);
                        layRegister.setVisibility(View.VISIBLE);
                        isRegister = true;
                    }


                break;
            case R.id.radio_register:

                if(isChecked){
                    radioGuest.setChecked(false);
                    layRegister.setVisibility(View.VISIBLE);
                    isRegister = true;
                }else{
                    radioGuest.setChecked(true);
                    layRegister.setVisibility(View.GONE);
                    isRegister = false;
                }
                break;
        }
    }
}

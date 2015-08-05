package com.fashion.krish.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.fashion.krish.model.Category;
import com.fashion.krish.model.HomeBanners;
import com.fashion.krish.model.HomeCategory;

import java.util.ArrayList;

public class AppPreferences {
	
	SharedPreferences preferences;
	Context context;
	final String App_PREFERENCE = "app_preference";
	final String SHOW_LOGIN_SCREEN = "showLoginScreen";
	final String COOKIE_NAME = "cookie_name";
	final String COOKIE_VALUE = "cookie_value";
	final String COOKIE_DOMAIN = "cookie_domain";	
	final String USERNAME = "username";
	final String PASSWORD = "password";
	final String IS_LOGGEDIN = "is_loggined";

	
	public AppPreferences(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		preferences = context.getSharedPreferences(App_PREFERENCE, Context.MODE_PRIVATE);

	}
	

	public boolean showLoginScreen()
	{
		return preferences.getBoolean(SHOW_LOGIN_SCREEN, true);
	}
	
	public void setShowLoginScreen(boolean showLoginScreen)
	{
		Editor editor = preferences.edit();		
		editor.putBoolean(SHOW_LOGIN_SCREEN, showLoginScreen);
		editor.commit();
	}
	
	public String getCookieDomain()
	{
		return preferences.getString(COOKIE_DOMAIN, "");
	}
	
	public String getCookieName()
	{
		return preferences.getString(COOKIE_NAME, "");
	}
	
	public String getCookieValue()
	{
		return preferences.getString(COOKIE_VALUE, "");
	}
	
	public void setCookies(String cookieName, String cookieValue, String cookieDomain)
	{
		Editor editor = preferences.edit();		
		editor.putString(COOKIE_NAME, cookieName);
		editor.putString(COOKIE_VALUE, cookieValue);
		editor.putString(COOKIE_DOMAIN, cookieDomain);
		editor.commit();
	}
	
	public String getUserName()
	{
		return preferences.getString(USERNAME, "");
	}
	
	public void setUserName(String userName)
	{
		Editor editor = preferences.edit();		
		editor.putString(USERNAME, userName);
		editor.commit();
	}
	
	public String getPassword()
	{
		return preferences.getString(PASSWORD, "");
	}
	
	public void setPassword(String password)
	{
		Editor editor = preferences.edit();		
		editor.putString(PASSWORD, password);
		editor.commit();
	}

	public String getIsLoggedIn()
	{
		return preferences.getString(IS_LOGGEDIN, "0");
	}

	public void setIsLoggedIn(String is_loggined)
	{
		Editor editor = preferences.edit();
		editor.putString(IS_LOGGEDIN, is_loggined);
		editor.commit();
	}
	
}

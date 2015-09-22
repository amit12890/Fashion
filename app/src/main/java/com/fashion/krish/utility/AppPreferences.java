package com.fashion.krish.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;

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
	final String F_NAME ="first_name";
	final String L_NAME ="last_name";
	final String EMAIL ="email";
	final String PRIMARY_COLOR = "primary_color";
	final String SECONDARY_COLOR = "secondary_color";
	final String BKG_COLOR = "bkg_color";
	final String WARN_COLOR = "warn_color";
	final String FOOTER_BKG_COLOR = "footer_bkg_color";



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
	
	public void setUserName(String userName){
		Editor editor = preferences.edit();		
		editor.putString(USERNAME, userName);
		editor.commit();
	}

	public String getFirstName()
	{
		return preferences.getString(F_NAME, "");
	}

	public void setFirstName(String firstName){
		Editor editor = preferences.edit();
		editor.putString(F_NAME, firstName);
		editor.commit();
	}

	public String getLastName()
	{
		return preferences.getString(L_NAME, "");
	}

	public void setLastName(String lastName){
		Editor editor = preferences.edit();
		editor.putString(L_NAME, lastName);
		editor.commit();
	}

	public String getEmail()
	{
		return preferences.getString(EMAIL, "");
	}

	public void setEmail(String email){
		Editor editor = preferences.edit();
		editor.putString(EMAIL, email);
		editor.commit();
	}
	
	public String getPassword()
	{
		return preferences.getString(PASSWORD, "");
	}
	
	public void setPassword(String password){
		Editor editor = preferences.edit();		
		editor.putString(PASSWORD, password);
		editor.commit();
	}

	public String getIsLoggedIn()
	{
		return preferences.getString(IS_LOGGEDIN, "0");
	}

	public void setIsLoggedIn(String is_loggined){
		Editor editor = preferences.edit();
		editor.putString(IS_LOGGEDIN, is_loggined);
		editor.commit();
	}

	public int getPrimaryColor()
	{
		int color = Color.parseColor(preferences.getString(PRIMARY_COLOR, "#7B1FA2"));
		return color;
	}

	public void setPrimaryColor(String color){
		Editor editor = preferences.edit();
		editor.putString(PRIMARY_COLOR, color);
		editor.commit();
	}

	public int getSecondaryColor()
	{
		int color = Color.parseColor(preferences.getString(SECONDARY_COLOR, "#E91E63"));
		return color;
	}

	public void setSecondaryColor(String color){
		Editor editor = preferences.edit();
		editor.putString(SECONDARY_COLOR, color);
		editor.commit();
	}

	public int getBkgColor()
	{
		int color = Color.parseColor(preferences.getString(BKG_COLOR, "#FAFAFA"));
		return color;
	}

	public void setBkgColor(String color){
		Editor editor = preferences.edit();
		editor.putString(BKG_COLOR, color);
		editor.commit();
	}

	public int getFooterBkgColor()
	{
		int color = Color.parseColor(preferences.getString(FOOTER_BKG_COLOR, "#3D3B3F"));
		return color;
	}

	public void setFooterBkgColor(String color){
		Editor editor = preferences.edit();
		editor.putString(FOOTER_BKG_COLOR, color);
		editor.commit();
	}

	public int getWarnColor()
	{
		int color = Color.parseColor(preferences.getString(WARN_COLOR, "#D32F2F"));
		return color;
	}

	public void setWarnColor(String color){
		Editor editor = preferences.edit();
		editor.putString(WARN_COLOR, color);
		editor.commit();
	}


}

package com.fashion.krish.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;


public class SplashActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    // Tag used to cancel the request
    String connection_tag = "connection_tag",dashboard_content_tag="dashboard_content_tag";
    Utility util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        util = new Utility(SplashActivity.this);
        establishConnection();
        AppController.categoryArrayList.clear();
        AppController.homeBannersArrayList.clear();
        AppController.homeCategoryArrayList.clear();

    }

    private void establishConnection(){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(SplashActivity.this).createConnection();

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(!jsonObject.get("body").equals(null)){
                        getHomeScreenData();
                    }else {
                        Toast.makeText(SplashActivity.this,"Some Error has been occured. Please try again later. ",Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        });
        if(RestClient.isNetworkAvailable(SplashActivity.this, util))
        {
            t.start();
        }
    }

    private void getHomeScreenData(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(SplashActivity.this).getHomeScreenData();

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(!jsonObject.get("general").equals(null)){
                        util.parseCategory(jsonObject);
                        util.parseHomeBanners(jsonObject);
                        util.parseStaticHomeBanners(jsonObject);
                        util.parseHomeCategory(jsonObject);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent mainIntent = new Intent(SplashActivity.this, DashboardActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }
                        });
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        });
        if(RestClient.isNetworkAvailable(SplashActivity.this, util))
        {
            t.start();


        }

    }



}

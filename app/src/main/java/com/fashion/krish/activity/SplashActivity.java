package com.fashion.krish.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.model.Product;
import com.fashion.krish.utility.AppPreferences;
import com.fashion.krish.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SplashActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    // Tag used to cancel the request
    String connection_tag = "connection_tag",dashboard_content_tag="dashboard_content_tag";
    Utility util;
    public static ArrayList<Product> recentViewedProducts;
    private AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Utility.changeStatusBarColor(SplashActivity.this);
        appPreferences = new AppPreferences(SplashActivity.this);


        ((RelativeLayout) findViewById(R.id.lay_splash_root)).setBackgroundColor(appPreferences.getPrimaryColor());

        FacebookSdk.sdkInitialize(getApplicationContext());
        util = new Utility(SplashActivity.this);

        establishConnection();
        AppController.categoryArrayList.clear();
        AppController.homeBannersArrayList.clear();
        AppController.homeCategoryArrayList.clear();
        AppController.recentViewedProducts.clear();
        //util.getHashKey();

       // Date d = new Date();


    }

    private void establishConnection(){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(SplashActivity.this).createConnection();
                if(response.equals(RestClient.ERROR)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                            util.hideAnimatedLogoProgressBar();
                            return;
                        }
                    });
                }else if(response.equals(RestClient.TIMEOUT_ERROR)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog(RestClient.TIMEOUT_ERROR_MESSAGE, "OK", "");
                            util.hideAnimatedLogoProgressBar();

                            return;
                        }
                    });
                }
                try{
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.get("body").equals(null)){
                        getHomeScreenData();
                        util.parseCategory(jsonObject);
                        util.setSocialNetworkData(jsonObject.getJSONObject("socialNetworking"));
                        util.parseLanguageData(jsonObject.getJSONObject("stores"));
                        util.parseCurrencyData(jsonObject.getJSONObject("currency"));
                        util.parseContentData(jsonObject.getJSONObject("content").getJSONObject("page"));
                        util.getCartInfo();
                        AppController.PRIMARY_COLOR = jsonObject.getJSONObject("body").get("primaryColor").toString();
                        AppController.SECONDARY_COLOR = jsonObject.getJSONObject("body").get("secondaryColor").toString();

                        appPreferences.setPrimaryColor(jsonObject.getJSONObject("body").get("primaryColor").toString());
                        appPreferences.setSecondaryColor(jsonObject.getJSONObject("body").get("secondaryColor").toString());

                        if(jsonObject.getJSONObject("general").has("copyright"))
                            AppController.COPY_RIGHT = jsonObject.getJSONObject("general").getString("copyright");


                    }else {
                        Toast.makeText(SplashActivity.this,"Some Error has been occurred. Please try again later. ",
                                Toast.LENGTH_SHORT).show();
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
                if(response.equals(RestClient.ERROR)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                            util.hideAnimatedLogoProgressBar();
                            return;
                        }
                    });
                }else if(response.equals(RestClient.TIMEOUT_ERROR)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog(RestClient.TIMEOUT_ERROR_MESSAGE, "OK", "");
                            util.hideAnimatedLogoProgressBar();

                            return;
                        }
                    });
                }

                try{
                    JSONObject jsonObject = new JSONObject(response);

                    if(!jsonObject.get("general").equals(null)){

                        util.parseHomeBanners(jsonObject);
                        util.parseStaticHomeBanners(jsonObject);
                        util.parseHomeCategory(jsonObject);
                        getRecentViewedProducts();

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

    private void getRecentViewedProducts(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(SplashActivity.this).getRecentViewedProducts();
                if(response.equals(RestClient.ERROR)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                            util.hideAnimatedLogoProgressBar();
                            return;
                        }
                    });
                }else if(response.equals(RestClient.TIMEOUT_ERROR)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog(RestClient.TIMEOUT_ERROR_MESSAGE, "OK", "");
                            util.hideAnimatedLogoProgressBar();

                            return;
                        }
                    });
                }
                try{
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.has("products")){

                        AppController.recentViewedProducts = util.parseRelatedProduct(jsonObject.getJSONObject("products"));
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

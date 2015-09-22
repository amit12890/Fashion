package com.fashion.krish.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.customview.TintableImageView;
import com.fashion.krish.fragment.MyAddressFragment;
import com.fashion.krish.utility.AppPreferences;
import com.fashion.krish.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MyAccountActivity extends Activity implements View.OnClickListener,View.OnTouchListener {

    Utility util;
    private AppPreferences preferences;
    private LinearLayout layBack;
    private int color_primary,color_secondary;
    private LinearLayout layChildContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_my_account);

        Utility.changeStatusBarColor(MyAccountActivity.this);
        util = new Utility(MyAccountActivity.this);
        preferences = new AppPreferences(MyAccountActivity.this);

        color_primary = Color.parseColor(AppController.PRIMARY_COLOR);
        color_secondary = Color.parseColor(AppController.SECONDARY_COLOR);

        ((RelativeLayout) findViewById(R.id.lay_title)).setBackgroundColor(color_primary);
        layBack = (LinearLayout) findViewById(R.id.lay_back);
        layBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        layChildContainer = (LinearLayout) findViewById(R.id.lay_myaccount_child_container);

        getUserAccountData();

    }

    @Override
    protected void onPause(){
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }


    @Override
    public void onClick(View v) {

        switch (v.getTag().toString()){
            case "account":
               // Intent accountIntent = new Intent(MyAccountActivity.this,AccountInformationActivity.class);
                //startActivity(accountIntent);
                break;
            case "addresses":
                Intent addressIntent = new Intent(MyAccountActivity.this,MyAddressFragment.class);
                startActivity(addressIntent);
                break;

        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP) {
            v.setBackgroundColor(Color.TRANSPARENT);
        } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
            v.setBackground(util.getSecondaryRippleDrawable());
        }
        return false;
    }

    private void getUserAccountData(){
        util.showLoadingDialog("Please Wait");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(MyAccountActivity.this).getUserAccountData();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            util.hideLoadingDialog();
                            JSONObject jsonObject = new JSONObject(response);
                            if(response.equals("error")){
                                util.hideLoadingDialog();
                                util.showErrorDialog("Some error has been occurred. Please try again","Ok","");
                            }else{

                                if(jsonObject.get("item") instanceof JSONObject){
                                    JSONObject itemObj = jsonObject.getJSONObject("item");

                                    String label = itemObj.getString("label");
                                    String text = itemObj.getString("text");
                                    String id = itemObj.getString("id");

                                    addUserAccountOptions(label, text, id);


                                }else{

                                    JSONArray itemArray = jsonObject.getJSONArray("item");
                                    for (int i = 0; i < itemArray.length(); i++) {
                                        JSONObject itemObj = itemArray.getJSONObject(i);

                                        String label = itemObj.getString("label");
                                        String text = itemObj.getString("text");
                                        String id = itemObj.getString("id");

                                        addUserAccountOptions(label, text, id);
                                    }
                                }


                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            util.hideLoadingDialog();
                            util.showErrorDialog("Some error has been occurred. Please try again", "Ok", "");
                        }

                    }
                });


            }
        });
        if(RestClient.isNetworkAvailable(MyAccountActivity.this, util))
        {
            t.start();


        }

    }

    public void addUserAccountOptions(String label,String text,String tag){

        ColorStateList tint = ColorStateList.valueOf(color_secondary);

        LayoutInflater infalInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View accountChildView = infalInflater.inflate(R.layout.layout_my_account_child, null);

        LinearLayout layViewMore = (LinearLayout) accountChildView.findViewById(R.id.lay_view_more);
        TextView txtLabel = (TextView) accountChildView.findViewById(R.id.txt_label);
        TextView txtValue = (TextView) accountChildView.findViewById(R.id.txt_value);
        TextView txtViewMore = (TextView) accountChildView.findViewById(R.id.txt_view_more);
        TintableImageView imgViewMore = (TintableImageView) accountChildView.findViewById(R.id.img_arrow);

        layViewMore.setTag(tag);
        layViewMore.setOnClickListener(this);
        layViewMore.setOnTouchListener(this);
        txtLabel.setText(label);
        txtValue.setText(Html.fromHtml(text));
        txtViewMore.setTextColor(color_secondary);
        imgViewMore.setColorFilter(tint);

        layChildContainer.addView(accountChildView);

    }

}

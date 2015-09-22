package com.fashion.krish.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.model.Cart;
import com.fashion.krish.utility.AppPreferences;
import com.fashion.krish.utility.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ShoppingCartActivity extends ActionBarActivity implements View.OnClickListener {

    ImageLoader imageLoader;
    DisplayImageOptions options;

    LinearLayout layProceedToCheckout,layBack;
    Button btnProceedToCheckout;
    private ViewPager mGallery;

    Utility util;

    private TextView txtTitle;
    private RelativeLayout layDiscount,layTax,layDiscountHeader,layRoot;
    private LinearLayout productContainer,layDiscountChild;
    private com.rey.material.widget.Button btnApplyCoupon;
    private EditText etCoupon;
    private boolean isDelete = false,isCouponOpen = false;
    public static Activity SHOPPING_CART_ACTIVITY;
    private LinearLayout layEmptyCart,layNonEmptyCart;
    private AppPreferences preferences;
    private boolean isCouponApplied = false;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_shopping_cart);

        util = new Utility(ShoppingCartActivity.this);
        preferences = new AppPreferences(ShoppingCartActivity.this);

        RelativeLayout titleLay = (RelativeLayout) findViewById(R.id.lay_title);
        titleLay.setBackgroundColor(preferences.getPrimaryColor());

        Utility.changeStatusBarColor(ShoppingCartActivity.this);
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.logo)
                .showImageOnFail(R.drawable.logo)
                .showImageOnLoading(R.drawable.logo).build();

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Sail_Regular.otf");
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText("Shopping Cart");
        txtTitle.setTypeface(tf);
        layBack = (LinearLayout) findViewById(R.id.lay_back);
        layBack.setOnClickListener(this);

        layRoot = (RelativeLayout) findViewById(R.id.root_productDetail);

        layDiscount = (RelativeLayout) findViewById(R.id.lay_discount_);
        layTax = (RelativeLayout) findViewById(R.id.lay_tax);

        layProceedToCheckout = (LinearLayout) findViewById(R.id.lay_proceed_to_checkout);

        GradientDrawable subTotalDrawable = (GradientDrawable) layProceedToCheckout.getBackground();
        subTotalDrawable.setColor(preferences.getSecondaryColor());

        btnProceedToCheckout = (Button) findViewById(R.id.btn_proceed_to_checkout);
        btnProceedToCheckout.setBackground(util.getSecondaryRippleDrawable());
        btnProceedToCheckout.setOnClickListener(this);

        productContainer = (LinearLayout) findViewById(R.id.lay_float_product_container);


        layDiscountHeader = (RelativeLayout) findViewById(R.id.lay_discount_label);
        layDiscountHeader.setOnClickListener(this);
        layDiscountChild = (LinearLayout) findViewById(R.id.lay_discount_child);

        etCoupon = (EditText) findViewById(R.id.edt_discount);
        etCoupon.applyTheme(preferences.getPrimaryColor(), preferences.getSecondaryColor());

        LinearLayout layApplyCoupon = (LinearLayout) findViewById(R.id.lay_apply_coupon);
        GradientDrawable couponDrawable = (GradientDrawable) layApplyCoupon.getBackground();
        couponDrawable.setColor(preferences.getSecondaryColor());
        btnApplyCoupon = (com.rey.material.widget.Button) findViewById(R.id.btn_apply_coupon);
        btnApplyCoupon.setOnClickListener(this);
        btnApplyCoupon.setBackground(util.getSecondaryRippleDrawable());

        layEmptyCart = (LinearLayout) findViewById(R.id.lay_empty_cart);
        layNonEmptyCart = (LinearLayout) findViewById(R.id.lay_non_empty_cart);

        //addCartProducts(productContainer);
        getCartInfo();

    }


    @Override
    protected void onPause(){
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lay_discount_label:
            ImageView imgArrow = (ImageView) findViewById(R.id.img_discount_arrow);
            RotateAnimation animClose = new RotateAnimation(90f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animClose.setInterpolator(new LinearInterpolator());
            animClose.setDuration(200);
            animClose.setFillAfter(true);

            RotateAnimation animOpen = new RotateAnimation(0f, 90f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animOpen.setInterpolator(new LinearInterpolator());
            animOpen.setDuration(200);
            animOpen.setFillAfter(true);

            if(isCouponOpen){
                isCouponOpen = false;
                layDiscountChild.setVisibility(View.GONE);
                imgArrow.startAnimation(animClose);


            }else{
                isCouponOpen = true;
                etCoupon.requestFocus();
                layDiscountChild.setVisibility(View.VISIBLE);
                imgArrow.startAnimation(animOpen);
                final ScrollView scrollView = (ScrollView) findViewById(R.id.float_cart_scroll);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        //scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
                //rotateOpen.start();
            }
            break;

            case R.id.lay_back:
                onBackPressed();
                break;
            case R.id.btn_proceed_to_checkout:
                Intent i = new Intent(ShoppingCartActivity.this,CheckoutActivity.class);
                SHOPPING_CART_ACTIVITY = this;
                startActivity(i);
                break;
            case R.id.btn_apply_coupon:
                applyCoupon(etCoupon.getText().toString());
                break;
        }
    }

    public void addCartProducts(final LinearLayout rootView) {

        DashboardActivity.txtCartBadge.setText(AppController.SHOPPING_CART.cart_qty);

        LayoutInflater infalInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView.removeAllViews();
        if (Integer.parseInt(AppController.SHOPPING_CART.cart_qty) > 0) {
            layNonEmptyCart.setVisibility(View.VISIBLE);
            layEmptyCart.setVisibility(View.GONE);
            ((TextView) findViewById(R.id.txt_subtotal_label)).setText(AppController.SHOPPING_CART.subtotal_title);
            ((TextView) findViewById(R.id.txt_subtotal_value)).setText(AppController.SHOPPING_CART.subtotal_formated_value);
            ((TextView) findViewById(R.id.txt_grand_total_label)).setText(AppController.SHOPPING_CART.grandtotal_title);
            ((TextView) findViewById(R.id.txt_grand_total_value)).setText(AppController.SHOPPING_CART.grandtotal_formated_value);

            if(AppController.SHOPPING_CART.tax_value.length() > 0){
                layTax.setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.txt_tax_label)).setText(AppController.SHOPPING_CART.tax_title);
                ((TextView)findViewById(R.id.txt_tax_value)).setText(AppController.SHOPPING_CART.tax_formated_value);
            }else if(AppController.SHOPPING_CART.tax_value.length() == 0){
                layTax.setVisibility(View.GONE);
            }

            if(AppController.SHOPPING_CART.discount_title.length() > 0){
                ((TextView)findViewById(R.id.txt_discount_title)).setText(AppController.SHOPPING_CART.discount_title);
                ((TextView)findViewById(R.id.txt_discount)).setText(AppController.SHOPPING_CART.discount_formated_value);
                ((TextView)findViewById(R.id.txt_discount)).setTextColor(preferences.getSecondaryColor());
                etCoupon.setVisibility(View.GONE);
                etCoupon.setText("");
                ((RelativeLayout) findViewById(R.id.lay_discount_applied)).setVisibility(View.VISIBLE);
                isCouponApplied = true;
                btnApplyCoupon.setText("Remove Coupon");
                layDiscount.setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.txt_discount_label)).setText(AppController.SHOPPING_CART.discount_title);
                ((TextView)findViewById(R.id.txt_discount_value)).setText(AppController.SHOPPING_CART.discount_formated_value);
            }else{
                etCoupon.setVisibility(View.VISIBLE);
                ((RelativeLayout) findViewById(R.id.lay_discount_applied)).setVisibility(View.GONE);
                isCouponApplied = false;
                btnApplyCoupon.setText("Apply Coupon");
                layDiscount.setVisibility(View.GONE);
            }

        for (int i = 0; i < AppController.SHOPPING_CART.cartItems.size(); i++) {

            ArrayList<Cart.CartItems> cartItemsArray = AppController.SHOPPING_CART.cartItems;

            final Cart.CartItems cartItems = cartItemsArray.get(i);

            final View productView = infalInflater.inflate(R.layout.cart_product_layout_strip, null);

            ImageView productImage = (ImageView) productView.findViewById(R.id.img_product_main);
            TextView productName = (TextView) productView.findViewById(R.id.txt_product_name);
            imageLoader.displayImage(cartItems.item_icon, productImage, options);
            productName.setText(cartItems.item_name);

            TextView productPrice = (TextView) productView.findViewById(R.id.txt_regular_price);
            productPrice.setText(cartItems.item_formated_price);
            productPrice.setTextColor(preferences.getSecondaryColor());

            final EditText etQty = (EditText) productView.findViewById(R.id.edt_qty);
            etQty.setText(cartItems.item_qty);
            etQty.applyTheme(preferences.getPrimaryColor(), preferences.getSecondaryColor());

            LinearLayout updateLay = (LinearLayout) productView.findViewById(R.id.lay_update);
            GradientDrawable updateLayDrawable = (GradientDrawable) updateLay.getBackground();
            updateLayDrawable.setColor(preferences.getSecondaryColor());

            com.rey.material.widget.Button btnUpdate = (com.rey.material.widget.Button) productView.findViewById(R.id.btn_update);
            btnUpdate.setBackgroundDrawable(util.getSecondaryRippleDrawable());
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> cartParams = new HashMap<String, String>();
                    cartParams.put(cartItems.item_code, etQty.getText().toString());
                    updateCartItem(cartParams);
                }
            });

            ImageView deleteItem = (ImageView) productView.findViewById(R.id.img_delete);
            deleteItem.setVisibility(View.VISIBLE);

            deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.removeView(productView);
                    deleteCartItem(cartItems.item_id);
                }
            });


            rootView.addView(productView);
        }
        }else {
            layNonEmptyCart.setVisibility(View.GONE);
            layEmptyCart.setVisibility(View.VISIBLE);
        }

    }

    private void applyCoupon(final String coupon){
        util.showAnimatedLogoProgressBar(layRoot);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(ShoppingCartActivity.this).applyCoupon(coupon);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       util.hideAnimatedLogoProgressBar();
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.get("status").equals(null)){

                                if(!jsonObject.get("status").toString().equals("success")){
                                    etCoupon.setHelper(jsonObject.get("text").toString());


                                }else{
                                    etCoupon.setHelper(jsonObject.get("text").toString());
                                    getCartInfo();
                                    if(isCouponApplied){
                                        etCoupon.setVisibility(View.VISIBLE);
                                        ((RelativeLayout) findViewById(R.id.lay_discount_applied)).setVisibility(View.GONE);
                                        isCouponApplied = false;
                                        btnApplyCoupon.setText("Apply Coupon");
                                    }else{
                                        etCoupon.setVisibility(View.GONE);
                                        etCoupon.setText("");
                                        ((RelativeLayout) findViewById(R.id.lay_discount_applied)).setVisibility(View.VISIBLE);
                                        isCouponApplied = true;
                                        btnApplyCoupon.setText("Remove Coupon");
                                    }
                                }

                            }else {
                                etCoupon.setHelper("Some error has been occurred. Please try again later.");

                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            etCoupon.setHelper("Some error has been occurred. Please try again later.");
                        }
                    }
                });



            }
        });
        if(RestClient.isNetworkAvailable(ShoppingCartActivity.this, util))
        {
            t.start();
        }
    }

    public void deleteCartItem(final String item_id){
        util.showAnimatedLogoProgressBar(layRoot);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(ShoppingCartActivity.this).deleteCartItems(item_id);
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

                    if(jsonObject.has("status")){
                        if(jsonObject.getString("status").equals("success")){
                            getCartInfo();
                        }else{
                            util.showErrorDialog("Items can not be deleted. Please try again.", "Ok", "");
                            util.hideAnimatedLogoProgressBar();
                        }
                    }else{
                        util.showErrorDialog(RestClient.ERROR_MESSAGE, "Ok", "");
                    }



                }catch (JSONException e){
                    e.printStackTrace();
                    util.showErrorDialog(RestClient.ERROR_MESSAGE, "Ok", "");
                }

            }
        });
        if(RestClient.isNetworkAvailable(ShoppingCartActivity.this, util))
        {
            t.start();

        }

    }

    public void updateCartItem(final HashMap<String,String> cart_params){
        util.showAnimatedLogoProgressBar(layRoot);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(ShoppingCartActivity.this).updateCart(cart_params);
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.has("status")){
                                if(jsonObject.getString("status").equals("success")){
                                    util.hideAnimatedLogoProgressBar();
                                    getCartInfo();
                                }else{
                                    util.showErrorDialog("Items can not be deleted. Please try again.", "Ok", "");
                                    util.hideAnimatedLogoProgressBar();
                                }
                            }else{
                                util.showErrorDialog(RestClient.ERROR_MESSAGE, "Ok", "");
                            }



                        }catch (JSONException e){
                            e.printStackTrace();
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "Ok", "");
                        }
                    }
                });


            }
        });
        if(RestClient.isNetworkAvailable(ShoppingCartActivity.this, util))
        {
            t.start();

        }

    }

    public void getCartInfo(){
        util.hideSoftKeyboard();
        util.showAnimatedLogoProgressBar(layRoot);
        AppController.SHOPPING_CART = null;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(ShoppingCartActivity.this).getCartDetails();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.has("summary_qty")){
                                AppController.SHOPPING_CART = util.parseCartDetails(jsonObject);
                                if(AppController.SHOPPING_CART != null)
                                    addCartProducts(productContainer);
                                util.hideAnimatedLogoProgressBar();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "Ok", "");
                        }
                    }
                });


            }
        });
        if(RestClient.isNetworkAvailable(ShoppingCartActivity.this, util))
        {
            t.start();

        }

    }


}

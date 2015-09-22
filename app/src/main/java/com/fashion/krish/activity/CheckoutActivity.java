package com.fashion.krish.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.customview.CircleImageView;
import com.fashion.krish.fragment.CheckoutLoginFragment;
import com.fashion.krish.model.Address;
import com.fashion.krish.model.PaymentMethod;
import com.fashion.krish.model.ShippingMethods;
import com.fashion.krish.model.ShippingMethodsRates;
import com.fashion.krish.options.TypeCheckBoxView;
import com.fashion.krish.options.TypeDateTimeView;
import com.fashion.krish.options.TypeDateView;
import com.fashion.krish.options.TypeLinkView;
import com.fashion.krish.options.TypeRadioView;
import com.fashion.krish.options.TypeSelectionView;
import com.fashion.krish.options.TypeTextView;
import com.fashion.krish.options.TypeTimeView;
import com.fashion.krish.utility.AnimationFactory;
import com.fashion.krish.utility.AnimationsClass;
import com.fashion.krish.utility.AppPreferences;
import com.fashion.krish.utility.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rey.material.widget.Button;
import com.rey.material.widget.CompoundButton;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.fashion.krish.model.PaymentMethod.*;


public class CheckoutActivity extends ActionBarActivity implements View.OnClickListener {

    ImageLoader imageLoader;
    DisplayImageOptions options;

    LinearLayout layBack;

    Utility util;

    private TextView txtTitle;
    private CircleImageView viewStepOne,viewStepTwo,viewStepThree;
    private ImageView firstSeparator,secondSeparator;
    private int color_primary,color_secondary;
    private ArrayList<Address> addressList;
    private ArrayList<ShippingMethods> methodList;
    private Address current_address;
    private TextView txtOne,txtTwo,txtThree,txtDelivery,txtPayment,txtReview;
    private LinearLayout layDelivery,layPayment,layReview, layLogin;
    private RelativeLayout layStepOne,layStepTwo,layStepThree, laySteps;
    private RelativeLayout rootLay;
    private int current_stage = 1;

    //Delivery Layout Declarations
    private TextView txtFullName,txtFullAddress;
    private Button btnContinue;
    private Spinner spinerAddress;
    private LinearLayout layShippingMethods;
    private ArrayList<RadioButton> radioArray;
    private boolean isMethodSelected = false,isAddressLayout = false;
    private String selected_shipping = "";
    private CircleImageView imgAdd;
    private ViewAnimator viewAnimator;

    //Payment Layout Declaration
    private Button btnPaymentContinue;
    private ArrayList<PaymentMethod> paymentMethodsList;
    private LinearLayout layPaymentMethodContainer;
    private ArrayList<RadioButton> paymentRadioArray;
    private PaymentMethod selected_payment = null;
    private boolean isPaymentMethodSelected = false, isPaymentValidated = false;
    private LinearLayout layPaymentForm;

    //Order Review Declaration
    private LinearLayout layRootOrderedItems;
    private TextView txtSubtotalValue,txtShippingTaxValue,txtGrandTotalValue;
    private TextView txtSubtotalLabel,txtShippingTaxLabel,txtGrandTotalLabel;
    private Button btnPlaceOrder;
    private HashMap<String,String> orderParams;

    private AppPreferences preferences;
    private CheckoutLoginFragment checkoutLoginFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_checkout);
        ((RelativeLayout)findViewById(R.id.lay_title)).setBackgroundColor(Color.parseColor(AppController.PRIMARY_COLOR));

        util = new Utility(CheckoutActivity.this);
        Utility.changeStatusBarColor(CheckoutActivity.this);
        rootLay = (RelativeLayout) findViewById(R.id.root_lay);
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.logo)
                .showImageOnFail(R.drawable.logo)
                .showImageOnLoading(R.drawable.logo).build();

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Sail_Regular.otf");
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText("Checkout");
        txtTitle.setTypeface(tf);
        layBack = (LinearLayout) findViewById(R.id.lay_back);
        layBack.setOnClickListener(this);

        color_primary = Color.parseColor(AppController.PRIMARY_COLOR);
        color_secondary = Color.parseColor(AppController.SECONDARY_COLOR);

        addressList = new ArrayList<>();
        methodList = new ArrayList<>();

        checkoutLoginFragment = new CheckoutLoginFragment(CheckoutActivity.this);
        init();

    }


    @Override
    protected void onPause(){
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void init(){


        viewStepOne = (CircleImageView) findViewById(R.id.img_step_one);
        viewStepOne.setColorFilter(color_secondary);
        viewStepOne.setBorderColor(color_primary);
        viewStepTwo = (CircleImageView) findViewById(R.id.img_step_two);
        //viewStepTwo.setColorFilter(color_primary);
        viewStepTwo.setBorderColor(color_primary);
        viewStepThree = (CircleImageView) findViewById(R.id.img_step_three);
        //viewStepThree.setColorFilter(color_primary);
        viewStepThree.setBorderColor(color_primary);

        firstSeparator = (ImageView) findViewById(R.id.img_first_separator);
        firstSeparator.setBackgroundColor(color_primary);

        txtOne = (TextView) findViewById(R.id.txt_one);
        txtOne.setTextColor(Color.WHITE);
        txtTwo = (TextView) findViewById(R.id.txt_two);
        txtThree = (TextView) findViewById(R.id.txt_three);
        txtDelivery = (TextView) findViewById(R.id.txt_delivery);
        txtPayment = (TextView) findViewById(R.id.txt_payment);
        txtReview = (TextView) findViewById(R.id.txt_review);

        laySteps = (RelativeLayout) findViewById(R.id.lay_steps);
        layStepOne = (RelativeLayout) findViewById(R.id.lay_step_one);
        layStepTwo = (RelativeLayout) findViewById(R.id.lay_step_two);
        layStepThree = (RelativeLayout) findViewById(R.id.lay_step_three);

        layDelivery = (LinearLayout) findViewById(R.id.lay_checkout_delivery);
        layPayment = (LinearLayout) findViewById(R.id.lay_checkout_payment);
        layReview = (LinearLayout) findViewById(R.id.lay_checkout_review);
        layLogin = (LinearLayout) findViewById(R.id.lay_checkout_login);


        txtFullName = (TextView) findViewById(R.id.txt_p_name);
        txtFullAddress = (TextView) findViewById(R.id.txt_full_address);
        spinerAddress = (Spinner) findViewById(R.id.spin_address);
        ((LinearLayout) findViewById(R.id.lay_continue_btn)).setBackgroundColor(color_secondary);
        imgAdd = (CircleImageView) findViewById(R.id.img_add);
        imgAdd.setColorFilter(color_secondary);
        imgAdd.setOnClickListener(this);
        btnContinue = (Button) findViewById(R.id.btn_continue);
        btnContinue.setBackground(util.getSecondaryRippleDrawable());
        btnContinue.setOnClickListener(this);
        viewAnimator = (ViewAnimator)this.findViewById(R.id.viewFlipper);

        layShippingMethods = (LinearLayout) findViewById(R.id.lay_shipping_methods);
        radioArray = new ArrayList<>();
        spinerAddress.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                if (position > 0) {
                    Address address = addressList.get(position - 1);
                    address.is_use_for_shipping = "1";
                    methodList.clear();
                    saveShippingAddress(address);
                    layShippingMethods.removeAllViews();
                    txtFullName.setText(address.address_fname + " " + address.address_lname);
                    parseCurrentAddress(address);
                } else if (position == 0) {

                }

            }
        });

        ((LinearLayout) findViewById(R.id.lay_continue_payment_btn)).setBackgroundColor(color_secondary);
        btnPaymentContinue = (Button) findViewById(R.id.btn_continue_payment);
        btnPaymentContinue.setBackground(util.getSecondaryRippleDrawable());
        btnPaymentContinue.setOnClickListener(this);

        layPaymentForm = (LinearLayout) findViewById(R.id.lay_payment_form);
        layPaymentForm.setVisibility(View.GONE);
        paymentMethodsList = new ArrayList<>();
        layPaymentMethodContainer = (LinearLayout) findViewById(R.id.lay_payment_methods);
        paymentRadioArray = new ArrayList<>();

        layRootOrderedItems =(LinearLayout) findViewById(R.id.lay_ordered_products);
        txtSubtotalValue = (TextView) findViewById(R.id.txt_subtotal_value);
        txtShippingTaxValue = (TextView) findViewById(R.id.txt_shipping_tax_value);
        txtGrandTotalValue = (TextView) findViewById(R.id.txt_grandtotal_value);
        txtSubtotalLabel = (TextView) findViewById(R.id.txt_subtotal_label);
        txtShippingTaxLabel = (TextView) findViewById(R.id.txt_shipping_label);
        txtGrandTotalLabel = (TextView) findViewById(R.id.txt_grandtotal_label);
        ((LinearLayout) findViewById(R.id.lay_place_order_btn)).setBackgroundColor(color_secondary);
        btnPlaceOrder = (Button) findViewById(R.id.btn_place_order);
        btnPlaceOrder.setBackground(util.getSecondaryRippleDrawable());
        btnPlaceOrder.setOnClickListener(this);
        orderParams = new HashMap<>();


        preferences = new AppPreferences(CheckoutActivity.this);
        if(preferences.getIsLoggedIn().equals("1")){
            layDelivery.setVisibility(View.VISIBLE);
            getCheckoutData();
        }else{
            layLogin.setVisibility(View.VISIBLE);
            layDelivery.setVisibility(View.GONE);
            laySteps.setVisibility(View.GONE);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_continue:
                    if(!isMethodSelected)
                        util.showErrorDialog("Please choose your shipping method","Ok","");
                    else{
                        saveShippingMethod();
                    }
                break;
            case R.id.lay_back:
                    if(isAddressLayout){
                        AnimationFactory.flipTransition(viewAnimator, AnimationFactory.FlipDirection.LEFT_RIGHT, 50);
                        isAddressLayout = false;

                    }else{
                        if(current_stage == 1){
                            finish();
                        }else if(current_stage == 2)
                            backFromPaymentToDelivery();
                        else  if(current_stage == 3)
                            backFromReviewToPayment();
                    }

                break;
            case R.id.btn_continue_payment:

                isPaymentValidated = false;
                ArrayList<PaymentMethod.FieldSet> fieldSetsList = selected_payment.fieldSetList;
                if(selected_payment!=null){

                    if(selected_payment.payment_form != null){
                        for (int i = 0; i < fieldSetsList.size(); i++) {
                            PaymentMethod.FieldSet fieldSet = fieldSetsList.get(i);

                            if(fieldSet.field_type.equals("text")){
                                isPaymentValidated = TypeTextView.isValidated(fieldSet.field_name);
                            }else if(fieldSet.field_type.equals("select")){
                                isPaymentValidated = TypeSelectionView.isValidated(fieldSet.field_name);
                            }
                        }
                    }else
                        isPaymentValidated = true;

                }


                if(isPaymentValidated){


                    HashMap<String,String> paymentParams = new HashMap<>();
                    paymentParams.put(selected_payment.payment_post_name, selected_payment.payment_code);
                    for (int i = 0; i < fieldSetsList.size(); i++) {
                        PaymentMethod.FieldSet fieldSet = fieldSetsList.get(i);

                        if(fieldSet.field_type.equals("text")){
                            paymentParams.put(fieldSet.field_name,TypeTextView.getValue(fieldSet.field_name));
                        }else if(fieldSet.field_type.equals("select")){
                            paymentParams.put(fieldSet.field_name,TypeSelectionView.getValue(fieldSet.field_name,fieldSet.valuesMap));
                        }
                    }
                    savePaymentMethod(paymentParams);


                }else{
                    Log.d("Payment Validation", "False");
                }


                break;
            case R.id.img_add:
                if(isAddressLayout)
                    isAddressLayout = false;
                else
                    isAddressLayout = true;

                AnimationFactory.flipTransition(viewAnimator, AnimationFactory.FlipDirection.LEFT_RIGHT, 50);
                break;
            case R.id.btn_place_order:
                saveOrders(orderParams);
            break;
        }
    }

    private void getCheckoutData(){
        //util.showLoadingDialog("Please Wait");
        util.showAnimatedLogoProgressBar(rootLay);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(CheckoutActivity.this).getCheckoutDetails();
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
                        try {
                            util.hideAnimatedLogoProgressBar();
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("item")){
                                if (jsonObject.get("item") instanceof JSONObject) {
                                    JSONObject itemObj = jsonObject.getJSONObject("item");
                                    addressList.add(parseAddress(itemObj));

                                } else {

                                    JSONArray itemArray = jsonObject.getJSONArray("item");
                                    for (int i = 0; i < itemArray.length(); i++) {
                                        JSONObject itemObj = itemArray.getJSONObject(i);
                                        addressList.add(parseAddress(itemObj));

                                    }
                                }
                                ArrayList<String> arrayValues = new ArrayList<>();
                                arrayValues.add("Saved Addresses");

                                for(Address address : addressList){
                                    arrayValues.add(address.address_label);
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(CheckoutActivity.this, R.layout.row_spn, arrayValues);
                                spinerAddress.setAdapter(adapter);
                            }

                            if(jsonObject.has("changed_shipping_address")){
                                JSONObject current_address_obj = jsonObject.getJSONObject("changed_shipping_address");
                                current_address = parseCurrentAddress(current_address_obj,txtFullAddress);
                                txtFullName.setText(current_address.address_fname+" "+current_address.address_lname);


                            }


                            getShippingList();



                        } catch (JSONException e) {
                            e.printStackTrace();
                            util.hideAnimatedLogoProgressBar();
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                        }

                    }
                });


            }
        });
        if(RestClient.isNetworkAvailable(CheckoutActivity.this, util))
        {
            t.start();

        }

    }

    private void getShippingList(){
        //util.showLoadingDialog("Please Wait");
        util.showAnimatedLogoProgressBar(rootLay);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(CheckoutActivity.this).getShippingMethodList();
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
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("method")){
                                if (jsonObject.get("method") instanceof JSONObject) {
                                    JSONObject methodObj = jsonObject.getJSONObject("method");
                                    methodList.add(parseShippingMethods(methodObj));

                                } else {

                                    JSONArray methodArray = jsonObject.getJSONArray("method");
                                    for (int i = 0; i < methodArray.length(); i++) {
                                        JSONObject methodObj = methodArray.getJSONObject(i);
                                        methodList.add(parseShippingMethods(methodObj));

                                    }
                                }
                            }
                            addShippingLayout();
                            util.hideAnimatedLogoProgressBar();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            util.hideAnimatedLogoProgressBar();
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                        }

                    }
                });


            }
        });
        if(RestClient.isNetworkAvailable(CheckoutActivity.this, util))
        {
            t.start();

        }

    }

    private void getPaymentMethods(){
        //util.showLoadingDialog("Please Wait");
        paymentMethodsList.clear();
        util.showAnimatedLogoProgressBar(rootLay);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(CheckoutActivity.this).getPaymentMethodList();
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
                        try {
                            util.hideAnimatedLogoProgressBar();
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("method")){
                                DeliveryToPaymentNavigation();
                                if(jsonObject.get("method") instanceof JSONArray){
                                    JSONArray methodArray = jsonObject.getJSONArray("method");
                                    for (int i = 0; i < methodArray.length(); i++) {
                                        paymentMethodsList.add(parsePaymentMethods(methodArray.getJSONObject(i)));
                                    }
                                }else {
                                    paymentMethodsList.add(parsePaymentMethods(jsonObject.getJSONObject("method")));
                                }
                                addPaymentMethodLayout();
                            }else{
                                util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                            }




                        } catch (JSONException e) {
                            e.printStackTrace();
                            util.hideAnimatedLogoProgressBar();
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                        }

                    }
                });


            }
        });
        if(RestClient.isNetworkAvailable(CheckoutActivity.this, util))
        {
            t.start();

        }

    }

    private void saveShippingMethod(){
        //util.showLoadingDialog("Please Wait");
        util.showAnimatedLogoProgressBar(rootLay);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(CheckoutActivity.this).saveShippingMethod(selected_shipping);
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
                        try {
                            util.hideAnimatedLogoProgressBar();
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("status")){
                                if (jsonObject.getString("status").equals("success")){
                                    getPaymentMethods();
                                } else {
                                    util.showErrorDialog(jsonObject.getString("text"),"OK","");
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            util.hideAnimatedLogoProgressBar();
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                        }

                    }
                });


            }
        });
        if(RestClient.isNetworkAvailable(CheckoutActivity.this, util))
        {
            t.start();

        }

    }

    private void saveShippingAddress(final Address address){
        //util.showLoadingDialog("Please Wait");
        util.showAnimatedLogoProgressBar(rootLay);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(CheckoutActivity.this).saveShippingAddress(address);
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
                        try {
                            util.hideAnimatedLogoProgressBar();
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("status")){
                                if (jsonObject.getString("status").equals("success")){
                                   getShippingList();
                                } else {
                                    util.showErrorDialog(jsonObject.getString("text"),"OK","");
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            util.hideAnimatedLogoProgressBar();
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                        }

                    }
                });


            }
        });
        if(RestClient.isNetworkAvailable(CheckoutActivity.this, util))
        {
            t.start();

        }

    }

    private void savePaymentMethod(final HashMap<String,String> paymentParams){
        //util.showLoadingDialog("Please Wait");
        util.showAnimatedLogoProgressBar(rootLay);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(CheckoutActivity.this).savePayment(paymentParams);
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
                        try {
                            util.hideAnimatedLogoProgressBar();
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("status")){
                                if (jsonObject.getString("status").equals("success")){
                                    //getShippingList();
                                    PaymentToReviewNavigation();
                                    getOrderDetails();
                                    orderParams = paymentParams;
                                } else {
                                    util.showErrorDialog(jsonObject.getString("text"),"OK","");
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            util.hideAnimatedLogoProgressBar();
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                        }

                    }
                });


            }
        });
        if(RestClient.isNetworkAvailable(CheckoutActivity.this, util))
        {
            t.start();

        }

    }

    private void saveOrders(final HashMap<String,String> paymentParams){
        util.showAnimatedLogoProgressBar(rootLay);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(CheckoutActivity.this).saveOrder(paymentParams);
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
                        try {
                            util.hideAnimatedLogoProgressBar();
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("status")){
                                if (jsonObject.getString("status").equals("success")){
                                    //getShippingList();
                                    Intent intent = new Intent(CheckoutActivity.this,OrderFulfillmentActivity.class);
                                    intent.putExtra("order_id", jsonObject.getString("order_id"));
                                    startActivity(intent);
                                    if(ShoppingCartActivity.SHOPPING_CART_ACTIVITY != null){
                                        ShoppingCartActivity.SHOPPING_CART_ACTIVITY.finish();
                                        ShoppingCartActivity.SHOPPING_CART_ACTIVITY = null;
                                    }
                                    finish();
                                } else {
                                    util.showErrorDialog(jsonObject.getString("text"), "OK", "");
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            util.hideAnimatedLogoProgressBar();
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                        }

                    }
                });


            }
        });
        if(RestClient.isNetworkAvailable(CheckoutActivity.this, util))
        {
            t.start();

        }

    }

    public Address parseAddress(JSONObject addressObject){
        Address address = new Address();
        try {
            address.address_fname = addressObject.getString("firstname");
            address.address_lname = addressObject.getString("lastname");
            address.address_street = addressObject.getString("street");
            if(addressObject.has("street1"))
                address.address_street1 = addressObject.getString("street1");
            address.address_city = addressObject.getString("city");
            if(addressObject.has("region"))
                address.address_region = addressObject.getString("region");
            address.address_country = addressObject.getString("country");
            address.address_country_id = addressObject.getString("country_id");
            address.address_zip = addressObject.getString("postcode");
            address.address_entity_id = addressObject.getString("entity_id");
            address.address_label = addressObject.getString("address_line");
            address.address_phone = addressObject.getString("telephone");
            if(addressObject.has("company"))
                address.address_company = addressObject.getString("company");
            if(addressObject.has("street2"))
                address.address_street2 = addressObject.getString("street2");
            if(addressObject.has("selected"))
                address.is_selected = addressObject.getInt("selected");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return address;
    }

    public Address parseCurrentAddress(JSONObject addressObject,TextView txtAddress){
        Address address = new Address();
        try {
            address.address_fname = addressObject.getString("firstname");
            address.address_lname = addressObject.getString("lastname");
            if(addressObject.has("street1"))
                address.address_street1 = addressObject.getString("street1")+"\n";
            address.address_city = addressObject.getString("city")+"\n";
            if(addressObject.has("region"))
                address.address_region = addressObject.getString("region")+"- ";
            address.address_country = addressObject.getString("country")+"\n";
            address.address_country_id = addressObject.getString("country_id");
            address.address_zip = addressObject.getString("postcode")+"\n";
            address.address_entity_id = addressObject.getString("entity_id");
            //address.address_label = addressObject.getString("address_line");
            address.address_phone = addressObject.getString("telephone");
            if(addressObject.has("company"))
                address.address_company = addressObject.getString("company");
            if(addressObject.has("street2"))
                address.address_street2 = addressObject.getString("street2")+"\n";
            if(addressObject.has("selected"))
                address.is_selected = addressObject.getInt("selected");

            txtAddress.setText(address.address_street1+address.address_street2+address.address_city+address.address_region
                                +address.address_zip+address.address_country+address.address_phone);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return address;
    }

    public void parseCurrentAddress(Address current_address){
        Address address = new Address();

        address.address_fname = current_address.address_fname;
        address.address_lname = current_address.address_lname;
        if(current_address.address_street.length() > 0)
            address.address_street1 = current_address.address_street1+"\n";
        address.address_city = current_address.address_city+"\n";
        if(current_address.address_region.length() > 0)
            address.address_region = current_address.address_region+"- ";
        address.address_country = current_address.address_country+"\n";
        address.address_country_id = current_address.address_country_id;
        address.address_zip = current_address.address_zip+"\n";
        address.address_entity_id = current_address.address_entity_id;
        //address.address_label = addressObject.getString("address_line");
        address.address_phone = current_address.address_phone;
        if(current_address.address_company.length() > 0)
            address.address_company = current_address.address_company;
        if(current_address.address_street2.length() > 0)
            address.address_street2 = current_address.address_street2+"\n";
        address.is_selected = 1;

        txtFullAddress.setText(address.address_street1+address.address_street2+address.address_city+address.address_region
                +address.address_zip+address.address_country+address.address_phone);


        return ;
    }

    public PaymentMethod parsePaymentMethods(JSONObject paymentObject){
        PaymentMethod methods = new PaymentMethod();
        try {
            methods.payment_id = paymentObject.getString("id");
            methods.payment_code = paymentObject.getString("code");
            methods.payment_post_name = paymentObject.getString("post_name");
            methods.payment_name = paymentObject.getString("label");
            if(paymentObject.has("form")){
                methods.payment_form = paymentObject.getJSONObject("form");
                if(methods.payment_form.has("fieldset")){
                    JSONObject fieldSetObj = methods.payment_form.getJSONObject("fieldset");
                    if(fieldSetObj.has("field")){
                        if(fieldSetObj.get("field") instanceof JSONArray){
                            JSONArray fieldArray = fieldSetObj.getJSONArray("field");
                            for (int i = 0; i < fieldArray.length(); i++) {
                                JSONObject fieldObj = fieldArray.getJSONObject(i);
                                methods.fieldSetList.add(getFiledSet(fieldObj,methods));

                            }
                        }else{
                            JSONObject fieldObj = fieldSetObj.getJSONObject("field");
                            methods.fieldSetList.add(getFiledSet(fieldObj,methods));
                        }
                    }


                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return methods;
    }

    public PaymentMethod.FieldSet getFiledSet(JSONObject fieldObj,PaymentMethod methods){
        PaymentMethod.FieldSet fieldSet = methods.new FieldSet();
        try{

            fieldSet.field_label = fieldObj.getString("label");
            fieldSet.field_name = fieldObj.getString("name");
            fieldSet.field_type = fieldObj.getString("type");

            if(fieldObj.has("required"))
                fieldSet.is_required = fieldObj.getBoolean("required");

            if(fieldObj.has("values")){
                JSONObject fieldValueObj = fieldObj.getJSONObject("values");
                if(fieldValueObj.get("item") instanceof JSONArray){
                    JSONArray itemArray = fieldValueObj.getJSONArray("item");
                    for (int j = 0; j < itemArray.length(); j++) {
                        fieldSet.valuesMap.put(itemArray.getJSONObject(j).getString("value"), itemArray.getJSONObject(j).getString("label"));

                    }
                }else {
                    fieldSet.valuesMap.put(fieldValueObj.getJSONObject("item").getString("label"),
                            fieldValueObj.getJSONObject("item").getString("value"));

                }
            }

            if(fieldObj.has("validators")){
                fieldSet.validatorObj = fieldObj.getJSONObject("validators").getJSONObject("validator");

            }

        }catch (JSONException e){
            e.printStackTrace();
        }


        return fieldSet;
    }

    public ShippingMethods parseShippingMethods(JSONObject methodObj){
        ShippingMethods shippingMethods = new ShippingMethods();

        try {
            if(methodObj.has("label"))
                shippingMethods.shipping_method_label = methodObj.getString("label");
            if(methodObj.has("rates")){
                if(methodObj.get("rates") instanceof JSONArray){
                    JSONArray rateArray = methodObj.getJSONArray("rates");
                    for (int i = 0; i < rateArray.length(); i++) {
                        JSONObject rateObj = rateArray.getJSONObject(i);
                        shippingMethods.rateArray.add(parseShippingRates(rateObj));
                    }
                }else{
                    JSONObject rateObj = methodObj.getJSONObject("rates").getJSONObject("rate");
                    shippingMethods.rateArray.add(parseShippingRates(rateObj));
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return shippingMethods;
    }

    public ShippingMethodsRates parseShippingRates(JSONObject rateObj){
        ShippingMethodsRates shippingMethodsRates = new ShippingMethodsRates();

        try{
            if(rateObj.has("label"))
                shippingMethodsRates.shipping_rate_label = rateObj.getString("label");
            if(rateObj.has("code"))
                shippingMethodsRates.shipping_rate_code = rateObj.getString("code");
            if(rateObj.has("price"))
                shippingMethodsRates.shipping_rate_price = rateObj.getString("price");
            if(rateObj.has("formated_price"))
                shippingMethodsRates.shipping_rate_f_price = rateObj.getString("formated_price");

        }catch (JSONException e){
            e.printStackTrace();
        }

        return shippingMethodsRates;
    }

    public void addShippingLayout(){

        layShippingMethods.removeAllViews();
        radioArray.clear();
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                isMethodSelected = true;
                int selected_position=0;
                if(isChecked){
                    int i = 0;
                    for(RadioButton radioButton : radioArray){
                        radioButton.setChecked(radioButton == buttonView);
                        if(radioButton == buttonView){
                            selected_position = i;
                            selected_shipping = radioButton.getTag().toString();
                            Log.d("Selected Shipping",radioButton.getTag().toString());
                        }
                        i++;

                    }

                }

            }
        };

        for (int i = 0; i < methodList.size(); i++) {
            TextView methodTitle = new TextView(CheckoutActivity.this);
            methodTitle.setText(methodList.get(i).shipping_method_label);
            layShippingMethods.addView(methodTitle);

            for (int j = 0; j < methodList.get(i).rateArray.size(); j++) {
                final RadioButton radio = new RadioButton(CheckoutActivity.this, color_secondary);
                radio.setText(methodList.get(i).rateArray.get(j).shipping_rate_label);
                radio.setTag(methodList.get(i).rateArray.get(j).shipping_rate_code);
                radio.setGravity(Gravity.CENTER_VERTICAL);
                radio.setOnCheckedChangeListener(listener);
                radioArray.add(radio);
                layShippingMethods.addView(radio);

            }
        }



    }

    public void addPaymentMethodLayout(){


        layPaymentMethodContainer.removeAllViews();
        paymentRadioArray.clear();
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                isPaymentMethodSelected = true;

                if(isChecked){
                    int i = 0;
                    for(RadioButton radioButton : paymentRadioArray){
                        radioButton.setChecked(radioButton == buttonView);
                        if(radioButton == buttonView){
                            //selected_payment = radioButton.getTag().toString();
                            Log.d("Selected Payment",radioButton.getTag().toString());
                            for (int j = 0; j < paymentMethodsList.size(); j++) {
                                if(paymentMethodsList.get(j).payment_code.equals(radioButton.getTag().toString())) {
                                    showPaymentForm(paymentMethodsList.get(j));
                                    selected_payment = paymentMethodsList.get(j);
                                }

                            }
                        }
                        i++;

                    }

                }

            }
        };

        for (int i = 0; i < paymentMethodsList.size(); i++) {

            int padding_value = (int) Utility.convertDpToPixel(2,CheckoutActivity.this);
            final RadioButton radio = new RadioButton(CheckoutActivity.this, color_secondary);
            radio.setPadding(0,padding_value,0,padding_value);
            radio.setText(paymentMethodsList.get(i).payment_name);
            radio.setTag(paymentMethodsList.get(i).payment_code);
            radio.setGravity(Gravity.CENTER_VERTICAL);
            radio.setOnCheckedChangeListener(listener);
            paymentRadioArray.add(radio);
            layPaymentMethodContainer.addView(radio);
        }



    }

    private void getOrderDetails(){
        util.showAnimatedLogoProgressBar(rootLay);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                final String response = new RestClient(CheckoutActivity.this).getOrderReviews();
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

                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("products")) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                util.hideAnimatedLogoProgressBar();
                                try {

                                    txtGrandTotalLabel.setText(jsonObject.getJSONObject("totals").getJSONObject("grand_total").getString("title"));
                                    txtSubtotalLabel.setText(jsonObject.getJSONObject("totals").getJSONObject("subtotal").getString("title"));
                                    txtShippingTaxLabel.setText(jsonObject.getJSONObject("totals").getJSONObject("shipping").getString("title"));

                                    txtGrandTotalValue.setText(jsonObject.getJSONObject("totals").getJSONObject("grand_total").getString("formated_value"));
                                    txtSubtotalValue.setText(jsonObject.getJSONObject("totals").getJSONObject("subtotal").getString("formated_value"));
                                    txtShippingTaxValue.setText(jsonObject.getJSONObject("totals").getJSONObject("shipping").getString("formated_value"));


                                    if(jsonObject.has("products")){
                                        if(jsonObject.getJSONObject("products").get("item") instanceof JSONArray){
                                            JSONArray itemArray = jsonObject.getJSONObject("products").getJSONArray("item");
                                            for (int i = 0; i < itemArray.length(); i++) {

                                                JSONObject itemObj = itemArray.getJSONObject(i);
                                                addOrderedItems(itemObj,i);
                                            }
                                        }else{
                                            JSONObject itemObj = jsonObject.getJSONObject("products").getJSONObject("item");
                                            addOrderedItems(itemObj,0);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });


                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                util.hideAnimatedLogoProgressBar();
                                util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                            }
                        });


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.hideAnimatedLogoProgressBar();
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                        }
                    });
                }

            }
        });
        if(RestClient.isNetworkAvailable(CheckoutActivity.this, util))
        {
            t.start();
        }
    }

    @Override
    public void onBackPressed() {

        if(isAddressLayout){
            AnimationFactory.flipTransition(viewAnimator, AnimationFactory.FlipDirection.LEFT_RIGHT, 50);
            isAddressLayout = false;
        }else{
            if(current_stage == 1){
               super.onBackPressed();
            }else if(current_stage == 2)
                backFromPaymentToDelivery();
            else  if(current_stage == 3)
                backFromReviewToPayment();
        }

    }

    public void backFromPaymentToDelivery(){
        AnimationsClass anim = new AnimationsClass(CheckoutActivity.this,layStepOne);
        anim.zoomInView();
        AnimationsClass deliveryAnim = new AnimationsClass(CheckoutActivity.this,layDelivery);
        deliveryAnim.slideInFromLeft(View.VISIBLE);
        AnimationsClass paymentAnim = new AnimationsClass(CheckoutActivity.this,layPayment);
        paymentAnim.slideOutToRight(View.GONE);
        current_stage = 1;

        viewStepOne.setColorFilter(color_secondary);
        txtDelivery.setTextColor(Color.parseColor("#333333"));
        txtDelivery.setTypeface(null, Typeface.BOLD);
        txtOne.setTextColor(Color.WHITE);
        txtOne.setVisibility(View.VISIBLE);

        viewStepTwo.setColorFilter(Color.WHITE);
        txtPayment.setTextColor(Color.parseColor("#999999"));
        txtPayment.setTypeface(null, Typeface.NORMAL);
        txtTwo.setTextColor(Color.parseColor("#999999"));
        txtTwo.setVisibility(View.VISIBLE);

        viewStepThree.setColorFilter(Color.WHITE);
        txtReview.setTextColor(Color.parseColor("#999999"));
        txtReview.setTypeface(null, Typeface.NORMAL);
        txtThree.setTextColor(Color.parseColor("#999999"));
        txtThree.setVisibility(View.VISIBLE);
    }

    public void backFromReviewToPayment(){
        AnimationsClass anim = new AnimationsClass(CheckoutActivity.this,layStepTwo);
        anim.zoomInView();
        AnimationsClass paymentAnim = new AnimationsClass(CheckoutActivity.this,layPayment);
        paymentAnim.slideInFromLeft(View.VISIBLE);
        AnimationsClass reviewAnim = new AnimationsClass(CheckoutActivity.this,layReview);
        reviewAnim.slideOutToRight(View.GONE);
        current_stage = 2;

        viewStepOne.setImageResource(R.drawable.checkout_step_done_img);
        viewStepOne.setColorFilter(Color.TRANSPARENT);
        txtDelivery.setTextColor(Color.parseColor("#999999"));
        txtDelivery.setTypeface(null, Typeface.NORMAL);
        txtOne.setTextColor(Color.WHITE);
        txtOne.setVisibility(View.GONE);

        viewStepTwo.setColorFilter(color_secondary);
        txtPayment.setTextColor(Color.parseColor("#333333"));
        txtPayment.setTypeface(null, Typeface.BOLD);
        txtTwo.setTextColor(Color.parseColor("#ffffff"));
        txtTwo.setVisibility(View.VISIBLE);

        viewStepThree.setColorFilter(Color.WHITE);
        txtReview.setTextColor(Color.parseColor("#999999"));
        txtReview.setTypeface(null, Typeface.NORMAL);
        txtThree.setTextColor(Color.parseColor("#999999"));
        txtThree.setVisibility(View.VISIBLE);
    }

    public void DeliveryToPaymentNavigation(){
        AnimationsClass anim = new AnimationsClass(CheckoutActivity.this,layStepTwo);
        anim.zoomInView();
        AnimationsClass deliveryAnim = new AnimationsClass(CheckoutActivity.this,layDelivery);
        deliveryAnim.slideOutToLeft(View.GONE);
        AnimationsClass paymentAnim = new AnimationsClass(CheckoutActivity.this,layPayment);
        paymentAnim.slideInFromRight(View.VISIBLE);
        current_stage = 2;


        viewStepOne.setImageResource(R.drawable.checkout_step_done_img);
        viewStepOne.setColorFilter(Color.TRANSPARENT);
        txtDelivery.setTextColor(Color.parseColor("#999999"));
        txtDelivery.setTypeface(null, Typeface.NORMAL);
        txtOne.setTextColor(Color.WHITE);
        txtOne.setVisibility(View.GONE);

        viewStepTwo.setColorFilter(color_secondary);
        txtPayment.setTextColor(Color.parseColor("#333333"));
        txtPayment.setTypeface(null, Typeface.BOLD);
        txtTwo.setTextColor(Color.WHITE);
        txtTwo.setVisibility(View.VISIBLE);

        viewStepThree.setColorFilter(Color.WHITE);
        txtReview.setTextColor(Color.parseColor("#999999"));
        txtReview.setTypeface(null, Typeface.NORMAL);
        txtThree.setTextColor(Color.parseColor("#999999"));
        txtThree.setVisibility(View.VISIBLE);
    }

    public void PaymentToReviewNavigation(){
        AnimationsClass anim = new AnimationsClass(CheckoutActivity.this,layStepThree);
        anim.zoomInView();
        AnimationsClass paymentAnim = new AnimationsClass(CheckoutActivity.this,layPayment);
        paymentAnim.slideOutToLeft(View.GONE);
        AnimationsClass reviewAnim = new AnimationsClass(CheckoutActivity.this,layReview);
        reviewAnim .slideInFromRight(View.VISIBLE);
        current_stage = 3;


        viewStepOne.setImageResource(R.drawable.checkout_step_done_img);
        viewStepOne.setColorFilter(Color.TRANSPARENT);
        txtDelivery.setTextColor(Color.parseColor("#999999"));
        txtDelivery.setTypeface(null, Typeface.NORMAL);
        txtOne.setTextColor(Color.WHITE);
        txtOne.setVisibility(View.GONE);

        viewStepTwo.setImageResource(R.drawable.checkout_step_done_img);
        viewStepTwo.setColorFilter(Color.TRANSPARENT);
        txtPayment.setTextColor(Color.parseColor("#999999"));
        txtPayment.setTypeface(null, Typeface.NORMAL);
        txtTwo.setTextColor(Color.WHITE);
        txtTwo.setVisibility(View.GONE);

        viewStepThree.setColorFilter(color_secondary);
        txtReview.setTextColor(Color.parseColor("#333333"));
        txtReview.setTypeface(null, Typeface.BOLD);
        txtThree.setTextColor(Color.WHITE);
        txtThree.setVisibility(View.VISIBLE);

    }

    public void showPaymentForm(PaymentMethod paymentMethod){

        isPaymentValidated = false ;
        layPaymentForm.setVisibility(View.VISIBLE);
        layPaymentForm.removeAllViews();
        ArrayList<PaymentMethod.FieldSet> fieldSetsList = paymentMethod.fieldSetList;

        if(fieldSetsList.size() == 0){
            layPaymentForm.setVisibility(View.GONE);
        }

        for (int i = 0; i < fieldSetsList.size(); i++) {
            PaymentMethod.FieldSet fieldSet = fieldSetsList.get(i);

            if(fieldSet.field_type.equals("text")){
                new TypeTextView(CheckoutActivity.this,fieldSet,layPaymentForm);
            }else if(fieldSet.field_type.equals("select")){
                new TypeSelectionView(CheckoutActivity.this,fieldSet,layPaymentForm);
            }
        }

    }

    public void addOrderedItems(JSONObject itemObj,int index) {
        LayoutInflater infalInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View productView = infalInflater.inflate(R.layout.review_order_product_layout_strip, null);

        if(index == 0){
            ((View) productView.findViewById(R.id.view_sep)).setVisibility(View.GONE);
        }
        ImageView productImage = (ImageView) productView.findViewById(R.id.img_product_main);

        TextView productName = (TextView) productView.findViewById(R.id.txt_product_name);
        TextView productPrice = (TextView) productView.findViewById(R.id.txt_product_subtotal);
        productPrice.setTypeface(null,Typeface.BOLD);
        productPrice.setTextColor(color_secondary);
        TextView productQty = (TextView) productView.findViewById(R.id.txt_product_qty);
        LinearLayout itemOptions = (LinearLayout) productView.findViewById(R.id.lay_item_options);

        try {

            productName.setText(itemObj.getString("name"));
            productQty.append(itemObj.getString("qty"));
            productPrice.append(itemObj.getJSONObject("formated_subtotal").getString("regular"));
            imageLoader.displayImage(itemObj.getString("icon"), productImage, options);
            if(itemObj.has("options")){
                if(itemObj.getJSONObject("options").get("option") instanceof JSONArray){
                    JSONArray optionArray = itemObj.getJSONObject("options").getJSONArray("option");
                    for (int i = 0; i < optionArray.length(); i++) {
                        itemOptions.addView(getOptionView(optionArray.getJSONObject(i).getString("label"),
                                optionArray.getJSONObject(i).getString("text")));
                    }

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        layRootOrderedItems.addView(productView);

    }

    private LinearLayout getOptionView(String label,String text){

        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams txtParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT );

        LinearLayout lay = new LinearLayout(CheckoutActivity.this);
        lay.setOrientation(LinearLayout.HORIZONTAL);
        lay.setLayoutParams(layParams);

        txtParams.setMargins(0, 0, (int) Utility.convertDpToPixel(10, CheckoutActivity.this), 0);
        TextView txt_option_label = new TextView(CheckoutActivity.this);
        txt_option_label.setText(label+ ": ");
        txt_option_label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        txt_option_label.setLayoutParams(txtParams);
        txt_option_label.setTextColor(Color.parseColor("#212121"));
        lay.addView(txt_option_label);


        TextView txt_option_text = new TextView(CheckoutActivity.this);
        txt_option_text.setText(text);
        txt_option_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        txt_option_text.setLayoutParams(txtParams);
        lay.addView(txt_option_text);

        return lay;
    }


}

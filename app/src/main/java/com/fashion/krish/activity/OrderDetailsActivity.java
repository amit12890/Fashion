package com.fashion.krish.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.utility.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class OrderDetailsActivity extends ActionBarActivity {

    ImageLoader imageLoader;
    DisplayImageOptions options;

    Utility util;
    private LinearLayout lay_back;
    private RelativeLayout rootLay;
    private String order_id;
    private int index;
    private TextView txtDeliveryAddressValue,txtShippingMethodValue,txtGrandTotalValue,
            txtStatusValue,txtOrderIdValue,txtOrderDateValue,txtPaymentMethodValue,txtSubtotalValue,txtShippingTaxValue,
            txtGrandTotalValue2;
    private LinearLayout layRootOrderedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_order_details);
        ((RelativeLayout)findViewById(R.id.lay_title)).setBackgroundColor(Color.parseColor(AppController.PRIMARY_COLOR));

        util = new Utility(OrderDetailsActivity.this);
        Utility.changeStatusBarColor(OrderDetailsActivity.this);
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.logo)
                .showImageOnFail(R.drawable.logo)
                .showImageOnLoading(R.drawable.logo).build();

        order_id = getIntent().getStringExtra("order_id");
        init();
    }

    @Override
    protected void onPause(){
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }


    public void init(){

        rootLay= (RelativeLayout) findViewById(R.id.root_layout);
        lay_back = (LinearLayout) findViewById(R.id.lay_back);
        lay_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        txtDeliveryAddressValue = (TextView) findViewById(R.id.txt_delivery_add_value);
        txtShippingMethodValue = (TextView) findViewById(R.id.txt_shipping_value);
        txtGrandTotalValue = (TextView) findViewById(R.id.txt_grand_total_value);
        txtStatusValue = (TextView) findViewById(R.id.txt_status_value);
        txtOrderIdValue = (TextView) findViewById(R.id.txt_order_id_value);
        txtOrderDateValue = (TextView) findViewById(R.id.txt_order_date_value);
        txtPaymentMethodValue = (TextView) findViewById(R.id.txt_payment_method_value);
        txtSubtotalValue = (TextView) findViewById(R.id.txt_subtotal_value);
        txtShippingTaxValue = (TextView) findViewById(R.id.txt_shipping_tax_value);
        txtGrandTotalValue2 = (TextView) findViewById(R.id.txt_grandtotal_value);

        layRootOrderedItems = (LinearLayout) findViewById(R.id.lay_ordered_item);

        getOrderDetails();

    }

    private void getOrderDetails(){
        util.showAnimatedLogoProgressBar(rootLay);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                final String response = new RestClient(OrderDetailsActivity.this).getOrderDetails(order_id);
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
                    if (jsonObject.has("order")) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                util.hideAnimatedLogoProgressBar();
                                try {
                                    txtDeliveryAddressValue.setText(jsonObject.getString("shipping_address"));
                                    txtShippingMethodValue.setText(jsonObject.getString("shipping_method"));
                                    txtOrderIdValue.setText(jsonObject.getJSONObject("order").getString("order_id"));
                                    txtOrderDateValue.setText(jsonObject.getJSONObject("order").getString("order_date"));
                                    txtStatusValue.setText(jsonObject.getJSONObject("order").getString("status"));
                                    txtGrandTotalValue.setText(jsonObject.getJSONObject("totals").getString("grand_total"));
                                    txtGrandTotalValue2.setText(jsonObject.getJSONObject("totals").getString("grand_total"));
                                    txtSubtotalValue.setText(jsonObject.getJSONObject("totals").getString("subtotal"));
                                    txtShippingTaxValue.setText(jsonObject.getJSONObject("totals").getString("shipping"));
                                    txtPaymentMethodValue.setText(jsonObject.getJSONObject("payment_method").getString("title"));

                                    if(jsonObject.has("ordered_items")){
                                        if(jsonObject.getJSONObject("ordered_items").get("item") instanceof JSONArray){
                                            JSONArray itemArray = jsonObject.getJSONObject("ordered_items").getJSONArray("item");
                                            for (int i = 0; i < itemArray.length(); i++) {

                                                JSONObject itemObj = itemArray.getJSONObject(i);
                                                addOrderedItems(itemObj);
                                            }
                                        }else{
                                            JSONObject itemObj = jsonObject.getJSONObject("ordered_items").getJSONObject("item");
                                            addOrderedItems(itemObj);
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
        if(RestClient.isNetworkAvailable(OrderDetailsActivity.this, util))
        {
            t.start();
        }
    }

    public void addOrderedItems(JSONObject itemObj) {
        LayoutInflater infalInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        final View productView = infalInflater.inflate(R.layout.ordered_product_layout_strip, null);

        ImageView productImage = (ImageView) productView.findViewById(R.id.img_product_main);
        TextView productName = (TextView) productView.findViewById(R.id.txt_product_name);
        TextView productPrice = (TextView) productView.findViewById(R.id.txt_product_subtotal);
        TextView productQty = (TextView) productView.findViewById(R.id.txt_product_qty);
        LinearLayout itemOptions = (LinearLayout) productView.findViewById(R.id.lay_item_options);

        try {

            productName.setText(itemObj.getString("name"));
            productQty.append(itemObj.getJSONObject("qty").getString("value"));
            productPrice.append(itemObj.getJSONObject("subtotal").
                    getJSONObject("excluding_tax").getJSONObject("@attributes").getString("value"));

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

        LinearLayout lay = new LinearLayout(OrderDetailsActivity.this);
        lay.setOrientation(LinearLayout.HORIZONTAL);
        lay.setLayoutParams(layParams);

        txtParams.setMargins(0, 0, (int) Utility.convertDpToPixel(10, OrderDetailsActivity.this), 0);
        TextView txt_option_label = new TextView(OrderDetailsActivity.this);
        txt_option_label.setText(label+ ": ");
        txt_option_label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        txt_option_label.setLayoutParams(txtParams);
        txt_option_label.setTextColor(Color.parseColor("#212121"));
        lay.addView(txt_option_label);


        TextView txt_option_text = new TextView(OrderDetailsActivity.this);
        txt_option_text.setText(text);
        txt_option_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        txt_option_text.setLayoutParams(txtParams);
        lay.addView(txt_option_text);

        return lay;
    }


}

package com.fashion.krish.options;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.model.ProductDetails;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.CompoundButton;
import com.rey.material.widget.EditText;
import com.rey.material.widget.RadioButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by amit.thakkar on 8/10/2015.
 */
public class TypeRadioView {

    ProductDetails.ProductOptions productOptions;
    Activity activity;
    private LayoutInflater inflate;
    private View radioRootview;
    private TextView txtHeader,txtError;
    private EditText editText,etQty;
    public static LinearLayout container,qtyLay;
    private String product_entity_type;
    private ArrayList<RadioButton> radioArray;

    public TypeRadioView(Activity activity, ProductDetails.ProductOptions productOptions, LinearLayout container, String product_type){
        this.activity = activity;
        this.productOptions = productOptions;
        this.container = container;
        this.product_entity_type = product_type;
        radioArray = new ArrayList<>();

        initLayout();
    }

    public void initLayout(){
        inflate = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        radioRootview = inflate.inflate(R.layout.layout_radio, null);

        qtyLay= (LinearLayout) radioRootview.findViewById(R.id.lay_qty);


        txtHeader = (TextView) radioRootview.findViewById(R.id.txt_radio_title);
        txtHeader.setText(productOptions.option_label);
        if (productOptions.option_is_required.equals("1"))
            txtHeader.append(" *");

        txtError = (TextView) radioRootview.findViewById(R.id.txt_radio_error);
        txtError.setVisibility(View.GONE);
        txtError.setTag("error" + productOptions.option_code);
        txtError.setVisibility(View.GONE);

        LinearLayout lay_= (LinearLayout) radioRootview.findViewById(R.id.lay_radio_container);
        lay_.setTag("radio"+productOptions.option_code);

        etQty = (EditText) radioRootview.findViewById(R.id.et_qty);
        etQty.setText("1");
        etQty.setEnabled(false);

        final TextView txtPriceTier = (TextView) radioRootview.findViewById(R.id.txt_price_tier);
        txtPriceTier.setVisibility(View.GONE);

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                int selected_position=0;
                if(isChecked){
                    int i = 0;
                    for(RadioButton radioButton : radioArray){
                        radioButton.setChecked(radioButton == buttonView);
                        if(radioButton == buttonView){
                            selected_position = i;
                        }
                        i++;

                    }

                }
                if(productOptions.productOptionsValues.get(selected_position).custom_qty == 0)
                    etQty.setEnabled(false);
                else
                    etQty.setEnabled(true);

                if(productOptions.options_isMulti == 0){

                    try {
                        JSONObject jObj = new JSONObject(productOptions.productOptionsValues.get(selected_position).value_tier_price_html);
                        if(jObj.get("tierPriceHtml").toString().length() != 0){
                            txtPriceTier.setVisibility(View.VISIBLE);
                            txtPriceTier.setText(Html.fromHtml(jObj.get("tierPriceHtml").toString()));
                        }else{
                            txtPriceTier.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }


        };


        for(ProductDetails.ProductOptionsValue productValue : productOptions.productOptionsValues){

            String price = productValue.value_formatted_price;
            if(price.length() > 0){
                price = " + " + price;
            }

            final RadioButton radio = new RadioButton(activity, Color.parseColor(AppController.SECONDARY_COLOR));
            radio.setText(productValue.value_label + price);
            radio.setTag(productValue.value_code);
            radio.setGravity(Gravity.CENTER_VERTICAL);
            lay_.addView(radio);
            radioArray.add(radio);
            radio.setOnCheckedChangeListener(listener);
        }

        if(product_entity_type.equals("bundle") && productOptions.options_isMulti == 0){
            qtyLay.setVisibility(View.VISIBLE);
        }else{
            qtyLay.setVisibility(View.GONE);
        }


        container.addView(radioRootview);
    }

    public static boolean isValidated(String code){
        boolean isValidate = false;
        LinearLayout lay_= (LinearLayout) container.findViewWithTag("radio"+code);
        TextView txtEr = (TextView) container.findViewWithTag("error" + code);

        for (int i = 0; i < lay_.getChildCount(); i++) {
            RadioButton radio = (RadioButton) lay_.getChildAt(i);
            if(radio.isChecked()){
                isValidate = true;
            }
        }

        if(isValidate)
            txtEr.setVisibility(View.GONE);
        else
            txtEr.setVisibility(View.VISIBLE);
        return isValidate;
    }


}

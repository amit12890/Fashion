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
public class TypeCheckBoxView {

    ProductDetails.ProductOptions productOptions;
    Activity activity;
    private LayoutInflater inflate;
    private View cbRootview;
    private TextView txtHeader,txtError;
    public static LinearLayout container;

    public TypeCheckBoxView(Activity activity, ProductDetails.ProductOptions productOptions, LinearLayout container){
        this.activity = activity;
        this.productOptions = productOptions;
        this.container = container;
        initLayout();
    }

    public void initLayout(){
        inflate = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cbRootview = inflate.inflate(R.layout.layout_radio, null);

        txtHeader = (TextView) cbRootview.findViewById(R.id.txt_radio_title);
        txtHeader.setText(productOptions.option_label);
        if (productOptions.option_is_required.equals("1"))
            txtHeader.append(" *");

        txtError = (TextView) cbRootview.findViewById(R.id.txt_radio_error);
        txtError.setVisibility(View.GONE);
        txtError.setTag("error" + productOptions.option_code);
        txtError.setVisibility(View.GONE);

        LinearLayout lay_= (LinearLayout) cbRootview.findViewById(R.id.lay_radio_container);
        lay_.setTag("cb"+productOptions.option_code);

        for(ProductDetails.ProductOptionsValue productValue : productOptions.productOptionsValues){

            String price = productValue.value_formatted_price;
            if(price.length() > 0){
                price = " + " + price;
            }

            CheckBox cb = new CheckBox(activity, Color.parseColor(AppController.SECONDARY_COLOR));
            cb.setText(productValue.value_label + price);
            cb.setTag(productValue.value_code);
            cb.setGravity(Gravity.CENTER_VERTICAL);
            lay_.addView(cb);
        }
        container.addView(cbRootview);

    }

    public static boolean isValidated(String code){
        boolean isValidate = false;
        LinearLayout lay_= (LinearLayout) container.findViewWithTag("cb"+code);
        TextView txtEr = (TextView) container.findViewWithTag("error" + code);

        for (int i = 0; i < lay_.getChildCount(); i++) {
            CheckBox cb = (CheckBox) lay_.getChildAt(i);
            if(cb.isChecked()){
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

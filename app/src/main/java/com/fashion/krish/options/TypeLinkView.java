package com.fashion.krish.options;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.model.ProductDetails;
import com.rey.material.widget.CheckBox;

import java.util.ArrayList;

/**
 * Created by amit.thakkar on 8/10/2015.
 */
public class TypeLinkView {

    ArrayList<ProductDetails.ProductLinks> productLinksArray;
    Activity activity;
    private LayoutInflater inflate;
    private View linkRootview;
    private TextView txtHeader;
    public static LinearLayout container,lay_;
    public static TextView txtError;

    public TypeLinkView(Activity activity, ArrayList<ProductDetails.ProductLinks> productLinksArray, LinearLayout container){
        this.activity = activity;
        this.productLinksArray = productLinksArray;
        this.container = container;
        initLayout();
    }

    public void initLayout(){
        inflate = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        linkRootview = inflate.inflate(R.layout.layout_radio, null);


        txtHeader = (TextView) linkRootview.findViewById(R.id.txt_radio_title);
        txtHeader.setText(productLinksArray.get(0).link_label);
        if (productLinksArray.get(0).link_is_required.equals("1"))
            txtHeader.append(" *");

        txtError = (TextView) linkRootview.findViewById(R.id.txt_radio_error);
        txtError.setVisibility(View.GONE);
        txtError.setTag("error" + productLinksArray.get(0).link_code);
        txtError.setVisibility(View.GONE);

        lay_= (LinearLayout) linkRootview.findViewById(R.id.lay_radio_container);
        lay_.setTag("error" + productLinksArray.get(0).link_code);
        for(ProductDetails.ProductLinks productLinks : productLinksArray){

            String price = productLinks.link_item_formatted_price;
            if(price.length() > 0){
                price = " + " + price;
            }

            CheckBox cb = new CheckBox(activity, Color.parseColor(AppController.SECONDARY_COLOR));
            cb.setText(productLinks.link_item_label + price);
            cb.setTag(productLinks.link_item_value);
            cb.setGravity(Gravity.CENTER_VERTICAL);
            lay_.addView(cb);
        }
        container.addView(linkRootview);

    }

    public static boolean isValidated(){
        boolean isValidate = false;

        for (int i = 0; i < lay_.getChildCount(); i++) {
            CheckBox cb = (CheckBox) lay_.getChildAt(i);
            if(cb.isChecked()){
                isValidate = true;
            }
        }

        if(isValidate)
            txtError.setVisibility(View.GONE);
        else
            txtError.setVisibility(View.VISIBLE);

        return isValidate;

    }


}

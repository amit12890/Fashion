package com.fashion.krish.options;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.activity.ProductDetailActivity;
import com.fashion.krish.model.PaymentMethod;
import com.fashion.krish.model.ProductDetails;
import com.rey.material.widget.EditText;
import com.rey.material.widget.Spinner;

import org.json.JSONException;

/**
 * Created by amit.thakkar on 8/10/2015.
 */
public class TypeTextView {

    ProductDetails.ProductOptions productOptions;
    Activity activity;
    private LayoutInflater inflate;
    private View textRootview;
    private TextView txtHeader,txtError;
    private EditText editText;
    public static LinearLayout container;
    private static PaymentMethod.FieldSet fieldSet;

    public TypeTextView(){

    }
    public TypeTextView(Activity activity, ProductDetails.ProductOptions productOptions, LinearLayout container){
        this.activity = activity;
        this.productOptions = productOptions;
        this.container = container;
        initLayout();

    }

    public TypeTextView(Activity activity, PaymentMethod.FieldSet fieldSet, LinearLayout container){
        this.activity = activity;
        this.fieldSet = fieldSet;
        this.container = container;
        initLayoutForPayment();

    }

    public void initLayout(){
        inflate = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        textRootview = inflate.inflate(R.layout.layout_text, null);

        txtHeader = (TextView) textRootview.findViewById(R.id.txt_text_title);
        //txtHeader.setTag(productOptions.option_code);
        txtHeader.setText(productOptions.option_label);
        txtHeader.setVisibility(View.GONE);

        txtError = (TextView) textRootview.findViewById(R.id.txt_text_error);
        txtError.setVisibility(View.GONE);
        txtError.setTag("error" + productOptions.option_code);
        txtError.setVisibility(View.GONE);

        String price = productOptions.option_formatted_price;
        if(price.length() > 0){
            price = " + " + price;
        }

        ((EditText) textRootview.findViewById(R.id.edt_txt_payment)).setVisibility(View.GONE);

        editText = (EditText) textRootview.findViewById(R.id.edt_txt);
        editText.applyTheme(Color.parseColor(AppController.PRIMARY_COLOR),Color.parseColor(AppController.SECONDARY_COLOR));
        editText.setTag(productOptions.option_code);


        if (productOptions.option_is_required.equals("1"))
            editText.setHint(productOptions.option_label+ price+ " *");
        else
            editText.setHint(productOptions.option_label+ price);


        if(productOptions.option_max_character.length() > 0)
            editText.setMaxChar(Integer.parseInt(productOptions.option_max_character));

        container.addView(textRootview);
    }

    public void initLayoutForPayment(){
        inflate = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        textRootview = inflate.inflate(R.layout.layout_text, null);

        txtHeader = (TextView) textRootview.findViewById(R.id.txt_text_title);
        //txtHeader.setTag(productOptions.option_code);
        txtHeader.setText(fieldSet.field_label);
        //txtHeader.setVisibility(View.GONE);

        ((View) textRootview.findViewById(R.id.view_seperater)).setVisibility(View.GONE);
        txtError = (TextView) textRootview.findViewById(R.id.txt_text_error);
        txtError.setVisibility(View.GONE);
        txtError.setTag("error" + fieldSet.field_name);
        txtError.setVisibility(View.GONE);

        ((EditText) textRootview.findViewById(R.id.edt_txt)).setVisibility(View.GONE);

        editText = (EditText) textRootview.findViewById(R.id.edt_txt_payment);
        editText.setVisibility(View.VISIBLE);
        editText.setHint(fieldSet.field_label);
        editText.applyTheme(Color.parseColor(AppController.PRIMARY_COLOR), Color.parseColor(AppController.SECONDARY_COLOR));

        editText.setTag(fieldSet.field_name);
        try{
            if(fieldSet.validatorObj != null){
                if(fieldSet.validatorObj.getString("type").equals("credit_card")){
                    editText.setInputType(InputType.TYPE_CLASS_PHONE);
                    editText.addTextChangedListener(new FourDigitCardFormatWatcher());
                }
            }

        }catch (JSONException e){
            e.printStackTrace();
        }


        container.addView(textRootview);
    }

    public static boolean isValidated(String code){

        boolean isValidate = false;
        EditText et = (EditText) container.findViewWithTag(code);
        TextView txtEr = (TextView) container.findViewWithTag("error" + code);

        if(et.getText().toString().trim().length() <= 0 ){
            txtEr.setVisibility(View.VISIBLE);
            isValidate = false;
        }else{
            txtEr.setVisibility(View.GONE);
            isValidate = true;
        }

        return isValidate;
    }

    public static String getValue(String code){

        String value = "";
        EditText et = (EditText) container.findViewWithTag(code);

        if(et.getText().toString().contains("-")){
            value = et.getText().toString().replace("-","");
        }else{
            value = et.getText().toString();
        }

        return value;
    }

}

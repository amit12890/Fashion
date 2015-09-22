package com.fashion.krish.options;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fashion.krish.R;
import com.fashion.krish.model.PaymentMethod;
import com.fashion.krish.model.ProductDetails;
import com.rey.material.widget.EditText;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by amit.thakkar on 8/10/2015.
 */
public class TypeSelectionView {

    ProductDetails.ProductOptions productOptions;
    Activity activity;
    private LayoutInflater inflate;
    private View spinnerRootview;
    private TextView txtHeader,txtError;
    private Spinner spiner;
    public static LinearLayout container;
    private String product_entity_type;
    private static PaymentMethod.FieldSet fieldSet;

    public TypeSelectionView(Activity activity, ProductDetails.ProductOptions productOptions, LinearLayout container,String product_type){
        this.activity = activity;
        this.productOptions = productOptions;
        this.container = container;
        this.product_entity_type = product_type;
        initLayout();
    }

    public TypeSelectionView(Activity activity, PaymentMethod.FieldSet fieldSet, LinearLayout container){
        this.activity = activity;
        this.productOptions = productOptions;
        this.container = container;
        this.fieldSet = fieldSet;
        initLayoutForPayment();
    }

    public void initLayout(){
        inflate = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        spinnerRootview = inflate.inflate(R.layout.layout_selection, null);

        spiner = (Spinner) spinnerRootview.findViewById(R.id.spin_selection);
        spiner.setTag("selection"+productOptions.option_code);

        LinearLayout spinnerLay= (LinearLayout) spinnerRootview.findViewById(R.id.lay_spinner);
        LinearLayout qtyLay= (LinearLayout) spinnerLay.findViewById(R.id.lay_qty);

        final EditText etQty = (EditText) spinnerRootview.findViewById(R.id.et_qty);
        etQty.setText("1");
        etQty.setEnabled(false);

        final TextView txtPriceTier = (TextView) spinnerRootview.findViewById(R.id.txt_price_tier);
        txtPriceTier.setVisibility(View.GONE);

        ArrayList<String> arrayValues = new ArrayList<>();
        arrayValues.add("Choose Option");

        for(ProductDetails.ProductOptionsValue productValue : productOptions.productOptionsValues){
            arrayValues.add(productValue.value_label);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.row_spn, arrayValues);
        spiner.setAdapter(adapter);

        spiner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                if (productOptions.productOptionsValues.get(position - 1).custom_qty == 0) {
                    etQty.setEnabled(false);
                } else {
                    etQty.setEnabled(true);
                }

                if (productOptions.options_isMulti == 0 && position != 0
                        && productOptions.productOptionsValues.get(position - 1).value_tier_price_html.length() != 0) {
                    txtPriceTier.setVisibility(View.VISIBLE);
                    txtPriceTier.setText(Html.fromHtml(productOptions.productOptionsValues.get(position - 1).value_tier_price_html));
                } else {
                    txtPriceTier.setVisibility(View.GONE);
                }
            }
        });
        if(product_entity_type.equals("bundle") && productOptions.options_isMulti == 0){
            qtyLay.setVisibility(View.VISIBLE);
        }else{
            qtyLay.setVisibility(View.GONE);
        }


        TextView txt = (TextView) spinnerRootview.findViewById(R.id.txt_selection_title);
        txt.setText(productOptions.option_label);
        if (productOptions.option_is_required.equals("1"))
            txt.append(" *");

        txtError = (TextView) spinnerRootview.findViewById(R.id.txt_selection_error);
        txtError.setVisibility(View.GONE);
        txtError.setTag("error" + productOptions.option_code);
        txtError.setVisibility(View.GONE);

        container.addView(spinnerRootview);
    }

    public void initLayoutForPayment(){
        inflate = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        spinnerRootview = inflate.inflate(R.layout.layout_selection, null);

        ((View) spinnerRootview.findViewById(R.id.view_seperater)).setVisibility(View.GONE);
        spiner = (Spinner) spinnerRootview.findViewById(R.id.spin_selection);
        spiner.setTag("selection" + fieldSet.field_name);

        LinearLayout spinnerLay= (LinearLayout) spinnerRootview.findViewById(R.id.lay_spinner);
        LinearLayout qtyLay= (LinearLayout) spinnerLay.findViewById(R.id.lay_qty);
        qtyLay.setVisibility(View.GONE);

        final EditText etQty = (EditText) spinnerRootview.findViewById(R.id.et_qty);
        etQty.setVisibility(View.GONE);

        final TextView txtPriceTier = (TextView) spinnerRootview.findViewById(R.id.txt_price_tier);
        txtPriceTier.setVisibility(View.GONE);

        ArrayList<String> arrayValues = new ArrayList<>();
        arrayValues.add("Choose Option");

        for (String key : fieldSet.valuesMap.keySet()) {
            arrayValues.add(fieldSet.valuesMap.get(key));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.row_spn, arrayValues);
        spiner.setAdapter(adapter);

        spiner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                etQty.setEnabled(false);
                txtPriceTier.setVisibility(View.GONE);
            }
        });
        qtyLay.setVisibility(View.GONE);

        TextView txt = (TextView) spinnerRootview.findViewById(R.id.txt_selection_title);
        txt.setVisibility(View.GONE);

        txtError = (TextView) spinnerRootview.findViewById(R.id.txt_selection_error);
        txtError.setVisibility(View.GONE);
        txtError.setTag("error" + fieldSet.field_name);
        txtError.setVisibility(View.GONE);

        container.addView(spinnerRootview);
    }

    public static boolean isValidated(String code){
        boolean isValidate = false;
        Spinner selection= (Spinner) container.findViewWithTag("selection"+code);
        TextView txtEr = (TextView) container.findViewWithTag("error" + code);

        if(selection.getSelectedItemPosition() == 0){
            txtEr.setVisibility(View.VISIBLE);
            isValidate = false;
        }else{
            txtEr.setVisibility(View.GONE);
            isValidate = true;
        }

        return isValidate;
    }


    public static String getValue(String code,HashMap<String,String> map){

        String value = "";
        Spinner selection= (Spinner) container.findViewWithTag("selection"+code);

        for (String key : map.keySet()) {
            String selected_value = (String) selection.getAdapter().getItem(selection.getSelectedItemPosition());
             if(map.get(key).equals(selected_value))
                 value = key;
        }


        return value;
    }

}

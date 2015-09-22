package com.fashion.krish.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.adapter.CountryAdapter;
import com.fashion.krish.customview.TintableImageView;
import com.fashion.krish.utility.AnimationFactory;
import com.fashion.krish.utility.AnimationsClass;
import com.fashion.krish.utility.AppPreferences;
import com.fashion.krish.utility.Utility;
import com.rey.material.widget.Button;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.EditText;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class MyAddressFragment extends Fragment implements View.OnClickListener,View.OnTouchListener,TextWatcher {

    Utility util;
    private AppPreferences preferences;
    private LinearLayout layBack;
    private int color_primary,color_secondary;
    private LinearLayout layChildContainer,layCountryFilter,layAddAddress, laySubmit;
    private Button btnAddAddress, btnSubmit;
    private ViewAnimator viewAnimator;
    private EditText etFname,etLname,etCompany,etFax,etTelephone,etStreetAdd,etArea,etCity,etState,etZip;
    private TextView txtCountryTitle,txtCountryName,txtCountryError;
    private boolean isListScreen = true, isFilterView = false;
    private CheckBox cb_save_as_default;
    private ListView countryList;
    private android.widget.EditText edtCountry;
    private JSONObject countryObject;
    private HashMap<String,String> countryMap;
    private CountryAdapter countryAdapter;
    private String[] mKeys;
    private ArrayList<String> addressParams;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_my_address, container, false);
        util = new Utility(getActivity());
        Utility.changeStatusBarColor(getActivity());
        preferences = new AppPreferences(getActivity());

        init(rootView);


        return rootView;
    }

    private void init(View rootView){


        color_primary = preferences.getPrimaryColor();
        color_secondary = preferences.getSecondaryColor();

        ((RelativeLayout) rootView.findViewById(R.id.lay_title)).setBackgroundColor(color_primary);
        layBack = (LinearLayout) rootView.findViewById(R.id.lay_back);
        layBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFilterView) {

                    AnimationsClass filterLayAnim = new AnimationsClass(getActivity(), layCountryFilter);
                    filterLayAnim.slideOutFromTop(View.GONE);

                    AnimationsClass listAnim = new AnimationsClass(getActivity(), countryList);
                    listAnim.slideOutFromBottom(View.GONE);

                    isFilterView = false;

                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layAddAddress.setVisibility(View.VISIBLE);
                        }
                    }, 100);
                } else {
                    //onBackPressed();
                }

            }
        });

        layChildContainer = (LinearLayout) rootView.findViewById(R.id.lay_my_address_child_container);
        viewAnimator = (ViewAnimator)rootView.findViewById(R.id.viewFlipper);

        layAddAddress = (LinearLayout) rootView.findViewById(R.id.lay_add_new_address);
        layAddAddress.setBackgroundColor(color_secondary);
        btnAddAddress = (Button) rootView.findViewById(R.id.btn_add_new_address);
        btnAddAddress.setBackground(util.getSecondaryRippleDrawable());
        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationFactory.flipTransition(viewAnimator, AnimationFactory.FlipDirection.LEFT_RIGHT, 50);
                if (isListScreen) {
                    isListScreen = false;
                    btnAddAddress.setText("All Addresses");
                } else {
                    isListScreen = true;
                    btnAddAddress.setText("Add new Address");
                }
            }
        });

        laySubmit = (LinearLayout) rootView.findViewById(R.id.lay_submit_btn);
        laySubmit.setBackgroundColor(color_secondary);
        btnSubmit = (Button) rootView.findViewById(R.id.btn_submit);
        btnSubmit.setBackground(util.getSecondaryRippleDrawable());
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateAddressLayout()){
                    Log.d("Validation","true");

                    saveAddress();

                }else{
                    Log.d("Validation","false");
                }
            }
        });

        edtCountry = (android.widget.EditText) rootView.findViewById(R.id.edt_filter_country);
        edtCountry.addTextChangedListener(this);
        GradientDrawable filterDrawable = (GradientDrawable) edtCountry.getBackground();
        filterDrawable.setColor(Color.parseColor("#ffffff"));

        countryList = (ListView) rootView.findViewById(R.id.list_country);
        layCountryFilter = (LinearLayout) rootView.findViewById(R.id.lay_filter_country);
        layCountryFilter.setBackgroundColor(color_secondary);
        layCountryFilter.setVisibility(View.GONE);

        util.loadJSONFromAsset("country.json");
        countryMap = new HashMap<>();
        try {
            countryObject = new JSONObject(util.loadJSONFromAsset("country.json").toString());
            JSONArray countryArray = countryObject.getJSONArray("item");

            for (int i = 0; i < countryArray.length(); i++) {
                JSONObject jObj = countryArray.getJSONObject(i);
                countryMap.put(jObj.getString("value"),jObj.getString("label"));
            }
        }catch (JSONException e){
            Log.d("Country parsing error",e.toString());
        }
        countryMap = sortByValues(countryMap);
        mKeys = countryMap.keySet().toArray(new String[countryMap.size()]);
        countryAdapter = new CountryAdapter(getActivity(),countryMap);
        countryList.setAdapter(countryAdapter);
        countryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AnimationsClass filterLayAnim = new AnimationsClass(getActivity(),layCountryFilter);
                filterLayAnim.slideOutFromTop(View.GONE);

                AnimationsClass listAnim = new AnimationsClass(getActivity(),countryList);
                listAnim.slideOutFromBottom(View.GONE);

                isFilterView = false;

                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layAddAddress.setVisibility(View.VISIBLE);
                    }
                }, 200);

                txtCountryName.setText(countryAdapter.getItem(position));
                txtCountryTitle.setVisibility(View.VISIBLE);
                txtCountryError.setVisibility(View.INVISIBLE);
            }
        });

        getUserAddress();
        manageAddressLayout(rootView);

    }


    @Override
    public void onClick(View v) {

        switch (v.getTag().toString()){
            case "account":
                //Intent i = new Intent(MyAddressFragment.this,AccountInformationActivity.class);
                //startActivity(i);
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

    private void getUserAddress(){
        util.showLoadingDialog("Please Wait");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(getActivity()).getUserAddresses();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            util.hideLoadingDialog();
                            JSONObject jsonObject = new JSONObject(response);
                            if (response.equals("error")) {
                                util.hideLoadingDialog();
                                util.showErrorDialog("Some error has been occurred. Please try again", "Ok", "");
                            } else if (jsonObject.has("status")) {
                                if (jsonObject.getString("status").equals("error"))
                                    util.hideLoadingDialog();
                            } else {

                                if (jsonObject.get("item") instanceof JSONObject) {
                                    JSONObject itemObj = jsonObject.getJSONObject("item");
                                    addUserAddressLayout(itemObj);

                                } else {

                                    JSONArray itemArray = jsonObject.getJSONArray("item");
                                    for (int i = 0; i < itemArray.length(); i++) {
                                        JSONObject itemObj = itemArray.getJSONObject(i);
                                        addUserAddressLayout(itemObj);
                                    }
                                }


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            util.hideLoadingDialog();
                            util.showErrorDialog("Some error has been occurred. Please try again", "Ok", "");
                        }

                    }
                });


            }
        });
        if(RestClient.isNetworkAvailable(getActivity(), util))
        {
            t.start();


        }

    }

    public void addUserAddressLayout(JSONObject addressObj){

        ColorStateList tint = ColorStateList.valueOf(color_secondary);

        LayoutInflater infalInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View addressChildView = infalInflater.inflate(R.layout.layout_my_address_child, null);

        LinearLayout layAddressFlag = (LinearLayout) addressChildView.findViewById(R.id.lay_address_selection);
        TextView txtPname = (TextView) addressChildView.findViewById(R.id.txt_pname);
        TextView txtStreet = (TextView) addressChildView.findViewById(R.id.txt_address_st);
        TextView txtCity = (TextView) addressChildView.findViewById(R.id.txt_address_city);
        TextView txtOther = (TextView) addressChildView.findViewById(R.id.txt_address_other);
        TextView txtPhNo = (TextView) addressChildView.findViewById(R.id.txt_address_ph_no);
        TextView txtLabel = (TextView) addressChildView.findViewById(R.id.txt_address_label);
        TintableImageView imgEdit = (TintableImageView) addressChildView.findViewById(R.id.img_edit);
        TintableImageView img = (TintableImageView) addressChildView.findViewById(R.id.img_delete);

        try {
            txtPname.setText(addressObj.getString("firstname")+ " "+ addressObj.getString("lastname"));
            txtStreet.setText(addressObj.getString("street")+ ", "+ addressObj.getString("street1"));
            txtCity.setText(addressObj.getString("city")+ "- "+ addressObj.getString("postcode"));
            txtOther.setText(addressObj.getString("region")+ ", "+ addressObj.getString("country"));
            txtPhNo.setText("Phone: "+addressObj.getString("telephone"));
            txtLabel.setText(addressObj.getString("label"));

            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimationFactory.flipTransition(viewAnimator, AnimationFactory.FlipDirection.LEFT_RIGHT, 50);
                    if (isListScreen) {
                        isListScreen = false;
                        btnAddAddress.setText("All Addresses");
                    } else {
                        isListScreen = true;
                        btnAddAddress.setText("Add new Address");
                    }
                }
            });
            /*if(addressObj.has("default_shipping")){

            }
            if(addressObj.has("additional")){

            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        layChildContainer.addView(addressChildView);

    }

    public void manageAddressLayout(View rootView){
        etFname = (EditText) rootView.findViewById(R.id.edt_fname);
        etFname.applyTheme(color_primary,color_secondary);
        etFname.addTextChangedListener(this);

        etLname = (EditText) rootView.findViewById(R.id.edt_lname);
        etLname.applyTheme(color_primary,color_secondary);
        etLname.addTextChangedListener(this);

        etCompany = (EditText) rootView.findViewById(R.id.edt_company);
        etCompany.applyTheme(color_primary,color_secondary);

        etTelephone = (EditText) rootView.findViewById(R.id.edt_telephone);
        etTelephone.applyTheme(color_primary,color_secondary);
        etTelephone.addTextChangedListener(this);

        etFax = (EditText) rootView.findViewById(R.id.edt_fax);
        etFax.applyTheme(color_primary,color_secondary);

        etStreetAdd = (EditText) rootView.findViewById(R.id.edt_street_add);
        etStreetAdd.applyTheme(color_primary,color_secondary);
        etStreetAdd.addTextChangedListener(this);

        etArea = (EditText) rootView.findViewById(R.id.edt_area);
        etArea.applyTheme(color_primary,color_secondary);

        etCity = (EditText) rootView.findViewById(R.id.edt_city);
        etCity.applyTheme(color_primary,color_secondary);
        etCity.addTextChangedListener(this);

        etState = (EditText) rootView.findViewById(R.id.edt_state);
        etState.applyTheme(color_primary,color_secondary);

        etZip = (EditText) rootView.findViewById(R.id.edt_zip);
        etZip.applyTheme(color_primary,color_secondary);
        etZip.addTextChangedListener(this);

        txtCountryTitle = (TextView) rootView.findViewById(R.id.txt_country_title);
        txtCountryTitle.setTextColor(color_primary);
        txtCountryTitle.setVisibility(View.INVISIBLE);

        txtCountryName = (TextView) rootView.findViewById(R.id.edt_country_name);
        txtCountryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationsClass filterLayAnim = new AnimationsClass(getActivity(),layCountryFilter);
                filterLayAnim.slideInFromTop(View.VISIBLE);

                AnimationsClass listAnim = new AnimationsClass(getActivity(),countryList);
                listAnim.slideInFromBottom(View.VISIBLE);

                isFilterView = true;
                layAddAddress.setVisibility(View.INVISIBLE);
            }
        });
        txtCountryError = (TextView) rootView.findViewById(R.id.txt_country_error);

        ((View) rootView.findViewById(R.id.country_separator)).setBackgroundColor(color_primary);

        LinearLayout layCheckBox = (LinearLayout) rootView.findViewById(R.id.lay_checkbox);
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cb_save_as_default = new CheckBox(getActivity(),color_secondary);
        cb_save_as_default.setText("Use as my default shipping address");
        cb_save_as_default.setGravity(Gravity.CENTER_VERTICAL);
        cb_save_as_default.setLayoutParams(layParams);
        layCheckBox.addView(cb_save_as_default);


    }

   /* @Override
    public void onBackPressed() {
        if(isFilterView){

            AnimationsClass filterLayAnim = new AnimationsClass(getActivity(),layCountryFilter);
            filterLayAnim.slideOutFromTop(View.GONE);

            AnimationsClass listAnim = new AnimationsClass(getActivity(),countryList);
            listAnim.slideOutFromBottom(View.GONE);

            isFilterView = false;

            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    layAddAddress.setVisibility(View.VISIBLE);
                }
            },200);

        }else{
            //super.onBackPressed();
        }
    }*/

    private HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if(edtCountry.getText().hashCode() == s.hashCode()){
            countryAdapter.getFilter().filter(s.toString());
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(etFname.getText().hashCode() == s.hashCode()){
            if(s.length() == 0)
                etFname.setHelper("This is required");
            else
                etFname.setHelper("");
        }else if(etLname.getText().hashCode() == s.hashCode()){
            if(s.length() == 0)
                etLname.setHelper("This is required");
            else
                etLname.setHelper("");
        }else if(etTelephone.getText().hashCode() == s.hashCode()){
            if(s.length() == 0)
                etTelephone.setHelper("This is required");
            else
                etTelephone.setHelper("");
        }else if(etStreetAdd.getText().hashCode() == s.hashCode()) {
            if (s.length() == 0)
                etStreetAdd.setHelper("This is required");
            else
                etStreetAdd.setHelper("");
        }else if(etCity.getText().hashCode() == s.hashCode()){
            if(s.length() == 0)
                etCity.setHelper("This is required");
            else
                etCity.setHelper("");
        }else if(etZip.getText().hashCode() == s.hashCode()){
            if(s.length() == 0)
                etZip.setHelper("This is required");
            else
                etZip.setHelper("");
        }

    }

    @Override
    public void afterTextChanged(Editable s) {}

    public boolean validateAddressLayout(){

        boolean validated = false;

        if(etFname.getText().toString().trim().length()==0){
            etFname.setHelper("This is required");
            validated = false;
            return validated;
        }else{
            etFname.setHelper("");
            validated = true;
        }

        if(etLname.getText().toString().trim().length()==0){
            etLname.setHelper("This is required");
            validated = false;
            return validated;
        }else{
            etLname.setHelper("");
            validated = true;
        }

        if(etTelephone.getText().toString().trim().length()==0){
            etTelephone.setHelper("This is required");
            validated = false;
            return validated;
        }else{
            etTelephone.setHelper("");
            validated = true;
        }

        if(etStreetAdd.getText().toString().trim().length()==0){
            etStreetAdd.setHelper("This is required");
            validated = false;
            return validated;
        }else{
            etStreetAdd.setHelper("");
            validated = true;
        }

        if(etCity.getText().toString().trim().length()==0){
            etCity.setHelper("This is required");
            validated = false;
            return validated;
        }else{
            etCity.setHelper("");
            validated = true;
        }

        if(txtCountryName.getText().toString().trim().length()==0){
            txtCountryError.setVisibility(View.VISIBLE);
            validated = false;
            return validated;
        }else{
            txtCountryError.setVisibility(View.INVISIBLE);
            validated = true;
        }

        if(etZip.getText().toString().trim().length()==0){
            etZip.setHelper("This is required");
            validated = false;
            return validated;
        }else{
            etZip.setHelper("");
            validated = true;
        }



        return  validated;
    }

    private void saveAddress(){

        addressParams = new ArrayList<String>();
        addressParams.add(etFname.getText().toString());
        addressParams.add(etLname.getText().toString());
        addressParams.add(etCompany.getText().toString());
        addressParams.add(etTelephone.getText().toString());
        addressParams.add(etStreetAdd.getText().toString());
        addressParams.add(etArea.getText().toString());
        addressParams.add(etCity.getText().toString());

        for (Map.Entry<String, String> e : countryMap.entrySet()) {
            if(e.getValue().equals(txtCountryName.getText()))
                addressParams.add(e.getKey());
        }

        addressParams.add(etState.getText().toString());
        addressParams.add(etZip.getText().toString());

        if(cb_save_as_default.isChecked())
            addressParams.add("1");
        else
            addressParams.add("0");
        addressParams.add(etFax.getText().toString());

        util.showLoadingDialog("Please Wait");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(getActivity()).saveAddress(addressParams);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            util.hideLoadingDialog();
                            JSONObject jsonObject = new JSONObject(response);
                            if (response.equals("error")) {
                                util.hideLoadingDialog();
                                util.showErrorDialog("Some error has been occurred. Please try again", "Ok", "");
                            } else if (jsonObject.has("status")) {
                                if (jsonObject.getString("status").equals("error")) {
                                    util.hideLoadingDialog();
                                    util.showErrorDialog(jsonObject.getString("text"), "Ok", "");
                                } else if (jsonObject.getString("status").equals("success")) {
                                    util.hideLoadingDialog();
                                    util.showErrorDialog(jsonObject.getString("text"), "Ok", "");
                                    //Intent intent = getIntent();
                                    //finish();
                                    //startActivity(intent);
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            util.hideLoadingDialog();
                            util.showErrorDialog("Some error has been occurred. Please try again", "Ok", "");
                        }

                    }
                });


            }
        });
        if(RestClient.isNetworkAvailable(getActivity(), util))
        {
            t.start();

        }

    }

}

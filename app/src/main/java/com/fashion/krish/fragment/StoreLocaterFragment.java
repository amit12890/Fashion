package com.fashion.krish.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.activity.DashboardActivity;
import com.fashion.krish.model.Stores;
import com.fashion.krish.utility.AnimationFactory;
import com.fashion.krish.utility.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class StoreLocaterFragment extends Fragment implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,OnMapReadyCallback {


    private Utility util;
    private RelativeLayout rootLay;
    private Button btnSearch,btnSwitchView;
    private boolean isMapView = false;
    private int color_primary,color_secondary;
    private GoogleApiClient mGoogleApiClient;
    private String latitude = "23.0225", longitude = "72.5714";
    private ArrayList<Stores> storeList;
    private LinearLayout layStoreList;
    private MapFragment mapFragment;
    private ViewAnimator viewAnimator;
    private GoogleMap gMap;
    private EditText edtLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_store_locater, container, false);
        util = new Utility(getActivity());


        color_primary = Color.parseColor(AppController.PRIMARY_COLOR);
        color_secondary = Color.parseColor(AppController.SECONDARY_COLOR);
        storeList = new ArrayList<>();

        rootLay = (RelativeLayout) rootView.findViewById(R.id.lay_root);


        ((LinearLayout) rootView.findViewById(R.id.lay_location_search)).setBackgroundColor(color_secondary);
        btnSearch = (Button) rootView.findViewById(R.id.btn_location_search);
        btnSearch.setBackgroundDrawable(util.getSecondaryRippleDrawable());
        btnSearch.setOnClickListener(this);

        ((LinearLayout) rootView.findViewById(R.id.lay_switch_view)).setBackgroundColor(color_secondary);
        btnSwitchView = (Button) rootView.findViewById(R.id.btn_switch_view);
        btnSwitchView.setBackgroundDrawable(util.getSecondaryRippleDrawable());
        btnSwitchView.setOnClickListener(this);

        edtLocation =(EditText) rootView.findViewById(R.id.edt_store_finder);
        edtLocation.applyTheme(color_primary,color_secondary);
        layStoreList = (LinearLayout) rootView.findViewById(R.id.lay_list_view);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        GoogleMapOptions options = new GoogleMapOptions();
        options.zoomControlsEnabled(true)
                .rotateGesturesEnabled(false);
        mapFragment.getMapAsync(this);


        viewAnimator = (ViewAnimator)rootView.findViewById(R.id.viewFlipper);

        getStores();
        //DashboardActivity.animateToggle(0, 1);
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void getStores(){
        // dialog.show();
        //util.showLoadingDialog("Please Wait");
        util.showAnimatedLogoProgressBar(rootLay);
        //progressLay.setVisibility(View.VISIBLE);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                final String response = new RestClient(getActivity()).getStores(latitude,longitude);
                if(response.equals("error")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog("Some error has occurred. Please try again later", "OK", "");
                            util.hideAnimatedLogoProgressBar();
                            return;
                        }
                    });
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            final JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("status")) {
                                if (jsonObject.getString("status").equals("error")) {
                                    util.showErrorDialog("Some error has occurred. Please try again later", "OK", "");
                                    util.hideAnimatedLogoProgressBar();
                                } else {

                                    if(jsonObject.get("stores") instanceof JSONArray){
                                        JSONArray storeArray = jsonObject.getJSONArray("stores");

                                        for (int i = 0; i < storeArray.length(); i++) {
                                            JSONObject storeObj = storeArray.getJSONObject(i);
                                            storeList.add(parseStores(storeObj));
                                        }

                                    }else{

                                        JSONObject storeObj = jsonObject.getJSONObject("stores");
                                        storeList.add(parseStores(storeObj));
                                    }
                                    addStoreDetailsView();
                                    util.hideAnimatedLogoProgressBar();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            util.showErrorDialog("Some error has occurred. Please try again later", "OK", "");
                            util.hideAnimatedLogoProgressBar();
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

    private void searchStores(){

        layStoreList.removeAllViews();
        gMap.clear();
        util.hideAnimatedLogoProgressBar();

        util.showAnimatedLogoProgressBar(rootLay);
        //progressLay.setVisibility(View.VISIBLE);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                final String response = new RestClient(getActivity()).searchStores(edtLocation.getText().toString());
                if(response.equals("error")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog("Some error has occurred. Please try again later", "OK", "");
                            util.hideAnimatedLogoProgressBar();
                            return;
                        }
                    });
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            final JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("status")) {
                                if (jsonObject.getString("status").equals("error")) {
                                    util.showErrorDialog("Some error has occurred. Please try again later", "OK", "");
                                    util.hideAnimatedLogoProgressBar();
                                } else {

                                    if(jsonObject.get("stores") instanceof JSONArray){
                                        JSONArray storeArray = jsonObject.getJSONArray("stores");

                                        for (int i = 0; i < storeArray.length(); i++) {
                                            JSONObject storeObj = storeArray.getJSONObject(i);
                                            storeList.add(parseStores(storeObj));
                                        }

                                    }else{

                                        JSONObject storeObj = jsonObject.getJSONObject("stores");
                                        storeList.add(parseStores(storeObj));
                                    }
                                    addStoreDetailsView();
                                    util.hideAnimatedLogoProgressBar();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            util.showErrorDialog("Some error has occurred. Please try again later", "OK", "");
                            util.hideAnimatedLogoProgressBar();
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

    public Stores parseStores(JSONObject jobj){
        Stores store = new Stores();
        try{

            store.store_location_id = jobj.getString("locations_id");
            store.store_name = jobj.getString("store_name");

            if(jobj.has("store_email"))
                store.store_email = jobj.getString("store_email");
            if(jobj.has("store_phone"))
                store.store_phone = jobj.getString("store_phone");

            store.store_address = jobj.getString("address");
            store.store_add1 = jobj.getString("store_addr1");
            store.store_add2 = jobj.getString("store_addr2");
            store.store_city = jobj.getString("store_city");
            store.store_state = jobj.getString("store_state");
            store.store_country = jobj.getString("store_country");
            store.store_postal_code = jobj.getString("store_postcode");

            if(jobj.has("distance"))
                store.store_distance = jobj.getString("distance");
            if(jobj.has("longitude"))
                store.store_longitude = jobj.getString("longitude");
            if(jobj.has("latitude"))
                store.store_latitude = jobj.getString("latitude");



        }catch (JSONException e){
            e.printStackTrace();
        }

        return store;
    }

    public void addStoreDetailsView(){

        if(storeList.size() > 0){
            for (int i = 0; i < storeList.size(); i++) {
                Stores store = storeList.get(i);
                LatLng storeLocation = new LatLng(Double.parseDouble(store.store_latitude),
                        Double.parseDouble(store.store_longitude));
                if(gMap != null){
                    gMap.addMarker(new MarkerOptions()
                            .title(store.store_name)
                            .snippet(store.store_address)
                            .position(storeLocation));
                    if(i == 0){
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(storeLocation, 13));
                    }
                }

            }

        }

        LayoutInflater infalInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if(storeList.size() > 0){
            for(int i = 0; i<storeList.size(); i++){
                View storeView = infalInflater.inflate(R.layout.layout_store_address_child, null);
                final Stores store = storeList.get(i);

                TextView txt_store_name = (TextView) storeView.findViewById(R.id.txt_sname);
                TextView txt_store_distance = (TextView) storeView.findViewById(R.id.txt_distance);
                TextView txt_store_add_1 = (TextView) storeView.findViewById(R.id.txt_address_1);
                TextView txt_store_add_2 = (TextView) storeView.findViewById(R.id.txt_address_2);
                TextView txt_store_other = (TextView) storeView.findViewById(R.id.txt_address_other);
                TextView txt_store_ph_no = (TextView) storeView.findViewById(R.id.txt_address_ph_no);
                TextView txt_store_email = (TextView) storeView.findViewById(R.id.txt_address_email);

                if(store.store_add1.length() <= 0)
                    txt_store_add_1.setVisibility(View.GONE);

                if(store.store_add2.length() <= 0)
                    txt_store_add_2.setVisibility(View.GONE);

                if(store.store_phone.length() <= 0)
                    txt_store_ph_no.setVisibility(View.GONE);

                if(store.store_email.length() <= 0)
                    txt_store_email.setVisibility(View.GONE);

                if(store.store_distance.length() <= 0)
                    txt_store_distance.setVisibility(View.GONE);

                txt_store_name.setText(store.store_name);
                txt_store_distance.setText("("+store.store_distance+")");
                txt_store_add_1.setText(store.store_add1);
                txt_store_add_2.setText(store.store_add2);
                txt_store_other.setText(store.store_city+", "+store.store_state+" "+store.store_postal_code);
                txt_store_ph_no.setText(store.store_phone);
                txt_store_email.setText(store.store_email);

                txt_store_ph_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + store.store_phone));
                        startActivity(callIntent);
                    }
                });

                txt_store_email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {store.store_email});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "CoupCommerce Android App");
                        //intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");

                        startActivity(Intent.createChooser(intent, "Send Email"));
                    }
                });
                layStoreList.addView(storeView);

            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back_arrow:
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                DashboardFragment dashboardFragment = new DashboardFragment();
                ft.replace(R.id.content_frame, dashboardFragment);
                ft.commit();
                break;

            case R.id.btn_location_search:
                searchStores();
                storeList.clear();

                break;
            case R.id.btn_switch_view:

                AnimationFactory.flipTransition(viewAnimator, AnimationFactory.FlipDirection.LEFT_RIGHT, 100);

                if(isMapView){
                    isMapView = false;
                    btnSwitchView.setText("View stores with Geo Map");
                }else{
                    isMapView = true;
                    btnSwitchView.setText("View stores list");

                }
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            Log.d("Latitude",String.valueOf(mLastLocation.getLatitude()));
            latitude = String.valueOf(mLastLocation.getLatitude());
            Log.d("Longitude", String.valueOf(mLastLocation.getLongitude()));
            longitude = String.valueOf(mLastLocation.getLongitude());
        }else{

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Suspended",String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Failed",connectionResult.toString());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng current = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        gMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 13));

        googleMap.addMarker(new MarkerOptions()
                .title("You are here")
                .position(current));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        FragmentManager fm = getFragmentManager();
        Fragment fragment = (fm.findFragmentById(R.id.map));
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commit();
        //DashboardActivity.animateToggle(1, 0);

    }

}

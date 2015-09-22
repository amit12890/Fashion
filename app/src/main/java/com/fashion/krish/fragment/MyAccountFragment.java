package com.fashion.krish.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.activity.DashboardActivity;
import com.fashion.krish.customview.TintableImageView;
import com.fashion.krish.utility.AppPreferences;
import com.fashion.krish.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MyAccountFragment extends Fragment implements View.OnClickListener,View.OnTouchListener {

    Utility util;
    private AppPreferences preferences;
    private LinearLayout layBack;
    private int color_primary,color_secondary;
    private LinearLayout layChildContainer;

    public MyAccountFragment() {
        // Required empty public constructor
        util = new Utility(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_my_account, container, false);
        util = new Utility(getActivity());

        Utility.changeStatusBarColor(getActivity());
        util = new Utility(getActivity());
        preferences = new AppPreferences(getActivity());

        color_primary = Color.parseColor(AppController.PRIMARY_COLOR);
        color_secondary = Color.parseColor(AppController.SECONDARY_COLOR);

        layChildContainer = (LinearLayout) rootView.findViewById(R.id.lay_myaccount_child_container);

        getUserAccountData();

        return rootView;
    }


    @Override
    public void onClick(View v) {

        switch (v.getTag().toString()){
            case "account":
                AccountInformationFragment accountInformationFragment= new AccountInformationFragment();
                updateFragment(accountInformationFragment);
                DashboardActivity.selectedFragment = MyAccountFragment.this;

                break;
            case "addresses":
                Intent addressIntent = new Intent(getActivity(),MyAddressFragment.class);
                startActivity(addressIntent);

                break;
            case "orders":
                MyOrdersFragment myOrdersFragment = new MyOrdersFragment();
                updateFragment(myOrdersFragment);
                DashboardActivity.selectedFragment = MyAccountFragment.this;

                break;
            case "wishlist":
                WishListFragment wishListFragment = new WishListFragment();
                updateFragment(wishListFragment);
                DashboardActivity.selectedFragment = MyAccountFragment.this;

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

    private void getUserAccountData(){
        util.showLoadingDialog("Please Wait");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(getActivity()).getUserAccountData();
                if(response.equals(RestClient.ERROR)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                            util.hideLoadingDialog();
                            return;
                        }
                    });
                }else if(response.equals(RestClient.TIMEOUT_ERROR)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog(RestClient.TIMEOUT_ERROR_MESSAGE, "OK", "");
                            util.hideLoadingDialog();
                            return;
                        }
                    });
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            util.hideLoadingDialog();
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.get("item") instanceof JSONObject) {
                                JSONObject itemObj = jsonObject.getJSONObject("item");

                                String label = itemObj.getString("label");
                                String text = itemObj.getString("text");
                                String id = itemObj.getString("id");

                                addUserAccountOptions(label, text, id);


                            } else {

                                JSONArray itemArray = jsonObject.getJSONArray("item");
                                for (int i = 0; i < itemArray.length(); i++) {
                                    JSONObject itemObj = itemArray.getJSONObject(i);

                                    String label = itemObj.getString("label");
                                    String text = itemObj.getString("text");
                                    String id = itemObj.getString("id");

                                    addUserAccountOptions(label, text, id);
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            util.hideLoadingDialog();
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
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

    public void addUserAccountOptions(String label,String text,String tag){

        ColorStateList tint = ColorStateList.valueOf(color_secondary);

        LayoutInflater infalInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View accountChildView = infalInflater.inflate(R.layout.layout_my_account_child, null);

        LinearLayout layViewMore = (LinearLayout) accountChildView.findViewById(R.id.lay_view_more);
        TextView txtLabel = (TextView) accountChildView.findViewById(R.id.txt_label);
        TextView txtValue = (TextView) accountChildView.findViewById(R.id.txt_value);
        TextView txtViewMore = (TextView) accountChildView.findViewById(R.id.txt_view_more);
        TintableImageView imgViewMore = (TintableImageView) accountChildView.findViewById(R.id.img_arrow);

        layViewMore.setTag(tag);
        layViewMore.setOnClickListener(this);
        layViewMore.setOnTouchListener(this);
        txtLabel.setText(label);
        txtValue.setText(Html.fromHtml(text));
        txtViewMore.setTextColor(color_secondary);
        imgViewMore.setColorFilter(tint);

        layChildContainer.addView(accountChildView);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DashboardActivity.animateToggle(1, 0);

    }

    @Override
    public void onResume() {
        super.onResume();
        DashboardActivity.animateToggle(0, 1);
    }

    public void updateFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        DashboardActivity.current_flag = fragment;
        String backStateName = fragment.getClass().getName();
        if(AppController.fragmentStack.contains(backStateName)){
            AppController.fragmentStack.remove(AppController.fragmentStack.indexOf(backStateName));
        }
        AppController.fragmentStack.push(backStateName);

        FragmentTransaction ft = fragmentManager.beginTransaction();

        ft.setCustomAnimations(R.anim.fragment_slide_in, R.anim.fragment_slide_out);
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }



}

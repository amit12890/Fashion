package com.fashion.krish.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.activity.DashboardActivity;
import com.fashion.krish.model.Content;
import com.fashion.krish.model.Currency;
import com.fashion.krish.model.Language;
import com.fashion.krish.model.ProductDetails;
import com.fashion.krish.utility.Utility;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SettingsFragment extends Fragment {


    private Utility utility;
    private Spinner spinnerCurrency,spinnerLanguage;

    public SettingsFragment() {
        // Required empty public constructor
        utility = new Utility(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        utility = new Utility(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);


        addFooterCMSButtons(rootView);

        spinnerCurrency = (Spinner) rootView.findViewById(R.id.spin_currency);
        spinnerLanguage = (Spinner) rootView.findViewById(R.id.spin_language);

        ArrayList<String> currencyArray = new ArrayList<>();
        ArrayList<String> languageArray = new ArrayList<>();

        for(Currency currencyValue : AppController.currencyArray){
            currencyArray.add(currencyValue.currency_symbol);
        }

        for(Language languageValue : AppController.languageArray){
            languageArray.add(languageValue.store_name);
        }

        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(getActivity(), R.layout.row_spn, currencyArray);
        spinnerCurrency.setAdapter(currencyAdapter);

        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(getActivity(), R.layout.row_spn, languageArray);
        spinnerLanguage.setAdapter(languageAdapter);

        if(DashboardActivity.selectedFragment != null)
            DashboardActivity.animateToggle(0, 1);

        return rootView;


    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public void updateFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();

        //Replace fragment
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
        DashboardActivity.animateToggle(1, 0);
    }

    public void addFooterCMSButtons(View rootView){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Button btnCopyRight = (Button) rootView.findViewById(R.id.btn_copy_right);
        btnCopyRight.setText(AppController.COPY_RIGHT);

        ImageView imgFb = (ImageView) rootView.findViewById(R.id.img_facebook);
        imgFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialFragment socialFragment = new SocialFragment(AppController.FACEBOOK_DETAILS.get("page"));
                DashboardActivity.selectedFragment = SettingsFragment.this;
                updateFragment(socialFragment);
            }
        });
        ImageView imgTwitter = (ImageView) rootView.findViewById(R.id.img_twitter);
        imgTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialFragment socialFragment = new SocialFragment(AppController.TWITTER_DETAILS.get("page"));
                DashboardActivity.selectedFragment = SettingsFragment.this;
                updateFragment(socialFragment);
            }
        });
        ImageView imgGPlus = (ImageView) rootView.findViewById(R.id.img_gplus);
        imgGPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialFragment socialFragment = new SocialFragment(AppController.GOOGLE_P_DETAILS.get("page"));
                DashboardActivity.selectedFragment = SettingsFragment.this;
                updateFragment(socialFragment);
            }
        });

        if(!AppController.FACEBOOK_DETAILS.get("isActive").equals("1")){
            imgFb.setVisibility(View.GONE);
        }
        if(!AppController.TWITTER_DETAILS.get("isActive").equals("1")){
            imgTwitter.setVisibility(View.GONE);
        }
        if(!AppController.GOOGLE_P_DETAILS.get("isActive").equals("1")){
            imgGPlus.setVisibility(View.GONE);
        }

        LinearLayout layout =  (LinearLayout) rootView.findViewById(R.id.lay_cms_actions);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        int count = 1;
        for (int i = 0; i < AppController.contentArray.size(); i++) {
            View footerView = inflater.inflate(R.layout.lay_cms_button_footer, null);
            footerView.setLayoutParams(params);
            final Content content = AppController.contentArray.get(i);
            if(content.content_action_type.equals("footer") && count <= 2){

                Button btnAction = (Button) footerView.findViewById(R.id.btn_cms);
                btnAction.setText(content.content_action_label);
                btnAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CMSFragment cmsFragment = new CMSFragment(content.content_action_id);
                        DashboardActivity.selectedFragment = SettingsFragment.this;
                        updateFragment(cmsFragment);
                    }
                });

                LinearLayout sepLay = (LinearLayout) footerView.findViewById(R.id.separator_cms);
                if(count == 2)
                    sepLay.setVisibility(View.GONE);

                layout.addView(footerView);

                count ++;

            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(DashboardActivity.selectedFragment != null)
            DashboardActivity.animateToggle(1, 0);

    }
}

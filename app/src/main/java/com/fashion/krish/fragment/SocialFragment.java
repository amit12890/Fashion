package com.fashion.krish.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.activity.DashboardActivity;
import com.fashion.krish.model.Content;
import com.fashion.krish.utility.Utility;
import com.rey.material.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;


public class SocialFragment extends Fragment {


    private Utility utility;
    private String url;
    private RelativeLayout rootLay;
    private WebView webView;
    private LinearLayout layDashboardItems;
    private ImageView imgLogo;


    public SocialFragment(String url) {
        // Required empty public constructor
        utility = new Utility(getActivity());
        this.url = url;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_social, container, false);
        utility = new Utility(getActivity());
        //txtProgress = (TextView) rootView.findViewById(R.id.txt_progress_text);
        rootLay =(RelativeLayout) getActivity().findViewById(R.id.lay_root);
        webView = (WebView) rootView.findViewById(R.id.web_social);

        utility.showAnimatedLogoProgressBar(rootLay);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webView.setLongClickable(false);

        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                utility.hideAnimatedLogoProgressBar();

            }
        });

        imgLogo = (ImageView) getActivity().findViewById(R.id.img_logo_title);
        imgLogo.setVisibility(View.GONE);
        layDashboardItems = (LinearLayout) getActivity().findViewById(R.id.lay_dashboard_items);
        layDashboardItems.setVisibility(View.GONE);
        DashboardActivity.animateToggle(0, 1);
        return rootView;


    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DashboardActivity.animateToggle(1, 0);
        imgLogo.setVisibility(View.VISIBLE);
        layDashboardItems.setVisibility(View.VISIBLE);
    }

}

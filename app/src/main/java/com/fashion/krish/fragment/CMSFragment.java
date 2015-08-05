package com.fashion.krish.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.utility.Utility;
import com.rey.material.widget.ProgressView;

import org.json.JSONException;
import org.json.JSONObject;


public class CMSFragment extends Fragment implements View.OnClickListener {


    private Utility utility;
    private String page_id;
    private ProgressView progressView;
    private RelativeLayout progressLay;
    private TextView txtProgress;
    private WebView webView;

    public CMSFragment(String page_id) {
        // Required empty public constructor
        utility = new Utility(getActivity());
        this.page_id = page_id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_cms, container, false);

        txtProgress = (TextView) rootView.findViewById(R.id.txt_progress_text);
        progressView =(ProgressView) rootView.findViewById(R.id.progressbar);
        progressLay =(RelativeLayout) rootView.findViewById(R.id.lay_progress_dialog);
        webView = (WebView) rootView.findViewById(R.id.web_cms);

        rootView.findViewById(R.id.btn_aboutus).setOnClickListener(this);
        rootView.findViewById(R.id.btn_shipping).setOnClickListener(this);
        rootView.findViewById(R.id.btn_faq).setOnClickListener(this);
        rootView.findViewById(R.id.btn_returns).setOnClickListener(this);

        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webView.setLongClickable(false);

        getCMSContent();

        return rootView;


    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void getCMSContent(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(getActivity()).getCMSPage(page_id);

                try{
                    final JSONObject jsonObject = new JSONObject(response);
                    if(!jsonObject.get("static_page").equals(null)){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    webView.loadData(jsonObject.getJSONObject("static_page").getJSONObject("html").get("content").toString(),
                                            "text/HTML", "UTF-8");
                                    webView.setWebViewClient(new WebViewClient() {

                                        public void onPageFinished(WebView view, String url) {
                                            // do your stuff here
                                            progressLay.setVisibility(View.GONE);

                                        }
                                    });
                                }catch (Exception e){

                                }

                            }
                        });


                    }else {
                        Toast.makeText(getActivity(), "Some error has been occurred. Please try again later. ", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        });
        if(RestClient.isNetworkAvailable(getActivity(), utility))
        {
            t.start();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_aboutus:
                page_id = "about-us";
                getCMSContent();
                progressLay.setVisibility(View.VISIBLE);

                break;
            case R.id.btn_shipping:
                page_id = "shipping";
                getCMSContent();
                progressLay.setVisibility(View.VISIBLE);

                break;
            case R.id.btn_faq:
                page_id = "faq";
                getCMSContent();
                progressLay.setVisibility(View.VISIBLE);

                break;
            case R.id.btn_returns:
                page_id = "returns";
                getCMSContent();
                progressLay.setVisibility(View.VISIBLE);

                break;
        }
    }

}

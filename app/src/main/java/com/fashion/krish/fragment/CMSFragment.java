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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.activity.DashboardActivity;
import com.fashion.krish.model.Content;
import com.fashion.krish.utility.Utility;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;

import org.json.JSONException;
import org.json.JSONObject;


public class CMSFragment extends Fragment {


    private Utility utility;
    private String page_id;
    private RelativeLayout rootLay;
    //private TextView txtProgress;
    private WebView webView;
    private LinearLayout layDashboardItems;
    private ImageView imgLogo;


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
        utility = new Utility(getActivity());
        //txtProgress = (TextView) rootView.findViewById(R.id.txt_progress_text);
        rootLay =(RelativeLayout) getActivity().findViewById(R.id.lay_root);
        webView = (WebView) rootView.findViewById(R.id.web_cms);




        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webView.setLongClickable(false);

        addFooterCMSButtons(rootView);
        getCMSContent(page_id);

        imgLogo = (ImageView) getActivity().findViewById(R.id.img_logo_title);
        imgLogo.setVisibility(View.GONE);
        layDashboardItems = (LinearLayout) getActivity().findViewById(R.id.lay_dashboard_items);
        layDashboardItems.setVisibility(View.GONE);

        if(DashboardActivity.selectedFragment != null)
            DashboardActivity.animateToggle(0, 1);

        return rootView;


    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void getCMSContent(final String page_id){
        utility.showAnimatedLogoProgressBar(rootLay);
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
                                            utility.hideAnimatedLogoProgressBar();

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


    public void addFooterCMSButtons(View rootView){

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Button btnCopyRight = (Button) rootView.findViewById(R.id.btn_copy_right);
        btnCopyRight.setText(AppController.COPY_RIGHT);

        ImageView imgFb = (ImageView) rootView.findViewById(R.id.img_facebook);
        imgFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialFragment socialFragment = new SocialFragment(AppController.FACEBOOK_DETAILS.get("page"));
                updateFragment(socialFragment);
            }
        });
        ImageView imgTwitter = (ImageView) rootView.findViewById(R.id.img_twitter);
        imgTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialFragment socialFragment = new SocialFragment(AppController.TWITTER_DETAILS.get("page"));
                updateFragment(socialFragment);
            }
        });
        ImageView imgGPlus = (ImageView) rootView.findViewById(R.id.img_gplus);
        imgGPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialFragment socialFragment = new SocialFragment(AppController.GOOGLE_P_DETAILS.get("page"));
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
                        getCMSContent(content.content_action_id);
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
        imgLogo.setVisibility(View.VISIBLE);
        layDashboardItems.setVisibility(View.VISIBLE);
    }

    public void updateFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        DashboardActivity.selectedFragment = CMSFragment.this;
        //Replace fragment
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }
}

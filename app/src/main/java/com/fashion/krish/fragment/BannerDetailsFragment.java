package com.fashion.krish.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.activity.DashboardActivity;
import com.fashion.krish.model.Content;
import com.fashion.krish.utility.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rey.material.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit.mime.TypedFile;


public class BannerDetailsFragment extends Fragment {


    private Utility util;
    private String page_id;
    private RelativeLayout rootLay,bannerTitleLay;
    private LinearLayout fragmentBaseLayout,layViewAll,layCategoryContainer;
    private ImageView img_banner,img_arrow;
    private TextView txt_title;
    int color_primary,color_secondary;
    private String category_id;

    ImageLoader imageLoader;
    DisplayImageOptions options;



    public BannerDetailsFragment(String category_id) {
        util = new Utility(getActivity());
        this.category_id = category_id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_banner_details, container, false);
        util = new Utility(getActivity());
        color_primary = Color.parseColor(AppController.PRIMARY_COLOR);
        color_secondary = Color.parseColor(AppController.SECONDARY_COLOR);

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.placeholder)
                .showImageOnFail(R.drawable.placeholder)
                .showImageOnLoading(R.drawable.placeholder).build();


        rootLay =(RelativeLayout) getActivity().findViewById(R.id.lay_root);

       addFooterCMSButtons(rootView);

        fragmentBaseLayout = (LinearLayout) rootView.findViewById(R.id.lay_root);
        bannerTitleLay = (RelativeLayout) rootView.findViewById(R.id.lay_banner_title);
        bannerTitleLay.setBackgroundColor(color_secondary);
        layCategoryContainer = (LinearLayout) rootView.findViewById(R.id.lay_banner_category_child);
        layViewAll = (LinearLayout) rootView.findViewById(R.id.lay_view_all);
        layViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!util.isDailogueVisible() && category_id.length()!=0){
                    ProductListFragment productListFragment = new ProductListFragment(category_id, "");
                    updateFragment(productListFragment);
                }
            }
        });
        txt_title = (TextView) rootView.findViewById(R.id.txt_banner_detail_title);
        img_banner = (ImageView) rootView.findViewById(R.id.img_banner_detail);
        img_arrow = (ImageView) rootView.findViewById(R.id.img_banner_detail_view_all);
        img_arrow.setColorFilter(Color.WHITE);
        DashboardActivity.animateToggle(0, 1);

        getCategory(category_id);

        return rootView;


    }


    private void getCategory(final String category_id){
        // dialog.show();
        //util.showLoadingDialog("Please Wait");
        util.showAnimatedLogoProgressBar(rootLay);
        //progressLay.setVisibility(View.VISIBLE);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                final String response = new RestClient(getActivity()).getCategory(category_id);
                if(response.equals(RestClient.ERROR)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                            util.hideAnimatedLogoProgressBar();
                            return;
                        }
                    });
                }else if(response.equals(RestClient.TIMEOUT_ERROR)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog(RestClient.TIMEOUT_ERROR_MESSAGE, "OK", "");
                            util.hideAnimatedLogoProgressBar();
                            return;
                        }
                    });
                }

                try{
                    final JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("category_info")) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                util.hideAnimatedLogoProgressBar();
                                try{
                                    if(jsonObject.getJSONObject("category_info").has("banner")){
                                        int width = util.getScreenWidth();
                                        int height = width / 2;
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,height);
                                        img_banner.setLayoutParams(params);
                                        String imageURI = jsonObject.getJSONObject("category_info").getString("banner");
                                        imageLoader.displayImage(imageURI,img_banner,options);
                                    }
                                    if(jsonObject.getJSONObject("category_info").has("label")){
                                        String label = jsonObject.getJSONObject("category_info").getString("label");
                                        txt_title.setText(label);
                                    }

                                    if(jsonObject.has("items")){
                                        if(jsonObject.getJSONObject("items").get("item")
                                                instanceof JSONArray){
                                            JSONArray itemArray = jsonObject.getJSONObject("items").getJSONArray("item");

                                            for (int i = 0; i < itemArray.length(); i++) {
                                                JSONObject itemObj = itemArray.getJSONObject(i);
                                                addSubCategories(itemObj,i);
                                            }
                                        }else{
                                            JSONObject itemObj = jsonObject.getJSONObject("items").getJSONObject("item");
                                            addSubCategories(itemObj,0);
                                        }
                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }

                            }
                        });



                    } else {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                util.hideAnimatedLogoProgressBar();
                                util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                            }
                        });


                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        });
        if(RestClient.isNetworkAvailable(getActivity(), util))
        {
            t.start();
        }
    }

    private void addSubCategories(final JSONObject itemObj,int index){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int padding = (int) Utility.convertDpToPixel(15,getActivity());

        TextView txt_sub_category = new TextView(getActivity());
        try{
            txt_sub_category.setText(itemObj.getString("label"));
            txt_sub_category.setTag(itemObj.getString("entity_id"));
            txt_sub_category.setTextColor(Color.parseColor("#333333"));
            txt_sub_category.setLayoutParams(params);
            txt_sub_category.setBackgroundColor(Color.WHITE);
            txt_sub_category.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            txt_sub_category.setPadding(padding, padding, padding, padding);
            txt_sub_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!util.isDailogueVisible()){
                        String category_id = null;
                        try {
                            category_id = itemObj.getString("entity_id");
                            ProductListFragment productListFragment = new ProductListFragment(category_id, "");
                            updateFragment(productListFragment);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
            layCategoryContainer.addView(txt_sub_category);
        }catch (JSONException e){
            e.printStackTrace();
        }

        int separator_height = (int) Utility.convertDpToPixel(1,getActivity());
        LinearLayout.LayoutParams separatorParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                separator_height);
        View v = new View(getActivity());
        v.setBackgroundColor(Color.parseColor("#f0f0f0"));
        v.setLayoutParams(separatorParams);
        layCategoryContainer.addView(v);
    }


    public void updateFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        //DashboardActivity.current_flag = fragment;
        DashboardActivity.selectedFragment = BannerDetailsFragment.this;
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
                DashboardActivity.selectedFragment = BannerDetailsFragment.this;
                updateFragment(socialFragment);
            }
        });
        ImageView imgTwitter = (ImageView) rootView.findViewById(R.id.img_twitter);
        imgTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialFragment socialFragment = new SocialFragment(AppController.TWITTER_DETAILS.get("page"));
                DashboardActivity.selectedFragment = BannerDetailsFragment.this;
                updateFragment(socialFragment);
            }
        });
        ImageView imgGPlus = (ImageView) rootView.findViewById(R.id.img_gplus);
        imgGPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialFragment socialFragment = new SocialFragment(AppController.GOOGLE_P_DETAILS.get("page"));
                DashboardActivity.selectedFragment = BannerDetailsFragment.this;
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
                        DashboardActivity.selectedFragment = BannerDetailsFragment.this;
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
}

package com.fashion.krish.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.activity.DashboardActivity;
import com.fashion.krish.activity.ProductDetailActivity;
import com.fashion.krish.model.Content;
import com.fashion.krish.model.HomeBanners;
import com.fashion.krish.model.Product;
import com.fashion.krish.utility.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rey.material.widget.Button;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private BannerPagerAdapter bannerPagerAdapter;

    private int NUM_PAGES = 3;
    private ViewPager mViewPager;
    private List<ImageView> dots;
    List<String> bannerUrlList;
    List<String> staticBannerUrlList;
    private LinearLayout layStaticBanner;
    ImageLoader imageLoader;
    DisplayImageOptions options;

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    private Utility util;

    private CirclePageIndicator circleIndicator;
    private LinearLayout layRelatedProductContainer,layRecentViewedProducts;

    private Toolbar toolbar;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), android.R.style.Theme_DeviceDefault);
        LayoutInflater localInflater = inflater.from(contextThemeWrapper);

        View rootView = localInflater.inflate(R.layout.fragment_home, container, false);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);


       layStaticBanner = (LinearLayout) rootView.findViewById(R.id.lay_static_banner);
        bannerUrlList = new ArrayList<String>();
        staticBannerUrlList = new ArrayList<String>();
        for(int i = 0; i< AppController.homeBannersArrayList.size(); i++){
            HomeBanners homeBanners = AppController.homeBannersArrayList.get(i);
            if(homeBanners.banner_type.equals("rotate")){
                bannerUrlList.add(homeBanners.banner_image);
            }else{
                staticBannerUrlList.add(homeBanners.banner_image);
            }
        }

        NUM_PAGES = bannerUrlList.size();

        bannerPagerAdapter = new BannerPagerAdapter(getActivity());

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.placeholder)
                .showImageOnFail(R.drawable.placeholder)
                .showImageOnLoading(R.drawable.placeholder).build();

        layRelatedProductContainer = (LinearLayout) rootView.findViewById(R.id.layout_product_container);
        layRecentViewedProducts = (LinearLayout) rootView.findViewById(R.id.lay_recent_view_products);
        addRecentViewedProduct();

        util = new Utility(getActivity());

        addFooterCMSButtons(rootView);

        mViewPager = (ViewPager) rootView.findViewById(R.id.pager_rotate_banner);
        mViewPager.setAdapter(bannerPagerAdapter);
        addDots(rootView);
        addStaticBanners();

        tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
        tabs.setIndicatorColor(Color.WHITE);
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setIndicatorHeight((int) Utility.convertDpToPixel(7, getActivity()));
        tabs.setBackgroundColor((Color.parseColor(AppController.SECONDARY_COLOR)));
        tabs.setTextColorResource(R.color.tab_text_color);
        tabs.setIndicatorColor(Color.WHITE);

        pager = (ViewPager) rootView.findViewById(R.id.pager_category);
        adapter = new MyPagerAdapter(getFragmentManager());
        pager.setAdapter(adapter);

        circleIndicator = (CirclePageIndicator)rootView.findViewById(R.id.indicator_dots);
        circleIndicator.setRadius(20);
        circleIndicator.setViewPager(mViewPager);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public void addDots(View view) {
        dots = new ArrayList<>();
        LinearLayout dotsLayout = (LinearLayout)view.findViewById(R.id.dots);

        for(int i = 0; i < NUM_PAGES; i++) {
            ImageView dot = new ImageView(getActivity());
            dot.setImageDrawable(getResources().getDrawable(R.drawable.pager_dot_not_selected));
            if(i==0){
                dot.setImageDrawable(getResources().getDrawable(R.drawable.pager_dot_selected));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            dotsLayout.addView(dot, params);

            dots.add(dot);
        }

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                selectDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void selectDot(int idx) {
        Resources res = getResources();
        for (int i = 0; i < NUM_PAGES; i++) {
            int drawableId = (i == idx) ? (R.drawable.pager_dot_selected) : (R.drawable.pager_dot_not_selected);
            Drawable drawable = res.getDrawable(drawableId);
            dots.get(i).setImageDrawable(drawable);
        }
    }

    class BannerPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public BannerPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            int width = util.getScreenWidth();
            int height = width / 2;

            View itemView = mLayoutInflater.inflate(R.layout.rotate_banner_child, container, false);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,height);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.image_rotate_banner);
            imageView.setLayoutParams(params);
            final String imageURI = bannerUrlList.get(position);
            //imageView.setImageResource(mResources[position]);
            //Glide.with(getActivity()).load(bannerUrlList.get(position)).into(imageView);
            imageLoader.displayImage(imageURI,imageView,options);
            container.addView(itemView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < AppController.homeBannersArrayList.size(); j++) {
                        if(AppController.homeBannersArrayList.get(j).banner_image.equals(imageURI)
                                && AppController.homeBannersArrayList.get(j).banner_content_type.equals("products")){
                            String category_id = AppController.homeBannersArrayList.get(j).banner_action_attribute;
                            ProductListFragment productListFragment = new ProductListFragment(category_id, "");
                            updateFragment(productListFragment);
                        }else if(AppController.homeBannersArrayList.get(j).banner_image.equals(imageURI)
                                && AppController.homeBannersArrayList.get(j).banner_content_type.equals("categories")){
                            String category_id = AppController.homeBannersArrayList.get(j).banner_action_attribute;
                            BannerDetailsFragment bannerDetailsFragment = new BannerDetailsFragment(category_id);
                            updateFragment(bannerDetailsFragment);
                        }else if(AppController.homeBannersArrayList.get(j).banner_image.equals(imageURI)
                                && AppController.homeBannersArrayList.get(j).banner_content_type.equals("cms")){

                        }
                    }
                }
            });

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }

    }

    public void addStaticBanners(){
        int width = util.getScreenWidth();
        int height = width / 2;
        for(int i = 0; i<staticBannerUrlList.size(); i++){
            final String imageURI = staticBannerUrlList.get(i);
            ImageView staticBannerImage = new ImageView(getActivity());
            imageLoader.displayImage(imageURI, staticBannerImage, options);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,height);
            layStaticBanner.addView(staticBannerImage, params);

            staticBannerImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < AppController.homeBannersArrayList.size(); j++) {
                        if(AppController.homeBannersArrayList.get(j).banner_image.equals(imageURI)
                                && AppController.homeBannersArrayList.get(j).banner_content_type.equals("products")){
                            String category_id = AppController.homeBannersArrayList.get(j).banner_action_attribute;
                            ProductListFragment productListFragment = new ProductListFragment(category_id, "");
                            updateFragment(productListFragment);
                        }else if(AppController.homeBannersArrayList.get(j).banner_image.equals(imageURI)
                                && AppController.homeBannersArrayList.get(j).banner_content_type.equals("categories")){
                            String category_id = AppController.homeBannersArrayList.get(j).banner_action_attribute;
                            BannerDetailsFragment bannerDetailsFragment = new BannerDetailsFragment(category_id);
                            updateFragment(bannerDetailsFragment);
                        }else if(AppController.homeBannersArrayList.get(j).banner_image.equals(imageURI)
                                && AppController.homeBannersArrayList.get(j).banner_content_type.equals("cms")){

                        }
                    }

                }
            });

        }

    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return AppController.homeCategoryArrayList.get(position).name;
        }

        @Override
        public int getCount() {
            return AppController.homeCategoryArrayList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return ProductCardFragment.newInstance(position);
        }

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
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    public void addRecentViewedProduct(){

        LayoutInflater infalInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ArrayList<Product> productList = AppController.recentViewedProducts;

        if(productList.size() > 0){
            for(int i = 0; i<productList.size(); i++){
                View productView = infalInflater.inflate(R.layout.product_layout, null);
                final Product product = productList.get(i);
                ImageView productImage = (ImageView) productView.findViewById(R.id.img_product_main);
                imageLoader.displayImage(product.product_icon, productImage, options);
                ((TextView) productView.findViewById(R.id.txt_regular_price)).setText(product.product_price_regular);
                ((TextView) productView.findViewById(R.id.txt_product_name)).setText(product.product_name);
                ((RatingBar) productView.findViewById(R.id.rating_product)).setVisibility(View.GONE);

                ImageView productTag = (ImageView) productView.findViewById(R.id.img_product_tag);
                if(product.product_is_new == 1 && product.product_is_salable == 1)
                    productTag.setImageResource(R.drawable.new_sale_tag);
                else if(product.product_is_new == 1 && product.product_is_salable == 0)
                    productTag.setImageResource(R.drawable.new_tag);
                else if(product.product_is_new == 0 && product.product_is_salable == 1)
                    productTag.setImageResource(R.drawable.sale_tag);
                else if(product.product_is_new == 0 && product.product_is_salable == 0)
                    productTag.setVisibility(View.GONE);

                layRelatedProductContainer.addView(productView);

                productView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), ProductDetailActivity.class);
                        i.putExtra("product_id", product.product_entity_id);
                        i.putExtra("product_sku", product.product_sku);
                        i.putExtra("product_name", product.product_name);
                        i.putExtra("product_price_regular", product.product_price_regular);
                        i.putExtra("product_rate", product.product_rating_summery);
                        i.putExtra("category_name", "Home");
                        startActivity(i);
                    }
                });
            }
        }else{
            layRecentViewedProducts.setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getRecentViewedProducts();

    }

    private void getRecentViewedProducts(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(getActivity()).getRecentViewedProducts();

                try{
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.has("products")){

                        AppController.recentViewedProducts = util.parseRelatedProduct(jsonObject.getJSONObject("products"));
                        if(getActivity()!=null){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    layRelatedProductContainer.removeAllViews();
                                    addRecentViewedProduct();
                                }
                            });
                        }

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
                        CMSFragment cmsFragment = new CMSFragment(content.content_action_id);
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

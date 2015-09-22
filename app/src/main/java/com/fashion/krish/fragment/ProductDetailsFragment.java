package com.fashion.krish.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.customview.FixedSpeedScroller;
import com.fashion.krish.customview.FlowLayout;
import com.fashion.krish.model.ProductDetails;
import com.fashion.krish.utility.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

public class ProductDetailsFragment extends Fragment {

    private String product_id;
    private Utility util;
    private boolean isDetailsLoaded = false,isGalleryLoaded = false,isOptionsLoaded = false,isReviewsLoaded = false;
    private ArrayList<ProductDetails> productDetailsArray;

    private ImageView productImage;
    private TextView txtPrice,txtName,txtSKU,txtDelivery,txtPrice2;
    private RatingBar rateProduct;
    private LinearLayout configurationLayout,colorLayout,sizeLayout;
    private TextView txtColor,txtSize;
    private FlowLayout colorFlow,sizeFlow;
    private RelativeLayout rootLayout;

    ImageLoader imageLoader;
    DisplayImageOptions options;

    private int NUM_PAGES = 0;
    private PagerSlidingTabStrip tabs;
    private ViewPager pagerDetail;
    private AutoScrollViewPager pagerGallery;
    private ProductDetailPagerAdapter adapter;
    ArrayList<String> titleArray;
    List<String> pagerGalleryUrlList;
    String product_code="";
    ArrayList<ImageView> dots;
    CirclePageIndicator circleIndicator;

    ProductDetailsFragment(String product_id){
        this.product_id = product_id;
        util = new Utility(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.logo)
                .showImageOnFail(R.drawable.logo)
                .showImageOnLoading(R.drawable.logo).build();

        titleArray = new ArrayList();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_product_details, container, false);
        util = new Utility(getActivity());
        util.showLoadingDialog("Please wait");

        productDetailsArray = new ArrayList();
        pagerGalleryUrlList = new ArrayList<>();
        initViews(rootView);

        getProductDetails(product_id);
        //getProductGallery(product_id);
        getProductOptionsConfig(product_id);
        getProductReviews(product_id);


        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void getProductDetails(final String product_id){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(getActivity()).getProductDetails(product_id);

                try{
                    final JSONObject jsonObject = new JSONObject(response);
                    productDetailsArray = util.parseProductDetails(jsonObject);
                    getProductGallery(product_id,productDetailsArray.get(0));
                    isDetailsLoaded = true;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            if (isAllDataLoaded()) {
                                fillProductData();
                                util.hideLoadingDialog();
                            }

                        }
                    });


                }catch (JSONException e){
                    e.printStackTrace();
                    util.showErrorDialog("Some error has been occurred. Please try again later. ", "Ok", "");
                }

            }
        });
        if(RestClient.isNetworkAvailable(getActivity(), util))
        {
            t.start();
        }
    }

    private void getProductGallery(final String product_id,final ProductDetails productDetails){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(getActivity()).getProductGallery(product_id);

                try{
                    final JSONObject jsonObject = new JSONObject(response);
                    productDetails.productGalleries = util.parseProductGallery(jsonObject,productDetails);
                    isGalleryLoaded = true;
                    getImageURIForProduct(product_code);
                    if(isAllDataLoaded()){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fillProductData();
                                util.hideLoadingDialog();
                            }
                        });
                    }


                }catch (JSONException e){
                    e.printStackTrace();
                    util.showErrorDialog("Some error has been occurred. Please try again later. ", "Ok", "");
                }

            }
        });
        if(RestClient.isNetworkAvailable(getActivity(), util))
        {
            t.start();
        }
    }

    private void getProductOptionsConfig(final String product_id){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(getActivity()).getProductOptionsConfig(product_id);

                try{
                    final JSONObject jsonObject = new JSONObject(response);
                    isOptionsLoaded = true;
                    if(isAllDataLoaded()){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fillProductData();
                                util.hideLoadingDialog();
                            }
                        });
                    }


                }catch (JSONException e){
                    e.printStackTrace();
                    util.showErrorDialog("Some error has been occurred. Please try again later. ", "Ok", "");
                }

            }
        });
        if(RestClient.isNetworkAvailable(getActivity(), util))
        {
            t.start();
        }
    }

    private void getProductReviews(final String product_id){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(getActivity()).getProductReviews(product_id);

                try{
                    final JSONObject jsonObject = new JSONObject(response);
                    isReviewsLoaded = true;
                    if(isAllDataLoaded()){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fillProductData();
                                util.hideLoadingDialog();
                            }
                        });
                    }


                }catch (JSONException e){
                    e.printStackTrace();
                    util.showErrorDialog("Some error has been occurred. Please try again later. ", "Ok", "");
                }

            }
        });
        if(RestClient.isNetworkAvailable(getActivity(), util))
        {
            t.start();
        }
    }

    public boolean isAllDataLoaded(){
        if(isDetailsLoaded && isGalleryLoaded && isOptionsLoaded && isReviewsLoaded){
            return true;
        }else {
            return false;
        }
    }

    public void initViews(View view){

        rootLayout = (RelativeLayout) view.findViewById(R.id.root_productDetail);
        rootLayout.setVisibility(View.GONE);
        productImage = (ImageView)view.findViewById(R.id.img_product_details);
        txtName = (TextView) view.findViewById(R.id.txt_product_name);
        txtPrice = (TextView) view.findViewById(R.id.txt_product_price);
        txtPrice2 = (TextView) view.findViewById(R.id.txt_product_price1);
        txtSKU = (TextView) view.findViewById(R.id.txt_product_sku);
        txtDelivery = (TextView) view.findViewById(R.id.txt_delivery_within);
        rateProduct = (RatingBar) view.findViewById(R.id.rate_product_detail);

        configurationLayout = (LinearLayout) view.findViewById(R.id.lay_configuration);
        sizeLayout = (LinearLayout) view.findViewById(R.id.lay_size_selection);
        colorLayout = (LinearLayout) view.findViewById(R.id.lay_color_selection);
        txtColor = (TextView) view.findViewById(R.id.txt_color);
        txtSize = (TextView) view.findViewById(R.id.txt_size);
        colorFlow = (FlowLayout) view.findViewById(R.id.flow_color);
        sizeFlow = (FlowLayout) view.findViewById(R.id.flow_size);

        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.product_details_tab);
        tabs.setIndicatorColor(Color.WHITE);
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setIndicatorHeight((int) Utility.convertDpToPixel(7, getActivity()));
        tabs.setBackgroundColor(Color.parseColor(AppController.SECONDARY_COLOR));
        tabs.setIndicatorColor(Color.WHITE);

        pagerDetail = (ViewPager) view.findViewById(R.id.product_details_pager);
        pagerGallery = (AutoScrollViewPager) view.findViewById(R.id.pager_product_gallery);
        dots = new ArrayList<>();
        circleIndicator = (CirclePageIndicator)view.findViewById(R.id.titles);
        circleIndicator.setRadius(15);

    }

    public void fillProductData(){

        rootLayout.setVisibility(View.VISIBLE);
        ProductDetails productDetails = productDetailsArray.get(0);

        txtPrice.setText(productDetails.product_price_regular);
        txtSKU.setText("SKU: " + productDetails.product_sku);
        txtName.setText(productDetails.product_name);

        if(productDetails.product_has_gallery == 0){

            productImage.setVisibility(View.VISIBLE);
            pagerGallery.setVisibility(View.GONE);

        }else{

            productImage.setVisibility(View.GONE);
            pagerGallery.setVisibility(View.VISIBLE);
            GalleryPagerAdapter galleryPagerAdapter = new GalleryPagerAdapter(getActivity());
            pagerGallery.setAdapter(galleryPagerAdapter);
            circleIndicator.setViewPager(pagerGallery);
            pagerGallery.setInterval(3000);
            pagerGallery.startAutoScroll();
            pagerGallery.setCycle(true);
            startAutoRotate();

        }

        if(productDetails.product_entity_type.equals("configurable")){
            configurationLayout.setVisibility(View.VISIBLE);
        }else{
            configurationLayout.setVisibility(View.GONE);
        }


        if(productDetails.product_expected_delivery.length() != 0) {
            txtDelivery.setVisibility(View.VISIBLE);
            txtDelivery.setText("Delivery Within: " + productDetails.product_expected_delivery);
        }

        if(productDetails.product_price_special.equals("")){
            txtPrice.setText(productDetails.product_price_regular);
        }else{
            txtPrice.setText(productDetails.product_price_special);
            txtPrice2.setVisibility(View.VISIBLE);
            txtPrice2.setText("PRP: "+productDetails.product_price_regular);
        }

        imageLoader.displayImage(productDetails.product_icon, productImage, options);
        rateProduct.setRating((float) productDetails.product_rating_summery);

        if(productDetails.product_desc.length() > 0){
            titleArray.add("DESCRIPTION");
            NUM_PAGES = NUM_PAGES + 1;
        }

        if(productDetails.product_additional_attribute.size() > 0){
            titleArray.add("ADDITIONAL INFO");
            NUM_PAGES = NUM_PAGES + 1;
        }

        if(productDetails.product_review_count > 0){
            titleArray.add("REVIEWS");
            NUM_PAGES = NUM_PAGES + 1;
        }
        adapter = new ProductDetailPagerAdapter(getFragmentManager());
        pagerDetail.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pagerDetail.setPageMargin(pageMargin);
        tabs.setViewPager(pagerDetail);

        util.hideLoadingDialog();

    }

    public class ProductDetailPagerAdapter extends FragmentStatePagerAdapter {

        public ProductDetailPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleArray.get(position);
        }

        @Override
        public int getCount() {
            return titleArray.size();
        }

        @Override
        public Fragment getItem(int position) {
            ProductDetails productDetails = productDetailsArray.get(0);
            return ProductDetailsPagerFragment.newInstance(position,productDetails,titleArray.get(position));
        }

    }

    class GalleryPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public GalleryPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return pagerGalleryUrlList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.rotate_banner_child, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.image_rotate_banner);
            //imageView.setImageResource(mResources[position]);
            //Glide.with(getActivity()).load(bannerUrlList.get(position)).into(imageView);

            imageLoader.displayImage(pagerGalleryUrlList.get(position),imageView,options);
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    public void getImageURIForProduct(String product_code){
        for(ProductDetails.ProductGallery gallery : productDetailsArray.get(0).productGalleries){
            if(gallery.image_code.equals(product_code))
                pagerGalleryUrlList.add(gallery.image_url_big);
            //something here
        }

    }

    public void startAutoRotate(){

        try {
            Field mScroller;
            Interpolator sInterpolator = new AccelerateInterpolator();

            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(pagerGallery.getContext(), sInterpolator);
            // scroller.setFixedDuration(5000);
            mScroller.set(pagerGallery, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }

        /*
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                int currentPage = pagerGallery.getCurrentItem();
                if ( pagerGallery.getCurrentItem() == pagerGalleryUrlList.size() - 1) {
                    //currentPage = 0;
                    pagerGallery.setCurrentItem(0);
                }
                pagerGallery.setCurrentItem(currentPage++, true);
            }
        };

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(Update);
            }
        }, 100);*/
    }

}

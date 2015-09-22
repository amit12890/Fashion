package com.fashion.krish.activity;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.format.Time;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.astuetz.PagerSlidingTabStrip;
import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.customview.CircleView;
import com.fashion.krish.customview.FixedSpeedScroller;
import com.fashion.krish.customview.FlowLayout;
import com.fashion.krish.fragment.ProductDetailsPagerFragment;
import com.fashion.krish.model.Product;
import com.fashion.krish.model.ProductDetails;
import com.fashion.krish.options.TypeCheckBoxView;
import com.fashion.krish.options.TypeDateTimeView;
import com.fashion.krish.options.TypeDateView;
import com.fashion.krish.options.TypeLinkView;
import com.fashion.krish.options.TypeRadioView;
import com.fashion.krish.options.TypeSelectionView;
import com.fashion.krish.options.TypeTextView;
import com.fashion.krish.options.TypeTimeView;
import com.fashion.krish.utility.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.TimePickerDialog;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.CompoundButton;
import com.rey.material.widget.EditText;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.SnackBar;
import com.rey.material.widget.Spinner;
import com.soundcloud.android.crop.Crop;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;


public class ProductDetailActivity extends ActionBarActivity implements View.OnClickListener {

    private String product_id;
    private Utility util;
    private boolean isDetailsLoaded = false,isGalleryLoaded = false,isReviewsLoaded = false;
    private ArrayList<ProductDetails> productDetailsArray;

    private ImageView productImage,imgBack,productTag ;
    private TextView txtPrice,txtName,txtSKU,txtDelivery,txtPrice2,txtTitle;
    private RatingBar rateProduct;
    private LinearLayout configurationLayout,colorLayout,sizeLayout,layBack,layRelatedProducts,layRelatedProductContainer;
    private TextView txtColor,txtSize;
    private FlowLayout colorFlow,sizeFlow;
    private RelativeLayout rootLayout;
    private Button btnShare,btnAddToCart,btnBuyNow;
    private RelativeLayout layShare,layCart,layBuy;
    private TextView txtColorError,txtSizeError;
    private MaterialDialog optionDialog;

    ImageLoader imageLoader;
    DisplayImageOptions options;

    private int NUM_PAGES = 0;
    private PagerSlidingTabStrip tabs;
    private ViewPager pagerDetail;
    private AutoScrollViewPager pagerGallery;
    private ProductDetailPagerAdapter adapter;
    ArrayList<String> titleArray;
    List<String> pagerGalleryUrlList;
    GalleryPagerAdapter galleryPagerAdapter;
    String product_code="";
    ArrayList<ImageView> dots;
    CirclePageIndicator circleIndicator;
    private HashMap<String,String> sizeMap;
    String selected_color="",selected_size="",entity_type="";
    private boolean isColorRequired,isSizeRequired,hasOptions=false,hasProduct=false,hasLinks = false;
    int image_max_x,image_max_y;
    int date,month,year,minute,second,hour;
    private SnackBar mSnakebar;
    public static TextView dialog_price;
    public static String total_price="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.fragment_product_details);

        Utility.changeStatusBarColor(ProductDetailActivity.this);

        product_id = getIntent().getStringExtra("product_id");
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true)
                .resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.placeholder)
                .showImageOnFail(R.drawable.placeholder)
                .showImageOnLoading(R.drawable.placeholder).build();

        sizeMap = new HashMap<>();

        titleArray = new ArrayList();

        util = new Utility(ProductDetailActivity.this);
        util.showLoadingDialog("Please wait");

        productDetailsArray = new ArrayList();
        pagerGalleryUrlList = new ArrayList<>();

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        date  = today.monthDay;
        month = today.month;
        year = today.year;
        minute = today.minute;
        second = today.second;
        hour = today.hour;


        initViews();

        getProductDetails(product_id);


    }

    private void getProductDetails(final String product_id){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(ProductDetailActivity.this).getProductDetails(product_id);

                if(response.equals("error")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.hideLoadingDialog();
                            util.showErrorDialog("Some error has occurred. Please try again later", "OK", "");
                            return;
                        }
                    });
                }

                try{
                    final JSONObject jsonObject = new JSONObject(response);
                    productDetailsArray = util.parseProductDetails(jsonObject);
                    getProductGallery(product_id,productDetailsArray.get(0));
                    isDetailsLoaded = true;

                    runOnUiThread(new Runnable() {
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
        if(RestClient.isNetworkAvailable(ProductDetailActivity.this, util))
        {
            t.start();
        }
    }

    private void getProductGallery(final String product_id,final ProductDetails productDetails){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(ProductDetailActivity.this).getProductGallery(product_id);

                try{
                    final JSONObject jsonObject = new JSONObject(response);
                    productDetails.productGalleries = util.parseProductGallery(jsonObject,productDetails);
                    isGalleryLoaded = true;
                    getImageURIForProduct(product_code);
                    getProductReviews(product_id);
                    if(isAllDataLoaded()){
                        runOnUiThread(new Runnable() {
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
        if(RestClient.isNetworkAvailable(ProductDetailActivity.this, util))
        {
            t.start();
        }
    }

    private void getProductReviews(final String product_id){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(ProductDetailActivity.this).getProductReviews(product_id);

                try{
                    final JSONObject jsonObject = new JSONObject(response);
                    isReviewsLoaded = true;
                    if(isAllDataLoaded()){
                        runOnUiThread(new Runnable() {
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
        if(RestClient.isNetworkAvailable(ProductDetailActivity.this, util))
        {
            t.start();
        }
    }

    public boolean isAllDataLoaded(){
        if(isDetailsLoaded && isGalleryLoaded && isReviewsLoaded){
            return true;
        }else {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void initViews(){

        rootLayout = (RelativeLayout) findViewById(R.id.root_productDetail);
        ((RelativeLayout)findViewById(R.id.lay_title)).setBackgroundColor(Color.parseColor(AppController.PRIMARY_COLOR));
        //rootLayout.setVisibility(View.GONE);
        productImage = (ImageView)findViewById(R.id.img_product_details);
        txtName = (TextView) findViewById(R.id.txt_product_name);
        txtName.setText(getIntent().getStringExtra("product_name"));
        txtPrice = (TextView) findViewById(R.id.txt_product_price);
        txtPrice.setText(getIntent().getStringExtra("product_price_regular"));
        txtPrice2 = (TextView) findViewById(R.id.txt_product_price1);
        txtSKU = (TextView) findViewById(R.id.txt_product_sku);
        txtSKU.setText("SKU: "+getIntent().getStringExtra("product_sku"));
        txtDelivery = (TextView) findViewById(R.id.txt_delivery_within);
        rateProduct = (RatingBar) findViewById(R.id.rate_product_detail);
        rateProduct.setRating(getIntent().getIntExtra("product_rate", 0));

        txtTitle = (TextView)findViewById(R.id.txt_title);
        txtTitle.setText(getIntent().getStringExtra("category_name"));
        imgBack = (ImageView) findViewById(R.id.img_back);

        productTag = (ImageView) findViewById(R.id.img_product_tag);
        layRelatedProducts = (LinearLayout) findViewById(R.id.lay_related_products);
        layRelatedProductContainer = (LinearLayout) findViewById(R.id.layout_product_container);
        layBack = (LinearLayout) findViewById(R.id.lay_back);
        layBack.setOnClickListener(this);
        configurationLayout = (LinearLayout) findViewById(R.id.lay_configuration);
        sizeLayout = (LinearLayout) findViewById(R.id.lay_size_selection);
        colorLayout = (LinearLayout) findViewById(R.id.lay_color_selection);
        txtColor = (TextView) findViewById(R.id.txt_color);
        txtSize = (TextView) findViewById(R.id.txt_size);
        colorFlow = (FlowLayout) findViewById(R.id.flow_color);
        sizeFlow = (FlowLayout) findViewById(R.id.flow_size);

        btnShare = (Button) findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);
        btnShare.setBackground(util.getPrimaryRippleDrawable());
        btnAddToCart = (Button) findViewById(R.id.btn_add_to_cart);
        btnAddToCart.setOnClickListener(this);
        btnAddToCart.setBackground(util.getPrimaryRippleDrawable());
        btnBuyNow = (Button) findViewById(R.id.btn_buy);
        btnBuyNow.setOnClickListener(this);

        layShare = (RelativeLayout) findViewById(R.id.lay_bottom_share);
        layShare.setBackgroundColor(Color.parseColor(AppController.PRIMARY_COLOR));
        layShare.setOnClickListener(this);
        layCart = (RelativeLayout) findViewById(R.id.lay_bottom_cart);
        layCart.setOnClickListener(this);
        layCart.setBackgroundColor(Color.parseColor(AppController.PRIMARY_COLOR));
        layBuy = (RelativeLayout) findViewById(R.id.lay_bottom_buy);
        layBuy.setOnClickListener(this);
        layBuy.setBackgroundColor(Color.parseColor(AppController.SECONDARY_COLOR));

        tabs = (PagerSlidingTabStrip) findViewById(R.id.product_details_tab);
        tabs.setIndicatorColor(Color.WHITE);
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setIndicatorHeight((int) Utility.convertDpToPixel(7, ProductDetailActivity.this));
        tabs.setBackgroundColor(Color.parseColor(AppController.SECONDARY_COLOR));
        tabs.setTextColorResource(R.color.tab_text_color);
        tabs.setIndicatorColor(Color.WHITE);

        pagerDetail = (ViewPager) findViewById(R.id.product_details_pager);
        pagerGallery = (AutoScrollViewPager) findViewById(R.id.pager_product_gallery);
        dots = new ArrayList<>();
        circleIndicator = (CirclePageIndicator)findViewById(R.id.titles);
        circleIndicator.setRadius(10);

        txtColorError = (TextView) findViewById(R.id.txt_color_error);
        txtSizeError = (TextView) findViewById(R.id.txt_size_error);

        mSnakebar = (SnackBar) findViewById(R.id.cart_snake_bar);

    }

    public void fillProductData() {

        rootLayout.setVisibility(View.VISIBLE);
        final ProductDetails productDetails = productDetailsArray.get(0);

        total_price = productDetails.product_price_regular;
        txtPrice.setText(productDetails.product_price_regular);
        txtSKU.setText("SKU: " + productDetails.product_sku);
        txtName.setText(productDetails.product_name);

        if(productDetails.relatedProducts.size() > 0 ){
            layRelatedProducts.setVisibility(View.VISIBLE);
            addRelatedProductView();
        }
        else
            layRelatedProducts.setVisibility(View.GONE);

        if(productDetails.product_has_gallery == 0){

            productImage.setVisibility(View.VISIBLE);
            pagerGallery.setVisibility(View.GONE);

        }else{

            productImage.setVisibility(View.GONE);
            pagerGallery.setVisibility(View.VISIBLE);
            galleryPagerAdapter = new GalleryPagerAdapter(ProductDetailActivity.this);
            pagerGallery.setAdapter(galleryPagerAdapter);
            circleIndicator.setViewPager(pagerGallery);
            pagerGallery.setInterval(3000);
            pagerGallery.startAutoScroll();
            pagerGallery.setCycle(true);
            startAutoRotate();

        }

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductDetailActivity.this,ProductGalleryActivity.class);
                i.putExtra("image_uri",productDetails.product_icon);
                i.putExtra("product_name", productDetails.product_name);
                //i.putStringArrayListExtra("image_uri_array", (ArrayList<String>) pagerGalleryUrlList);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);
            }
        });

        entity_type = productDetails.product_entity_type;
        if(entity_type.equals("configurable")){
            configurationLayout.setVisibility(View.VISIBLE);
            ArrayList<String> colorList = new ArrayList<>();
            ArrayList<ProductDetails.ProductOptions> productOptions = productDetails.productOptions;
            ArrayList<ProductDetails.ProductOptionsValue> productOptionsValuesColor = null;

            for(ProductDetails.ProductOptions options : productOptions){
                if(options.option_code.contains("option")){
                    hasOptions = true;
                }
                if(options.option_attr_code.equals("color")){
                    productOptionsValuesColor = options.productOptionsValues;
                    if(options.option_is_required.equals("1"))
                        isColorRequired = true;
                    else
                        isColorRequired = false;

                }

                if(options.option_attr_code.equals("size")){
                    for (int i =0; i<options.productOptionsValues.size(); i++){
                        String code = options.productOptionsValues.get(i).value_code;
                        String label = options.productOptionsValues.get(i).value_label;
                        sizeMap.put(code,label);
                    }
                    if(options.option_is_required.equals("1"))
                        isSizeRequired = true;
                    else
                        isSizeRequired = false;
                }

            }

            addColorLayout(productOptionsValuesColor);
            if(sizeMap.size() > 0){
                addSizeLayout(sizeMap);
            }else{
                sizeFlow.setVisibility(View.GONE);
            }


        }else if(entity_type.equals("grouped")){
            configurationLayout.setVisibility(View.GONE);
            ArrayList<ProductDetails.ProductOptions> productOptions = productDetails.productOptions;
            for(ProductDetails.ProductOptions options : productOptions){
                if(options.option_code.contains("super_group"))
                    hasProduct = true;
                }
        }else if (entity_type.equals("downloadable")){
            configurationLayout.setVisibility(View.GONE);
            if(productDetails.productLinks.size() > 0){
                hasLinks = true;
            }else{
                hasLinks = false;
            }
        }
        else {
            configurationLayout.setVisibility(View.GONE);
            ArrayList<ProductDetails.ProductOptions> productOptions = productDetails.productOptions;
            for(ProductDetails.ProductOptions options : productOptions){
                if(options.option_code.contains("option"))
                    hasOptions = true;
            }
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

        if(productDetails.product_entity_type.equals("downloadable")){
            if(productDetails.productSamples.size() > 0){
                titleArray.add(productDetails.productSamples.get(0).sample_label);
                NUM_PAGES = NUM_PAGES + 1;
            }

        }

        adapter = new ProductDetailPagerAdapter(getFragmentManager());
        pagerDetail.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pagerDetail.setPageMargin(pageMargin);
        tabs.setViewPager(pagerDetail);


        if(productDetails.product_is_new == 1 && productDetails.product_is_salable == 1)
            productTag.setImageResource(R.drawable.new_sale_tag);
        else if(productDetails.product_is_new == 1 && productDetails.product_is_salable == 0)
            productTag.setImageResource(R.drawable.new_tag);
        else if(productDetails.product_is_new == 0 && productDetails.product_is_salable == 1)
            productTag.setImageResource(R.drawable.sale_tag);
        else if(productDetails.product_is_new == 0 && productDetails.product_is_salable == 0)
            productTag.setVisibility(View.GONE);

        util.hideLoadingDialog();

    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_add_to_cart:

                    if(entity_type.equals("configurable")){
                        if(selected_color.length() == 0 && isColorRequired){
                            txtColorError.setVisibility(View.VISIBLE);
                            return;
                        }else{
                            txtColorError.setVisibility(View.GONE);
                        }

                        if(selected_size.length() == 0 && isSizeRequired){
                            txtSizeError.setVisibility(View.VISIBLE);
                            return;
                        }else {
                            txtSizeError.setVisibility(View.GONE);
                        }
                    }

                    if(hasOptions ||  hasLinks){
                        showOptionDialog();
                    }else if(hasProduct){
                        showGroupedOptionDialog();
                    }else{

                        //mSnakebar.text("test").duration(5000).show();
                        mSnakebar.text("Product Added in the Cart")
                                .singleLine(true)
                                .actionText("Close")
                                .actionClickListener(new SnackBar.OnActionClickListener() {
                                    @Override
                                    public void onActionClick(SnackBar sb, int actionId) {
                                        sb.dismiss();
                                    }
                                })
                                .duration(5000)
                                .show();
                    }



                    break;

                case R.id.lay_back:
                    finish();

                    break;
            }

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
            return ProductDetailsPagerFragment.newInstance(position, productDetails,titleArray.get(position));
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
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ProductDetailActivity.this,ProductGalleryActivity.class);
                    i.putExtra("image_uri",productDetailsArray.get(0).product_icon);
                    i.putExtra("product_name",productDetailsArray.get(0).product_name);
                    i.putStringArrayListExtra("image_uri_array", (ArrayList<String>) pagerGalleryUrlList);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);
                }
            });
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

    }

    public void addColorLayout(final ArrayList<ProductDetails.ProductOptionsValue> productOptionsValues){

        int radius = (int) Utility.convertDpToPixel((float) 12, ProductDetailActivity.this);
        int margin = (int) Utility.convertDpToPixel((float) 2, ProductDetailActivity.this);
        int padding = (int) Utility.convertDpToPixel((float)2,ProductDetailActivity.this);

        for(int i =0 ; i<productOptionsValues.size();i++) {
            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(radius*2, radius*2);
            params.setMargins(margin, margin, margin, margin);
            final CircleView circleImageView = new CircleView(ProductDetailActivity.this);
            circleImageView.setPadding(padding, padding, padding, padding);
            circleImageView.setBorderColor(Color.parseColor("#ffffff"));
            circleImageView.setLayoutParams(params);
            circleImageView.setCircleRadius(radius);
            circleImageView.setTag(productOptionsValues.get(i).value_code);
            circleImageView.setViewColor(Color.parseColor(util.getColorMap().get(productOptionsValues.get(i).value_label)));
            colorFlow.addView(circleImageView);
            final int position = i;
            final String current_code = productOptionsValues.get(i).value_code;
            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap bmp3 = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_tick_white);

                    selected_color = v.getTag().toString();
                    //sizeFlow.removeAllViews();
                    try {

                        String relation = productOptionsValues.get(position).value_relation;
                        if (relation.length() > 0) {
                            sizeMap.clear();
                            JSONObject relationObj = new JSONObject(relation);
                            if (relationObj.get("value") instanceof JSONArray) {
                                JSONArray jArray = relationObj.getJSONArray("value");
                                for (int j = 0; j < jArray.length(); j++) {
                                    String code = jArray.getJSONObject(j).getJSONObject("@attributes").get("code").toString();
                                    String value = jArray.getJSONObject(j).getJSONObject("@attributes").get("label").toString();
                                    sizeMap.put(code, value);

                                }
                            } else {
                                String code = relationObj.getJSONObject("@attributes").get("code").toString();
                                String value = relationObj.getJSONObject("@attributes").get("label").toString();
                                sizeMap.put(code, value);
                            }
                            sizeFlow.removeAllViews();
                            addSizeLayout(sizeMap);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int k = 0; k < productOptionsValues.size(); k++) {
                        if (productOptionsValues.get(k).value_code.equals(current_code)) {
                            circleImageView.drawBitmap(bmp3);
                        } else {
                            ((CircleView) colorFlow.findViewWithTag(productOptionsValues.get(k).value_code)).drawBitmap(null);
                        }
                    }
                    ArrayList<String> subProductsArray = productOptionsValues.get(position).sub_products;
                    pagerGalleryUrlList.clear();
                    for (int i = 0; i < subProductsArray.size(); i++) {
                        if (productDetailsArray.get(0).productGalleries.size() > 0) {

                            for (ProductDetails.ProductGallery gallery : productDetailsArray.get(0).productGalleries) {
                                if (gallery.image_code.equals(subProductsArray.get(i))) {
                                    pagerGalleryUrlList.add(gallery.image_url_big);
                                }
                            }

                        }
                    }
                    //pagerGallery.setAdapter(galleryPagerAdapter);
                    //galleryPagerAdapter.notifyDataSetChanged();

                }
            });


        }

    }

    public void addSizeLayout(final HashMap<String,String> sizeMap){


        int margin = (int) Utility.convertDpToPixel((float) 4, ProductDetailActivity.this);
        int padding = (int) Utility.convertDpToPixel((float) 2, ProductDetailActivity.this);
        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                FlowLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, margin, margin, margin);
        for (final HashMap.Entry<String,String> entry : sizeMap.entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue();

            final TextView txtSize = new TextView(ProductDetailActivity.this);
            txtSize.setText(value);
            txtSize.setTag(key);
            txtSize.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            txtSize.setTextColor(Color.parseColor("#000000"));
            txtSize.setBackgroundColor(Color.WHITE);
            txtSize.setPadding(padding * 5, padding, padding * 5, padding);
            txtSize.setLayoutParams(params);
            sizeFlow.addView(txtSize);
            txtSize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selected_size = v.getTag().toString();
                    for (HashMap.Entry<String,String> entry1 : sizeMap.entrySet()) {
                        String key = entry1.getKey();
                        if(v.getTag().equals(key)){
                            txtSize.setTextColor(Color.parseColor("#ffffff"));
                            txtSize.setBackgroundColor(Color.parseColor("#E91E63"));

                        }else{
                            ((TextView)sizeFlow.findViewWithTag(key)).setTextColor(Color.parseColor("#000000"));
                            ((TextView)sizeFlow.findViewWithTag(key)).setBackgroundColor(Color.WHITE);
                        }

                    }
                }
            });
        }

    }

    public void showGroupedOptionDialog(){

        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflate.inflate(R.layout.dialog_product_options, null);
        LinearLayout dialog_container_lay = (LinearLayout) view.findViewById(R.id.dialog_container);
        optionDialog = new MaterialDialog.Builder(ProductDetailActivity.this)
                .cancelable(true)
                .customView(view, false)
                .build();

        ArrayList<ProductDetails.ProductOptions> options = productDetailsArray.get(0).productOptions;

        for (int i = 0; i < options.size(); i++) {
            View innerView = inflate.inflate(R.layout.layout_product, null);

            ((TextView) innerView.findViewById(R.id.txt_product_name)).setText(options.get(i).option_label);
            ((TextView) innerView.findViewById(R.id.txt_regular_price)).setText(options.get(i).option_formatted_price);
            ImageView img = (ImageView) innerView.findViewById(R.id.img_product_main);

            imageLoader.displayImage(options.get(i).option_icon, img, this.options);
            dialog_container_lay.addView(innerView);
        }

        optionDialog.show();
    }

    public void showOptionDialog(){

        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflate.inflate(R.layout.dialog_product_options, null);

        ((TextView) view.findViewById(R.id.txt_dialog_title)).setBackgroundColor(Color.parseColor(AppController.SECONDARY_COLOR));
        ((LinearLayout) view.findViewById(R.id.lay_dlg_add_to_cart)).setBackgroundColor(Color.parseColor(AppController.SECONDARY_COLOR));
        ((LinearLayout) view.findViewById(R.id.lay_dlg_close)).setBackgroundColor(Color.WHITE);
        final LinearLayout dialog_container_lay = (LinearLayout) view.findViewById(R.id.dialog_container);
        final ScrollView dialog_scroll = (ScrollView) view.findViewById(R.id.dialog_scroll);

        optionDialog = new MaterialDialog.Builder(ProductDetailActivity.this)
                .cancelable(true)
                .customView(view, false)
                .build();

        optionDialog.getBuilder().callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);

            }


        });

        final ArrayList<ProductDetails.ProductOptions> options = productDetailsArray.get(0).productOptions;

        if(productDetailsArray.get(0).product_entity_type.equals("downloadable")){
            if(productDetailsArray.get(0).productLinks.size() > 0){
                new TypeLinkView(ProductDetailActivity.this, productDetailsArray.get(0).productLinks, dialog_container_lay);
            }

        }

        for (int i = 0; i < options.size(); i++) {
            if(options.get(i).option_type.equals("select") && options.get(i).option_code.contains("option")){
                new TypeSelectionView(ProductDetailActivity.this, options.get(i),
                        dialog_container_lay,productDetailsArray.get(0).product_entity_type);
            }

            if(options.get(i).option_type.equals("text") && options.get(i).option_code.contains("option")){
                new TypeTextView(ProductDetailActivity.this, options.get(i),dialog_container_lay);

            }

            if(options.get(i).option_type.equals("radio") && options.get(i).option_code.contains("option")){
                new TypeRadioView(ProductDetailActivity.this, options.get(i),
                        dialog_container_lay,productDetailsArray.get(0).product_entity_type);
            }

            if(options.get(i).option_type.equals("checkbox") && options.get(i).option_code.contains("option")){
                new TypeCheckBoxView(ProductDetailActivity.this, options.get(i),dialog_container_lay);
            }

            if(options.get(i).option_type.equals("file") && options.get(i).option_code.contains("option")){
                addFileView(dialog_container_lay, options.get(i));
            }

            if(options.get(i).option_type.equals("time") && options.get(i).option_code.contains("option")){
                new TypeTimeView(ProductDetailActivity.this, options.get(i), dialog_container_lay);
            }

            if(options.get(i).option_type.equals("date") && options.get(i).option_code.contains("option")){
                new TypeDateView(ProductDetailActivity.this, options.get(i), dialog_container_lay);
            }

            if(options.get(i).option_type.equals("date_time") && options.get(i).option_code.contains("option")){
                new TypeDateTimeView(ProductDetailActivity.this, options.get(i), dialog_container_lay);
            }

        }
        Display mDisplay = getWindowManager().getDefaultDisplay();
        final int height  = mDisplay.getHeight();
        final int height_to_compare = (int) Utility.convertDpToPixel(400, ProductDetailActivity.this);
        final int height_to_deduct = (int) Utility.convertDpToPixel(220, ProductDetailActivity.this);
        android.os.Handler h = new android.os.Handler();
        h.post(new Runnable() {
            @Override
            public void run() {

                if (dialog_scroll.getMeasuredWidth() > (height - height_to_compare)) {
                    dialog_scroll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height - height_to_deduct)));
                }

            }
        });

        dialog_price = (TextView) view.findViewById(R.id.txt_final_price);
        if(productDetailsArray.get(0).product_price_special.equals("")){
            dialog_price.setText(productDetailsArray.get(0).product_price_regular);
        }else{
            dialog_price.setText(productDetailsArray.get(0).product_price_special);
        }

        Button btnAddToCart = (Button) view.findViewById(R.id.btn_dlg_add_to_cart);
        btnAddToCart.setBackgroundDrawable(util.getSecondaryRippleDrawable());
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValidated = false;
                for (int i = 0; i < options.size(); i++) {
                    if(options.get(i).option_type.equals("text") && options.get(i).option_code.contains("option")
                            && options.get(i).option_is_required.equals("1")){
                        isValidated = TypeTextView.isValidated(options.get(i).option_code);
                    }else if(options.get(i).option_type.equals("checkbox") && options.get(i).option_code.contains("option")
                            && options.get(i).option_is_required.equals("1")){
                        isValidated = TypeCheckBoxView.isValidated(options.get(i).option_code);
                    }else if(options.get(i).option_type.equals("radio") && options.get(i).option_code.contains("option")
                            && options.get(i).option_is_required.equals("1")){
                        isValidated = TypeRadioView.isValidated(options.get(i).option_code);
                    }else if(options.get(i).option_type.equals("select") && options.get(i).option_code.contains("option")
                            && options.get(i).option_is_required.equals("1")){
                        isValidated = TypeSelectionView.isValidated(options.get(i).option_code);
                    }else if(options.get(i).option_type.equals("time") && options.get(i).option_code.contains("option")
                            && options.get(i).option_is_required.equals("1")){
                        isValidated = TypeTimeView.isValidated(options.get(i).option_code);
                    }else if(options.get(i).option_type.equals("date") && options.get(i).option_code.contains("option")
                            && options.get(i).option_is_required.equals("1")){
                        isValidated = TypeDateView.isValidated(options.get(i).option_code);
                    }else if(options.get(i).option_type.equals("date_time") && options.get(i).option_code.contains("option")
                            && options.get(i).option_is_required.equals("1")){
                        isValidated = TypeDateTimeView.isValidated(options.get(i).option_code);
                    }

                }

                if(productDetailsArray.get(0).product_entity_type.equals("downloadable")){
                    if(productDetailsArray.get(0).productLinks.size() > 0){
                        if(productDetailsArray.get(0).productLinks.get(0).link_is_required.equals("1"))
                            isValidated = TypeLinkView.isValidated();

                    }

                }

                if(isValidated){
                    mSnakebar.text("Product Added in the Cart")
                            .singleLine(true)
                            .actionText("Close")
                            .actionClickListener(new SnackBar.OnActionClickListener() {
                                @Override
                                public void onActionClick(SnackBar sb, int actionId) {
                                    sb.dismiss();
                                }
                            })
                            .duration(5000)
                            .show();
                    optionDialog.dismiss();
                }else{
                    Toast.makeText(ProductDetailActivity.this,"Not Validated",Toast.LENGTH_SHORT).show();
                }

            }
        });
        Button btnClose = (Button) view.findViewById(R.id.btn_dlg_close);
        btnClose.setBackground(util.getSecondaryRippleDrawable());
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionDialog.dismiss();
            }
        });



        optionDialog.show();
    }

    public void addFileView(LinearLayout container,final ProductDetails.ProductOptions productOptions) {
        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View fileRootview = inflate.inflate(R.layout.layout_file, null);
        ((LinearLayout) fileRootview.findViewById(R.id.lay_file_chooser)).
                setBackgroundColor(Color.parseColor(AppController.SECONDARY_COLOR));
        String[] array = {"Take Photo","Choose From Library"};

        final MaterialDialog fileChooserOptionDialog = new MaterialDialog.Builder(this)
                .title("Choose Picture")
                .items(array)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        if(which == 1){
                            Crop.pickImage(ProductDetailActivity.this);
                        }
                        return true;
                    }
                })
                .positiveText("Choose")
                .build();

        String price = productOptions.option_formatted_price;
        if(price.length() > 0){
            price = " + " + price;
        }

        TextView txt = (TextView) fileRootview.findViewById(R.id.txt_file_title);
        txt.setText(productOptions.option_label + price);
        if (productOptions.option_is_required.equals("1"))
            txt.append(" *");

        //LinearLayout lay_ = (LinearLayout) fileRootview.findViewById(R.id.lay_file_chooser_container);

        Button btnFileChooser = (Button) fileRootview.findViewById(R.id.btn_file_chooser);
        btnFileChooser.setBackgroundColor(Color.parseColor(AppController.SECONDARY_COLOR));
        TextView txtNoFile = (TextView) fileRootview.findViewById(R.id.txt_no_file);

        btnFileChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileChooserOptionDialog.show();
            }
        });
        if (productOptions.option_image_size_x.length() > 0) {
            TextView txtWidth = (TextView) fileRootview.findViewById(R.id.txt_file_size_width);
            txtWidth.setVisibility(View.VISIBLE);
            txtWidth.setText("Maximum Image Width: " + productOptions.option_image_size_x);
            image_max_x = Integer.parseInt(productOptions.option_image_size_x);
        }

        if (productOptions.option_image_size_y.length() > 0) {
            TextView txtHeight = (TextView) fileRootview.findViewById(R.id.txt_file_size_height);
            txtHeight.setVisibility(View.VISIBLE);
            txtHeight.setText("Maximum Image Height: " + productOptions.option_image_size_y);
            image_max_y = Integer.parseInt(productOptions.option_image_size_y);
        }


        container.addView(fileRootview);

    }

    public void addRelatedProductView(){

        LayoutInflater infalInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ArrayList<Product> productList = productDetailsArray.get(0).relatedProducts;

        for(int i = 0; i<productList.size(); i++){
            View productView = infalInflater.inflate(R.layout.product_layout, null);
            Product product = productList.get(i);
            ImageView productImage = (ImageView) productView.findViewById(R.id.img_product_main);
            imageLoader.displayImage(product.product_icon, productImage,options);
            ((TextView) productView.findViewById(R.id.txt_regular_price)).setText(product.product_price_regular);
            ((TextView) productView.findViewById(R.id.txt_product_name)).setText(product.product_name);
            ((RatingBar) productView.findViewById(R.id.rating_product)).setRating((product.product_rating_summery) / 2);

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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        if(image_max_x == 0 || image_max_y == 0)
            Crop.of(source, destination).asSquare().start(this);
        else
            Crop.of(source, destination).asSquare().withAspect(image_max_x,image_max_y).start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {

            //resultView.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //closing transition animations
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }



}

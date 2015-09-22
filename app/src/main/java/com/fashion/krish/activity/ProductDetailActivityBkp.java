package com.fashion.krish.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.format.Time;
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
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.customview.CircleView;
import com.fashion.krish.customview.FixedSpeedScroller;
import com.fashion.krish.customview.FlowLayout;
import com.fashion.krish.fragment.ProductDetailsPagerFragment;
import com.fashion.krish.model.Product;
import com.fashion.krish.model.ProductDetails;
import com.fashion.krish.options.TypeTextView;
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

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;


public class ProductDetailActivityBkp extends ActionBarActivity implements View.OnClickListener {

    private String product_id;
    private Utility util;
    private boolean isDetailsLoaded = false,isGalleryLoaded = false,isReviewsLoaded = false;
    private ArrayList<ProductDetails> productDetailsArray;

    private ImageView productImage,imgBack;
    private TextView txtPrice,txtName,txtSKU,txtDelivery,txtPrice2,txtTitle;
    private RatingBar rateProduct;
    private LinearLayout configurationLayout,colorLayout,sizeLayout,layBack,layRelatedProducts,layRelatedProductContainer;
    private TextView txtColor,txtSize;
    private FlowLayout colorFlow,sizeFlow;
    private RelativeLayout rootLayout;
    private Button btnShare,btnAddToCart,btnBuyNow;
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
    private JSONObject configAttrs,configOptions;
    String selected_color="",selected_size="",entity_type="";
    private boolean isColorRequired,isSizeRequired,hasOptions=false,hasProduct=false,hasLinks = false;
    int image_max_x,image_max_y;
    boolean isTimeDialogOpen = false;
    int date,month,year,minute,second,hour;
    private String total_price="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.fragment_product_details);

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

        util = new Utility(ProductDetailActivityBkp.this);
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
        getProductReviews(product_id);

    }

    private void getProductDetails(final String product_id){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(ProductDetailActivityBkp.this).getProductDetails(product_id);

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
        if(RestClient.isNetworkAvailable(ProductDetailActivityBkp.this, util))
        {
            t.start();
        }
    }

    private void getProductGallery(final String product_id,final ProductDetails productDetails){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(ProductDetailActivityBkp.this).getProductGallery(product_id);

                try{
                    final JSONObject jsonObject = new JSONObject(response);
                    productDetails.productGalleries = util.parseProductGallery(jsonObject,productDetails);
                    isGalleryLoaded = true;
                    getImageURIForProduct(product_code);
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
        if(RestClient.isNetworkAvailable(ProductDetailActivityBkp.this, util))
        {
            t.start();
        }
    }

    private void getProductReviews(final String product_id){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(ProductDetailActivityBkp.this).getProductReviews(product_id);

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
        if(RestClient.isNetworkAvailable(ProductDetailActivityBkp.this, util))
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

    public void initViews(){

        rootLayout = (RelativeLayout) findViewById(R.id.root_productDetail);
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
        rateProduct.setRating(getIntent().getIntExtra("product_rate",0));

        txtTitle = (TextView)findViewById(R.id.txt_title);
        txtTitle.setText(getIntent().getStringExtra("category_name"));
        imgBack = (ImageView) findViewById(R.id.img_back);

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
        btnAddToCart = (Button) findViewById(R.id.btn_add_to_cart);
        btnAddToCart.setOnClickListener(this);
        btnBuyNow = (Button) findViewById(R.id.btn_buy);
        btnBuyNow.setOnClickListener(this);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.product_details_tab);
        tabs.setIndicatorColor(Color.WHITE);
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setIndicatorHeight((int) Utility.convertDpToPixel(7, ProductDetailActivityBkp.this));
        tabs.setTabBackground(R.drawable.holo_red_white_ripple);
        tabs.setIndicatorColor(Color.WHITE);

        pagerDetail = (ViewPager) findViewById(R.id.product_details_pager);
        pagerGallery = (AutoScrollViewPager) findViewById(R.id.pager_product_gallery);
        dots = new ArrayList<>();
        circleIndicator = (CirclePageIndicator)findViewById(R.id.titles);
        circleIndicator.setRadius(10);

        txtColorError = (TextView) findViewById(R.id.txt_color_error);
        txtSizeError = (TextView) findViewById(R.id.txt_size_error);

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
            galleryPagerAdapter = new GalleryPagerAdapter(ProductDetailActivityBkp.this);
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
                Intent i = new Intent(ProductDetailActivityBkp.this,ProductGalleryActivity.class);
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
                        Toast.makeText(ProductDetailActivityBkp.this,
                                "Added to Cart",Toast.LENGTH_SHORT).show();
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
                    Intent i = new Intent(ProductDetailActivityBkp.this,ProductGalleryActivity.class);
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

        int radius = (int) Utility.convertDpToPixel((float) 12, ProductDetailActivityBkp.this);
        int margin = (int) Utility.convertDpToPixel((float)2,ProductDetailActivityBkp.this);
        int padding = (int) Utility.convertDpToPixel((float)2,ProductDetailActivityBkp.this);

        for(int i =0 ; i<productOptionsValues.size();i++) {
            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(radius*2, radius*2);
            params.setMargins(margin, margin, margin, margin);
            final CircleView circleImageView = new CircleView(ProductDetailActivityBkp.this);
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
                        if(relation.length() > 0){
                            sizeMap.clear();
                            JSONObject relationObj = new JSONObject(relation);
                            if(relationObj.get("value") instanceof JSONArray){
                                JSONArray jArray = relationObj.getJSONArray("value");
                                for (int j = 0; j < jArray.length(); j++) {
                                    String code = jArray.getJSONObject(j).getJSONObject("@attributes").get("code").toString();
                                    String value = jArray.getJSONObject(j).getJSONObject("@attributes").get("label").toString();
                                    sizeMap.put(code, value);

                                }
                            }else{
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
                    for(int k =0 ; k<productOptionsValues.size();k++){
                        if(productOptionsValues.get(k).value_code.equals(current_code)){
                            circleImageView.drawBitmap(bmp3);
                        }else{
                            ((CircleView)colorFlow.findViewWithTag(productOptionsValues.get(k).value_code)).drawBitmap(null);
                        }
                    }
                    ArrayList<String> subProductsArray = productOptionsValues.get(position).sub_products;
                    pagerGalleryUrlList.clear();
                    for (int i = 0; i < subProductsArray.size(); i++) {
                        if(productDetailsArray.get(0).productGalleries.size() > 0){

                            for(ProductDetails.ProductGallery gallery : productDetailsArray.get(0).productGalleries){
                                if(gallery.image_code.equals(subProductsArray.get(i))){
                                    pagerGalleryUrlList.add(gallery.image_url_big);
                                }
                            }

                        }
                    }
                    pagerGallery.setAdapter(galleryPagerAdapter);
                    galleryPagerAdapter.notifyDataSetChanged();

                }
            });


        }

    }

    public void addSizeLayout(final HashMap<String,String> sizeMap){


        int margin = (int) Utility.convertDpToPixel((float) 4, ProductDetailActivityBkp.this);
        int padding = (int) Utility.convertDpToPixel((float)2,ProductDetailActivityBkp.this);
        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                FlowLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, margin, margin, margin);
        for (final HashMap.Entry<String,String> entry : sizeMap.entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue();

            final TextView txtSize = new TextView(ProductDetailActivityBkp.this);
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
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        optionDialog = new MaterialDialog.Builder(ProductDetailActivityBkp.this)
                .cancelable(true)
                .positiveText("Add To Cart")
                .positiveColorRes(R.color.drawer_pressed)
                .negativeText("Close")
                .negativeColorRes(R.color.text_normal)
                .customView(view,false)
                .backgroundColor(Color.WHITE)
                .build();
        //optionDialog.addContentView(view, layoutParams);

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
        LinearLayout dialog_container_lay = (LinearLayout) view.findViewById(R.id.dialog_container);
        final ScrollView dialog_scroll = (ScrollView) view.findViewById(R.id.dialog_scroll);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        optionDialog = new MaterialDialog.Builder(ProductDetailActivityBkp.this)
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

        /*.positiveText("Add To Cart")
                .positiveColorRes(R.color.drawer_pressed)
                .negativeText("Close")
                .negativeColorRes(R.color.text_normal)
                .customView(view,false)
                .backgroundColor(Color.WHITE)
                .neutralText("Price")*/


        ArrayList<ProductDetails.ProductOptions> options = productDetailsArray.get(0).productOptions;

        if(productDetailsArray.get(0).product_entity_type.equals("downloadable")){
            if(productDetailsArray.get(0).productLinks.size() > 0)
                addLinksView(dialog_container_lay,productDetailsArray.get(0).productLinks);
        }


        for (int i = 0; i < options.size(); i++) {
            if(options.get(i).option_type.equals("select") && options.get(i).option_code.contains("option")){
                addSelectionView(dialog_container_lay,options.get(i));
            }

            if(options.get(i).option_type.equals("text") && options.get(i).option_code.contains("option")){
                new TypeTextView(ProductDetailActivityBkp.this, options.get(i),dialog_container_lay);
            }

            if(options.get(i).option_type.equals("radio") && options.get(i).option_code.contains("option")){
                addRadioView(dialog_container_lay, options.get(i));
            }

            if(options.get(i).option_type.equals("checkbox") && options.get(i).option_code.contains("option")){
                addCheckboxView(dialog_container_lay, options.get(i));
            }

            if(options.get(i).option_type.equals("file") && options.get(i).option_code.contains("option")){
                addFileView(dialog_container_lay, options.get(i));
            }

            if(options.get(i).option_type.equals("time") && options.get(i).option_code.contains("option")){
                addTimeView(dialog_container_lay, options.get(i));
            }

            if(options.get(i).option_type.equals("date") && options.get(i).option_code.contains("option")){
                addDateView(dialog_container_lay, options.get(i));
            }

            if(options.get(i).option_type.equals("date_time") && options.get(i).option_code.contains("option")){
                addDateTimeView(dialog_container_lay, options.get(i));
            }

        }
        Display mDisplay = getWindowManager().getDefaultDisplay();
        final int height  = mDisplay.getHeight();
        final int height_to_compare = (int) Utility.convertDpToPixel(400,ProductDetailActivityBkp.this);
        final int height_to_deduct = (int) Utility.convertDpToPixel(200,ProductDetailActivityBkp.this);
        android.os.Handler h = new android.os.Handler();
        h.post(new Runnable() {
            @Override
            public void run() {
                if (dialog_scroll.getMeasuredWidth() > (height-height_to_compare)){
                    dialog_scroll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height-height_to_deduct)));
                }

            }
        });





        optionDialog.show();
    }

    public void addSelectionView(LinearLayout container,final ProductDetails.ProductOptions productOptions){
        int custom_qty = 0;

        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View spinnerRootview = inflate.inflate(R.layout.layout_selection, null);
        Spinner spiner = (Spinner) spinnerRootview.findViewById(R.id.spin_selection);
        LinearLayout spinnerLay= (LinearLayout) spinnerRootview.findViewById(R.id.lay_spinner);
        LinearLayout qtyLay= (LinearLayout) spinnerLay.findViewById(R.id.lay_qty);
        final EditText etQty = (EditText) spinnerRootview.findViewById(R.id.et_qty);
        etQty.setText("1");
        etQty.setEnabled(false);

        final TextView txtPriceTier = (TextView) spinnerRootview.findViewById(R.id.txt_price_tier);
        txtPriceTier.setVisibility(View.GONE);

        ArrayList<String> arrayValues = new ArrayList<>();
        arrayValues.add("Choose Option");


        for(ProductDetails.ProductOptionsValue productValue : productOptions.productOptionsValues){
            arrayValues.add(productValue.value_label);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(ProductDetailActivityBkp.this, R.layout.row_spn, arrayValues);
        spiner.setAdapter(adapter);

        spiner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                if (productOptions.productOptionsValues.get(position-1).custom_qty == 0) {
                    etQty.setEnabled(false);
                } else {
                    etQty.setEnabled(true);
                }

                if(productOptions.options_isMulti == 0 && position !=0
                        && productOptions.productOptionsValues.get(position-1).value_tier_price_html.length() != 0){
                    txtPriceTier.setVisibility(View.VISIBLE);
                    txtPriceTier.setText(Html.fromHtml(productOptions.productOptionsValues.get(position-1).value_tier_price_html));
                }else{
                    txtPriceTier.setVisibility(View.GONE);
                }
            }
        });
        if(productDetailsArray.get(0).product_entity_type.equals("bundle") && productOptions.options_isMulti == 0){
            qtyLay.setVisibility(View.VISIBLE);
        }else{
            qtyLay.setVisibility(View.GONE);
        }


        TextView txt = (TextView) spinnerRootview.findViewById(R.id.txt_selection_title);
        txt.setText(productOptions.option_label);
        if (productOptions.option_is_required.equals("1"))
            txt.append(" *");
        container.addView(spinnerRootview);



    }

    public void addTextView(LinearLayout container,final ProductDetails.ProductOptions productOptions){
        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View textRootview = inflate.inflate(R.layout.layout_text, null);

        TextView txt = (TextView) textRootview.findViewById(R.id.txt_text_title);
        txt.setText(productOptions.option_label);
        txt.setVisibility(View.GONE);

        String price = productOptions.option_formatted_price;
        if(price.length() > 0){
            price = " + " + price;
        }
        EditText et = (EditText) textRootview.findViewById(R.id.edt_txt);
        if (productOptions.option_is_required.equals("1"))
            et.setHint(productOptions.option_label+ price+ " *");
        else
            et.setHint(productOptions.option_label+ price);


        if(productOptions.option_max_character.length() > 0)
            et.setMaxChar(Integer.parseInt(productOptions.option_max_character));

        container.addView(textRootview);

    }

    public void addRadioView(LinearLayout container,final ProductDetails.ProductOptions productOptions){

        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View radioRootview = inflate.inflate(R.layout.layout_radio, null);

        final LinearLayout qtyLay= (LinearLayout) radioRootview.findViewById(R.id.lay_qty);

        final ArrayList<RadioButton> radioArray = new ArrayList<>();
        TextView txt = (TextView) radioRootview.findViewById(R.id.txt_radio_title);
        txt.setText(productOptions.option_label);
        if (productOptions.option_is_required.equals("1"))
            txt.append(" *");

        LinearLayout lay_= (LinearLayout) radioRootview.findViewById(R.id.lay_radio_container);
        final EditText etQty = (EditText) radioRootview.findViewById(R.id.et_qty);
        etQty.setText("1");
        etQty.setEnabled(false);

        final TextView txtPriceTier = (TextView) radioRootview.findViewById(R.id.txt_price_tier);
        txtPriceTier.setVisibility(View.GONE);

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                int selected_position=0;
                if(isChecked){
                    int i = 0;
                    for(RadioButton radioButton : radioArray){
                        radioButton.setChecked(radioButton == buttonView);
                        if(radioButton == buttonView){
                            selected_position = i;
                            optionDialog.setActionButton(DialogAction.NEUTRAL,productOptions.option_label);
                        }
                        i++;

                    }

                }
                if(productOptions.productOptionsValues.get(selected_position).custom_qty == 0)
                    etQty.setEnabled(false);
                else
                    etQty.setEnabled(true);

                if(productOptions.options_isMulti == 0){

                    try {
                        JSONObject jObj = new JSONObject(productOptions.productOptionsValues.get(selected_position).value_tier_price_html);
                        if(jObj.get("tierPriceHtml").toString().length() != 0){
                            txtPriceTier.setVisibility(View.VISIBLE);
                            txtPriceTier.setText(Html.fromHtml(jObj.get("tierPriceHtml").toString()));
                        }else{
                            txtPriceTier.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }


        };


        for(ProductDetails.ProductOptionsValue productValue : productOptions.productOptionsValues){

            String price = productValue.value_formatted_price;
            if(price.length() > 0){
                price = " + " + price;
            }

            final RadioButton radio = new RadioButton(ProductDetailActivityBkp.this);
            radio.setText(productValue.value_label + price);
            radio.setTag(productValue.value_code);
            radio.setGravity(Gravity.CENTER_VERTICAL);
            lay_.addView(radio);
            radioArray.add(radio);
            radio.setOnCheckedChangeListener(listener);
        }

        if(productDetailsArray.get(0).product_entity_type.equals("bundle") && productOptions.options_isMulti == 0){
            qtyLay.setVisibility(View.VISIBLE);
        }else{
            qtyLay.setVisibility(View.GONE);
        }


        container.addView(radioRootview);



    }

    public void addCheckboxView(LinearLayout container,final ProductDetails.ProductOptions productOptions){
        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cbRootview = inflate.inflate(R.layout.layout_radio, null);

        TextView txt = (TextView) cbRootview.findViewById(R.id.txt_radio_title);
        txt.setText(productOptions.option_label);
        if (productOptions.option_is_required.equals("1"))
            txt.append(" *");

        LinearLayout lay_= (LinearLayout) cbRootview.findViewById(R.id.lay_radio_container);

        for(ProductDetails.ProductOptionsValue productValue : productOptions.productOptionsValues){

            String price = productValue.value_formatted_price;
            if(price.length() > 0){
                price = " + " + price;
            }

            CheckBox cb = new CheckBox(ProductDetailActivityBkp.this);
            cb.setText(productValue.value_label + price);
            cb.setTag(productValue.value_code);
            cb.setGravity(Gravity.CENTER_VERTICAL);
            lay_.addView(cb);
        }
        container.addView(cbRootview);

    }

    public void addLinksView(LinearLayout container,ArrayList<ProductDetails.ProductLinks> productLinksArray){

        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cbRootview = inflate.inflate(R.layout.layout_radio, null);

        TextView txt = (TextView) cbRootview.findViewById(R.id.txt_radio_title);
        txt.setText(productLinksArray.get(0).link_label);
        if (productLinksArray.get(0).link_is_required.equals("1"))
            txt.append(" *");

        LinearLayout lay_= (LinearLayout) cbRootview.findViewById(R.id.lay_radio_container);

        for(ProductDetails.ProductLinks productLinks : productLinksArray){

            String price = productLinks.link_item_formatted_price;
            if(price.length() > 0){
                price = " + " + price;
            }

            CheckBox cb = new CheckBox(ProductDetailActivityBkp.this);
            cb.setText(productLinks.link_item_label + price);
            cb.setTag(productLinks.link_item_value);
            cb.setGravity(Gravity.CENTER_VERTICAL);
            lay_.addView(cb);
        }
        container.addView(cbRootview);

    }

    public void addFileView(LinearLayout container,final ProductDetails.ProductOptions productOptions) {
        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View fileRootview = inflate.inflate(R.layout.layout_file, null);

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
                            Crop.pickImage(ProductDetailActivityBkp.this);
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

    public void addTimeView(LinearLayout container,final ProductDetails.ProductOptions productOptions) {
        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View timeRootview = inflate.inflate(R.layout.layout_time, null);

        String price = productOptions.option_formatted_price;
        if(price.length() > 0){
            price = " + " + price;
        }

        TextView txt = (TextView) timeRootview.findViewById(R.id.txt_time_title);
        txt.setText(productOptions.option_label + price);
        if (productOptions.option_is_required.equals("1"))
            txt.append(" *");

        final TextView etTime = (TextView) timeRootview.findViewById(R.id.edt_time);
        etTime.setText("HH:mm AA");

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.Builder timePickerDialog = new TimePickerDialog.Builder(R.style.Material_App_Dialog_TimePicker_Light, hour, minute) {
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {

                        TimePickerDialog dialog = (TimePickerDialog)fragment.getDialog();
                        isTimeDialogOpen = false;
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                        String time = dialog.getFormattedTime(sdf);

                        etTime.setText(time.split(" ")[0].split(":")[0] +":"+
                                time.split(" ")[0].split(":")[1] +
                                " "+time.split(" ")[1]);

                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                        isTimeDialogOpen = false;
                    }
                };

                timePickerDialog.positiveAction("OK").negativeAction("Cancel");
                //timePickerDialog.getDialog().show();
                if (!isTimeDialogOpen) {
                    DialogFragment fragment = DialogFragment.newInstance(timePickerDialog);
                    fragment.show(getSupportFragmentManager(), "");
                    //getSupportFragmentManager().executePendingTransactions();
                }

            }
        });

        
        container.addView(timeRootview);

    }

    public void addDateView(LinearLayout container,final ProductDetails.ProductOptions productOptions) {
        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dateRootview = inflate.inflate(R.layout.layout_date, null);

        String price = productOptions.option_formatted_price;
        if(price.length() > 0){
            price = " + " + price;
        }

        TextView txt = (TextView) dateRootview.findViewById(R.id.txt_date_title);
        txt.setText(productOptions.option_label + price);
        if (productOptions.option_is_required.equals("1"))
            txt.append(" *");

        final TextView etDate = (TextView) dateRootview.findViewById(R.id.edt_date);
        etDate.setText("YYYY/MM/DD");

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.Builder datePickerDialog = new DatePickerDialog.Builder(R.style.Material_App_Dialog_TimePicker_Light) {
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {

                        DatePickerDialog dialog = (DatePickerDialog)fragment.getDialog();
                        isTimeDialogOpen = false;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String date = dialog.getFormattedDate(sdf);

                        etDate.setText(date.split("-")[0] +"/"+
                                date.split("-")[1] +
                                "/"+date.split("-")[2]);

                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                        isTimeDialogOpen = false;
                    }
                };

                datePickerDialog.positiveAction("OK").negativeAction("Cancel");
                //timePickerDialog.getDialog().show();
                if (!isTimeDialogOpen) {
                    DialogFragment fragment = DialogFragment.newInstance(datePickerDialog);
                    fragment.show(getSupportFragmentManager(), "");
                    //getSupportFragmentManager().executePendingTransactions();
                }

            }
        });


        container.addView(dateRootview);

    }

    public void addDateTimeView(LinearLayout container,final ProductDetails.ProductOptions productOptions) {
        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dateTimeRootview = inflate.inflate(R.layout.layout_date_time, null);

        String price = productOptions.option_formatted_price;
        if(price.length() > 0){
            price = " + " + price;
        }

        TextView txt = (TextView) dateTimeRootview.findViewById(R.id.txt_date_title);
        txt.setText(productOptions.option_label + price);
        if (productOptions.option_is_required.equals("1"))
            txt.append(" *");

        final TextView etDate = (TextView) dateTimeRootview.findViewById(R.id.edt_date);
        etDate.setText("YYYY/MM/DD");

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.Builder datePickerDialog = new DatePickerDialog.Builder(R.style.Material_App_Dialog_TimePicker_Light) {
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {

                        DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
                        isTimeDialogOpen = false;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String date = dialog.getFormattedDate(sdf);

                        etDate.setText(date.split("-")[0] + "/" +
                                date.split("-")[1] +
                                "/" + date.split("-")[2]);

                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                        isTimeDialogOpen = false;
                    }
                };

                datePickerDialog.positiveAction("OK").negativeAction("Cancel");
                //timePickerDialog.getDialog().show();
                if (!isTimeDialogOpen) {
                    DialogFragment fragment = DialogFragment.newInstance(datePickerDialog);
                    fragment.show(getSupportFragmentManager(), "");
                    //getSupportFragmentManager().executePendingTransactions();
                }

            }
        });

        final TextView etTime = (TextView) dateTimeRootview.findViewById(R.id.edt_time);
        etTime.setText("HH:mm AA");

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.Builder timePickerDialog = new TimePickerDialog.Builder(R.style.Material_App_Dialog_TimePicker_Light, hour, minute) {
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {

                        TimePickerDialog dialog = (TimePickerDialog)fragment.getDialog();
                        isTimeDialogOpen = false;
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                        String time = dialog.getFormattedTime(sdf);

                        etTime.setText(time.split(" ")[0].split(":")[0] +":"+
                                time.split(" ")[0].split(":")[1] +
                                " "+time.split(" ")[1]);

                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                        isTimeDialogOpen = false;
                    }
                };

                timePickerDialog.positiveAction("OK").negativeAction("Cancel");
                //timePickerDialog.getDialog().show();
                if (!isTimeDialogOpen) {
                    DialogFragment fragment = DialogFragment.newInstance(timePickerDialog);
                    fragment.show(getSupportFragmentManager(), "");
                    //getSupportFragmentManager().executePendingTransactions();
                }

            }
        });

        container.addView(dateTimeRootview);

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
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
    }


    // Unused Methods
    private void getProductOptionsConfig(final String product_id){
    /*
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(ProductDetailActivity.this).getProductOptionsConfig(product_id);

                try{
                    final JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("configurable")){
                        configAttrs = jsonObject.getJSONObject("configurable").getJSONObject("attributes");
                    }
                    isOptionsLoaded = true;
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
        }*/
    }

    public void addSampleView(LinearLayout container,ArrayList<ProductDetails.ProductSample> productSampleArray){

        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sampleRootview = inflate.inflate(R.layout.layout_radio, null);

        TextView txt = (TextView) sampleRootview.findViewById(R.id.txt_radio_title);
        txt.setText(productSampleArray.get(0).sample_label);

        LinearLayout lay_= (LinearLayout) sampleRootview.findViewById(R.id.lay_radio_container);

        for(final ProductDetails.ProductSample productSample : productSampleArray){

            com.rey.material.widget.TextView sampleTxt = new com.rey.material.widget.TextView(ProductDetailActivityBkp.this);
            sampleTxt.setText("• " + productSample.sample_label_item);
            sampleTxt.setTag(productSample.sample_url);
            sampleTxt.setLinksClickable(true);
            sampleTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (productSample.sample_url.length() > 0) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(productSample.sample_url));
                        startActivity(i);
                    }

                }
            });

            sampleTxt.setGravity(Gravity.CENTER_VERTICAL);
            lay_.addView(sampleTxt);
        }
        container.addView(sampleRootview);

    }

}

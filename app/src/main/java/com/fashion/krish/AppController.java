package com.fashion.krish;

import android.app.Application;
import android.app.Fragment;

import com.crashlytics.android.Crashlytics;
import com.fashion.krish.model.Cart;
import com.fashion.krish.model.Category;
import com.fashion.krish.model.Content;
import com.fashion.krish.model.Currency;
import com.fashion.krish.model.FilterCategory;
import com.fashion.krish.model.FilterValue;
import com.fashion.krish.model.HomeBanners;
import com.fashion.krish.model.HomeCategory;
import com.fashion.krish.model.Language;
import com.fashion.krish.model.Product;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import io.fabric.sdk.android.Fabric;

/**
 * Created by amit.thakkar on 7/6/2015.
 */
public class AppController extends Application{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "xXmScGx8IFLvQGoKJCdF5PCYD";
    private static final String TWITTER_SECRET = "G5SKk1LIAON5eIiPCZCXhpXmSgbSKZJQF8V3I8ttcNeRZgEGSV";


    public static final String TAG = AppController.class.getSimpleName();
    public static String URL = "http://10.16.16.121/coupcommerce/jsonconnect";
    //public static String URL = "http://coupcommerce.magentoprojects.net/jsonconnect";
    public static ArrayList<Category> categoryArrayList;
    public static ArrayList<HomeBanners> homeBannersArrayList;
    public static ArrayList<HomeCategory> homeCategoryArrayList;
    public static ArrayList<Product> productArrayList;
    public static ArrayList<FilterCategory> filterDataArrayList;
    public static ArrayList<FilterValue> filterValueArrayList;
    public static ArrayList<Product> recentViewedProducts;
    public static ArrayList<Currency> currencyArray;
    public static ArrayList<Content> contentArray;
    public static ArrayList<Language> languageArray;
    public static HashMap<String,String> FACEBOOK_DETAILS;
    public static HashMap<String,String> TWITTER_DETAILS;
    public static HashMap<String,String> GOOGLE_P_DETAILS;
    public static HashMap<String,String> LINKED_IN_DETAILS;
    public static String PRIMARY_COLOR = "",SECONDARY_COLOR = "",COPY_RIGHT = "©Copyright 2015";
    public static int CMS_MENU_COUNT = 0,CMS_FOOTER_COUNT =0;
    public static Cart SHOPPING_CART = null;
    public static Stack<String> fragmentStack = new Stack<>();

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        //TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_DETAILS.get(""), TWITTER_DETAILS.get(""));
        //Fabric.with(this, new Crashlytics(), new Twitter(authConfig));
        mInstance = this;

        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

        categoryArrayList = new ArrayList<Category>();
        homeBannersArrayList = new ArrayList<HomeBanners>();
        homeCategoryArrayList = new ArrayList<HomeCategory>();
        productArrayList = new ArrayList<Product>();
        filterDataArrayList = new ArrayList<FilterCategory>();
        filterValueArrayList = new ArrayList<FilterValue>();
        recentViewedProducts = new ArrayList<Product>();
        currencyArray = new ArrayList<>();
        contentArray = new ArrayList<>();
        languageArray = new ArrayList<>();

        FACEBOOK_DETAILS = new HashMap<>();
        TWITTER_DETAILS = new HashMap<>();
        GOOGLE_P_DETAILS = new HashMap<>();
        LINKED_IN_DETAILS = new HashMap<>();
        // END - UNIVERSAL IMAGE LOADER SETUP
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }


}

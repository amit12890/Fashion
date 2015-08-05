package com.fashion.krish;

import android.app.Application;

import com.fashion.krish.model.Category;
import com.fashion.krish.model.FilterCategory;
import com.fashion.krish.model.FilterValue;
import com.fashion.krish.model.HomeBanners;
import com.fashion.krish.model.HomeCategory;
import com.fashion.krish.model.Product;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

/**
 * Created by amit.thakkar on 7/6/2015.
 */
public class AppController extends Application{

    public static final String TAG = AppController.class.getSimpleName();
    public static String URL = "http://10.16.16.121/coupcommerce/jsonconnect";
    //public static String URL = "http://coupcommerce.magentoprojects.net/jsonconnect";
    public static ArrayList<Category> categoryArrayList;
    public static ArrayList<HomeBanners> homeBannersArrayList;
    public static ArrayList<HomeCategory> homeCategoryArrayList;
    public static ArrayList<Product> productArrayList;
    public static ArrayList<FilterCategory> filterDataArrayList;
    public static ArrayList<FilterValue> filterValueArrayList;


    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
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

        // END - UNIVERSAL IMAGE LOADER SETUP
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }
    /*
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
            httpClient = HttpClients.custom()
                    .setConnectionManager(new PoolingHttpClientConnectionManager())
                    .setDefaultCookieStore(new PersistentCookieStore(getApplicationContext()))
                    .build();
            mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new HttpClientStack(httpClient));
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }*/

}

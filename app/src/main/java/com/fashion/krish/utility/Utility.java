package com.fashion.krish.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.activity.DashboardActivity;
import com.fashion.krish.activity.SplashActivity;
import com.fashion.krish.customview.ProgressWheel;
import com.fashion.krish.fragment.ProductListFragment;
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
import com.fashion.krish.model.ProductDetails;
import com.fashion.krish.model.SubCategory;
import com.rey.material.app.Dialog;
import com.rey.material.drawable.RippleDrawable;
import com.rey.material.widget.ProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by amit.thakkar on 7/6/2015.
 */
public class Utility {

    Activity activity;
    private AppPreferences preferences;
    public static MaterialDialog errorDialog,loadingDialog;
    public static ProgressView progressView;
    RelativeLayout progressLay;
    private boolean isDailogueVisible = false;
    public Dialog progressDialog;

    public Utility(Activity activity){
        this.activity = activity;
       // progressDialog = new Dialog(activity,android.R.style.Theme_Black_NoTitleBar);

       // showAnimatedLogoProgressBar();

    }

    public void setSocialNetworkData(JSONObject jsonObject){

        try {
            JSONObject socialObj;
            Iterator<?> keys = jsonObject.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                if(key.equals("facebook")){

                    socialObj = jsonObject.getJSONObject("facebook");
                    if(socialObj.getInt("isActive")==1){
                        AppController.FACEBOOK_DETAILS.put("appID",socialObj.get("appID").toString());
                        AppController.FACEBOOK_DETAILS.put("isActive",socialObj.get("isActive").toString());
                        AppController.FACEBOOK_DETAILS.put("secret",socialObj.get("secret").toString());
                        AppController.FACEBOOK_DETAILS.put("page",socialObj.get("page").toString());
                    }else
                        AppController.FACEBOOK_DETAILS.put("isActive",socialObj.get("isActive").toString());


                }else if(key.equals("twitter")){
                    socialObj = jsonObject.getJSONObject("twitter");
                    if(socialObj.getInt("isActive")==1){
                        AppController.TWITTER_DETAILS.put("apiKey",socialObj.get("apiKey").toString());
                        AppController.TWITTER_DETAILS.put("isActive",socialObj.get("isActive").toString());
                        AppController.TWITTER_DETAILS.put("secretKey",socialObj.get("secretKey").toString());
                        AppController.TWITTER_DETAILS.put("page",socialObj.get("page").toString());
                    }else
                        AppController.TWITTER_DETAILS.put("isActive",socialObj.get("isActive").toString());

                }else if(key.equals("linkedin")){
                    socialObj = jsonObject.getJSONObject("linkedin");
                    if(socialObj.getInt("isActive")==1){
                        AppController.LINKED_IN_DETAILS.put("apiKey",socialObj.get("apiKey").toString());
                        AppController.LINKED_IN_DETAILS.put("isActive",socialObj.get("isActive").toString());
                        AppController.LINKED_IN_DETAILS.put("secretKey",socialObj.get("secretKey").toString());
                        AppController.LINKED_IN_DETAILS.put("page",socialObj.get("page").toString());
                    }else
                        AppController.LINKED_IN_DETAILS.put("isActive",socialObj.get("isActive").toString());

                }else if(key.equals("google")){
                    socialObj = jsonObject.getJSONObject("google");
                    if(socialObj.getInt("isActive")==1){
                        AppController.GOOGLE_P_DETAILS.put("apiKey",socialObj.get("apiKey").toString());
                        AppController.GOOGLE_P_DETAILS.put("isActive",socialObj.get("isActive").toString());
                        AppController.GOOGLE_P_DETAILS.put("secretKey",socialObj.get("secretKey").toString());
                        AppController.GOOGLE_P_DETAILS.put("page",socialObj.get("page").toString());
                    }else
                        AppController.GOOGLE_P_DETAILS.put("isActive",socialObj.get("isActive").toString());

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void parseCurrencyData(JSONObject jObj){
        try{
            if(jObj.has("item")){
                if(jObj.get("item") instanceof JSONArray){
                    JSONArray currencyArray = jObj.getJSONArray("item");
                    for (int i = 0; i < currencyArray.length(); i++) {
                        JSONObject currencyObj = currencyArray.getJSONObject(i);
                        Currency currency = new Currency();
                        currency.currency_code = currencyObj.getString("currency_code");
                        currency.currency_symbol = currencyObj.getString("currency_symbol");
                        currency.currency_is_current = currencyObj.getString("current");
                        AppController.currencyArray.add(currency);
                    }
                }else{
                    JSONObject currencyObj = jObj.getJSONObject("item");
                    Currency currency = new Currency();
                    currency.currency_code = currencyObj.getString("currency_code");
                    currency.currency_symbol = currencyObj.getString("currency_symbol");
                    currency.currency_is_current = currencyObj.getString("current");
                    AppController.currencyArray.add(currency);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    public void parseLanguageData(JSONObject jObj){
        try{
            if(jObj.has("item")){
                if(jObj.get("item") instanceof JSONArray){
                    JSONArray languageArray = jObj.getJSONArray("item");
                    for (int i = 0; i < languageArray.length(); i++) {
                        JSONObject languageObj = languageArray.getJSONObject(i);
                        Language language = new Language();
                        language.store_id = languageObj.getString("store_id");
                        language.store_name = languageObj.getString("store_name");
                        language.store_is_current= languageObj.getString("current");
                        AppController.languageArray.add(language);
                    }
                }else{
                    JSONObject languageObj = jObj.getJSONObject("item");
                    Language language = new Language();
                    language.store_id = languageObj.getString("store_id");
                    language.store_name = languageObj.getString("store_name");
                    language.store_is_current= languageObj.getString("current");
                    AppController.languageArray.add(language);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    public void parseContentData(JSONObject jObj){
        AppController.contentArray.clear();
        try{
            if(jObj.has("footer")){
                if(jObj.get("footer") instanceof JSONArray){
                    JSONArray footerArray = jObj.getJSONArray("footer");
                    for (int i = 0; i < footerArray.length(); i++) {
                        JSONObject footerObj = footerArray.getJSONObject(i);
                        Content content = new Content();
                        content.content_action_type = "footer";
                        content.content_action_id = footerObj.getString("id");
                        content.content_action_page_id = footerObj.getString("page_id");
                        content.content_action_label = footerObj.getString("label");
                        AppController.contentArray.add(content);
                        AppController.CMS_FOOTER_COUNT ++;
                    }
                }else{
                    JSONObject footerObj = jObj.getJSONObject("footer");
                    Content content = new Content();
                    content.content_action_type = "footer";
                    content.content_action_id = footerObj.getString("id");
                    content.content_action_page_id = footerObj.getString("page_id");
                    content.content_action_label = footerObj.getString("label");
                    AppController.contentArray.add(content);
                    AppController.CMS_MENU_COUNT ++;
                }
            }
            if(jObj.has("menu")){
                if(jObj.get("menu") instanceof JSONArray){
                    JSONArray footerArray = jObj.getJSONArray("menu");
                    for (int i = 0; i < footerArray.length(); i++) {
                        JSONObject footerObj = footerArray.getJSONObject(i);
                        Content content = new Content();
                        content.content_action_type = "menu";
                        content.content_action_id = footerObj.getString("id");
                        content.content_action_page_id = footerObj.getString("page_id");
                        content.content_action_label = footerObj.getString("label");
                        AppController.contentArray.add(content);
                    }
                }else{
                    JSONObject footerObj = jObj.getJSONObject("menu");
                    Content content = new Content();
                    content.content_action_type = "menu";
                    content.content_action_id = footerObj.getString("id");
                    content.content_action_page_id = footerObj.getString("page_id");
                    content.content_action_label = footerObj.getString("label");
                    AppController.contentArray.add(content);
                }
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    public ArrayList<Category> parseCategory(JSONObject initialObject){

        try {
            JSONObject categoryObj = initialObject.getJSONObject("categories");
            JSONArray itemArray = categoryObj.getJSONArray("item");
            for (int i = 0; i< itemArray.length(); i++){
                ArrayList<SubCategory> subCategoryArrayList = new ArrayList<SubCategory>();
                Category category = new Category();
                category.label = itemArray.getJSONObject(i).get("label").toString();
                category.entity_id = itemArray.getJSONObject(i).get("entity_id").toString();
                if(itemArray.getJSONObject(i).has("icon")){
                    category.icon = itemArray.getJSONObject(i).get("icon").toString();
                }
                category.content_type = itemArray.getJSONObject(i).get("content_type").toString();
                category.url_key = itemArray.getJSONObject(i).get("url_key").toString();

                if(itemArray.getJSONObject(i).has("subitem")){

                    if(itemArray.getJSONObject(i).getJSONObject("subitem").get("subitem") instanceof JSONArray){
                        category.subitem = itemArray.getJSONObject(i).getJSONObject("subitem").getJSONArray("subitem");
                        for (int j = 0; j< category.subitem.length(); j++){

                            SubCategory subCategory = new SubCategory();
                            subCategory.entity_id = category.subitem.getJSONObject(j).get("entity_id").toString();
                            subCategory.url_key = category.subitem.getJSONObject(j).get("url_key").toString();
                            subCategory.count = category.subitem.getJSONObject(j).get("count").toString();
                            subCategory.content_type = category.subitem.getJSONObject(j).get("content_type").toString();
                            subCategory.label = category.subitem.getJSONObject(j).get("label").toString();
                            subCategoryArrayList.add(subCategory);

                        }
                    }else{
                        JSONObject subitemObj = itemArray.getJSONObject(i).getJSONObject("subitem").getJSONObject("subitem");

                        SubCategory subCategory = new SubCategory();
                        subCategory.entity_id = subitemObj.get("entity_id").toString();
                        subCategory.url_key = subitemObj.get("url_key").toString();
                        subCategory.count = subitemObj.get("count").toString();
                        subCategory.content_type = subitemObj.get("content_type").toString();
                        subCategory.label = subitemObj.get("label").toString();
                        subCategoryArrayList.add(subCategory);

                    }

                    category.subCategories = subCategoryArrayList;
                }

                AppController.categoryArrayList.add(category);
            }

        } catch (JSONException e) {

            e.printStackTrace();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
                }
            });

        }

        return  AppController.categoryArrayList;

    }

    public ArrayList<HomeBanners> parseHomeBanners(JSONObject jsonObject){

        try {
            JSONObject categoryObj = jsonObject.getJSONObject("home_banners");
            JSONArray itemArray = categoryObj.getJSONArray("item");
            for (int i = 0; i< itemArray.length(); i++){
                HomeBanners homeBanners = new HomeBanners();
                homeBanners.banner_image = itemArray.getJSONObject(i).get("image").toString();
                homeBanners.banner_type = "rotate";

                if(itemArray.getJSONObject(i).has("action"))
                {
                    homeBanners.banner_action_type = itemArray.getJSONObject(i).getJSONObject("action").getString("type");
                    homeBanners.banner_action_attribute = itemArray.getJSONObject(i).getJSONObject("action").getString("attribute");
                    homeBanners.banner_entity_id = itemArray.getJSONObject(i).getString("entity_id");
                    homeBanners.banner_content_type = itemArray.getJSONObject(i).getJSONObject("action").getString("content_type");
                }

                AppController.homeBannersArrayList.add(homeBanners);
            }

        } catch (JSONException e) {

            e.printStackTrace();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return AppController.homeBannersArrayList;
    }

    public ArrayList<HomeBanners> parseStaticHomeBanners(JSONObject jsonObject){

        try {
            JSONObject categoryObj = jsonObject.getJSONObject("home_static_banners");
            JSONArray itemArray = categoryObj.getJSONArray("item");
            for (int i = 0; i< itemArray.length(); i++){
                HomeBanners homeBanners = new HomeBanners();
                homeBanners.banner_image = itemArray.getJSONObject(i).get("image").toString();
                homeBanners.banner_type = "static";
                if(itemArray.getJSONObject(i).has("action"))
                {
                    homeBanners.banner_action_type = itemArray.getJSONObject(i).getJSONObject("action").getString("type");
                    homeBanners.banner_content_type = itemArray.getJSONObject(i).getJSONObject("action").getString("content_type");
                    homeBanners.banner_action_attribute = itemArray.getJSONObject(i).getJSONObject("action").getString("attribute");

                }
                homeBanners.banner_entity_id = itemArray.getJSONObject(i).getJSONObject("@attributes").getString("entity_id");
                AppController.homeBannersArrayList.add(homeBanners);
            }

        } catch (JSONException e) {

            e.printStackTrace();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return AppController.homeBannersArrayList;
    }

    public ArrayList<HomeCategory> parseHomeCategory(JSONObject jsonObject){

        try {
            JSONObject categoryObj = jsonObject.getJSONObject("home_categories");
            JSONArray itemArray = categoryObj.getJSONArray("item");
            for (int i = 0; i< itemArray.length(); i++){
                ArrayList<Product> productArrayList = new ArrayList<Product>();
                HomeCategory homeCategory = new HomeCategory();
                homeCategory.name = itemArray.getJSONObject(i).get("name").toString();

                if(itemArray.getJSONObject(i).has("products")){

                    if(itemArray.getJSONObject(i).getJSONObject("products").get("item") instanceof JSONArray){
                        homeCategory.product_json_array = itemArray.getJSONObject(i).getJSONObject("products").getJSONArray("item");
                        for (int j = 0; j< homeCategory.product_json_array.length(); j++){

                            Product products = new Product();
                            products.product_entity_id = homeCategory.product_json_array.getJSONObject(j).get("entity_id").toString();
                            products.product_entity_type = homeCategory.product_json_array.getJSONObject(j).get("entity_type").toString();
                            products.product_name = homeCategory.product_json_array.getJSONObject(j).get("name").toString();
                            products.product_sku = homeCategory.product_json_array.getJSONObject(j).get("sku").toString();
                            products.product_url_key = homeCategory.product_json_array.getJSONObject(j).get("url_key").toString();
                            products.product_short_desc = homeCategory.product_json_array.getJSONObject(j).get("short_description").toString();
                            products.product_desc = homeCategory.product_json_array.getJSONObject(j).get("description").toString();
                            products.product_link = homeCategory.product_json_array.getJSONObject(j).get("link").toString();
                            products.product_icon = homeCategory.product_json_array.getJSONObject(j).get("icon").toString();
                            products.product_in_stock = homeCategory.product_json_array.getJSONObject(j).getInt("in_stock");
                            products.product_is_salable = homeCategory.product_json_array.getJSONObject(j).getInt("is_salable");
                            products.product_is_new = homeCategory.product_json_array.getJSONObject(j).getInt("is_new");
                            products.product_has_gallery = homeCategory.product_json_array.getJSONObject(j).getInt("has_gallery");
                            products.product_has_option = homeCategory.product_json_array.getJSONObject(j).getInt("has_options");
                            products.product_price_regular = homeCategory.product_json_array.getJSONObject(j).getJSONObject("price").
                                    get("regular").toString();
                            products.product_type = "home_category";
                            products.product_category = homeCategory.name;

                            productArrayList.add(products);

                        }
                    }else{
                        homeCategory.product_json_obj = itemArray.getJSONObject(i).getJSONObject("products").getJSONObject("item");
                        Product products = new Product();
                        products.product_entity_id = homeCategory.product_json_obj.get("entity_id").toString();
                        products.product_entity_type = homeCategory.product_json_obj.get("entity_type").toString();
                        products.product_name = homeCategory.product_json_obj.get("name").toString();
                        products.product_sku = homeCategory.product_json_obj.get("sku").toString();
                        products.product_url_key = homeCategory.product_json_obj.get("url_key").toString();
                        products.product_short_desc = homeCategory.product_json_obj.get("short_description").toString();
                        products.product_desc = homeCategory.product_json_obj.get("description").toString();
                        products.product_link = homeCategory.product_json_obj.get("link").toString();
                        products.product_icon = homeCategory.product_json_obj.get("icon").toString();
                        products.product_in_stock = homeCategory.product_json_obj.getInt("in_stock");
                        products.product_is_salable = homeCategory.product_json_obj.getInt("is_salable");
                        products.product_is_new = homeCategory.product_json_obj.getInt("is_new");
                        products.product_has_gallery = homeCategory.product_json_obj.getInt("has_gallery");
                        products.product_has_option = homeCategory.product_json_obj.getInt("has_options");
                        products.product_price_regular = homeCategory.product_json_obj.getJSONObject("price").
                                get("regular").toString();
                        products.product_type = "home_category";
                        products.product_category = homeCategory.name;

                        productArrayList.add(products);

                    }
                    homeCategory.productsList = productArrayList;
                }
                AppController.homeCategoryArrayList.add(homeCategory);
                }

        }catch(JSONException e) {

            e.printStackTrace();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return AppController.homeCategoryArrayList;
    }

    public ArrayList<Product> parseProduct(JSONObject jsonobject,String category_id){

        ArrayList<Product> productArrayList = new ArrayList<>();
        try {

            JSONObject categoryInfoObj = jsonobject.getJSONObject("category_info");
            JSONObject productObj = jsonobject.getJSONObject("products");

            if(productObj.get("item") instanceof JSONArray){

                JSONArray productInfoArray = productObj.getJSONArray("item");

                for(int i = 0; i<productInfoArray.length(); i++){
                    Product products = new Product();
                    JSONObject productInfo = (JSONObject) productInfoArray.get(i);
                    products.product_parent_id = categoryInfoObj.get("parent_id").toString();
                    products.product_entity_id = productInfo.get("entity_id").toString();
                    products.product_entity_type = productInfo.get("entity_type").toString();
                    products.product_name = productInfo.get("name").toString();
                    products.product_sku = productInfo.get("sku").toString();
                    products.product_url_key = productInfo.get("url_key").toString();
                    products.product_short_desc = productInfo.get("short_description").toString();
                    products.product_desc = productInfo.get("description").toString();
                    products.product_link = productInfo.get("link").toString();
                    products.product_icon = productInfo.get("icon").toString();
                    products.product_in_stock = productInfo.getInt("in_stock");
                    products.product_is_salable = productInfo.getInt("is_salable");
                    products.product_is_new = productInfo.getInt("is_new");
                    products.product_has_gallery = productInfo.getInt("has_gallery");
                    products.product_has_option = productInfo.getInt("has_options");
                    products.product_rating_summery = productInfo.getInt("rating_summary");
                    products.product_review_count = productInfo.getInt("reviews_count");
                    products.product_category = category_id;
                    setProductPrice(products.product_entity_type,productInfo.getJSONObject("price"),
                            products);

                    int index = getIndexOfProduct(products.product_entity_id);
                    if(index > 0)
                        productArrayList.set(index - 1, products);
                    else
                        productArrayList.add(products);
                }

                    
            }else{

                Product products = new Product();
                JSONObject productInfo = productObj.getJSONObject("item");
                products.product_parent_id = categoryInfoObj.get("parent_id").toString();
                products.product_entity_id = productInfo.get("entity_id").toString();
                products.product_entity_type = productInfo.get("entity_type").toString();
                products.product_name = productInfo.get("name").toString();
                products.product_sku = productInfo.get("sku").toString();
                products.product_url_key = productInfo.get("url_key").toString();
                products.product_short_desc = productInfo.get("short_description").toString();
                products.product_desc = productInfo.get("description").toString();
                products.product_link = productInfo.get("link").toString();
                products.product_icon = productInfo.get("icon").toString();
                products.product_in_stock = productInfo.getInt("in_stock");
                products.product_is_salable = productInfo.getInt("is_salable");
                products.product_is_new = productInfo.getInt("is_new");
                products.product_has_gallery = productInfo.getInt("has_gallery");
                products.product_has_option = productInfo.getInt("has_options");
                products.product_rating_summery = productInfo.getInt("rating_summary");
                products.product_review_count = productInfo.getInt("reviews_count");
                products.product_category = category_id;
                setProductPrice(products.product_entity_type,productInfo.getJSONObject("price"),
                        products);
                int index = getIndexOfProduct(products.product_entity_id);
                if(index > 0)
                    productArrayList.set(index-1,products);
                else
                    productArrayList.add(products);

            }

        } catch (JSONException e){
            System.out.print(e.toString());
        }
        return productArrayList;
    }

    public ArrayList<Product> getProductForCategory(String category_id){
        ArrayList<Product> products = new ArrayList<Product>();

        for (int i=0; i<AppController.productArrayList.size(); i++ ){
            if(AppController.productArrayList.get(i).product_category.equals(category_id)){
                products.add(AppController.productArrayList.get(i));
            }
        }
        return products;
    }

    public int getIndexOfProduct(String product_id){

        int i=0,index=0;
        for(Product product : AppController.productArrayList){
            if(product.product_entity_id.equals(product_id)){
                index = i+1;
                return index;
            }else{
                i++;
            }
        }
        return index;
    }

    public ArrayList<FilterCategory> parseFilterData(JSONObject jsonobject) {
        ArrayList<FilterCategory> filterDataArrayList = new ArrayList<>();
        try {

            JSONObject filterMainObj = jsonobject.getJSONObject("filters");
            if (filterMainObj.get("item") instanceof JSONArray) {

                JSONArray filterItemArray = filterMainObj.getJSONArray("item");

                for (int i = 0; i < filterItemArray.length(); i++) {

                    FilterCategory filterCategory = new FilterCategory();
                    JSONObject filterCategoryInfo = (JSONObject) filterItemArray.get(i);
                    filterCategory.filter_name = filterCategoryInfo.get("name").toString();
                    filterCategory.filter_code = filterCategoryInfo.get("code").toString();
                    filterCategory.filterValues = filterCategoryInfo.getJSONObject("values");
                    filterCategory.filterValuesArray = parseFilterValues(filterCategory.filterValues,filterCategory.filter_code);
                    if(i==0){
                        filterCategory.isSelected = true;
                    }else{
                        filterCategory.isSelected = false;
                    }
                    filterDataArrayList.add(filterCategory);
                }

            } else {

                FilterCategory filterCategory = new FilterCategory();
                JSONObject filterCategoryInfo = filterMainObj.getJSONObject("item");
                filterCategory.filter_name = filterCategoryInfo.get("name").toString();
                filterCategory.filter_code = filterCategoryInfo.get("code").toString();
                filterCategory.filterValues = filterCategoryInfo.getJSONObject("values");
                filterCategory.filterValuesArray = parseFilterValues(filterCategory.filterValues,filterCategory.filter_code);
                filterDataArrayList.add(filterCategory);
            }


        } catch (JSONException e) {

                System.err.print(e);
        }
        return filterDataArrayList;
    }

    public HashMap parseSortData(JSONObject jsonObject){
        HashMap<String,String> sortMap = new HashMap<>();
        try {
            if(jsonObject.get("item") instanceof JSONArray){
                JSONArray jsonArray = jsonObject.getJSONArray("item");
                for (int i = 0; i < jsonArray.length(); i++) {

                    String code = jsonArray.getJSONObject(i).get("code").toString();
                    String name = jsonArray.getJSONObject(i).get("name").toString();
                    sortMap.put(code, name);
                    if(jsonArray.getJSONObject(i).has("@attributes")){
                        if(jsonArray.getJSONObject(i).has("isDefault") &&
                                jsonArray.getJSONObject(i).get("isDefault").toString().equals("1"))
                            ProductListFragment.defaultSort = code ;
                    }
                }
            }else{
                String code = jsonObject.get("code").toString();
                String name = jsonObject.get("name").toString();

                if(jsonObject.has("@attributes")){
                    if(jsonObject.has("isDefault") &&
                            jsonObject.get("isDefault").toString().equals("1"))
                        ProductListFragment.defaultSort = code ;
                }

                sortMap.put(code, name);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sortMap;
    }

    public ArrayList<FilterValue> parseFilterValues(JSONObject jsonobject,String code) {
        ArrayList<FilterValue> filterValueArrayList = new ArrayList<FilterValue>();
        try {

            JSONObject filterMainObj = jsonobject;
            if (filterMainObj.get("value") instanceof JSONArray) {

                JSONArray filterValueArray = filterMainObj.getJSONArray("value");

                for (int i = 0; i < filterValueArray.length(); i++) {

                    FilterValue filterValue = new FilterValue();
                    JSONObject filterValuesInfo = (JSONObject) filterValueArray.get(i);
                    filterValue.filter_value_id = filterValuesInfo.get("id").toString();
                    filterValue.filter_value_count = filterValuesInfo.get("count").toString();
                    filterValue.filter_value_label = filterValuesInfo.get("label").toString();
                    filterValue.filter_value_parent = code;
                    filterValueArrayList.add(filterValue);
                }

            } else {

                FilterValue filterValue = new FilterValue();
                JSONObject filterValuesInfo = filterMainObj.getJSONObject("value");
                filterValue.filter_value_id = filterValuesInfo.get("id").toString();
                filterValue.filter_value_count = filterValuesInfo.get("count").toString();
                filterValue.filter_value_label = filterValuesInfo.get("label").toString();
                filterValue.filter_value_parent = code;
                filterValueArrayList.add(filterValue);
            }


        } catch (JSONException e) {
            System.err.print(e);
        }
        return filterValueArrayList;
    }

    public ArrayList<ProductDetails> parseProductDetails(JSONObject jsonobject) {
        ArrayList<ProductDetails> productDetailsArrayList = new ArrayList<>();
        try {

            JSONObject detailMainObj = jsonobject;
            ProductDetails productDetails = new ProductDetails();
            productDetails.product_entity_id = detailMainObj.get("entity_id").toString();
            productDetails.product_name = detailMainObj.get("name").toString();
            productDetails.product_entity_type = detailMainObj.get("entity_type").toString();
            productDetails.product_sku = detailMainObj.get("sku").toString();
            productDetails.product_short_desc = detailMainObj.get("short_description").toString();
            productDetails.product_desc = detailMainObj.get("description").toString();
            productDetails.product_url_key = detailMainObj.get("url_key").toString();
            productDetails.product_link = detailMainObj.get("link").toString();
            productDetails.product_icon = detailMainObj.get("icon").toString();

            if(detailMainObj.has("expected_delivery"))
                productDetails.product_expected_delivery = detailMainObj.get("expected_delivery").toString();

            setProductDetailPrice(productDetails.product_entity_type, detailMainObj.getJSONObject("price"),
                    productDetails);

            productDetails.product_is_salable = detailMainObj.getInt("is_salable");
            productDetails.product_is_new = detailMainObj.getInt("is_new");
            productDetails.product_has_gallery = detailMainObj.getInt("has_gallery");
            productDetails.product_has_option = detailMainObj.getInt("has_options");
            productDetails.product_in_stock = detailMainObj.getInt("in_stock");
            productDetails.product_review_count = detailMainObj.getInt("reviews_count");
            productDetails.product_rating_summery = detailMainObj.getInt("rating_summary");

            if(detailMainObj.has("related_products"))
                productDetails.relatedProducts = parseRelatedProduct(detailMainObj.getJSONObject("related_products"));

            if(detailMainObj.has("additional_attributes"))
                productDetails.product_additional_attribute = getAdditionalAttribute(detailMainObj.getJSONObject("additional_attributes"));

            if(detailMainObj.getJSONObject("product").getJSONObject("options").has("option")){

                if(detailMainObj.getJSONObject("product").getJSONObject("options").get("option") instanceof JSONArray){
                    JSONArray jsonArray = detailMainObj.getJSONObject("product").getJSONObject("options").getJSONArray("option");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        productDetails.productOptions.add(parseProductOptions(jsonArray.getJSONObject(i),productDetails));
                    }
                }else{
                    productDetails.productOptions.add(parseProductOptions(detailMainObj.getJSONObject("product").
                            getJSONObject("options").getJSONObject("option"),productDetails));
                }
            }

            JSONObject jObject = detailMainObj.getJSONObject("product").getJSONObject("options");
            if(jObject.has("samples")){
                JSONObject jObj = jObject.getJSONObject("samples");
                productDetails.productSamples = parseProductSamples(jObj,productDetails);
            }

            if(jObject.has("links")){
                JSONObject jObj = jObject.getJSONObject("links");
                productDetails.productLinks = parseProductLinks(jObj, productDetails);
            }


            productDetailsArrayList.add(productDetails);

        } catch (JSONException e) {
            System.err.print(e);
        }
        return productDetailsArrayList;
    }

    public HashMap<String,String> getAdditionalAttribute(JSONObject jsonObject){
        HashMap additionalInfo = new HashMap();
        try {
            if(jsonObject.get("item") instanceof JSONArray){
                JSONArray jsonArray = jsonObject.getJSONArray("item");
                for (int i = 0; i < jsonArray.length() ; i++) {
                    String label = jsonArray.getJSONObject(i).get("label").toString();
                    String value = jsonArray.getJSONObject(i).get("value").toString();
                    additionalInfo.put(label, value);
                }

            }else{

                String label = jsonObject.getJSONObject("item").get("label").toString();
                String value = jsonObject.getJSONObject("item").get("value").toString();
                additionalInfo.put(label, value);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return additionalInfo;
    }

    public ArrayList<Product> parseRelatedProduct(JSONObject jsonobject){

        ArrayList<Product> productArrayList = new ArrayList<>();
        try {

            if(jsonobject.get("item") instanceof JSONArray){

                JSONArray productInfoArray = jsonobject.getJSONArray("item");

                for(int i = 0; i<productInfoArray.length(); i++){
                    Product products = new Product();
                    JSONObject productInfo = (JSONObject) productInfoArray.get(i);
                    products.product_entity_id = productInfo.get("entity_id").toString();

                    if(productInfo.has("entity_type"))
                        products.product_entity_type = productInfo.get("entity_type").toString();

                    if(productInfo.has("entity_type_id"))
                        products.product_entity_type = productInfo.get("entity_type_id").toString();

                    products.product_name = productInfo.get("name").toString();
                    if(productInfo.has("sku"))
                        products.product_sku = productInfo.get("sku").toString();

                    if(productInfo.has("url_key"))
                        products.product_url_key = productInfo.get("url_key").toString();

                    products.product_short_desc = productInfo.get("short_description").toString();
                    products.product_desc = productInfo.get("description").toString();
                    products.product_link = productInfo.get("link").toString();
                    products.product_icon = productInfo.get("icon").toString();
                    products.product_in_stock = productInfo.getInt("in_stock");
                    products.product_is_salable = productInfo.getInt("is_salable");
                    products.product_is_new = productInfo.getInt("is_new");
                    products.product_has_gallery = productInfo.getInt("has_gallery");
                    products.product_has_option = productInfo.getInt("has_options");
                    products.product_rating_summery = productInfo.getInt("rating_summary");
                    products.product_review_count = productInfo.getInt("reviews_count");
                    setProductPrice(products.product_entity_type,productInfo.getJSONObject("price"),
                            products);

                    int index = getIndexOfProduct(products.product_entity_id);
                    if(index > 0)
                        productArrayList.set(index - 1, products);
                    else
                        productArrayList.add(products);
                }


            }else{

                Product products = new Product();
                JSONObject productInfo = jsonobject.getJSONObject("item");
                products.product_entity_id = productInfo.get("entity_id").toString();

                if(productInfo.has("entity_type"))
                    products.product_entity_type = productInfo.get("entity_type").toString();

                if(productInfo.has("entity_type_id"))
                    products.product_entity_type = productInfo.get("entity_type_id").toString();

                products.product_name = productInfo.get("name").toString();
                if(productInfo.has("sku"))
                    products.product_sku = productInfo.get("sku").toString();

                if(productInfo.has("url_key"))
                    products.product_url_key = productInfo.get("url_key").toString();

                products.product_short_desc = productInfo.get("short_description").toString();
                products.product_desc = productInfo.get("description").toString();
                products.product_link = productInfo.get("link").toString();
                products.product_icon = productInfo.get("icon").toString();
                products.product_in_stock = productInfo.getInt("in_stock");
                products.product_is_salable = productInfo.getInt("is_salable");
                products.product_is_new = productInfo.getInt("is_new");
                products.product_has_gallery = productInfo.getInt("has_gallery");
                products.product_has_option = productInfo.getInt("has_options");
                products.product_rating_summery = productInfo.getInt("rating_summary");
                products.product_review_count = productInfo.getInt("reviews_count");
                setProductPrice(products.product_entity_type,productInfo.getJSONObject("price"),
                        products);
                int index = getIndexOfProduct(products.product_entity_id);
                if(index > 0)
                    productArrayList.set(index-1,products);
                else
                    productArrayList.add(products);

            }

        }catch (JSONException e){
            System.out.print(e.toString());
        }
        return productArrayList;
    }

    public ArrayList<Product> parseWishlistProduct(JSONObject jsonobject){

        ArrayList<Product> productArrayList = new ArrayList<>();
        try {

            if(jsonobject.get("item") instanceof JSONArray){

                JSONArray productInfoArray = jsonobject.getJSONArray("item");

                for(int i = 0; i<productInfoArray.length(); i++){
                    Product products = new Product();
                    JSONObject productInfo = (JSONObject) productInfoArray.get(i);
                    products.product_entity_id = productInfo.get("entity_id").toString();

                    if(productInfo.has("entity_type_id"))
                        products.product_entity_type = productInfo.get("entity_type_id").toString();

                    products.product_name = productInfo.get("name").toString();

                    if(productInfo.has("sku"))
                        products.product_sku = productInfo.get("sku").toString();

                    if(productInfo.has("url_path"))
                        products.product_url_key = productInfo.get("url_path").toString();

                    products.product_desc = productInfo.get("description").toString();
                    products.product_icon = productInfo.get("icon").toString();
                    products.product_in_stock = productInfo.getInt("in_stock");
                    products.product_is_salable = productInfo.getInt("is_salable");

                    products.product_has_option = productInfo.getInt("has_options");
                    products.product_rating_summery = productInfo.getInt("rating_summary");
                    //products.product_review_count = productInfo.getInt("reviews_count");
                    setProductPrice(products.product_entity_type,productInfo.getJSONObject("price"),
                            products);

                    int index = getIndexOfProduct(products.product_entity_id);
                    if(index > 0)
                        productArrayList.set(index - 1, products);
                    else
                        productArrayList.add(products);
                }


            }else{

                Product products = new Product();
                JSONObject productInfo = jsonobject.getJSONObject("item");
                products.product_entity_id = productInfo.get("entity_id").toString();

                if(productInfo.has("entity_type"))
                    products.product_entity_type = productInfo.get("entity_type").toString();

                if(productInfo.has("entity_type_id"))
                    products.product_entity_type = productInfo.get("entity_type_id").toString();

                products.product_name = productInfo.get("name").toString();
                if(productInfo.has("sku"))
                    products.product_sku = productInfo.get("sku").toString();

                if(productInfo.has("url_key"))
                    products.product_url_key = productInfo.get("url_key").toString();

                products.product_desc = productInfo.get("description").toString();
                products.product_icon = productInfo.get("icon").toString();
                products.product_in_stock = productInfo.getInt("in_stock");
                products.product_is_salable = productInfo.getInt("is_salable");
                products.product_has_option = productInfo.getInt("has_options");
                products.product_rating_summery = productInfo.getInt("rating_summary");
                //products.product_review_count = productInfo.getInt("reviews_count");
                setProductPrice(products.product_entity_type,productInfo.getJSONObject("price"),
                        products);
                int index = getIndexOfProduct(products.product_entity_id);
                if(index > 0)
                    productArrayList.set(index-1,products);
                else
                    productArrayList.add(products);

            }

        }catch (JSONException e){
            System.out.print(e.toString());
        }
        return productArrayList;
    }

    public ProductDetails.ProductOptions parseProductOptions(JSONObject jsonObject,ProductDetails productDetails){
        ProductDetails.ProductOptions productOptions = null;

        try {
            JSONObject jsonObj = jsonObject;
            productOptions = productDetails.new ProductOptions();
            Iterator<String> keys = jsonObj.keys();
                /*while (keys.hasNext()) {
                    String key = keys.next();
                }*/
            if(jsonObj.has("code")){
                productOptions.option_code = jsonObj.get("code").toString();
            }
            if(jsonObj.has("type")){
                productOptions.option_type = jsonObj.get("type").toString();
            }
            if(jsonObj.has("icon")){
                productOptions.option_icon = jsonObj.get("icon").toString();
            }
            if(jsonObj.has("qty")){
                productOptions.option_qty = jsonObj.getInt("qty");
            }
            if(jsonObj.has("is_qty_editable")){
                productOptions.option_qty = jsonObj.getInt("is_qty_editable");
            }
            if(jsonObj.has("isMulti")){
                productOptions.options_isMulti = jsonObj.getInt("isMulti");
            }
            if(jsonObj.has("attr_code")){
                productOptions.option_attr_code = jsonObj.get("attr_code").toString();
            }
            if(jsonObj.has("label")){
                productOptions.option_label = jsonObj.get("label").toString();
            }
            if(jsonObj.has("is_required")){
                productOptions.option_is_required = jsonObj.get("is_required").toString();
            }
            if(jsonObj.has("price")){
                productOptions.option_price = jsonObj.get("price").toString();
            }
            if(jsonObj.has("formated_price")){
                productOptions.option_formatted_price = jsonObj.get("formated_price").toString();
            }
            if(jsonObj.has("max_characters")){
                productOptions.option_max_character = jsonObj.get("max_characters").toString();
            }
            if(jsonObj.has("price_notice")){
                productOptions.option_price_notice = jsonObj.get("price_notice").toString();
            }
            if(jsonObj.has("image_size_x")){
                productOptions.option_image_size_x = jsonObj.get("image_size_x").toString();
            }
            if(jsonObj.has("image_size_y")){
                productOptions.option_image_size_y = jsonObj.get("image_size_y").toString();
            }


            if(jsonObject.has("value")){
                if(jsonObject.get("value") instanceof JSONArray){
                    JSONArray jsonArray = jsonObject.getJSONArray("value");
                    for (int i = 0; i <jsonArray.length(); i++) {
                        productOptions.productOptionsValues.add(parseProductOptionValue(jsonArray.getJSONObject(i),productDetails));
                    }
                }else{
                    productOptions.productOptionsValues.add(parseProductOptionValue(jsonObject.getJSONObject("value"), productDetails));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return productOptions;
    }

    public ProductDetails.ProductOptionsValue parseProductOptionValue(JSONObject jsonObject,ProductDetails productDetails){
        ProductDetails.ProductOptionsValue productOptionsValue = null;

        try {
            JSONObject jsonObj = jsonObject;
            productOptionsValue = productDetails.new ProductOptionsValue();
            Iterator<String> keys = jsonObj.keys();
                /*while (keys.hasNext()) {
                    String key = keys.next();
                }*/
            if(jsonObj.has("code")){
                productOptionsValue.value_code = jsonObj.get("code").toString();
            }
            if(jsonObj.has("label")){
                productOptionsValue.value_label = jsonObj.get("label").toString();
            }
            if(jsonObj.has("customQty")){
                productOptionsValue.custom_qty = jsonObj.getInt("customQty");
            }
            if(jsonObj.has("price")){
                productOptionsValue.value_price = jsonObj.get("price").toString();
            }
            if(jsonObj.has("formated_price")){
                productOptionsValue.value_formatted_price = jsonObj.get("formated_price").toString();
            }

            if(jsonObject.has("relation")){
                productOptionsValue.value_relation = jsonObject.get("relation").toString();
            }

            if(jsonObject.has("tier_price")){
                productOptionsValue.value_tier_price = jsonObject.get("tier_price").toString();
            }
            if(jsonObject.has("tierPriceHtml")){
                if(!jsonObject.get("tierPriceHtml").toString().equals("{}"))
                    productOptionsValue.value_tier_price_html = jsonObject.get("tierPriceHtml").toString();
            }

            if(jsonObject.has("products")){
                if(jsonObject.get("products") instanceof JSONArray){
                    JSONArray jArray = jsonObject.getJSONArray("products");
                    if (jArray != null) {
                        for (int i=0;i<jArray.length();i++){
                            productOptionsValue.sub_products.add(jArray.get(i).toString());
                        }
                    }
                }else{
                    productOptionsValue.sub_products.add(jsonObject.getString("products").toLowerCase());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return productOptionsValue;
    }

    public ArrayList<ProductDetails.ProductGallery> parseProductGallery(JSONObject jsonObject,ProductDetails productDetails){

        ArrayList<ProductDetails.ProductGallery> galleryArrayList = new ArrayList<>();

        try {
            if(jsonObject.get("image") instanceof  JSONArray){
                JSONArray jArray = jsonObject.getJSONArray("image");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObj = jArray.getJSONObject(i);
                    ProductDetails.ProductGallery productGallery = parseGalleryFile(jObj,productDetails);
                    galleryArrayList.add(productGallery);
                }
            }else{
                JSONObject imageObj = jsonObject.getJSONObject("image");
                ProductDetails.ProductGallery productGallery = parseGalleryFile(imageObj, productDetails);
                galleryArrayList.add(productGallery);

                /*if(imageObj.get("type").toString().equals("big"))
                    productGallery.image_url_big = imageObj.get("url").toString();
                else
                    productGallery.image_url_small = imageObj.get("url").toString();

                if(imageObj.has("w"))
                    productGallery.image_width = imageObj.get("w").toString();

                if(imageObj.has("h"))
                    productGallery.image_height = imageObj.get("h").toString();

                if(imageObj.has("modification_time"))
                    productGallery.image_modification_time = imageObj.get("modification_time").toString();*/

            }

            if(jsonObject.has("child")){
                JSONObject jChildObj = jsonObject.getJSONObject("child");
                for (int i = 0; i < jChildObj.length(); i++) {

                }
                for(Iterator<String> iter = jChildObj.keys();iter.hasNext();) {
                    String key = iter.next();
                    String[] key_split = key.split("_");
                    JSONObject jObj = jChildObj.getJSONObject(key);
                    if(jObj.has("image")){
                        if(jObj.get("image") instanceof  JSONArray){
                            JSONArray jArray = jObj.getJSONArray("image");
                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject jObj1 = jArray.getJSONObject(i);
                                ProductDetails.ProductGallery productGallery = parseGalleryFile(jObj1, productDetails);
                                productGallery.image_code = key_split[1];
                                galleryArrayList.add(productGallery);
                            }
                        }else{
                            JSONObject imageObj = jObj.getJSONObject("image");
                            ProductDetails.ProductGallery productGallery = parseGalleryFile(imageObj, productDetails);
                            productGallery.image_code = key_split[1];
                            galleryArrayList.add(productGallery);
                        }
                    }

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return galleryArrayList;
    }

    public ProductDetails.ProductGallery parseGalleryFile(JSONObject jObj,ProductDetails productDetails){
        ProductDetails.ProductGallery productGallery = productDetails.new ProductGallery();
        try{
            if(jObj.get("file") instanceof JSONArray){

                JSONArray imageArray = jObj.getJSONArray("file");
                for (int j = 0; j < imageArray.length(); j++) {
                    if(imageArray.getJSONObject(j).has("type")){
                        if(imageArray.getJSONObject(j).get("type").equals("big")) {
                            productGallery.image_url_big = imageArray.getJSONObject(j).
                                    get("url").toString();
                            productGallery.image_height = imageArray.getJSONObject(j).
                                    get("modification_time").toString();
                        }
                        else
                            productGallery.image_url_small = imageArray.getJSONObject(j).
                                    get("url").toString();
                    }

                    if(imageArray.getJSONObject(j).has("id"))
                        productGallery.image_id = imageArray.getJSONObject(j).get("id").toString();

                    if(imageArray.getJSONObject(j).has("w"))
                        productGallery.image_width = imageArray.getJSONObject(j).get("w").toString();

                    if(imageArray.getJSONObject(j).has("h"))
                        productGallery.image_height = imageArray.getJSONObject(j).get("h").toString();

                }

            }else{

                JSONObject imageObj = jObj.getJSONObject("file");
                if(imageObj.get("type").toString().equals("big"))
                    productGallery.image_url_big = imageObj.get("url").toString();
                else
                    productGallery.image_url_small = imageObj.get("url").toString();

                if(imageObj.has("w"))
                    productGallery.image_width = imageObj.get("w").toString();

                if(imageObj.has("h"))
                    productGallery.image_height = imageObj.get("h").toString();

                if(imageObj.has("modification_time"))
                    productGallery.image_modification_time = imageObj.get("modification_time").toString();

            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        return productGallery;
    }

    public ArrayList<ProductDetails.ProductSample> parseProductSamples(JSONObject jsonObject,ProductDetails productDetails){

        String parent_label;
        ArrayList<ProductDetails.ProductSample> sampleArrayList = new ArrayList<>();

        try {

            parent_label = jsonObject.get("label").toString();
            if(jsonObject.get("item") instanceof  JSONArray){
                JSONArray jArray = jsonObject.getJSONArray("item");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObj = jArray.getJSONObject(i);
                    ProductDetails.ProductSample productSample = productDetails.new ProductSample();
                    productSample.sample_label = parent_label;

                    if(jObj.has("label"))
                        productSample.sample_label_item = jObj.get("label").toString();

                    if(jObj.has("url"))
                        productSample.sample_url = jObj.get("url").toString();

                    sampleArrayList.add(productSample);


                }
            }else{
                JSONObject jObj = jsonObject.getJSONObject("item");
                ProductDetails.ProductSample productSample = productDetails.new ProductSample();

                if(jObj.has("label"))
                    productSample.sample_label_item = jObj.get("label").toString();

                if(jObj.has("url"))
                    productSample.sample_url = jObj.get("url").toString();

                sampleArrayList.add(productSample);


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sampleArrayList;
    }

    public ArrayList<ProductDetails.ProductLinks> parseProductLinks(JSONObject jsonObject,ProductDetails productDetails){

        String parent_label="",parent_code="",is_required="";
        ArrayList<ProductDetails.ProductLinks> linksArrayList = new ArrayList<>();

        try {

            parent_label = jsonObject.get("label").toString();
            parent_code = jsonObject.get("code").toString();
            is_required = jsonObject.get("is_required").toString();
            if(jsonObject.get("item") instanceof  JSONArray){
                JSONArray jArray = jsonObject.getJSONArray("item");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObj = jArray.getJSONObject(i);
                    ProductDetails.ProductLinks productLinks= productDetails.new ProductLinks();
                    productLinks.link_label = parent_label;
                    productLinks.link_code = parent_code;
                    productLinks.link_is_required = is_required;

                    if(jObj.has("label"))
                        productLinks.link_item_label = jObj.get("label").toString();

                    if(jObj.has("value"))
                        productLinks.link_item_value = jObj.get("value").toString();

                    if(jObj.has("formatted_price"))
                        productLinks.link_item_formatted_price = jObj.get("formatted_price").toString();

                    if(jObj.has("is_required"))
                        productLinks.link_is_required = jObj.get("is_required").toString();

                    linksArrayList.add(productLinks);


                }
            }else{
                JSONObject jObj = jsonObject.getJSONObject("item");
                ProductDetails.ProductLinks productLinks= productDetails.new ProductLinks();

                if(jObj.has("label"))
                    productLinks.link_item_label = jObj.get("label").toString();

                if(jObj.has("value"))
                    productLinks.link_item_value = jObj.get("value").toString();

                if(jObj.has("formatted_price"))
                    productLinks.link_item_formatted_price = jObj.get("formatted_price").toString();

                if(jObj.has("is_required"))
                    productLinks.link_is_required = jObj.get("is_required").toString();

                linksArrayList.add(productLinks);


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return linksArrayList;
    }

    public void getCartInfo(){
        AppController.SHOPPING_CART = null;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(activity).getCartDetails();

                try{
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.has("summary_qty")){
                        AppController.SHOPPING_CART = parseCartDetails(jsonObject);
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        });
        if(RestClient.isNetworkAvailable(activity, this))
        {
            t.start();

        }

    }

    public Cart parseCartDetails(JSONObject jsonObject){
        Cart cart = new Cart();
        try{
            if(jsonObject.has("summary_qty"))
                cart.cart_qty = jsonObject.getString("summary_qty");
            if(jsonObject.has("products") && jsonObject.get("products") != null){
                if(jsonObject.getJSONObject("products").get("item") instanceof JSONArray){
                    JSONArray productArray = jsonObject.getJSONObject("products").getJSONArray("item");
                    for (int i = 0; i < productArray.length(); i++) {
                        cart.cartItems.add(parseCartItems(productArray.getJSONObject(i),cart));
                    }

                }else {
                    JSONObject productObj = jsonObject.getJSONObject("products").getJSONObject("item");
                    cart.cartItems.add(parseCartItems(productObj,cart));
                }
            }

            if(jsonObject.has("totals")){
                if(jsonObject.getJSONObject("totals").has("subtotal")){
                    cart.subtotal_title = jsonObject.getJSONObject("totals").getJSONObject("subtotal").getString("title");
                    cart.subtotal_value = jsonObject.getJSONObject("totals").getJSONObject("subtotal").getString("value");
                    cart.subtotal_formated_value = jsonObject.getJSONObject("totals").getJSONObject("subtotal").getString("formated_value");
                }

                if(jsonObject.getJSONObject("totals").has("grand_total")){
                    cart.grandtotal_title = jsonObject.getJSONObject("totals").getJSONObject("grand_total").getString("title");
                    cart.grandtotal_value = jsonObject.getJSONObject("totals").getJSONObject("grand_total").getString("value");
                    cart.grandtotal_formated_value = jsonObject.getJSONObject("totals").getJSONObject("grand_total").getString("formated_value");
                }

                if(jsonObject.getJSONObject("totals").has("discount")){
                    cart.discount_title = jsonObject.getJSONObject("totals").getJSONObject("discount").getString("title");
                    cart.discount_value = jsonObject.getJSONObject("totals").getJSONObject("discount").getString("value");
                    cart.discount_formated_value= jsonObject.getJSONObject("totals").getJSONObject("discount").getString("formated_value");
                }

                if(jsonObject.getJSONObject("totals").has("tax")){
                    cart.tax_title = jsonObject.getJSONObject("totals").getJSONObject("tax").getString("title");
                    cart.tax_value = jsonObject.getJSONObject("totals").getJSONObject("tax").getString("value");
                    cart.tax_formated_value = jsonObject.getJSONObject("totals").getJSONObject("tax").getString("formated_value");
                }

                if(jsonObject.getJSONObject("totals").has("shipping")){
                    cart.shipping_title = jsonObject.getJSONObject("totals").getJSONObject("shipping").getString("title");
                    cart.shipping_value = jsonObject.getJSONObject("totals").getJSONObject("shipping").getString("value");
                    cart.shipping_formated_value = jsonObject.getJSONObject("totals").getJSONObject("shipping").getString("formated_value");
                }
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
        return cart;
    }

    public Cart.CartItems parseCartItems(JSONObject jsonobject,Cart cart) {

        Cart.CartItems cartItems = cart.new CartItems();
        try {

            cartItems.item_entity_id = jsonobject.get("entity_id").toString();

            if (jsonobject.has("entity_type"))
                cartItems.item_entity_type = jsonobject.get("entity_type").toString();

            if (jsonobject.has("item_id"))
                cartItems.item_id = jsonobject.get("item_id").toString();

            cartItems.item_name = jsonobject.get("name").toString();
            if (jsonobject.has("code"))
                cartItems.item_code = jsonobject.get("code").toString();

            if (jsonobject.has("icon"))
                cartItems.item_icon = jsonobject.get("icon").toString();

            if (jsonobject.has("qty"))
                cartItems.item_qty = jsonobject.get("qty").toString();

            if (jsonobject.has("max_qty"))
                cartItems.item_max_qty = jsonobject.get("max_qty").toString();

            if (jsonobject.has("min_qty"))
                cartItems.item_min_qty = jsonobject.get("min_qty").toString();

            if (jsonobject.has("price"))
                cartItems.item_price = jsonobject.getJSONObject("price").get("regular").toString();

            if (jsonobject.has("formated_price"))
                cartItems.item_formated_price = jsonobject.getJSONObject("formated_price").get("regular").toString();

            if (jsonobject.has("subtotal"))
                cartItems.item_subtotal = jsonobject.getJSONObject("subtotal").get("regular").toString();

            if (jsonobject.has("formated_subtotal"))
                cartItems.item_formated_subtotal = jsonobject.getJSONObject("formated_subtotal").get("regular").toString();

            if(jsonobject.has("options")){
                if(jsonobject.getJSONObject("options").get("option") instanceof  JSONArray){
                    JSONArray optionsArray = jsonobject.getJSONObject("options").getJSONArray("option");
                    for (int i = 0; i < optionsArray.length(); i++) {
                        String key = optionsArray.getJSONObject(i).getString("label");
                        String value = optionsArray.getJSONObject(i).getString("text");
                        cartItems.item_options_map.put(key,value);
                    }
                }else{
                    JSONObject optionsObj = jsonobject.getJSONObject("options").getJSONObject("option");
                    String key = optionsObj.getString("label");
                    String value = optionsObj.getString("text");
                }
            }


        } catch (JSONException e) {
            System.out.print(e.toString());
        }
        return cartItems;
    }



    public void updateCartItem(final HashMap<String,String> cart_params){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(activity).updateCart(cart_params);
                if(response.equals(RestClient.ERROR)) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                            hideAnimatedLogoProgressBar();
                            return;
                        }
                    });
                }else if(response.equals(RestClient.TIMEOUT_ERROR)) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showErrorDialog(RestClient.TIMEOUT_ERROR_MESSAGE, "OK", "");
                            hideAnimatedLogoProgressBar();
                            return;
                        }
                    });
                }
                try{
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.has("status")){
                        if(jsonObject.getString("status").equals("success")){

                        }else{
                            showErrorDialog("Items can not be deleted. Please try again.","Ok","");
                        }
                    }else{
                        showErrorDialog(RestClient.ERROR_MESSAGE,"Ok","");
                    }



                }catch (JSONException e){
                    e.printStackTrace();
                    showErrorDialog(RestClient.ERROR_MESSAGE, "Ok", "");
                }

            }
        });
        if(RestClient.isNetworkAvailable(activity, this))
        {
            t.start();

        }

    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param activity Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Activity activity){
        Resources resources = activity.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param activity Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Activity activity){
        Resources resources = activity.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public String loadJSONFromAsset(String path) {
        String json = null;
        try {

            InputStream is = activity.getAssets().open(path);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public void showErrorDialog(String message,String positiveText,String negativeText){
        errorDialog = new MaterialDialog.Builder(activity)
                .content(message)
                .positiveText(positiveText)
                .negativeText(negativeText).build();

        if(!errorDialog.isShowing()){
            errorDialog.show();
        }

    }

    public void hideErrorDialog(){
        errorDialog.dismiss();
    }

    public void showLoadingDialog(String message){

        progressView = new ProgressView(activity);

        loadingDialog = new MaterialDialog.Builder(activity)
                .content(message)
                .progress(true, 0)
                .cancelable(false)
                .build();
        if(!loadingDialog.isShowing()){
            loadingDialog.show();
            //progressView.start();
        }


    }

    public void hideLoadingDialog(){
        loadingDialog.dismiss();
        //progressView.stop();
    }

    public void setProductPrice(String productType,JSONObject productPriceJson,Product products){

        try {
            if(productType.equals("bundle")){
                products.product_price_to = productPriceJson.get("to").toString();
                products.product_price_from = productPriceJson.get("from").toString();
            }else if(productType.equals("grouped")){
                products.product_price_regular = productPriceJson.get("starting_at").toString();
                if(productPriceJson.has("special"))
                    products.product_price_special = productPriceJson.get("special").toString();
            }else{
                products.product_price_regular = productPriceJson.get("regular").toString();
                if(productPriceJson.has("special"))
                    products.product_price_special = productPriceJson.get("special").toString();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setProductDetailPrice(String productType,JSONObject productPriceJson,ProductDetails products){

        try {
            if(productType.equals("bundle")){
                products.product_price_to = productPriceJson.get("to").toString();
                products.product_price_from = productPriceJson.get("from").toString();
            }else if(productType.equals("grouped")){
                products.product_price_regular = productPriceJson.get("starting_at").toString();
                if(productPriceJson.has("special"))
                    products.product_price_special = productPriceJson.get("special").toString();
            }else{
                products.product_price_regular = productPriceJson.get("regular").toString();
                if(productPriceJson.has("special"))
                    products.product_price_special = productPriceJson.get("special").toString();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String,String> getColorMap(){
        HashMap<String,String > colorMap = new HashMap<>();

        colorMap.put("Black","#000000");
        colorMap.put("Purple","#8e2972");
        colorMap.put("Green","#56b420");
        colorMap.put("Blue","#269eb2");
        colorMap.put("Peru","#b37a48");
        colorMap.put("Royal Blue","#6628d7");
        colorMap.put("Red","#ff0000");
        colorMap.put("Khaki","#f0e68c");
        colorMap.put("Charcoal","#36454f");
        colorMap.put("Royal-blue","#6628d7");
        colorMap.put("Pink","#ff69b4");
        colorMap.put("White","#ffffff");
        colorMap.put("Indigo","#490083");

        return colorMap;
    }


    public void getHashKey() {

        // Add code to print out the key hash
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(
                    "com.fashion.krish",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void changeStatusBarColor(Activity activity){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            float[] hsv = new float[3];
            int color;
            if(AppController.PRIMARY_COLOR.equals("")){
                color = Color.parseColor("#000000");
            }else{
                color = Color.parseColor(AppController.PRIMARY_COLOR);
            }

            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.8f; // value component
            color = Color.HSVToColor(hsv);

            Window window = activity.getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(color);
        }

    }

    public RippleDrawable getPrimaryRippleDrawable(){

        int color = (int) Long.parseLong(AppController.PRIMARY_COLOR.replace("#",""),16);
        RippleDrawable rippleDrawable = new RippleDrawable.Builder()
                .backgroundColor(Color.parseColor(AppController.PRIMARY_COLOR))
                .rippleColor(Color.parseColor("#4DFFFFFF"))
                .cornerRadius(5)
                .delayClickType(RippleDrawable.DELAY_CLICK_AFTER_RELEASE)
                .build();
        return  rippleDrawable;
    }

    public RippleDrawable getSecondaryRippleDrawable(){

        int color = (int) Long.parseLong(AppController.SECONDARY_COLOR.replace("#",""),16);
        RippleDrawable rippleDrawable = new RippleDrawable.Builder()
                .backgroundColor(Color.parseColor(AppController.SECONDARY_COLOR))
                .rippleColor(Color.parseColor("#33FFFFFF"))
                .cornerRadius(5)
                .delayClickType(RippleDrawable.DELAY_CLICK_AFTER_RELEASE)
                .build();
        return  rippleDrawable;
    }

    public void showAnimatedLogoProgressBar(ViewGroup rootView){
        isDailogueVisible = true;

        LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View progressView = infalInflater.inflate(R.layout.loading_layout, null);
        progressLay = (RelativeLayout) progressView.findViewById(R.id.lay_progress_bar);
        progressLay.setVisibility(View.VISIBLE);

        ProgressWheel pw = (ProgressWheel) progressView.findViewById(R.id.progressBarTwo);
        pw.setImageSource(R.drawable.logo_vs);
        pw.setBarColor(Color.parseColor(AppController.PRIMARY_COLOR));
        pw.setRimColor(Color.TRANSPARENT);
        pw.setImageColor(Color.parseColor(AppController.SECONDARY_COLOR));
        pw.startSpinning();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
        progressLay.setLayoutParams(params);

        rootView.addView(progressLay);

    }

    public void showAnimatedLogoProgressBar(){

        LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View progressView = infalInflater.inflate(R.layout.loading_layout, null);
        progressLay = (RelativeLayout) progressView.findViewById(R.id.lay_progress_bar);
        progressLay.setVisibility(View.VISIBLE);

        ProgressWheel pw = (ProgressWheel) progressView.findViewById(R.id.progressBarTwo);
        pw.setImageSource(R.drawable.logo_vs);
        if(AppController.PRIMARY_COLOR.length() != 0 || AppController.SECONDARY_COLOR.length() !=0){
            pw.setBarColor(Color.parseColor(AppController.PRIMARY_COLOR));
            pw.setImageColor(Color.parseColor(AppController.SECONDARY_COLOR));
        }

        pw.setRimColor(Color.TRANSPARENT);
        pw.startSpinning();
        //progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.loading_layout);
        progressDialog.show();
        //progressDialog.getWindow().setBackgroundDrawable(null);


    }

    public View getLoadingLayout(){
        LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View progressView = infalInflater.inflate(R.layout.loading_layout_footer, null);
        progressLay = (RelativeLayout) progressView.findViewById(R.id.lay_progress_bar);
        progressLay.setVisibility(View.VISIBLE);

        ProgressWheel pw = (ProgressWheel) progressView.findViewById(R.id.progressBarTwo);
        pw.setImageSource(R.drawable.logo_vs);
        pw.setBarColor(Color.parseColor(AppController.PRIMARY_COLOR));
        pw.setRimColor(Color.TRANSPARENT);
        pw.setImageColor(Color.parseColor(AppController.SECONDARY_COLOR));
        pw.startSpinning();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
        progressLay.setLayoutParams(params);

        return  progressView;

    }

    public void hideAnimatedLogoProgressBar(){
        if(progressLay!=null){
            progressLay.setVisibility(View.GONE);
            isDailogueVisible = false;
        }

    }

    public boolean isDailogueVisible(){
        return isDailogueVisible;
    }

    public int getScreenWidth(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        return displaymetrics.widthPixels;
    }

    public int getScreenHeight(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        return displaymetrics.heightPixels;
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if(activity.getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard
     */
    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }
}

package com.fashion.krish.utility;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.activity.DashboardActivity;
import com.fashion.krish.fragment.CMSFragment;
import com.fashion.krish.fragment.ProductListFragment;
import com.fashion.krish.model.Category;
import com.fashion.krish.model.FilterCategory;
import com.fashion.krish.model.FilterValue;
import com.fashion.krish.model.HomeBanners;
import com.fashion.krish.model.HomeCategory;
import com.fashion.krish.model.Product;
import com.fashion.krish.model.ProductDetails;
import com.fashion.krish.model.SubCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
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

    public Utility(Activity activity){
        this.activity = activity;

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
                //category.icon = itemArray.getJSONObject(i).get("icon").toString();
                category.content_type = itemArray.getJSONObject(i).get("content_type").toString();
                category.url_key = itemArray.getJSONObject(i).get("url_key").toString();

                if(itemArray.getJSONObject(i).has("subitem")){

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
                homeBanners.image = itemArray.getJSONObject(i).get("image").toString();
                homeBanners.type = "rotate";
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
                homeBanners.image = itemArray.getJSONObject(i).get("image").toString();
                homeBanners.type = "static";
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
                            products.product_has_gallery = homeCategory.product_json_array.getJSONObject(j).getInt("has_gallery");
                            products.product_has_option = homeCategory.product_json_array.getJSONObject(j).getInt("has_options");
                            products.product_price_regular = homeCategory.product_json_array.getJSONObject(j).getJSONObject("price").
                                    getJSONObject("@attributes").get("regular").toString();
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
                        products.product_has_gallery = homeCategory.product_json_obj.getInt("has_gallery");
                        products.product_has_option = homeCategory.product_json_obj.getInt("has_options");
                        products.product_price_regular = homeCategory.product_json_obj.getJSONObject("price").
                                getJSONObject("@attributes").get("regular").toString();
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
                    products.product_has_gallery = productInfo.getInt("has_gallery");
                    products.product_has_option = productInfo.getInt("has_options");
                    products.product_rating_summery = productInfo.getInt("rating_summary");
                    products.product_review_count = productInfo.getInt("reviews_count");
                    products.product_category = category_id;
                    setProductPrice(products.product_entity_type,productInfo.getJSONObject("price").getJSONObject("@attributes"),
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
                products.product_has_gallery = productInfo.getInt("has_gallery");
                products.product_has_option = productInfo.getInt("has_options");
                products.product_rating_summery = productInfo.getInt("rating_summary");
                products.product_review_count = productInfo.getInt("reviews_count");
                products.product_category = category_id;
                setProductPrice(products.product_entity_type,productInfo.getJSONObject("price").getJSONObject("@attributes"),
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
                        if(jsonArray.getJSONObject(i).getJSONObject("@attributes").has("isDefault") &&
                                jsonArray.getJSONObject(i).getJSONObject("@attributes").get("isDefault").toString().equals("1"))
                            ProductListFragment.defaultSort = code ;
                    }
                }
            }else{
                String code = jsonObject.get("code").toString();
                String name = jsonObject.get("name").toString();

                if(jsonObject.has("@attributes")){
                    if(jsonObject.getJSONObject("@attributes").has("isDefault") &&
                            jsonObject.getJSONObject("@attributes").get("isDefault").toString().equals("1"))
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

            setProductDetailPrice(productDetails.product_entity_type, detailMainObj.getJSONObject("price").getJSONObject("@attributes"),
                    productDetails);

            /*productDetails.product_price_regular = detailMainObj.getJSONObject("price").
                    getJSONObject("@attributes").get("regular").toString();

            if(detailMainObj.getJSONObject("price").getJSONObject("@attributes").has("special"))
                productDetails.product_price_special = detailMainObj.getJSONObject("price").
                        getJSONObject("@attributes").get("special").toString();*/
            productDetails.product_is_salable = detailMainObj.getInt("is_salable");
            productDetails.product_has_gallery = detailMainObj.getInt("has_gallery");
            productDetails.product_has_option = detailMainObj.getInt("has_options");
            productDetails.product_in_stock = detailMainObj.getInt("in_stock");
            productDetails.product_review_count = detailMainObj.getInt("reviews_count");
            productDetails.product_rating_summery = detailMainObj.getInt("rating_summary");
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
                    additionalInfo.put(label,value);
                }

            }else{

                String label = jsonObject.getJSONObject("item").get("label").toString();
                String value = jsonObject.getJSONObject("item").get("value").toString();
                additionalInfo.put(label,value);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return additionalInfo;
    }

    public ProductDetails.ProductOptions parseProductOptions(JSONObject jsonObject,ProductDetails productDetails){
        ProductDetails.ProductOptions productOptions = null;

        try {
            JSONObject jsonObj = jsonObject.getJSONObject("@attributes");
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
            JSONObject jsonObj = jsonObject.getJSONObject("@attributes");
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
                    if(imageArray.getJSONObject(j).getJSONObject("@attributes").has("type")){
                        if(imageArray.getJSONObject(j).getJSONObject("@attributes").get("type").equals("big")) {
                            productGallery.image_url_big = imageArray.getJSONObject(j).getJSONObject("@attributes").
                                    get("url").toString();
                            productGallery.image_height = imageArray.getJSONObject(j).getJSONObject("@attributes").
                                    get("modification_time").toString();
                        }
                        else
                            productGallery.image_url_small = imageArray.getJSONObject(j).getJSONObject("@attributes").
                                    get("url").toString();
                    }

                    if(imageArray.getJSONObject(j).getJSONObject("@attributes").has("id"))
                        productGallery.image_id = imageArray.getJSONObject(j).getJSONObject("@attributes").get("id").toString();

                    if(imageArray.getJSONObject(j).getJSONObject("@attributes").has("w"))
                        productGallery.image_width = imageArray.getJSONObject(j).getJSONObject("@attributes").get("w").toString();

                    if(imageArray.getJSONObject(j).getJSONObject("@attributes").has("h"))
                        productGallery.image_height = imageArray.getJSONObject(j).getJSONObject("@attributes").get("h").toString();

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

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = activity.getAssets().open("initialContent.json");

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

    public void setPopUpMenuItems(){
        PopupMenu menu = DashboardActivity.popup;
        preferences = new AppPreferences(activity);
        if(preferences.getIsLoggedIn().equals("1")){
            menu.getMenu().getItem(4).setVisible(false);
            menu.getMenu().getItem(6).setVisible(false);

            menu.getMenu().getItem(0).setVisible(true);
            menu.getMenu().getItem(1).setVisible(true);
            menu.getMenu().getItem(2).setVisible(true);
            menu.getMenu().getItem(3).setVisible(true);
            menu.getMenu().getItem(9).setVisible(true);


        }else{
            menu.getMenu().getItem(0).setVisible(false);
            menu.getMenu().getItem(1).setVisible(false);
            menu.getMenu().getItem(2).setVisible(false);
            menu.getMenu().getItem(3).setVisible(false);
            menu.getMenu().getItem(9).setVisible(false);

            menu.getMenu().getItem(4).setVisible(true);
            menu.getMenu().getItem(6).setVisible(true);



        }
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
        loadingDialog = new MaterialDialog.Builder(activity)
                .content(message)
                .progress(true, 0)
                .cancelable(false)
                .build();
        if(!loadingDialog.isShowing()){
            loadingDialog.show();
        }

    }

    public void hideLoadingDialog(){
        loadingDialog.dismiss();
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
}

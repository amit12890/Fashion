package com.fashion.krish.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by amit.thakkar on 7/8/2015.
 */
public class ProductDetails {

    public String product_entity_id="",product_name="",product_entity_type="",product_sku="",product_url_key="",product_parent_id="",
            product_short_desc="",product_desc="",product_link="",product_icon="",product_price_regular="",product_type="",product_category="",
            product_expected_delivery="",product_price_special="",product_price_to="",product_price_from="";

    public HashMap<String,String> product_additional_attribute = new HashMap<>();

    public int product_in_stock=0,product_is_salable=0,product_has_gallery=0,product_has_option=0,product_rating_summery=0,product_review_count=0;

    public ArrayList<ProductOptions> productOptions = new ArrayList<>();

    public ArrayList<ProductGallery> productGalleries = new ArrayList<>();



    public class ProductOptions{
        public String option_code="",option_type="",option_label="",option_price="", option_formatted_price="",option_price_notice="",
                option_max_character="",option_is_required="",option_image_size_x="",option_image_size_y="",
                option_icon="",option_attr_code="";

        public int option_is_qty_editable = 0,option_qty = 0,options_isMulti=1;
        public ArrayList<ProductOptionsValue> productOptionsValues = new ArrayList<>();
    }

    public class ProductOptionsValue{
        public String value_code="",value_label="",value_relation="",value_price="",
                value_formatted_price="",value_tier_price="",value_tier_price_html="";
        public int custom_qty = 0;
        public ArrayList<String> sub_products = new ArrayList<>();
    }

    public class ProductGallery{
        public String image_code="",image_url_big="",image_url_small="",image_width="",
                image_height="",image_id="",image_modification_time="";
    }
}

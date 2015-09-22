package com.fashion.krish.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by amit.thakkar on 7/8/2015.
 */
public class Cart {

    public String cart_qty="0",subtotal_title="",subtotal_value="",subtotal_formated_value="",
            grandtotal_title="",grandtotal_value="",grandtotal_formated_value="",
            tax_title="",tax_value="",tax_formated_value="",discount_title="",discount_value="",discount_formated_value="",
            shipping_title="",shipping_value="",shipping_formated_value="";


    public ArrayList<CartItems> cartItems = new ArrayList<>();

    public class CartItems{
        public String item_entity_id = "", item_entity_type = "", item_id = "",item_name = "", item_code = "", item_qty = "",
                        item_max_qty = "", item_min_qty = "", item_icon = "", item_price = "", item_formated_price = "",
                        item_subtotal = "", item_formated_subtotal = "";

        public HashMap<String,String> item_options_map = new HashMap<>();
    }


}

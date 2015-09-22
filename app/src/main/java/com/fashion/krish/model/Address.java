package com.fashion.krish.model;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by amit.thakkar on 7/6/2015.
 */
public class Address {

    public String address_entity_id = "",address_fname = "",address_lname = "",address_street = "",address_street1 = "",
            address_city = "",address_region = "",address_country = "",address_country_id = "",
            address_zip = "",address_phone = "",address_label = "", address_company = "" , address_street2 = "",
            is_use_for_shipping = "0", save_in_address_book = "1";

    public boolean is_default = false, is_additional = false;

    public int is_selected = 0;


}

package com.fashion.krish.model;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by amit.thakkar on 7/6/2015.
 */
public class Category {

    public String label,entity_id,url_key,icon,content_type;
    public JSONArray subitem;
    public boolean is_selected = false;
    public ArrayList<SubCategory> subCategories = new ArrayList<SubCategory>();

}

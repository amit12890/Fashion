package com.fashion.krish.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by amit.thakkar on 7/6/2015.
 */
public class FilterCategory {

    public String filter_name,filter_code;
    public JSONObject filterValues;
    public boolean isSelected = false;
    public ArrayList<FilterValue> filterValuesArray = new ArrayList<FilterValue>();

}

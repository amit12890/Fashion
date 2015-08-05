package com.fashion.krish.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fashion.krish.R;
import com.fashion.krish.model.FilterCategory;
import com.rey.material.widget.RadioButton;

import java.util.ArrayList;

/**
 * Created by amit.thakkar on 7/14/2015.
 */
public class FilterCategoryAdapter extends ArrayAdapter<FilterCategory> {

    Activity activity;
    ArrayList<FilterCategory> filterCategories;

    public FilterCategoryAdapter(Activity activity, int layoutResourceId, ArrayList<FilterCategory> filterCategories) {

        super(activity, layoutResourceId, filterCategories);
        this.activity = activity;
        this.filterCategories = filterCategories;

    }
    @Override
    public int getCount() {
        return filterCategories.size();
    }

    @Override
    public FilterCategory getItem(int position) {
        return filterCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.filter_list_parent, null);

        if(filterCategories.size() > 0){
            final FilterCategory filterCategory = filterCategories.get(position);
            ((TextView) convertView.findViewById(R.id.text1)).setText(filterCategory.filter_name);


            if(filterCategory.isSelected){
                ((RelativeLayout) convertView.findViewById(R.id.lay_filter_list_parent)).setBackgroundResource(R.drawable.holo_white_red_ripple);
                ((TextView) convertView.findViewById(R.id.text1)).setTextColor(Color.parseColor("#000000"));
            }else{
                ((RelativeLayout) convertView.findViewById(R.id.lay_filter_list_parent)).setBackgroundResource(R.color.drawer_pressed);
                ((TextView) convertView.findViewById(R.id.text1)).setTextColor(Color.parseColor("#ffffff"));
            }
        }

        return convertView;
    }
}

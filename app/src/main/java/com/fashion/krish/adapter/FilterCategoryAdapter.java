package com.fashion.krish.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fashion.krish.AppController;
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

        ImageView separator = (ImageView) convertView.findViewById(R.id.filter_separator);
        separator.setBackgroundColor(Color.parseColor(AppController.SECONDARY_COLOR));
        separator.setAlpha(45);

        if(filterCategories.size() > 0){
            final FilterCategory filterCategory = filterCategories.get(position);
            ((TextView) convertView.findViewById(R.id.text1)).setText(filterCategory.filter_name);


            if(filterCategory.isSelected){
                ((RelativeLayout) convertView.findViewById(R.id.lay_filter_list_parent)).setBackgroundColor(Color.WHITE);
                ((TextView) convertView.findViewById(R.id.text1)).setTextColor(Color.parseColor("#000000"));
            }else{
                ((RelativeLayout) convertView.findViewById(R.id.lay_filter_list_parent)).
                        setBackgroundColor(Color.TRANSPARENT);
                ((TextView) convertView.findViewById(R.id.text1)).setTextColor(Color.parseColor("#ffffff"));
            }
        }

        return convertView;
    }
}

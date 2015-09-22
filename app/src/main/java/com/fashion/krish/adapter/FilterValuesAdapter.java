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
import android.widget.Toast;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.fragment.ProductListFragment;
import com.fashion.krish.model.FilterValue;
import com.fashion.krish.utility.Utility;
import com.rey.material.widget.RadioButton;

import java.util.ArrayList;

/**
 * Created by amit.thakkar on 7/14/2015.
 */
public class FilterValuesAdapter extends ArrayAdapter<FilterValue> {

    Activity activity;
    ArrayList<FilterValue> filterValues;

    public FilterValuesAdapter(Activity activity, int layoutResourceId, ArrayList<FilterValue> filterValues) {

        super(activity, layoutResourceId, filterValues);
        this.activity = activity;
        this.filterValues = filterValues;

    }
    @Override
    public int getCount() {
        return filterValues.size();
    }

    @Override
    public FilterValue getItem(int position) {
        return filterValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.filter_list_child, null);

        final FilterValue filterValue = filterValues.get(position);
        RelativeLayout baseLay = (RelativeLayout) convertView.findViewById(R.id.lay_filter_child);
        ((TextView) convertView.findViewById(R.id.text1)).setText(filterValue.filter_value_label);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.
                LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL,1);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,1);
        layoutParams.setMargins(0,0, (int) Utility.convertDpToPixel(10,activity),0);

        RadioButton radioButton = new RadioButton(activity, Color.parseColor( AppController.SECONDARY_COLOR));
        radioButton.setLayoutParams(layoutParams);

        baseLay.addView(radioButton);
        //RadioButton radioButton = (RadioButton) convertView.findViewById(R.id.radio_selection);

        if(filterValue.isSelected){
            radioButton.setChecked(true);

        }else {
            radioButton.setChecked(false);
        }
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {

                    ProductListFragment.filterValuesMap.put(filterValue.filter_value_parent, filterValue.filter_value_id);
                    Toast.makeText(activity, "Checked", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return convertView;
    }
}

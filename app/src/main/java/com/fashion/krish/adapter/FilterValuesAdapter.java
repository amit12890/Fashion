package com.fashion.krish.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fashion.krish.R;
import com.fashion.krish.fragment.ProductListFragment;
import com.fashion.krish.model.FilterValue;
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
        ((TextView) convertView.findViewById(R.id.text1)).setText(filterValue.filter_value_label);

        if(filterValue.isSelected){
            ((RadioButton) convertView.findViewById(R.id.radio_selection)).setChecked(true);
        }else {
            ((RadioButton) convertView.findViewById(R.id.radio_selection)).setChecked(false);
        }
        ((RadioButton) convertView.findViewById(R.id.radio_selection)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if(isChecked){

                    ProductListFragment.filterValuesMap.put(filterValue.filter_value_parent,filterValue.filter_value_id);
                    Toast.makeText(activity,"Checked",Toast.LENGTH_SHORT).show();
                }
            }
        });


        return convertView;
    }
}

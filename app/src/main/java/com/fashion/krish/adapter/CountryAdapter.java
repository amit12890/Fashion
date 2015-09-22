package com.fashion.krish.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
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
import java.util.HashMap;
import java.util.List;

/**
 * Created by amit.thakkar on 7/14/2015.
 */
public class CountryAdapter extends BaseAdapter implements Filterable {

    private ArrayList<String>originalData = null;
    private ArrayList<String>filteredData = null;
    Activity activity;
    HashMap<String,String> countryMap;
    private String[] mKeys;
    private ItemFilter mFilter = new ItemFilter();

    public CountryAdapter(Activity activity, HashMap<String,String> countryMap) {

        this.activity = activity;
        this.countryMap = countryMap;
        mKeys = countryMap.keySet().toArray(new String[countryMap.size()]);

        originalData = new ArrayList<>();
        filteredData =  new ArrayList<>();

        for (String key: countryMap.keySet()) {
            originalData.add(countryMap.get(key));
            filteredData.add(countryMap.get(key));
        }


    }
    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public String getItem(int position) {
        return filteredData.get(position);
    }



    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.country_list_child, null);

        ((TextView) convertView.findViewById(R.id.text1)).setText(filteredData.get(position));

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<String> list = originalData;

            int count = list.size();
            final ArrayList<String> nlist = new ArrayList<String>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }

    }

}

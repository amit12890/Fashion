/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fashion.krish.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.model.ProductDetails;
import com.fashion.krish.utility.AppPreferences;
import com.fashion.krish.utility.Utility;
import com.rey.material.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductDetailsPagerFragment extends Fragment {

	private static final String ARG_POSITION = "position";
	private static final String ARG_TITLE = "title";
	private int position;
	private String title;


	Utility util;

	private AppPreferences preferences;
	private static ProductDetails productDetails;

	public static ProductDetailsPagerFragment newInstance(int position,ProductDetails productDetails,String title) {

		ProductDetailsPagerFragment.productDetails = productDetails;
		ProductDetailsPagerFragment f = new ProductDetailsPagerFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		b.putString(ARG_TITLE,title);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		position = getArguments().getInt(ARG_POSITION);
		title = getArguments().getString(ARG_TITLE);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		LayoutInflater infalInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		util = new Utility(getActivity());
		preferences = new AppPreferences(getActivity());

		LinearLayout baseView = (LinearLayout) inflater.inflate(R.layout.product_details_pager_fragment, null);

		if(title.equals("ADDITIONAL INFO")){
			addAdditionInfoView(baseView);
		}else if(title.equals("REVIEWS")){
			addReviewLayout(baseView);
			//addReviewLayout(baseView);
		}else if(title.equals("DESCRIPTION")) {
			addDescriptionView(baseView);
		}else {
			addSampleView(baseView);
		}

		return baseView;
	}

	public void addDescriptionView(LinearLayout baseView){

		//LinearLayout baseView = (LinearLayout) inflater.inflate(R.layout.product_details_pager_fragment, null);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		TextView txt_product_detail = new TextView(getActivity());
		txt_product_detail.setText(productDetails.product_short_desc);
		txt_product_detail.setTextColor(Color.parseColor("#5F5F5F"));
		txt_product_detail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		txt_product_detail.setLayoutParams(params);
		baseView.addView(txt_product_detail);

	}

	public void addAdditionInfoView(LinearLayout baseView){

		//LinearLayout baseView = (LinearLayout) inflater.inflate(R.layout.product_details_pager_fragment, null);

		for (HashMap.Entry<String,String> entry : productDetails.product_additional_attribute.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			int padding_value = (int) Utility.convertDpToPixel(5,getActivity());
			int padding_value_start = (int) Utility.convertDpToPixel(10,getActivity());

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);

			TextView txt_key = new TextView(getActivity());
			txt_key.setText(key);
			txt_key.setTextColor(Color.parseColor("#5F5F5F"));
			txt_key.setTypeface(null, Typeface.BOLD);
			txt_key.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			txt_key.setBackgroundColor(Color.parseColor("#F8F8F8"));
			txt_key.setLayoutParams(params);
			txt_key.setPadding(padding_value_start,padding_value,0,padding_value);
			baseView.addView(txt_key);

			TextView txt_value = new TextView(getActivity());
			txt_value.setText(value);
			txt_value.setTextColor(Color.parseColor("#5F5F5F"));
			txt_value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			txt_value.setLayoutParams(params);
			txt_value.setPadding(padding_value_start, padding_value, 0, padding_value);
			baseView.addView(txt_value);
		}

	}

	public void addReviewLayout(LinearLayout baseView){

		//LinearLayout baseView = (LinearLayout) inflater.inflate(R.layout.product_details_pager_fragment, null);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		TextView txt_product_detail = new TextView(getActivity());
		txt_product_detail.setText(productDetails.product_short_desc);
		txt_product_detail.setTextColor(Color.parseColor("#5F5F5F"));
		txt_product_detail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		txt_product_detail.setLayoutParams(params);
		baseView.addView(txt_product_detail);

	}

	public void addSampleView(LinearLayout baseView){


		LayoutInflater inflate = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View sampleRootview = inflate.inflate(R.layout.layout_sample, null);

		ArrayList<ProductDetails.ProductSample> productSampleArray = productDetails.productSamples;
		TextView txt = (TextView) sampleRootview.findViewById(R.id.txt_sample_title);
		txt.setText(productSampleArray.get(0).sample_label);

		LinearLayout lay_= (LinearLayout) sampleRootview.findViewById(R.id.lay_sample_container);

		for(final ProductDetails.ProductSample productSample : productSampleArray){

			com.rey.material.widget.TextView sampleTxt = new com.rey.material.widget.TextView(getActivity());
			sampleTxt.setText("• " + productSample.sample_label_item);
			sampleTxt.setTag(productSample.sample_url);
			sampleTxt.setLinksClickable(true);
			sampleTxt.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (productSample.sample_url.length() > 0) {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(productSample.sample_url));
						startActivity(i);
					}

				}
			});

			sampleTxt.setGravity(Gravity.CENTER_VERTICAL);
			lay_.addView(sampleTxt);
		}
		baseView.addView(sampleRootview);

	}

}
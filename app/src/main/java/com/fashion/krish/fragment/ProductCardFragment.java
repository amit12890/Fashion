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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.activity.ProductDetailActivity;
import com.fashion.krish.model.Product;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class ProductCardFragment extends Fragment {

	private static final String ARG_POSITION = "position";
	private int position;
	private ArrayList<Product> productList;

	ImageLoader imageLoader;
	DisplayImageOptions options;


	public static ProductCardFragment newInstance(int position) {
		ProductCardFragment f = new ProductCardFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		position = getArguments().getInt(ARG_POSITION);
		productList = AppController.homeCategoryArrayList.get(position).productsList;

		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).resetViewBeforeLoading(true)
				.showImageForEmptyUri(R.drawable.placeholder)
				.showImageOnFail(R.drawable.placeholder)
				.showImageOnLoading(R.drawable.placeholder).build();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		LayoutInflater infalInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View baseView = infalInflater.inflate(R.layout.home_category_child, null);

		LinearLayout productListLay = (LinearLayout) baseView.findViewById(R.id.layout_product_container);


		//LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		for(int i = 0; i<productList.size(); i++){
			View productView = infalInflater.inflate(R.layout.product_layout, null);

			final Product product = productList.get(i);
			ImageView productImage = (ImageView) productView.findViewById(R.id.img_product_main);
			imageLoader.displayImage(product.product_icon, productImage, options);
			((TextView) productView.findViewById(R.id.txt_regular_price)).setText(product.product_price_regular);
			((TextView) productView.findViewById(R.id.txt_product_name)).setText(product.product_name);
			((RatingBar) productView.findViewById(R.id.rating_product)).setRating((product.product_rating_summery) / 2);
			ImageView productTag = (ImageView) productView.findViewById(R.id.img_product_tag);
			if(product.product_is_new == 1 && product.product_is_salable == 1)
				productTag.setImageResource(R.drawable.new_sale_tag);
			else if(product.product_is_new == 1 && product.product_is_salable == 0)
				productTag.setImageResource(R.drawable.new_tag);
			else if(product.product_is_new == 0 && product.product_is_salable == 1)
				productTag.setImageResource(R.drawable.sale_tag);
			else if(product.product_is_new == 0 && product.product_is_salable == 0)
				productTag.setVisibility(View.GONE);

			productListLay.addView(productView);

			productView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(getActivity(), ProductDetailActivity.class);
					i.putExtra("product_id", product.product_entity_id);
					i.putExtra("product_sku", product.product_sku);
					i.putExtra("product_name", product.product_name);
					i.putExtra("product_price_regular", product.product_price_regular);
					i.putExtra("product_rate", product.product_rating_summery);
					i.putExtra("category_name", "Home");
					startActivity(i);
				}
			});
		}

		return baseView;
	}

}
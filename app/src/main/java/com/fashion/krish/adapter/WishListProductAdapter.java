package com.fashion.krish.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.fashion.krish.R;
import com.fashion.krish.model.Product;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by amit.thakkar on 7/14/2015.
 */
public class WishListProductAdapter extends ArrayAdapter<Product> {

    Activity activity;
    ArrayList<Product> products;
    ImageLoader imageLoader;
    DisplayImageOptions options;
    int resource_layout;



    public WishListProductAdapter(Activity activity, int layoutResourceId, ArrayList<Product> productsData) {

        super(activity, layoutResourceId, productsData);
        this.activity = activity;
        this.products = productsData;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true)
                .resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.placeholder)
                .showImageOnFail(R.drawable.placeholder)
                .showImageOnLoading(R.drawable.placeholder).build();
        this.resource_layout = layoutResourceId;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Product getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(resource_layout, null);

        final Product product = products.get(position);
        RelativeLayout productLay = (RelativeLayout) convertView.findViewById(R.id.lay_productview);
        ImageView productImage = (ImageView) convertView.findViewById(R.id.img_product_main);
        imageLoader.displayImage(product.product_icon, productImage, options);

        if(product.product_entity_type.equals("bundle")){
            ((TextView) convertView.findViewById(R.id.txt_regular_price)).setText("From: "+product.product_price_from
                        +" To: "+product.product_price_to);
        }else if(product.product_entity_type.equals("grouped")){
            ((TextView) convertView.findViewById(R.id.txt_regular_price)).setText(product.product_price_regular);
        }else{
            if(product.product_price_special.length() != 0){
                ((TextView) convertView.findViewById(R.id.txt_regular_price)).setText(product.product_price_special);
            }else{
                ((TextView) convertView.findViewById(R.id.txt_regular_price)).setText(product.product_price_regular);
            }
        }

        ImageView productTag = (ImageView) convertView.findViewById(R.id.img_product_tag);
        productTag.setVisibility(View.GONE);

        //((TextView) convertView.findViewById(R.id.txt_regular_price)).setText(product.product_price_regular);
        ((TextView) convertView.findViewById(R.id.txt_product_name)).setText(product.product_name);
        ((RatingBar) convertView.findViewById(R.id.rating_product)).setVisibility(View.GONE);

        return convertView;
    }
}

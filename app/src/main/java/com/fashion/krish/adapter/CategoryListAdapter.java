package com.fashion.krish.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fashion.krish.R;
import com.fashion.krish.model.Category;
import com.fashion.krish.model.SubCategory;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by amit.thakkar on 7/7/2015.
 */
public class CategoryListAdapter extends BaseExpandableListAdapter {

    private Activity activity;
    private ArrayList<Category> categoryArrayList;
    private static final int GROUP_ITEM_RESOURCE = R.layout.expandablelist_group;
    private static final int CHILD_ITEM_RESOURCE = R.layout.expandablelist_child;

    ImageLoader imageLoader;
    DisplayImageOptions options;

    public CategoryListAdapter(Activity activity, ArrayList<Category> categoryArrayList){

        this.categoryArrayList = categoryArrayList;
        this.activity = activity;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();

    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        Category category = categoryArrayList.get(groupPosition);
        return category.subCategories.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        View view = convertView;
        Category category = categoryArrayList.get(groupPosition);
        SubCategory subCategory = category.subCategories.get(childPosition);

        if (subCategory != null) {
            LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(CHILD_ITEM_RESOURCE, null);
            ViewHolder holder = new ViewHolder(view);
            holder.text.setText(Html.fromHtml(subCategory.label));

            if(subCategory.is_selected){
                holder.divider.setVisibility(View.VISIBLE);

            }else{
                holder.divider.setVisibility(View.INVISIBLE);

            }

        }
        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Category category = categoryArrayList.get(groupPosition);
        return category.subCategories.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categoryArrayList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        Category category = categoryArrayList.get(groupPosition);

        if (category != null) {
            LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(GROUP_ITEM_RESOURCE, null);
            final ViewHolder holder = new ViewHolder(view);

            if(isExpanded){
                holder.imageview.setImageResource(R.drawable.arrow_down);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.layBase.setBackgroundResource(R.drawable.holo_red_white_ripple);
                        holder.text.setTextColor(Color.WHITE);
                    }
                }, 100);

                //holder.text.setTextColor(Color.parseColor("#ffffff"));
            }else{

                holder.imageview.setImageResource(R.drawable.arrow_side);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.layBase.setBackgroundResource(R.drawable.holo_white_red_ripple);
                        holder.text.setTextColor(Color.parseColor("#333333"));
                    }
                }, 400);



                //holder.layBase.setBackgroundColor(Color.parseColor("#ffffff"));
                //holder.text.setTextColor(Color.parseColor("#333333"));
            }


            holder.text.setText(Html.fromHtml(category.label));
            holder.categoryicon.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            SVG svg = SVGParser.getSVGFromResource(activity.getResources(), R.raw.men_menu_icon,  0xFF9FBF3B, 0xFF1756c9);
             //svg.setDocumentHeight(Utility.convertDpToPixel(20,activity));
             //svg.setDocumentWidth(Utility.convertDpToPixel(20,activity));
             //svg.setRenderDPI(15);


            holder.categoryicon.setImageDrawable(svg.createPictureDrawable());

            //imageLoader.displayImage("http://coupcommerce.magentoprojects.net/media/catalog/category/men-menu-icon.svg",holder.categoryicon,options);
            if(category.subCategories.size()==0){
                holder.imageview.setVisibility(View.INVISIBLE);
            }

        }
        return view;
    }

    @Override
    public int getGroupCount() {
        return categoryArrayList.size();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public class ViewHolder {
        public TextView text;
        public ImageView imageview,categoryicon,divider;
        public RelativeLayout layBase;
        public ViewHolder(View v) {
            this.text = (TextView)v.findViewById(R.id.text1);
            this.imageview = (ImageView)v.findViewById(R.id.img_indicator);
            this.categoryicon = (ImageView)v.findViewById(R.id.img_category);
            this.layBase = (RelativeLayout)v.findViewById(R.id.layout_expandable_base);
            this.divider = (ImageView)v.findViewById(R.id.img_divider);
        }

    }

}

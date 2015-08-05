package com.fashion.krish.activity;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fashion.krish.R;
import com.liuguangqiang.swipeback.SwipeBackActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;


public class ProductGalleryActivity extends ActionBarActivity {

    ImageLoader imageLoader;
    DisplayImageOptions options;
    List<String> pagerGalleryUrlList;
    private TextView txtTitle;
    private ImageView imgProduct;
    private LinearLayout layBack;
    private String title,imageIcon;
    private ViewPager mGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        setContentView(R.layout.activity_product_image_fullscreen);

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.logo)
                .showImageOnFail(R.drawable.logo)
                .showImageOnLoading(R.drawable.logo).build();

        title = getIntent().getStringExtra("product_name");
        pagerGalleryUrlList = new ArrayList<>();

        if(getIntent().hasExtra("image_uri"))
            imageIcon = getIntent().getStringExtra("image_uri");

        if(getIntent().hasExtra("image_uri_array"))
            pagerGalleryUrlList = getIntent().getStringArrayListExtra("image_uri_array");


        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText(title);
        imgProduct = (ImageView) findViewById(R.id.image_full);

        layBack = (LinearLayout) findViewById(R.id.lay_back);
        layBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);
            }
        });

        mGallery = (ViewPager) findViewById(R.id.pager_fullscreen_glry);
        GalleryPagerAdapter galleryPagerAdapter = new GalleryPagerAdapter();


        if(imageIcon.length()>0 && pagerGalleryUrlList.size() == 0){
            imgProduct.setVisibility(View.VISIBLE);
            mGallery.setVisibility(View.GONE);
            imageLoader.displayImage(imageIcon, imgProduct, options);
        }else{
            imgProduct.setVisibility(View.GONE);
            mGallery.setVisibility(View.VISIBLE);
            mGallery.setAdapter(galleryPagerAdapter);
        }
    }

    class GalleryPagerAdapter extends PagerAdapter {

        LayoutInflater mLayoutInflater;

        public GalleryPagerAdapter() {
            mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return pagerGalleryUrlList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.fullscreen_pager_child, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.image_full);
            //imageView.setImageResource(mResources[position]);
            //Glide.with(getActivity()).load(bannerUrlList.get(position)).into(imageView);

            imageLoader.displayImage(pagerGalleryUrlList.get(position),imageView,options);
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);
    }
}

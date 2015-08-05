package com.fashion.krish.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.model.HomeBanners;
import com.fashion.krish.utility.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment implements View.OnClickListener{

    private BannerPagerAdapter bannerPagerAdapter;

    private int NUM_PAGES = 3;
    private ViewPager mViewPager;
    private List<ImageView> dots;
    List<String> bannerUrlList;
    List<String> staticBannerUrlList;
    private LinearLayout layStaticBanner;
    ImageLoader imageLoader;
    DisplayImageOptions options;

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;

    private CirclePageIndicator circleIndicator;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), android.R.style.Theme_DeviceDefault);
        LayoutInflater localInflater = inflater.from(contextThemeWrapper);

        View rootView = localInflater.inflate(R.layout.fragment_home, container, false);

        rootView.findViewById(R.id.btn_aboutus).setOnClickListener(this);
        rootView.findViewById(R.id.btn_shipping).setOnClickListener(this);
        rootView.findViewById(R.id.btn_faq).setOnClickListener(this);
        rootView.findViewById(R.id.btn_returns).setOnClickListener(this);

        /*ImageView img =(ImageView) rootView.findViewById(R.id.img_overflow);
        PopupMenu popup = new PopupMenu(getActivity(), img);
        MenuInflater inflater1 = popup.getMenuInflater();
        inflater1.inflate(R.menu.popup_dashboard, popup.getMenu());
        popup.show();*/

        layStaticBanner = (LinearLayout) rootView.findViewById(R.id.lay_static_banner);
        bannerUrlList = new ArrayList<String>();
        staticBannerUrlList = new ArrayList<String>();
        for(int i = 0; i< AppController.homeBannersArrayList.size(); i++){
            HomeBanners homeBanners = AppController.homeBannersArrayList.get(i);
            if(homeBanners.type.equals("rotate")){
                bannerUrlList.add(homeBanners.image);
            }else{
                staticBannerUrlList.add(homeBanners.image);
            }
        }

        NUM_PAGES = bannerUrlList.size();

        bannerPagerAdapter = new BannerPagerAdapter(getActivity());

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.logo)
                .showImageOnFail(R.drawable.logo)
                .showImageOnLoading(R.drawable.logo).build();

        mViewPager = (ViewPager) rootView.findViewById(R.id.pager_rotate_banner);
        mViewPager.setAdapter(bannerPagerAdapter);
        addDots(rootView);
        addStaticBanners();

        tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
        tabs.setIndicatorColor(Color.WHITE);
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setIndicatorHeight((int) Utility.convertDpToPixel(7, getActivity()));
        tabs.setTabBackground(R.drawable.holo_red_white_ripple);
        tabs.setIndicatorColor(Color.WHITE);

        pager = (ViewPager) rootView.findViewById(R.id.pager_category);
        adapter = new MyPagerAdapter(getFragmentManager());
        pager.setAdapter(adapter);

        circleIndicator = (CirclePageIndicator)rootView.findViewById(R.id.indicator_dots);
        circleIndicator.setRadius(15);
        circleIndicator.setViewPager(mViewPager);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public void addDots(View view) {
        dots = new ArrayList<>();
        LinearLayout dotsLayout = (LinearLayout)view.findViewById(R.id.dots);

        for(int i = 0; i < NUM_PAGES; i++) {
            ImageView dot = new ImageView(getActivity());
            dot.setImageDrawable(getResources().getDrawable(R.drawable.pager_dot_not_selected));
            if(i==0){
                dot.setImageDrawable(getResources().getDrawable(R.drawable.pager_dot_selected));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            dotsLayout.addView(dot, params);

            dots.add(dot);
        }

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                selectDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void selectDot(int idx) {
        Resources res = getResources();
        for (int i = 0; i < NUM_PAGES; i++) {
            int drawableId = (i == idx) ? (R.drawable.pager_dot_selected) : (R.drawable.pager_dot_not_selected);
            Drawable drawable = res.getDrawable(drawableId);
            dots.get(i).setImageDrawable(drawable);
        }
    }

    class BannerPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public BannerPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.rotate_banner_child, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.image_rotate_banner);
            //imageView.setImageResource(mResources[position]);
            //Glide.with(getActivity()).load(bannerUrlList.get(position)).into(imageView);
            imageLoader.displayImage(bannerUrlList.get(position),imageView,options);
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    public void addStaticBanners(){
        for(int i = 0; i<staticBannerUrlList.size(); i++){
            ImageView staticBannerImage = new ImageView(getActivity());
            //Glide.with(getActivity()).load(staticBannerUrlList.get(i)).into(staticBannerImage);
            staticBannerImage.setScaleType(ImageView.ScaleType.FIT_XY);
            imageLoader.displayImage(staticBannerUrlList.get(i), staticBannerImage,options);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) Utility.convertDpToPixel(150,getActivity())
            );
            layStaticBanner.addView(staticBannerImage, params);

        }

    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return AppController.homeCategoryArrayList.get(position).name;
        }

        @Override
        public int getCount() {
            return AppController.homeCategoryArrayList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return ProductCardFragment.newInstance(position);
        }

    }

    @Override
    public void onClick(View v) {
        CMSFragment cmsFragment;
        switch (v.getId()){
            case R.id.btn_aboutus:
                cmsFragment = new CMSFragment("about-us");
                updateFragment(cmsFragment);

                break;
            case R.id.btn_shipping:
                cmsFragment = new CMSFragment("shipping");
                updateFragment(cmsFragment);

                break;
            case R.id.btn_faq:
                cmsFragment = new CMSFragment("faq");
                updateFragment(cmsFragment);

                break;
            case R.id.btn_returns:
                cmsFragment = new CMSFragment("returns");
                updateFragment(cmsFragment);

                break;
        }
    }

    public void updateFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();

        //Replace fragment
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }


}

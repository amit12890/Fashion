package com.fashion.krish.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.activity.DashboardActivity;
import com.fashion.krish.utility.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rey.material.widget.TabPageIndicator;

public class LoginFragment extends Fragment{


    private int NUM_PAGES = 2;
    ImageLoader imageLoader;
    DisplayImageOptions options;

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private LoginPagerAdapter adapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.logo)
                .showImageOnFail(R.drawable.logo)
                .showImageOnLoading(R.drawable.logo).build();

        tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.login_tabs);
        tabs.setIndicatorColor(Color.WHITE);
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setIndicatorHeight((int) Utility.convertDpToPixel(7, getActivity()));
        tabs.setBackgroundColor(Color.parseColor(AppController.SECONDARY_COLOR));
        tabs.setTextColorResource(R.color.tab_text_color);
        tabs.setIndicatorColor(Color.WHITE);

        pager = (ViewPager) rootView.findViewById(R.id.login_pager);
        adapter = new LoginPagerAdapter(getFragmentManager());
        pager.setAdapter(adapter);

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

    public class LoginPagerAdapter extends FragmentStatePagerAdapter {

        String[] TITLES = {"SIGN IN","SIGN UP"};
        public LoginPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return LoginSignUpFragment.newInstance(position);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DashboardActivity.animateToggle(1, 0);
    }

}

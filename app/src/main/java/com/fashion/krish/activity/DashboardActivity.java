package com.fashion.krish.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.adapter.CategoryListAdapter;
import com.fashion.krish.fragment.CMSFragment;
import com.fashion.krish.fragment.ContactUsFragment;
import com.fashion.krish.fragment.DashboardFragment;
import com.fashion.krish.fragment.LoginFragment;
import com.fashion.krish.fragment.ProductListFragment;
import com.fashion.krish.fragment.StoreLocaterFragment;
import com.fashion.krish.model.Category;
import com.fashion.krish.model.SubCategory;
import com.fashion.krish.utility.AppPreferences;
import com.fashion.krish.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;


public class DashboardActivity extends ActionBarActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private RelativeLayout mDrawerInnerLayout,mDrawerCartlayout,mDrawerFilterLayout,mDrawerLayoutOther;
    private RelativeLayout mLayShareApp,mLayContactUs,mLayStoreLocate,mLayHome,mLayCustomerService,mLayPrivacy,mLayRewards;
    private CategoryListAdapter categoryListAdapter;
    private ExpandableListView categoryListView;
    private ScrollView scrollView;

    private Toolbar toolbar;
    private ImageView imgCart,imgSearch,imgMenu;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private LinearLayout layFilterItems,layDashboardItems;
    private ImageView imgLogo;
    private TextView txtTitle;

    private MaterialDialog dialog;

    private AppPreferences preferences;
    public static PopupMenu popup;
    private Utility util;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_dashboard);

        preferences = new AppPreferences(DashboardActivity.this);
        util = new Utility(DashboardActivity.this);
        initViews();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
           // toolbar.setLogo(R.drawable.logo);
            setSupportActionBar(toolbar);
        }
        initDrawer();

        categoryListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                v.setSelected(true);
                RelativeLayout lay = (RelativeLayout) v;
                lay.setBackgroundResource(R.color.drawer_pressed);
                parent.setItemChecked(groupPosition, true);
                if (categoryListAdapter.getChildrenCount(groupPosition) == 0) {
                    Category category = (Category) categoryListAdapter.getGroup(groupPosition);
                    ProductListFragment productListFragment = new ProductListFragment(category.entity_id,category.label);
                    updateFragment(productListFragment);
                    drawerLayout.closeDrawers();
                }
                return false;
            }
        });
        categoryListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                SubCategory subcategory = (SubCategory) categoryListAdapter.getChild(groupPosition, childPosition);
                ProductListFragment productListFragment = new ProductListFragment(subcategory.entity_id, subcategory.label);
                updateFragment(productListFragment);
                //((View) v.findViewById(R.id.divider_child)).setVisibility(View.VISIBLE);
                drawerLayout.closeDrawers();
                for (int i = 0; i < AppController.categoryArrayList.size(); i++) {
                    if (AppController.categoryArrayList.get(i).subCategories.size() > 0) {
                        for (int j = 0; j < AppController.categoryArrayList.get(i).subCategories.size(); j++) {
                            if (i == groupPosition && j == childPosition) {
                                AppController.categoryArrayList.get(i).subCategories.get(j).is_selected = true;
                                // ((View) v.findViewById(R.id.divider_child)).setVisibility(View.VISIBLE);
                            } else {
                                AppController.categoryArrayList.get(i).subCategories.get(j).is_selected = false;
                                // ((View) v.findViewById(R.id.divider_child)).setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }
                categoryListAdapter.notifyDataSetChanged();

                return false;
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(mDrawerCartlayout) && !drawerLayout.isDrawerOpen(mDrawerFilterLayout) &&
                        !drawerLayout.isDrawerOpen(mDrawerInnerLayout) &&
                        !drawerLayout.isDrawerOpen(mDrawerLayoutOther)) {

                    drawerLayout.openDrawer(mDrawerInnerLayout);

                } else if (drawerLayout.isDrawerOpen(mDrawerCartlayout)) {

                    drawerLayout.closeDrawer(mDrawerCartlayout);

                } else if (drawerLayout.isDrawerOpen(mDrawerFilterLayout)) {

                    drawerLayout.closeDrawer(mDrawerFilterLayout);
                    layFilterItems.setVisibility(View.GONE);
                    layDashboardItems.setVisibility(View.VISIBLE);
                    imgLogo.setVisibility(View.VISIBLE);
                    txtTitle.setVisibility(View.GONE);

                } else if (drawerLayout.isDrawerOpen(mDrawerInnerLayout)) {

                    drawerLayout.closeDrawer(mDrawerInnerLayout);

                } else if(drawerLayout.isDrawerOpen(mDrawerLayoutOther)){

                    drawerLayout.closeDrawer(mDrawerLayoutOther);
                    layFilterItems.setVisibility(View.GONE);
                    layDashboardItems.setVisibility(View.GONE);
                    imgLogo.setVisibility(View.GONE);
                    txtTitle.setVisibility(View.GONE);

                }

            }
        });




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

        }
        Toast.makeText(DashboardActivity.this, "Click",Toast.LENGTH_SHORT).show();
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if(item.getItemId()==android.R.id.home){
            Toast.makeText(DashboardActivity.this, "Home",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);

    }

    public void initViews(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        LayoutInflater infalInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = infalInflater.inflate(R.layout.drawer_bottom_part, null);
        View topview = infalInflater.inflate(R.layout.drawer_top_part, null);

        mLayStoreLocate = (RelativeLayout) view.findViewById(R.id.navdrawer_lay_store_locate);
        mLayStoreLocate.setOnClickListener(this);
        mLayShareApp = (RelativeLayout) view.findViewById(R.id.navdrawer_lay_share_app);
        mLayShareApp.setOnClickListener(this);
        mLayContactUs = (RelativeLayout) view.findViewById(R.id.navdrawer_lay_contact_us);
        mLayContactUs.setOnClickListener(this);
        mLayCustomerService = (RelativeLayout) view.findViewById(R.id.navdrawer_lay_customer_service);
        mLayCustomerService.setOnClickListener(this);
        mLayPrivacy = (RelativeLayout) view.findViewById(R.id.navdrawer_lay_privacy);
        mLayPrivacy.setOnClickListener(this);
        mLayRewards = (RelativeLayout) view.findViewById(R.id.navdrawer_lay_rewards);
        mLayRewards.setOnClickListener(this);

        mLayHome = (RelativeLayout) topview.findViewById(R.id.navdrawer_lay_home);
        mLayHome.setOnClickListener(this);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        categoryListView = (ExpandableListView) findViewById(R.id.categoryExpandableListView);
        categoryListAdapter = new CategoryListAdapter(DashboardActivity.this, AppController.categoryArrayList);
        categoryListView.addFooterView(view);
        categoryListView.addHeaderView(topview);

        imgSearch =(ImageView) findViewById(R.id.img_search);
        imgSearch.setOnClickListener(this);
        imgCart =(ImageView) findViewById(R.id.img_cart);
        imgCart.setOnClickListener(this);
        imgMenu =(ImageView) findViewById(R.id.img_overflow);
        imgMenu.setOnClickListener(this);

        layDashboardItems = (LinearLayout) findViewById(R.id.lay_dashboard_items);
        layFilterItems = (LinearLayout) findViewById(R.id.lay_filter_items);
        imgLogo = (ImageView) findViewById(R.id.img_logo_title);
        txtTitle = (TextView) findViewById(R.id.txt_filter_title);

        dialog = new MaterialDialog.Builder(DashboardActivity.this)
                .content("Please Wait")
                .progress(true, 0)
                .build();


        popup = new PopupMenu(DashboardActivity.this, imgMenu, Gravity.NO_GRAVITY, R.attr.actionOverflowMenuStyle, R.style.OverflowMenu);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_dashboard, popup.getMenu());
        util.setPopUpMenuItems();
        //popup.getMenu().getItem(1).setVisible(false);
        popup.setOnMenuItemClickListener(this);

        mDrawerInnerLayout = (RelativeLayout) findViewById(R.id.navdrawerlayout);
        mDrawerCartlayout = (RelativeLayout) findViewById(R.id.navdrawerlayout_right);
        mDrawerFilterLayout = (RelativeLayout) findViewById(R.id.navdrawerlayout_filer);
        mDrawerLayoutOther = (RelativeLayout) findViewById(R.id.navdrawerlayout_other);

        DashboardFragment dashboardFragment = new DashboardFragment();
        updateFragment(dashboardFragment);
        categoryListView.setAdapter(categoryListAdapter);
    }

    @Override
    public void onClick(View v) {
        final Handler handler = new Handler();
        switch (v.getId()){
            case R.id.navdrawer_lay_store_locate:

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        StoreLocaterFragment storeLocaterFragment = new StoreLocaterFragment();
                        updateFragment(storeLocaterFragment);
                        drawerLayout.closeDrawers();

                    }
                }, 400);
                break;
            case R.id.navdrawer_lay_contact_us:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        ContactUsFragment contactUsFragment = new ContactUsFragment();
                        CMSFragment cmsFragment = new CMSFragment("shipping");
                        updateFragment(cmsFragment);

                        drawerLayout.closeDrawers();

                    }
                }, 400);
                break;
            case R.id.navdrawer_lay_home:

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        DashboardFragment dashboardFragment = new DashboardFragment();
                        updateFragment(dashboardFragment);
                        drawerLayout.closeDrawers();

                    }
                }, 400);
                break;
            case R.id.img_cart:
                    if(!drawerLayout.isDrawerOpen(mDrawerInnerLayout)){
                        if(!drawerLayout.isDrawerOpen(mDrawerCartlayout)){
                            drawerLayout.openDrawer(Gravity.RIGHT);

                        }else{
                            drawerLayout.closeDrawer(mDrawerCartlayout);
                        }
                    }else{
                        drawerLayout.closeDrawer(mDrawerInnerLayout);
                     }

                break;
            case R.id.img_overflow:
                drawerLayout.closeDrawer(mDrawerCartlayout);
                drawerLayout.closeDrawer(mDrawerFilterLayout);
                drawerLayout.closeDrawer(mDrawerInnerLayout);
                drawerLayout.closeDrawer(mDrawerLayoutOther);
                popup.show();

                break;
            case R.id.navdrawer_lay_customer_service:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        ContactUsFragment contactUsFragment = new ContactUsFragment();
                        CMSFragment cmsFragment = new CMSFragment("customer-service");
                        updateFragment(cmsFragment);

                        drawerLayout.closeDrawers();

                    }
                }, 400);
                break;

            case R.id.navdrawer_lay_privacy:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        ContactUsFragment contactUsFragment = new ContactUsFragment();
                        CMSFragment cmsFragment = new CMSFragment("privacy-policy-cookie-restriction-mode");
                        updateFragment(cmsFragment);

                        drawerLayout.closeDrawers();

                    }
                }, 400);
                break;
            case R.id.navdrawer_lay_rewards:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        ContactUsFragment contactUsFragment = new ContactUsFragment();
                        CMSFragment cmsFragment = new CMSFragment("reward-points");
                        updateFragment(cmsFragment);

                        drawerLayout.closeDrawers();

                    }
                }, 400);
                break;

            case R.id.img_search:
                /*try {

                    executeMultipartPost();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(e.getClass().getName(), e.getMessage());
                }*/
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

    private void initDrawer() {


        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                    if(drawerView == mDrawerFilterLayout || drawerView == mDrawerLayoutOther) {

                        drawerLayout.closeDrawer(mDrawerFilterLayout);
                        layFilterItems.setVisibility(View.GONE);
                        layDashboardItems.setVisibility(View.VISIBLE);
                        imgLogo.setVisibility(View.VISIBLE);
                        txtTitle.setVisibility(View.GONE);
                    }


                }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if(drawerLayout.isDrawerOpen(mDrawerInnerLayout)){
                    drawerLayout.closeDrawer(mDrawerFilterLayout);
                    layFilterItems.setVisibility(View.GONE);
                    layDashboardItems.setVisibility(View.VISIBLE);
                    imgLogo.setVisibility(View.VISIBLE);
                    txtTitle.setVisibility(View.GONE);
                }
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);


    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        final Handler handler = new Handler();
        switch (item.getItemId()){
            case R.id.action_login:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        LoginFragment loginFragment = new LoginFragment();
                        updateFragment(loginFragment);

                    }
                }, 400);
            break;

            case R.id.action_log_out:
                    logout();
                break;
        }
        return true;
    }

    private void logout(){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(DashboardActivity.this).logout();
                dialog.dismiss();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(!jsonObject.get("status").equals(null)){

                        if(!jsonObject.get("status").toString().equals("success")){
                            new MaterialDialog.Builder(DashboardActivity.this)
                                    .content(jsonObject.get("text").toString())
                                    .positiveText("Ok").show();

                        }else{
                            preferences.setIsLoggedIn("0");
                            util.setPopUpMenuItems();
                        }

                    }else {
                        new MaterialDialog.Builder(DashboardActivity.this)
                                .content("Some error has been occurred. Please try again later.")
                                .positiveText("Ok").show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    new MaterialDialog.Builder(DashboardActivity.this)
                            .content("Some error has been occurred. Please try again later.")
                            .positiveText("Ok").show();
                }

            }
        });
        if(RestClient.isNetworkAvailable(DashboardActivity.this, util))
        {
            t.start();
        }
    }


}

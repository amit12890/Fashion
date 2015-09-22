package com.fashion.krish.activity;

import android.animation.ValueAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.adapter.CategoryListAdapter;
import com.fashion.krish.customview.MyPopupMenu;
import com.fashion.krish.fragment.CMSFragment;
import com.fashion.krish.fragment.ContactUsFragment;
import com.fashion.krish.fragment.DashboardFragment;
import com.fashion.krish.fragment.LoginFragment;
import com.fashion.krish.fragment.LoginSignUpFragment;
import com.fashion.krish.fragment.MyAccountFragment;
import com.fashion.krish.fragment.MyOrdersFragment;
import com.fashion.krish.fragment.ProductListFragment;
import com.fashion.krish.fragment.SettingsFragment;
import com.fashion.krish.fragment.SocialFragment;
import com.fashion.krish.fragment.StoreLocaterFragment;
import com.fashion.krish.fragment.WishListFragment;
import com.fashion.krish.model.Cart;
import com.fashion.krish.model.Category;
import com.fashion.krish.model.Content;
import com.fashion.krish.model.SubCategory;
import com.fashion.krish.utility.AppPreferences;
import com.fashion.krish.utility.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;
import com.rey.material.widget.ProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class DashboardActivity extends ActionBarActivity implements View.OnClickListener,View.OnTouchListener {

    private RelativeLayout mDrawerInnerLayout,mDrawerCartlayout,mDrawerFilterLayout,mDrawerLayoutOther;
    private RelativeLayout mLaySettings,mLayContactUs,mLayStoreLocate,mLayHome,layRoot;
    private CategoryListAdapter categoryListAdapter;
    private ExpandableListView categoryListView;
    private ScrollView scrollView;

    private static LinearLayout layAccountInfo,layMyOrders,layWishList,layLogin,layShoppingCart,
            laySettings,layLogout,layLastSep,layAfterLogin,layBeforeLogin;

    private Toolbar toolbar;
    private ImageView imgCart,imgSearch,imgMenu,imgDelete,imgNotification;
    public static DrawerLayout drawerLayout;
    public static ActionBarDrawerToggle drawerToggle;

    private LinearLayout layFilterItems,layDashboardItems;
    private ImageView imgLogo,imgBack;
    private TextView txtTitle;
    private MaterialDialog dialog;

    private static AppPreferences preferences;
    public static MyPopupMenu popup;
    private Utility util;
    public static Fragment current_flag;

    CallbackManager callbackManager;
    PopupWindow menuPopUpWindow;
    Point p;
    private RelativeLayout laySubtotal,layGrandTotal,layDiscountHeader,layTax;
    private Button btnCheckout;
    private LinearLayout productContainer,layDiscountChild;
    private com.rey.material.widget.Button btnApplyCoupon;
    private EditText etCoupon;
    private boolean isDelete = false,isCouponOpen = false;
    private ProgressView linerProgress;
    public static Fragment selectedFragment = null;
    public static TextView txtNotificationBadge,txtCartBadge;
    private boolean isCouponApplied = false;

    ImageLoader imageLoader;
    DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_dashboard);
        Utility.changeStatusBarColor(DashboardActivity.this);

        preferences = new AppPreferences(DashboardActivity.this);
        util = new Utility(DashboardActivity.this);

        initViews();
        initDrawer();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            toolbar.setTitle("");
            // toolbar.setLogo(R.drawable.logo);
            setSupportActionBar(toolbar);
        }


        categoryListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                v.setSelected(true);
                RelativeLayout lay = (RelativeLayout) v;
                lay.setBackgroundResource(R.color.drawer_pressed);
                parent.setItemChecked(groupPosition, true);
                if (categoryListAdapter.getChildrenCount(groupPosition) == 0) {
                    Category category = (Category) categoryListAdapter.getGroup(groupPosition);
                    ProductListFragment productListFragment = new ProductListFragment(category.entity_id, category.label);
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


        getSupportActionBar().setHomeAsUpIndicator(getColoredArrow());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!util.isDailogueVisible()){
                    if (!drawerLayout.isDrawerOpen(mDrawerCartlayout) && !drawerLayout.isDrawerOpen(mDrawerFilterLayout) &&
                            !drawerLayout.isDrawerOpen(mDrawerInnerLayout) &&
                            !drawerLayout.isDrawerOpen(mDrawerLayoutOther)) {

                        if(!(current_flag instanceof DashboardFragment) && !(current_flag instanceof ProductListFragment)
                                && !(current_flag instanceof  StoreLocaterFragment)){
                            if(AppController.fragmentStack.size() > 1){
                                AppController.fragmentStack.pop();
                                Fragment f = null;
                                try {
                                    f = (Fragment)(Class.forName(AppController.fragmentStack.peek()).newInstance());
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                                updateFragment(f);
                            }else{
                                finish();
                                AppController.fragmentStack.clear();
                            }
                        }else{
                            drawerLayout.openDrawer(mDrawerInnerLayout);
                        }

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

                    } else if (drawerLayout.isDrawerOpen(mDrawerLayoutOther)) {

                        drawerLayout.closeDrawer(mDrawerLayoutOther);
                        layFilterItems.setVisibility(View.GONE);
                        layDashboardItems.setVisibility(View.GONE);
                        imgLogo.setVisibility(View.GONE);
                        txtTitle.setVisibility(View.GONE);

                    }
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

        //addToCart();

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.placeholder)
                .showImageOnFail(R.drawable.placeholder)
                .showImageOnLoading(R.drawable.placeholder).build();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        current_flag = new DashboardFragment();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setFocusableInTouchMode(false);

        LayoutInflater infalInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = infalInflater.inflate(R.layout.drawer_bottom_part, null);
        View topview = infalInflater.inflate(R.layout.drawer_top_part, null);

        ((RelativeLayout)findViewById(R.id.main_title)).setBackgroundColor(Color.parseColor(AppController.PRIMARY_COLOR));
        mLayStoreLocate = (RelativeLayout) view.findViewById(R.id.navdrawer_lay_store_locate);
        mLayStoreLocate.setOnClickListener(this);
        mLayStoreLocate.setOnTouchListener(this);
        mLaySettings = (RelativeLayout) view.findViewById(R.id.navdrawer_lay_settings);
        mLaySettings.setOnClickListener(this);
        mLaySettings.setOnTouchListener(this);
        mLayContactUs = (RelativeLayout) view.findViewById(R.id.navdrawer_lay_contact_us);
        mLayContactUs.setOnClickListener(this);
        mLayContactUs.setOnTouchListener(this);

        mLayHome = (RelativeLayout) topview.findViewById(R.id.navdrawer_lay_home);
        mLayHome.setOnClickListener(this);
        mLayHome.setOnTouchListener(this);

        layRoot = (RelativeLayout)findViewById(R.id.lay_root);
        layRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                drawerLayout.closeDrawers();
                return false;
            }
        });
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        categoryListView = (ExpandableListView) findViewById(R.id.categoryExpandableListView);
        categoryListAdapter = new CategoryListAdapter(DashboardActivity.this, AppController.categoryArrayList);
        categoryListView.addFooterView(view);
        addMenuCMSItems();
        categoryListView.addHeaderView(topview);
        //categoryListView.setSelector(R.color.drawer_pressed);

        // RippleDrawable rippleDrawable = new RippleDrawable();

        imgSearch =(ImageView) findViewById(R.id.img_search);
        imgSearch.setOnClickListener(this);
        imgSearch.setColorFilter(Color.WHITE);
        imgNotification =(ImageView) findViewById(R.id.img_notification);
        imgNotification.setOnClickListener(this);
        imgNotification.setColorFilter(Color.WHITE);
        imgCart =(ImageView) findViewById(R.id.img_cart);
        imgCart.setOnClickListener(this);
        imgCart.setColorFilter(Color.WHITE);
        imgMenu =(ImageView) findViewById(R.id.img_overflow);
        imgMenu.setOnClickListener(this);
        imgMenu.setColorFilter(Color.WHITE);
        imgDelete =(ImageView) findViewById(R.id.img_delete);
        imgDelete.setOnClickListener(this);
        imgDelete.setColorFilter(Color.WHITE);

        layDashboardItems = (LinearLayout) findViewById(R.id.lay_dashboard_items);
        layFilterItems = (LinearLayout) findViewById(R.id.lay_filter_items);
        imgLogo = (ImageView) findViewById(R.id.img_logo_title);
        imgLogo.setColorFilter(Color.WHITE);
        imgBack = (ImageView) findViewById(R.id.img_back_arrow);
        imgBack.setVisibility(View.GONE);
        imgBack.setColorFilter(Color.WHITE);
        txtTitle = (TextView) findViewById(R.id.txt_filter_title);

        dialog = new MaterialDialog.Builder(DashboardActivity.this)
                .content("Please Wait")
                .progress(true, 0)
                .build();


        popup = new MyPopupMenu(DashboardActivity.this, imgMenu, Gravity.NO_GRAVITY, R.attr.actionOverflowMenuStyle, R.style.OverflowMenu);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_dashboard, popup.getMenu());
        popup.getMenu().add(1, 0, 0, "Test");
        //setPopUpMenuItems();
        //popup.getMenu().getItem(1).setVisible(false);
        //popup.setOnMenuItemClickListener(this);

        mDrawerInnerLayout = (RelativeLayout) findViewById(R.id.navdrawerlayout);
        mDrawerCartlayout = (RelativeLayout) findViewById(R.id.navdrawerlayout_right);
        mDrawerFilterLayout = (RelativeLayout) findViewById(R.id.navdrawerlayout_filer);
        mDrawerLayoutOther = (RelativeLayout) findViewById(R.id.navdrawerlayout_other);

        DashboardFragment dashboardFragment = new DashboardFragment();
        updateFragment(dashboardFragment);
        categoryListView.setAdapter(categoryListAdapter);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        //Log.d("Success", loginResult.toString());
                        LoginSignUpFragment.isBackFromFacebook = true;
                        LoginSignUpFragment.flag = "Success";
                        LoginSignUpFragment.loginResult = loginResult;
                        //new LoginSignUpFragment().facebookCallBack("Success",loginResult,null);

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d("Cancel", "");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        //Log.d("Error", exception.toString());
                        LoginSignUpFragment.isBackFromFacebook = true;
                        LoginSignUpFragment.flag = "Error";
                        LoginSignUpFragment.fbException = exception;
                        //new LoginSignUpFragment().facebookCallBack("Error", null, exception);
                    }
                });

        laySubtotal = (RelativeLayout) findViewById(R.id.lay_subtotal);
        GradientDrawable subTotalDrawable = (GradientDrawable) laySubtotal.getBackground();
        subTotalDrawable.setColor(Color.parseColor("#3f3f3f"));

        layGrandTotal = (RelativeLayout) findViewById(R.id.lay_grand_total);
        GradientDrawable grandTotalDrawable = (GradientDrawable) layGrandTotal.getBackground();
        grandTotalDrawable.setColor(Color.parseColor("#3f3f3f"));

        layTax = (RelativeLayout) findViewById(R.id.lay_tax);
        GradientDrawable taxDrawable = (GradientDrawable) layTax.getBackground();
        taxDrawable.setColor(Color.parseColor("#3f3f3f"));

        LinearLayout layCheckout = (LinearLayout) findViewById(R.id.lay_chekout_btn);
        GradientDrawable checkoutDrawable = (GradientDrawable) layCheckout.getBackground();
        checkoutDrawable.setColor(preferences.getSecondaryColor());

        btnCheckout = (Button) findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(this);
        btnCheckout.setBackgroundDrawable(util.getSecondaryRippleDrawable());

        productContainer = (LinearLayout) findViewById(R.id.lay_float_product_container);

        layDiscountHeader = (RelativeLayout) findViewById(R.id.lay_discount_label);
        layDiscountHeader.setOnClickListener(this);
        layDiscountChild = (LinearLayout) findViewById(R.id.lay_discount_child);

        etCoupon = (EditText) findViewById(R.id.edt_discount);
        etCoupon.applyTheme(Color.parseColor(AppController.PRIMARY_COLOR),Color.parseColor(AppController.SECONDARY_COLOR));

        btnApplyCoupon = (com.rey.material.widget.Button) findViewById(R.id.btn_apply_coupon);
        btnApplyCoupon.setOnClickListener(this);
        btnApplyCoupon.setBackgroundDrawable(util.getSecondaryRippleDrawable());

        linerProgress = (ProgressView) findViewById(R.id.progress_linear);
        //linerProgress.setBackgroundColor(Color.parseColor(AppController.PRIMARY_COLOR));
        linerProgress.setStrokeColor(Color.parseColor(AppController.SECONDARY_COLOR));
        linerProgress.start();
        linerProgress.setProgress(0f);

        LinearLayout layApplyCoupon = (LinearLayout) findViewById(R.id.lay_apply_coupon);
        GradientDrawable couponDrawable = (GradientDrawable) layApplyCoupon.getBackground();
        couponDrawable.setColor(preferences.getSecondaryColor());

        txtNotificationBadge = (TextView) findViewById(R.id.txt_notification_badge);
        txtCartBadge = (TextView) findViewById(R.id.txt_cart_badge);

        addCartProducts(productContainer);


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
                        if(!(current_flag instanceof StoreLocaterFragment)){
                            StoreLocaterFragment storeLocaterFragment = new StoreLocaterFragment();
                            updateFragment(storeLocaterFragment);

                        }
                        drawerLayout.closeDrawers();

                    }
                }, 100);

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
                if(!util.isDailogueVisible()){
                    if(!drawerLayout.isDrawerOpen(mDrawerInnerLayout)){
                        if(!drawerLayout.isDrawerOpen(mDrawerCartlayout)){
                            addCartProducts(productContainer);
                            drawerLayout.openDrawer(Gravity.RIGHT);
                            imgDelete.setVisibility(View.VISIBLE);
                            imgMenu.setVisibility(View.GONE);

                        }else{
                            drawerLayout.closeDrawer(mDrawerCartlayout);
                            imgDelete.setVisibility(View.GONE);
                            imgMenu.setVisibility(View.VISIBLE);
                        }
                    }else{
                        drawerLayout.closeDrawer(mDrawerInnerLayout);
                    }
                }


                break;
            case R.id.img_overflow:

                if(!util.isDailogueVisible()){
                    drawerLayout.closeDrawer(mDrawerCartlayout);
                    drawerLayout.closeDrawer(mDrawerFilterLayout);
                    drawerLayout.closeDrawer(mDrawerInnerLayout);
                    drawerLayout.closeDrawer(mDrawerLayoutOther);
                    if (p != null)
                        showStatusPopup(p);
                }

                //popup.show();

                break;
            case R.id.img_delete:
                    if(isDelete){
                        isDelete = false;
                        imgDelete.setImageResource(R.drawable.delete_icon);
                        addCartProducts(productContainer);
                    }else{
                        isDelete = true;
                        imgDelete.setImageResource(R.drawable.tick_float_icon);
                        addCartProducts(productContainer);
                    }
                break;

            case R.id.img_search:

                break;
            case R.id.lay_account_info:
                layAccountInfo.setBackgroundColor(R.color.drawer_pressed);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        MyAccountFragment myAccountFragment = new MyAccountFragment();
                        if(!(current_flag instanceof MyAccountFragment))
                            updateFragment(myAccountFragment);
                        layAccountInfo.setBackgroundColor(Color.TRANSPARENT);
                        //selectedFragment = new DashboardFragment();
                    }
                }, 400);

                menuPopUpWindow.dismiss();
                break;
            case R.id.lay_my_orders:
                layMyOrders.setBackgroundColor(R.color.drawer_pressed);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        MyOrdersFragment myOrdersFragment = new MyOrdersFragment();
                        if(!(current_flag instanceof MyOrdersFragment))
                            updateFragment(myOrdersFragment);
                        layMyOrders.setBackgroundColor(Color.TRANSPARENT);

                        //animateToggle();

                    }
                }, 400);
                menuPopUpWindow.dismiss();

                break;
            case R.id.lay_wishlist:
                layWishList.setBackgroundColor(R.color.drawer_pressed);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms

                        WishListFragment wishListFragment = new WishListFragment();
                        //selectedFragment = current_flag;
                        if(!(current_flag instanceof WishListFragment))
                            updateFragment(wishListFragment);
                        layWishList.setBackgroundColor(Color.TRANSPARENT);

                        //animateToggle();

                    }
                }, 400);
                menuPopUpWindow.dismiss();
                break;
            case R.id.lay_login:
                layLogin.setBackgroundColor(R.color.drawer_pressed);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        LoginFragment loginFragment = new LoginFragment();
                        updateFragment(loginFragment);
                        layLogin.setBackgroundColor(Color.TRANSPARENT);

                    }
                }, 400);
                menuPopUpWindow.dismiss();
                break;
            case R.id.lay_shopping_cart:
                layShoppingCart.setBackgroundColor(R.color.drawer_pressed);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        Intent i = new Intent(DashboardActivity.this, ShoppingCartActivity.class);
                        startActivity(i);
                        layShoppingCart.setBackgroundColor(Color.TRANSPARENT);
                    }
                }, 400);

                menuPopUpWindow.dismiss();
                break;

            case R.id.lay_setting:
                laySettings.setBackgroundColor(R.color.drawer_pressed);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //selectedFragment = current_flag;
                        SettingsFragment settingsFragment = new SettingsFragment();
                        updateFragment(settingsFragment);
                        layLogin.setBackgroundColor(Color.TRANSPARENT);


                    }
                }, 400);

                menuPopUpWindow.dismiss();
                break;

            case R.id.navdrawer_lay_settings:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        SettingsFragment settingsFragment = new SettingsFragment();
                        updateFragment(settingsFragment);
                        drawerLayout.closeDrawers();
                    }
                }, 400);
                break;

            case R.id.lay_logout:
                logout();
                menuPopUpWindow.dismiss();
                break;

            case R.id.lay_discount_label:
                ImageView imgArrow = (ImageView) findViewById(R.id.img_discount_arrow);
                RotateAnimation animClose = new RotateAnimation(90f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animClose.setInterpolator(new LinearInterpolator());
                animClose.setDuration(200);
                animClose.setFillAfter(true);

                RotateAnimation animOpen = new RotateAnimation(0f, 90f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animOpen.setInterpolator(new LinearInterpolator());
                animOpen.setDuration(200);
                animOpen.setFillAfter(true);

                if(isCouponOpen){
                    isCouponOpen = false;
                    layDiscountChild.setVisibility(View.GONE);
                    imgArrow.startAnimation(animClose);


                }else{
                    isCouponOpen = true;
                    etCoupon.requestFocus();
                    layDiscountChild.setVisibility(View.VISIBLE);
                    imgArrow.startAnimation(animOpen);
                    final ScrollView scrollView = (ScrollView) findViewById(R.id.float_cart_scroll);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                    //rotateOpen.start();
                }
                break;
            case R.id.btn_apply_coupon:
                applyCoupon(etCoupon.getText().toString());

                break;
            case R.id.btn_checkout:
                Intent i = new Intent(DashboardActivity.this,CheckoutActivity.class);
                startActivity(i);
                drawerLayout.closeDrawer(mDrawerCartlayout);
                break;

        }
    }

    public void updateFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();

        FragmentManager fragmentManager = getFragmentManager();

        if(AppController.fragmentStack.contains(backStateName)){
            AppController.fragmentStack.remove(AppController.fragmentStack.indexOf(backStateName));
        }
        AppController.fragmentStack.push(backStateName);

        current_flag = fragment;

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
                }else if(drawerView == mDrawerCartlayout){
                    imgMenu.setVisibility(View.VISIBLE);
                    imgDelete.setVisibility(View.GONE);
                }

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(drawerView == mDrawerCartlayout){
                    addCartProducts(productContainer);
                }

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if(drawerLayout.isDrawerOpen(mDrawerInnerLayout) && !(current_flag instanceof CMSFragment)){
                    drawerLayout.closeDrawer(mDrawerFilterLayout);
                    layFilterItems.setVisibility(View.GONE);
                    layDashboardItems.setVisibility(View.VISIBLE);
                    imgLogo.setVisibility(View.VISIBLE);
                    txtTitle.setVisibility(View.GONE);
                }
            }
        };

        //toolbar.setBackgroundColor(Color.WHITE);
        drawerLayout.setDrawerListener(drawerToggle);


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
                            setPopUpMenuItems();
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

    @Override
    public void onBackPressed() {

        if (!drawerLayout.isDrawerOpen(mDrawerCartlayout) && !drawerLayout.isDrawerOpen(mDrawerFilterLayout) &&
                !drawerLayout.isDrawerOpen(mDrawerInnerLayout) &&
                !drawerLayout.isDrawerOpen(mDrawerLayoutOther)) {

            if(AppController.fragmentStack.size() > 1){
                AppController.fragmentStack.pop();
                Fragment f = null;
                try {
                    f = (Fragment)(Class.forName(AppController.fragmentStack.peek()).newInstance());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                updateFragment(f);
            }else{
                finish();
                AppController.fragmentStack.clear();
            }


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

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LoginSignUpFragment.RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            LoginSignUpFragment.mIntentInProgress = false;

            if (!LoginSignUpFragment.mGoogleApiClient.isConnecting()) {
                LoginSignUpFragment.mGoogleApiClient.connect();
            }
        }else if(LoginSignUpFragment.SOCIAL_INDEX == LoginSignUpFragment.FACEBOOK)
            callbackManager.onActivityResult(requestCode, resultCode, data);
        //else if(LoginSignUpFragment.SOCIAL_INDEX == LoginSignUpFragment.TWITTER)
            //LoginSignUpFragment.mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }

    // The method that displays the popup.
    private void showStatusPopup(Point p) {

        // Inflate the popup_layout.xml
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupLayout = layoutInflater.inflate(R.layout.popmenu, null);

        // Creating the PopupWindow
        menuPopUpWindow = new PopupWindow(DashboardActivity.this);
        menuPopUpWindow.setContentView(popupLayout);
        menuPopUpWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        menuPopUpWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        menuPopUpWindow.setFocusable(true);

        // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.
        int OFFSET_X = 0;
        int OFFSET_Y = 0;

        //Clear the default translucent background
        menuPopUpWindow.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        menuPopUpWindow.showAtLocation(popupLayout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

        //Popup Menu item initializations
        layAccountInfo = (LinearLayout) popupLayout.findViewById(R.id.lay_account_info);
        layAccountInfo.setOnClickListener(this);
        layAccountInfo.setOnTouchListener(this);
        layMyOrders = (LinearLayout) popupLayout.findViewById(R.id.lay_my_orders);
        layMyOrders.setOnClickListener(this);
        layMyOrders.setOnTouchListener(this);
        layWishList = (LinearLayout) popupLayout.findViewById(R.id.lay_wishlist);
        layWishList.setOnClickListener(this);
        layWishList.setOnTouchListener(this);
        layLogin = (LinearLayout) popupLayout.findViewById(R.id.lay_login);
        layLogin.setOnClickListener(this);
        layLogin.setOnTouchListener(this);
        layShoppingCart = (LinearLayout) popupLayout.findViewById(R.id.lay_shopping_cart);
        layShoppingCart.setOnClickListener(this);
        layShoppingCart.setOnTouchListener(this);
        laySettings = (LinearLayout) popupLayout.findViewById(R.id.lay_setting);
        laySettings.setOnClickListener(this);
        laySettings.setOnTouchListener(this);
        layLogout = (LinearLayout) popupLayout.findViewById(R.id.lay_logout);
        layLogout.setOnClickListener(this);
        layLogout.setOnTouchListener(this);
        layLastSep = (LinearLayout) popupLayout.findViewById(R.id.lay_last_sep);
        layAfterLogin =(LinearLayout) popupLayout.findViewById(R.id.lay_after_login_items);
        layBeforeLogin =(LinearLayout) popupLayout.findViewById(R.id.lay_before_login_items);
        setPopUpMenuItems();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];
        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        imgMenu.getLocationOnScreen(location);

        //Initialize the Point with x, and y positions
        p = new Point();
        p.x = location[0];
        p.y = location[1];
    }

    public static void setPopUpMenuItems(){

        if(preferences.getIsLoggedIn().equals("1")){
            layLogin.setVisibility(View.GONE);
            layBeforeLogin.setVisibility(View.GONE);

            layAccountInfo.setVisibility(View.VISIBLE);
            layMyOrders.setVisibility(View.VISIBLE);
            layWishList.setVisibility(View.VISIBLE);
            layLastSep.setVisibility(View.VISIBLE);
            layLogout.setVisibility(View.VISIBLE);
            layAfterLogin.setVisibility(View.VISIBLE);


        }else{
            layLogin.setVisibility(View.VISIBLE);
            layBeforeLogin.setVisibility(View.VISIBLE);

            layAccountInfo.setVisibility(View.GONE);
            layMyOrders.setVisibility(View.GONE);
            layWishList.setVisibility(View.GONE);
            layLastSep.setVisibility(View.GONE);
            layLogout.setVisibility(View.GONE);
            layAfterLogin.setVisibility(View.GONE);

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        /*if(event.getAction() == MotionEvent.ACTION_UP) {
            v.setBackgroundColor(Color.TRANSPARENT);
        } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
            v.setBackgroundColor(Color.parseColor(AppController.SECONDARY_COLOR));
        }*/
        return false;
    }

    private Drawable getColoredArrow() {
        Drawable arrowDrawable = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        Drawable wrapped = DrawableCompat.wrap(arrowDrawable);

        if (arrowDrawable != null && wrapped != null) {
            // This should avoid tinting all the arrows
            arrowDrawable.mutate();
            DrawableCompat.setTint(wrapped, Color.WHITE);
        }

        return wrapped;
    }

    public static void animateToggle(float start, float end){
        ValueAnimator anim = ValueAnimator.ofFloat(start, end);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                drawerToggle.onDrawerSlide(drawerLayout, slideOffset);
            }
        });
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(300);
        anim.start();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Toast.makeText(DashboardActivity.this,"On Resume",Toast.LENGTH_SHORT).show();
        animateToggle(90, 0);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    public void addMenuCMSItems(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout layout =  (LinearLayout) findViewById(R.id.lay_menu_cms);
        int count = 1;

        for (int i = 0; i < AppController.contentArray.size(); i++) {
            View footerView = inflater.inflate(R.layout.layout_menu_cms_child, null);
            final Content content = AppController.contentArray.get(i);
            if(content.content_action_type.equals("menu") ){

                TextView txtCMS = (TextView) footerView.findViewById(R.id.txt_customer_service);
                txtCMS.setText(content.content_action_label);
                txtCMS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        drawerLayout.closeDrawers();
                        CMSFragment cmsFragment = new CMSFragment(content.content_action_id);
                        updateFragment(cmsFragment);

                    }
                });

                LinearLayout sepLay = (LinearLayout) footerView.findViewById(R.id.lay_navdrawer_separator);


                layout.addView(footerView);

                count ++;

            }

        }
    }

    public void addCartProducts(final LinearLayout rootView){

        txtCartBadge.setText(AppController.SHOPPING_CART.cart_qty);

        if(Integer.parseInt(AppController.SHOPPING_CART.cart_qty) > 0){
            findViewById(R.id.root_float_cart).setVisibility(View.VISIBLE);
            findViewById(R.id.lay_no_cart_item).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.txt_subtotal_label)).setText(AppController.SHOPPING_CART.subtotal_title);
            ((TextView)findViewById(R.id.txt_subtotal_value)).setText(AppController.SHOPPING_CART.subtotal_formated_value);
            ((TextView)findViewById(R.id.txt_grand_total_label)).setText(AppController.SHOPPING_CART.grandtotal_title);
            ((TextView)findViewById(R.id.txt_grand_total_value)).setText(AppController.SHOPPING_CART.grandtotal_formated_value);
            if(AppController.SHOPPING_CART.tax_value.length() > 0){
                layTax.setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.txt_tax_label)).setText(AppController.SHOPPING_CART.tax_title);
                ((TextView)findViewById(R.id.txt_tax_value)).setText(AppController.SHOPPING_CART.tax_formated_value);
            }else if(AppController.SHOPPING_CART.tax_value.length() == 0){
                layTax.setVisibility(View.GONE);
            }

            if(AppController.SHOPPING_CART.discount_title.length() > 0){
                ((TextView)findViewById(R.id.txt_discount_title)).setText(AppController.SHOPPING_CART.discount_title);
                ((TextView)findViewById(R.id.txt_discount)).setText(AppController.SHOPPING_CART.discount_formated_value);
                ((TextView)findViewById(R.id.txt_discount)).setTextColor(preferences.getSecondaryColor());
                etCoupon.setVisibility(View.GONE);
                etCoupon.setText("");
                ((RelativeLayout) findViewById(R.id.lay_discount_applied)).setVisibility(View.VISIBLE);
                isCouponApplied = true;
                btnApplyCoupon.setText("Remove Coupon");
            }else{
                etCoupon.setVisibility(View.VISIBLE);
                ((RelativeLayout) findViewById(R.id.lay_discount_applied)).setVisibility(View.GONE);
                isCouponApplied = false;
                btnApplyCoupon.setText("Apply Coupon");
            }

        }else{
            findViewById(R.id.root_float_cart).setVisibility(View.GONE);
            findViewById(R.id.lay_no_cart_item).setVisibility(View.VISIBLE);
        }

        LayoutInflater infalInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView.removeAllViews();
        ArrayList<Cart.CartItems> cartItemsArray = AppController.SHOPPING_CART.cartItems;

        for (int i = 0; i < cartItemsArray.size(); i++ ){

            final Cart.CartItems cartItems = cartItemsArray.get(i);

            final View productView = infalInflater.inflate(R.layout.float_cart_product_layout, null);
            ImageView productImage = (ImageView) productView.findViewById(R.id.img_product_main);
            imageLoader.displayImage(cartItems.item_icon, productImage, options);
            TextView productName = (TextView) productView.findViewById(R.id.txt_product_name);
            productName.setText(cartItems.item_name);

            TextView productPrice = (TextView) productView.findViewById(R.id.txt_regular_price);
            productPrice.setText(cartItems.item_formated_price);
            productPrice.setTextColor(Color.parseColor(AppController.SECONDARY_COLOR));

            final EditText etQty = (EditText) productView.findViewById(R.id.edt_qty);
            etQty.setText(cartItems.item_qty);
            etQty.applyTheme(Color.parseColor(AppController.PRIMARY_COLOR), Color.parseColor(AppController.SECONDARY_COLOR));

            LinearLayout updateLay =(LinearLayout) productView.findViewById(R.id.lay_update);
            GradientDrawable updateLayDrawable = (GradientDrawable) updateLay.getBackground();
            updateLayDrawable.setColor(preferences.getSecondaryColor());

            com.rey.material.widget.Button btnUpdate = (com.rey.material.widget.Button) productView.findViewById(R.id.btn_update);
            btnUpdate.setBackgroundDrawable(util.getSecondaryRippleDrawable());
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String,String> cartParams = new HashMap<String, String>();
                    cartParams.put(cartItems.item_code,etQty.getText().toString());
                    updateCartItem(cartParams);
                }
            });

            ImageView deleteItem =(ImageView) productView.findViewById(R.id.img_delete);
            if(isDelete){
                deleteItem.setVisibility(View.VISIBLE);
            }else{
                deleteItem.setVisibility(View.GONE);
            }
            deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.removeView(productView);
                    deleteCartItem(cartItems.item_id);
                }
            });
            rootView.addView(productView);
        }
    }

    private void applyCoupon(final String coupon){
        linerProgress.setVisibility(View.VISIBLE);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(DashboardActivity.this).applyCoupon(coupon);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        linerProgress.setVisibility(View.GONE);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.get("status").equals(null)){

                                if(jsonObject.get("status").toString().equals("error")){
                                    etCoupon.setHelper(jsonObject.get("text").toString());

                                }else if(jsonObject.get("status").toString().equals("success")){
                                    etCoupon.setHelper(jsonObject.get("text").toString());
                                    getCartInfo();
                                    if(isCouponApplied){
                                        etCoupon.setVisibility(View.VISIBLE);
                                        ((RelativeLayout) findViewById(R.id.lay_discount_applied)).setVisibility(View.GONE);
                                        isCouponApplied = false;
                                        btnApplyCoupon.setText("Apply Coupon");
                                    }else{
                                        etCoupon.setVisibility(View.GONE);
                                        etCoupon.setText("");
                                        ((RelativeLayout) findViewById(R.id.lay_discount_applied)).setVisibility(View.VISIBLE);
                                        isCouponApplied = true;
                                        btnApplyCoupon.setText("Remove Coupon");
                                    }


                                }

                            }else {
                                etCoupon.setHelper("Some error has been occurred. Please try again later.");

                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            etCoupon.setHelper("Some error has been occurred. Please try again later.");
                        }
                    }
                });



            }
        });
        if(RestClient.isNetworkAvailable(DashboardActivity.this, util))
        {
            t.start();
        }
    }

    public void deleteCartItem(final String item_id){
        linerProgress.setVisibility(View.VISIBLE);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(DashboardActivity.this).deleteCartItems(item_id);
                if(response.equals(RestClient.ERROR)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                            linerProgress.setVisibility(View.GONE);
                            return;
                        }
                    });
                }else if(response.equals(RestClient.TIMEOUT_ERROR)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog(RestClient.TIMEOUT_ERROR_MESSAGE, "OK", "");
                            linerProgress.setVisibility(View.GONE);
                            return;
                        }
                    });
                }
                try{
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.has("status")){
                        if(jsonObject.getString("status").equals("success")){
                            getCartInfo();
                        }else{
                            util.showErrorDialog("Items can not be deleted. Please try again.", "Ok", "");
                            linerProgress.setVisibility(View.GONE);
                        }
                    }else{
                        util.showErrorDialog(RestClient.ERROR_MESSAGE, "Ok", "");
                    }



                }catch (JSONException e){
                    e.printStackTrace();
                    util.showErrorDialog(RestClient.ERROR_MESSAGE, "Ok", "");
                }

            }
        });
        if(RestClient.isNetworkAvailable(DashboardActivity.this, util))
        {
            t.start();

        }

    }

    public void updateCartItem(final HashMap<String,String> cart_params){
        linerProgress.setVisibility(View.VISIBLE);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(DashboardActivity.this).updateCart(cart_params);
                if(response.equals(RestClient.ERROR)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                            linerProgress.setVisibility(View.GONE);
                            return;
                        }
                    });
                }else if(response.equals(RestClient.TIMEOUT_ERROR)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog(RestClient.TIMEOUT_ERROR_MESSAGE, "OK", "");
                            linerProgress.setVisibility(View.GONE);
                            return;
                        }
                    });
                }
                try{
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.has("status")){
                        if(jsonObject.getString("status").equals("success")){
                            getCartInfo();
                        }else{
                            util.showErrorDialog("Items can not be deleted. Please try again.", "Ok", "");
                            linerProgress.setVisibility(View.GONE);
                        }
                    }else{
                        util.showErrorDialog(RestClient.ERROR_MESSAGE, "Ok", "");
                    }



                }catch (JSONException e){
                    e.printStackTrace();
                    util.showErrorDialog(RestClient.ERROR_MESSAGE, "Ok", "");
                }

            }
        });
        if(RestClient.isNetworkAvailable(DashboardActivity.this, util))
        {
            t.start();

        }

    }

    public void getCartInfo(){
        util.hideSoftKeyboard();
        AppController.SHOPPING_CART = null;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(DashboardActivity.this).getCartDetails();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.has("summary_qty")){
                                AppController.SHOPPING_CART = util.parseCartDetails(jsonObject);
                                addCartProducts(productContainer);
                                linerProgress.setVisibility(View.GONE);
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "Ok", "");
                        }
                    }
                });


            }
        });
        if(RestClient.isNetworkAvailable(DashboardActivity.this, util))
        {
            t.start();

        }

    }

    public void addToCart(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = new RestClient(DashboardActivity.this).addToCart("373");

            }
        });
        if(RestClient.isNetworkAvailable(DashboardActivity.this, util))
        {
            t.start();

        }

    }

}

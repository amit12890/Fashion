package com.fashion.krish.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.activity.DashboardActivity;
import com.fashion.krish.activity.ProductDetailActivity;
import com.fashion.krish.adapter.FilterCategoryAdapter;
import com.fashion.krish.adapter.FilterValuesAdapter;
import com.fashion.krish.adapter.ProductAdapter;
import com.fashion.krish.model.FilterCategory;
import com.fashion.krish.model.FilterValue;
import com.fashion.krish.model.Product;
import com.fashion.krish.utility.Utility;
import com.rey.material.drawable.RippleDrawable;
import com.rey.material.util.ColorUtil;
import com.rey.material.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductListFragment extends Fragment implements View.OnClickListener{

    private String category_id,category_name;
    private GridView productGrid;
    private Utility util;
    private MaterialDialog sortDialog;
    private ProductAdapter productAdapter,productAdapter1;
    private ListView productList;
    private ArrayList<Product> productArrayList,fullProductList;
    private LinearLayout laySwitchLayout;
    private boolean isGrid = true;
    private DrawerLayout drawerLayout;
    private RelativeLayout mDrawerFilterLayout,mDrawerOtherLayout,mRootLayout;
    private LinearLayout layFilterItems,layDashboardItems,layFilter,laySort;
    private ImageView imgLogo;
    private TextView txtTitle;
    private ListView filterCategoryList,filterValueList;
    private FilterCategoryAdapter filterParentAdapter;
    private FilterValuesAdapter filterChildAdapter;
    private ArrayList<FilterCategory> filterCategories;
    private HashMap<String,String> sortMap;
    private int currentSelectedItem = 0;
    int offset=0;
    //private TextView txtLoading;
    private RelativeLayout layLoading;
    private boolean has_more_data = false, is_loading_grid =false;
    private boolean is_loading_list =false;
    public static HashMap<String,String> filterValuesMap;
    private Button btnClear,btnApply;
    public static String defaultSort;
    private ArrayList<TextView> viewArray;
    private ImageView imgLaySwitch;
    int LIMIT = 10;

    public ProductListFragment(String category_id,String category_name) {
        // Required empty public constructor
        this.category_id = category_id;
        this.category_name = category_name;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_product_list, container, false);
        filterValuesMap = new HashMap<String,String>();
        util = new Utility(getActivity());

        if(DashboardActivity.selectedFragment instanceof BannerDetailsFragment){
            DashboardActivity.animateToggle(0, 1);
        }

        defaultSort = "";
        init(rootView);


        util.showAnimatedLogoProgressBar(mRootLayout);
        getFilterData();

        filterCategoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (int j = 0; j < filterParentAdapter.getCount(); j++) {
                    // parent.getChildAt(j).setBackgroundResource(R.color.drawer_pressed);
                    if(j == position){
                        filterCategories.get(j).isSelected = true;
                    }else{
                        filterCategories.get(j).isSelected = false;
                    }

                }

                currentSelectedItem = position;

                filterParentAdapter.notifyDataSetChanged();
                ArrayList<FilterValue> filterValues = filterCategories.get(position).filterValuesArray;
                filterChildAdapter = new FilterValuesAdapter(getActivity(),
                        R.layout.filter_list_child, filterValues);
                filterValueList.setAdapter(filterChildAdapter);
                filterChildAdapter.notifyDataSetChanged();

            }
        });

        filterValueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (int j = 0; j < filterChildAdapter.getCount(); j++) {
                    // parent.getChildAt(j).setBackgroundResource(R.color.drawer_pressed);
                    if(j == position){
                        filterCategories.get(currentSelectedItem).filterValuesArray.get(position).isSelected = true;
                        String parent_code =filterChildAdapter.getItem(j).filter_value_parent;
                        String value_id =filterChildAdapter.getItem(j).filter_value_id;
                        ProductListFragment.filterValuesMap.put(parent_code,value_id);
                        //((RadioButton) view.findViewById(R.id.radio_selection)).setChecked(true);
                    }else{
                        filterCategories.get(currentSelectedItem).filterValuesArray.get(j).isSelected = false;
                        //((RadioButton) parent.getChildAt(j).findViewById(R.id.radio_selection)).setChecked(false);
                    }
                    filterChildAdapter.notifyDataSetChanged();

                }

            }
        });

        productGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), ProductDetailActivity.class);
                i.putExtra("product_id", fullProductList.get(position).product_entity_id);
                i.putExtra("product_sku", fullProductList.get(position).product_sku);
                i.putExtra("product_name", fullProductList.get(position).product_name);
                i.putExtra("product_price_regular", fullProductList.get(position).product_price_regular);
                i.putExtra("product_rate", fullProductList.get(position).product_rating_summery);
                i.putExtra("category_name", category_name);
                startActivity(i);

            }
        });

        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), ProductDetailActivity.class);
                i.putExtra("product_id", productArrayList.get(position).product_entity_id);
                i.putExtra("product_sku", productArrayList.get(position).product_sku);
                i.putExtra("product_name", productArrayList.get(position).product_name);
                i.putExtra("product_price_regular", productArrayList.get(position).product_price_regular);
                i.putExtra("product_rate", productArrayList.get(position).product_rating_summery);
                i.putExtra("category_name", category_name);
                startActivity(i);

            }
        });

        productGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int l = visibleItemCount + firstVisibleItem;
                if (has_more_data) {
                    if (l >= totalItemCount && !is_loading_grid) {
                        getProducts();
                        is_loading_grid = true;
                        //txtLoading.setVisibility(View.VISIBLE);
                        layLoading.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

        productList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int l = visibleItemCount + firstVisibleItem;
                if (has_more_data) {
                    if (l >= totalItemCount && !is_loading_list) {
                        getProducts();
                        is_loading_list = true;
                        //txtLoading.setVisibility(View.VISIBLE);
                        layLoading.setVisibility(View.VISIBLE);
                    }
                }

            }
        });


        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public void init(View rootView){

        mRootLayout = (RelativeLayout) getActivity().findViewById(R.id.lay_root);
        drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        mDrawerFilterLayout = (RelativeLayout) getActivity().findViewById(R.id.navdrawerlayout_filer);
        mDrawerOtherLayout = (RelativeLayout) getActivity().findViewById(R.id.navdrawerlayout_other);
        layDashboardItems = (LinearLayout) getActivity().findViewById(R.id.lay_dashboard_items);
        layFilterItems = (LinearLayout) getActivity().findViewById(R.id.lay_filter_items);
        imgLogo = (ImageView) getActivity().findViewById(R.id.img_logo_title);
        txtTitle = (TextView) getActivity().findViewById(R.id.txt_filter_title);
        filterCategoryList = (ListView) getActivity().findViewById(R.id.list_filter_category);
        filterCategoryList.setBackgroundColor(Color.parseColor(AppController.SECONDARY_COLOR));
        filterValueList = (ListView) getActivity().findViewById(R.id.list_filter_values);
        btnClear = (Button) getActivity().findViewById(R.id.btn_clear_all);
        btnClear.setBackgroundColor(Color.parseColor(AppController.PRIMARY_COLOR));
        btnClear.setOnClickListener(this);

        btnApply = (Button) getActivity().findViewById(R.id.btn_apply);
        btnApply.setOnClickListener(this);
        btnApply.setBackgroundColor(Color.parseColor(AppController.SECONDARY_COLOR));


        productGrid = (GridView) rootView.findViewById(R.id.gridview_product);
        productList = (ListView) rootView.findViewById(R.id.listview_product);

        imgLaySwitch = (ImageView) rootView.findViewById(R.id.img_switch_lay);
        laySwitchLayout = (LinearLayout) rootView.findViewById(R.id.lay_switch_layout);
        laySwitchLayout.setOnClickListener(this);
        laySwitchLayout.setBackgroundColor(Color.parseColor(AppController.SECONDARY_COLOR));

        layFilter = (LinearLayout) rootView.findViewById(R.id.lay_filter);
        layFilter.setOnClickListener(this);
        laySort = (LinearLayout) rootView.findViewById(R.id.lay_sort);
        laySort.setOnClickListener(this);

        //txtLoading = (TextView) rootView.findViewById(R.id.txt_loading);
        //txtLoading.setVisibility(View.GONE);

        layLoading = (RelativeLayout) rootView.findViewById(R.id.lay_loading);
        layLoading.setVisibility(View.GONE);
        layLoading.addView(util.getLoadingLayout());
        layLoading.setVisibility(View.GONE);

        productArrayList = new ArrayList<>();
        productArrayList.clear();
        fullProductList = new ArrayList<>();
        fullProductList.clear();
        productAdapter = new ProductAdapter(getActivity(), R.layout.product_layout, productArrayList);
        productAdapter.clear();
        productGrid.setAdapter(productAdapter);
        productAdapter1 = new ProductAdapter(getActivity(), R.layout.product_layout_strip, productArrayList);
        productAdapter1.clear();
        productList.setAdapter(productAdapter1);
        sortMap = new HashMap<>();
        viewArray = new ArrayList<>();


        btnClear.setBackgroundDrawable(util.getPrimaryRippleDrawable());
        btnApply.setBackgroundDrawable(util.getSecondaryRippleDrawable());
    }

    private void getProducts(){
       // dialog.show();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                final String response = new RestClient(getActivity()).getProductForCategory(category_id,
                        String.valueOf(offset), String.valueOf(LIMIT));
                if(response.equals(RestClient.ERROR)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                            util.hideAnimatedLogoProgressBar();
                            return;
                        }
                    });
                }else if(response.equals(RestClient.TIMEOUT_ERROR)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog(RestClient.TIMEOUT_ERROR_MESSAGE, "OK", "");
                            util.hideAnimatedLogoProgressBar();
                            return;
                        }
                    });
                }

                try{
                    final JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getJSONObject("category_info").has("has_more_items")){
                        if(jsonObject.getJSONObject("category_info").get("has_more_items").toString().equals("0")) {
                            has_more_data = false;
                        }
                        else{
                            has_more_data = true;

                        }

                    }
                    if(jsonObject.has("products")){

                        fullProductList.addAll(util.parseProduct(jsonObject, category_id));
                        //final ArrayList<Product> tempProduct = util.parseProduct(jsonObject, category_id);
                        productArrayList = util.parseProduct(jsonObject, category_id);;

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                util.hideAnimatedLogoProgressBar();

                                if (isGrid) {

                                    productAdapter.addAll(productArrayList);
                                    productAdapter.notifyDataSetChanged();
                                    offset = (offset + LIMIT);
                                    is_loading_grid = false;
                                    //txtLoading.setVisibility(View.GONE);
                                    layLoading.setVisibility(View.GONE);
                                } else {

                                    offset = (offset + LIMIT);
                                    productAdapter1.addAll(productArrayList);
                                    productAdapter1.notifyDataSetChanged();
                                    is_loading_list = false;
                                    //txtLoading.setVisibility(View.GONE);
                                    layLoading.setVisibility(View.GONE);
                                }

                            }
                        });
                    }else {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                util.hideAnimatedLogoProgressBar();
                                util.showErrorDialog("No Product found.", "OK", "");
                            }
                        });

                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        });
        if(RestClient.isNetworkAvailable(getActivity(), util))
        {
            t.start();
        }
    }

    private void getFilterData(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                final String response = new RestClient(getActivity()).getFilterDataForCategory(category_id);
                if(response.equals(RestClient.ERROR)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
                            util.hideAnimatedLogoProgressBar();
                            return;
                        }
                    });
                }else if(response.equals(RestClient.TIMEOUT_ERROR)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog(RestClient.TIMEOUT_ERROR_MESSAGE, "OK", "");
                            util.hideAnimatedLogoProgressBar();
                            return;
                        }
                    });
                }

                try{
                    final JSONObject jsonObject = new JSONObject(response);
                    if(!jsonObject.get("filters").equals(null)){

                        filterCategories = util.parseFilterData(jsonObject);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                filterParentAdapter = new FilterCategoryAdapter(getActivity(),
                                        R.layout.filter_list_parent,filterCategories);
                                filterCategoryList.setAdapter(filterParentAdapter);
                                getViewByPosition(0,filterCategoryList).setBackgroundResource(R.color.drawer_default_white);

                                if(filterCategories.size() > 0){
                                    ArrayList<FilterValue> filterValues= filterCategories.get(0).filterValuesArray;
                                    filterChildAdapter = new FilterValuesAdapter(getActivity(),
                                            R.layout.filter_list_child,filterValues);
                                    filterValueList.setAdapter(filterChildAdapter);
                                    filterChildAdapter.notifyDataSetChanged();
                                }
                                getProducts();


                            }
                        });

                    }else {
                        util.showErrorDialog("Some error has been occurred. Please try again later.","Ok","");
                    }

                    if(jsonObject.has("orders")){

                        sortMap = util.parseSortData(jsonObject.getJSONObject("orders"));

                    }else {
                        util.showErrorDialog("Some error has been occurred. Please try again later.", "Ok", "");
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        });
        if(RestClient.isNetworkAvailable(getActivity(), util))
        {
            t.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lay_filter:
                if(!util.isDailogueVisible()){
                    drawerLayout.openDrawer(mDrawerFilterLayout);
                    layFilterItems.setVisibility(View.VISIBLE);
                    layDashboardItems.setVisibility(View.GONE);
                    imgLogo.setVisibility(View.GONE);
                    txtTitle.setVisibility(View.VISIBLE);
                }


                break;

            case R.id.lay_switch_layout:
                if(!util.isDailogueVisible()){
                    if(!is_loading_list &&  !is_loading_grid){
                        if (isGrid) {
                            if(offset == 0){
                                getProducts();
                                util.hideAnimatedLogoProgressBar();
                            }
                            isGrid = false;
                            imgLaySwitch.setImageResource(R.drawable.grid_icon);
                            productGrid.setVisibility(View.GONE);
                            productList.setVisibility(View.VISIBLE);

                        } else {
                            isGrid = true;
                            imgLaySwitch.setImageResource(R.drawable.list_icon);
                            productGrid.setVisibility(View.VISIBLE);
                            productList.setVisibility(View.GONE);

                        }
                    }
                }

                break;

            case R.id.lay_sort:
                if(!util.isDailogueVisible())
                    showShortDialog();;
                break;

            case R.id.btn_clear_all:
                    filterValuesMap.clear();

                    for (int i = 0; i < filterCategoryList.getCount(); i++) {
                        for (int j = 0; j < filterCategories.get(i).filterValuesArray.size(); j++) {
                            filterCategories.get(i).filterValuesArray.get(j).isSelected = false;
                        }
                    }

                    filterChildAdapter.notifyDataSetChanged();

                    resetAdapters();
                    closeFilterView();

                break;

            case R.id.btn_apply:
                    //Log.d("Filter",filterValuesMap.toString());
                    resetAdapters();
                    closeFilterView();
                break;

        }


    }

    public View getViewByPosition(int position, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (position < firstListItemPosition || position > lastListItemPosition ) {
            return listView.getAdapter().getView(position, null, listView);
        } else {
            final int childIndex = position - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @SuppressLint("NewApi")
    public void showShortDialog(){

        sortDialog = new MaterialDialog.Builder(getActivity()).cancelable(true).build();

        LayoutInflater inflate = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflate.inflate(R.layout.dialog_sort, null);
        LinearLayout dialog_container_lay = (LinearLayout) view.findViewById(R.id.dialog_container);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        sortDialog.addContentView(view, layoutParams);
        int i =123456;
        for (HashMap.Entry<String,String> entry : sortMap.entrySet()) {

            LinearLayout.LayoutParams txtParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) Utility.convertDpToPixel((float) 50, getActivity()));
            LinearLayout.LayoutParams sepParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) Utility.convertDpToPixel((float) 1, getActivity()));

            String key = entry.getKey();
            String value = entry.getValue();

            final TextView label = new TextView(getActivity());
            label.setText(value);
            label.setId(i);
            label.setTag(key);
            label.setLayoutParams(txtParams);
            label.setGravity(Gravity.CENTER_VERTICAL);
            int padding = (int) Utility.convertDpToPixel((float)10,getActivity());
            label.setPadding(padding, padding, padding, padding);
            label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            label.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleSortDialogClickEvent(v, v.getTag().toString());
                }
            });
            viewArray.add(label);

            if(key.equals(defaultSort)){
                label.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_tick,0);
            }

            View separator = new View(getActivity());
            separator.setLayoutParams(sepParams);
            separator.setBackgroundColor(R.color.drawer_default_gray);

            dialog_container_lay.addView(label);
            dialog_container_lay.addView(separator);
            i++;

        }
        sortDialog.show();
    }

    public void updateFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();

        //Replace fragment
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    public void closeFilterView(){
        drawerLayout.closeDrawer(mDrawerFilterLayout);
        layFilterItems.setVisibility(View.GONE);
        layDashboardItems.setVisibility(View.VISIBLE);
        imgLogo.setVisibility(View.VISIBLE);
        txtTitle.setVisibility(View.GONE);
    }

    public void resetAdapters(){
        LIMIT=10;
        offset=0;
        has_more_data = false;
        is_loading_grid =false;
        is_loading_list =false;
        productAdapter1.clear();
        productAdapter.clear();
        productAdapter.notifyDataSetChanged();
        productAdapter1.notifyDataSetChanged();

        getProducts();
        util.hideAnimatedLogoProgressBar();

    }

    public void handleSortDialogClickEvent(View v,String tag){

        for (int i = 0; i < viewArray.size(); i++) {
            TextView txt = viewArray.get(i);
            if(txt.getTag().equals(tag)){
                txt.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_tick,0);
                defaultSort = tag;
            }else{
                txt.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
            }
        }
        sortDialog.dismiss();
        resetAdapters();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(DashboardActivity.selectedFragment instanceof BannerDetailsFragment){
            DashboardActivity.animateToggle(1, 0);
        }

    }
}

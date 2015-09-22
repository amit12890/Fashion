package com.fashion.krish.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.support.v7.widget.Toolbar;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.activity.DashboardActivity;
import com.fashion.krish.activity.ProductDetailActivity;
import com.fashion.krish.adapter.WishListProductAdapter;
import com.fashion.krish.model.Product;
import com.fashion.krish.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WishListFragment extends Fragment implements View.OnClickListener{

    private GridView productGrid;
    private Utility util;
    private WishListProductAdapter productAdapter,productAdapter1;
    private ListView productList;
    private ArrayList<Product> productArrayList,fullProductList;
    private LinearLayout laySwitchLayout;
    private boolean isGrid = true;
    int limit=10,offset=0;
    //private TextView txtLoading;
    private RelativeLayout layLoading;
    private boolean has_more_data = false, is_loading_grid =false;
    private boolean is_loading_list =false;
    private ArrayList<TextView> viewArray;
    private ImageView imgLaySwitch;
    private Toolbar toolbar;
    ImageView imgBack;
    RelativeLayout rootLay;

    protected WishListInterface backHandlerInterface;

    public WishListFragment() {
        // Required empty public constructor
        util = new Utility(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_wishlist, container, false);
        util = new Utility(getActivity());
        init(rootView);
        getProducts();
        //dialog.show();
        //getFilterData();


        productGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), ProductDetailActivity.class);
                i.putExtra("product_id", fullProductList.get(position).product_entity_id);
                i.putExtra("product_sku", fullProductList.get(position).product_sku);
                i.putExtra("product_name", fullProductList.get(position).product_name);
                i.putExtra("product_price_regular", fullProductList.get(position).product_price_regular);
                i.putExtra("product_rate", fullProductList.get(position).product_rating_summery);
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

        rootLay = (RelativeLayout) getActivity().findViewById(R.id.lay_root);

        productGrid = (GridView) rootView.findViewById(R.id.gridview_product);
        productList = (ListView) rootView.findViewById(R.id.listview_product);

        imgLaySwitch = (ImageView) rootView.findViewById(R.id.img_switch_lay);
        laySwitchLayout = (LinearLayout) rootView.findViewById(R.id.lay_switch_layout);
        laySwitchLayout.setOnClickListener(this);
        laySwitchLayout.setBackgroundColor(Color.parseColor(AppController.SECONDARY_COLOR));

        layLoading = (RelativeLayout) rootView.findViewById(R.id.lay_loading);
        layLoading.setVisibility(View.GONE);
        layLoading.addView(util.getLoadingLayout());
        layLoading.setVisibility(View.GONE);

        productArrayList = new ArrayList<>();
        productArrayList.clear();
        fullProductList = new ArrayList<>();
        fullProductList.clear();
        productAdapter = new WishListProductAdapter(getActivity(), R.layout.wish_list_product_layout, productArrayList);
        productAdapter.clear();
        productGrid.setAdapter(productAdapter);
        productAdapter1 = new WishListProductAdapter(getActivity(), R.layout.wish_list_product_layout_strip, productArrayList);
        productAdapter1.clear();
        productList.setAdapter(productAdapter1);
        viewArray = new ArrayList<>();

        //toolbar.setVisibility(View.GONE);

        DashboardActivity.animateToggle(0,1);
        imgBack = (ImageView) getActivity().findViewById(R.id.img_back_arrow);
        imgBack.setVisibility(View.GONE);
        imgBack.setOnClickListener(this);

    }

    private void getProducts(){
       // dialog.show();
        //util.showLoadingDialog("Please Wait");
        util.showAnimatedLogoProgressBar(rootLay);
        //progressLay.setVisibility(View.VISIBLE);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                final String response = new RestClient(getActivity()).getWishListProduct(String.valueOf(offset),
                        String.valueOf(limit));
                if(response.equals("error")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            util.showErrorDialog("Some error has occurred. Please try again later", "OK", "");
                            util.hideAnimatedLogoProgressBar();
                            return;
                        }
                    });
                }

                try{
                    final JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("has_more_items")){
                        if(jsonObject.get("has_more_items").toString().equals("0")) {
                            has_more_data = false;
                        }
                        else{
                            has_more_data = true;

                        }

                    }
                    if(jsonObject.has("item")){
                        //util.hideLoadingDialog();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                util.hideAnimatedLogoProgressBar();
                            }
                        });

                        fullProductList.addAll(util.parseWishlistProduct(jsonObject));
                        //final ArrayList<Product> tempProduct = util.parseProduct(jsonObject, category_id);
                        productArrayList = util.parseWishlistProduct(jsonObject);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (isGrid) {

                                    productAdapter.addAll(productArrayList);
                                    productAdapter.notifyDataSetChanged();
                                    offset = (offset + limit);
                                    is_loading_grid = false;
                                    layLoading.setVisibility(View.GONE);
                                } else {

                                    offset = (offset + limit);
                                    productAdapter1.addAll(productArrayList);
                                    productAdapter1.notifyDataSetChanged();
                                    is_loading_list = false;
                                    layLoading.setVisibility(View.GONE);
                                }

                            }
                        });
                    }else {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                util.hideAnimatedLogoProgressBar();
                            }
                        });
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lay_clear_list:


                break;

            case R.id.lay_switch_layout:
                if(!is_loading_list &&  !is_loading_grid){
                    if (isGrid) {
                        if(offset == 0){
                            getProducts();

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
                break;

            case R.id.toolbar:
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    DashboardFragment dashboardFragment = new DashboardFragment();
                    ft.replace(R.id.content_frame, dashboardFragment);
                    ft.commit();
                    toolbar.setVisibility(View.VISIBLE);
                    imgBack.setVisibility(View.GONE);
                break;


        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DashboardActivity.animateToggle(1,0);
    }

    public interface WishListInterface {
        public void setSelectedFragment(Fragment fragment);
    }
}

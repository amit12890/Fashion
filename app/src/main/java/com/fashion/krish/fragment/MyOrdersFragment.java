package com.fashion.krish.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.RestClient;
import com.fashion.krish.activity.DashboardActivity;
import com.fashion.krish.activity.OrderDetailsActivity;
import com.fashion.krish.model.Orders;
import com.fashion.krish.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyOrdersFragment extends Fragment{

    private Utility util;
    private ArrayList<Orders> orderArrayList;
    private LinearLayout layOrderListLayout;
    RelativeLayout rootLay;

    protected MyOrdersInterface backHandlerInterface;


    public MyOrdersFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_my_orders, container, false);
        util = new Utility(getActivity());
        init(rootView);


        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public void init(View rootView){

        rootLay = (RelativeLayout) getActivity().findViewById(R.id.lay_root);

        orderArrayList = new ArrayList<>();

        layOrderListLayout = (LinearLayout) rootView.findViewById(R.id.lay_order_list);
        getOrders();
        DashboardActivity.animateToggle(0, 1);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DashboardActivity.animateToggle(1,0);
    }

    private void getOrders(){
        // dialog.show();
        //util.showLoadingDialog("Please Wait");
        util.showAnimatedLogoProgressBar(rootLay);
        //progressLay.setVisibility(View.VISIBLE);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                final String response = new RestClient(getActivity()).getUserOrders();
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
                    if (jsonObject.has("orders_count")) {
                        if (jsonObject.getInt("orders_count") > 0) {
                            if (jsonObject.has("item")) {
                                //util.hideLoadingDialog();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        util.hideAnimatedLogoProgressBar();
                                        try {
                                            if(jsonObject.get("item") instanceof JSONArray){

                                                JSONArray itemArray = jsonObject.getJSONArray("item");
                                                for (int i = 0; i < itemArray.length(); i++) {
                                                    JSONObject itemObj = itemArray.getJSONObject(i);
                                                    orderArrayList.add(parseOrders(itemObj));
                                                }
                                            }else{
                                                JSONObject itemObj = jsonObject.getJSONObject("item");
                                                orderArrayList.add(parseOrders(itemObj));
                                            }
                                            addOrdersView();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                util.showErrorDialog("No Orders found for this account.", "OK", "");

                            }

                        }

                    } else {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                util.hideAnimatedLogoProgressBar();
                                util.showErrorDialog(RestClient.ERROR_MESSAGE, "OK", "");
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

    public Orders parseOrders(JSONObject jObj){
        Orders orders = new Orders();
        try {
            orders.order_entity_id = jObj.getString("entity_id");
            orders.order_number= jObj.getString("number");
            orders.order_date = jObj.getString("date");
            orders.order_ship_to = jObj.getString("ship_to");
            orders.order_grand_total = jObj.getString("total");
            orders.order_status = jObj.getString("status");

        }catch (JSONException e){
            e.printStackTrace();
        }
        return orders;
    }

    public void addOrdersView(){

        LayoutInflater infalInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(orderArrayList.size() > 0) {
            for (int i = 0; i < orderArrayList.size(); i++) {
                View orderView = infalInflater.inflate(R.layout.layout_order_list_child, null);
                LinearLayout layRoot = (LinearLayout) orderView.findViewById(R.id.lay_order_child_root);
                TextView txtOrderNo = (TextView) orderView.findViewById(R.id.txt_order_number);
                TextView txtOrderDate = (TextView) orderView.findViewById(R.id.txt_placed_date);
                TextView txtOrderTotalLabel = (TextView) orderView.findViewById(R.id.txt_total_label);
                TextView txtOrderTotalValue = (TextView) orderView.findViewById(R.id.txt_total_value);
                TextView txtStatus = (TextView) orderView.findViewById(R.id.txt_status);

                final Orders orders = orderArrayList.get(i);

                txtOrderNo.append(orders.order_number);
                txtOrderDate.append(orders.order_date);
                txtOrderTotalValue.setText(orders.order_grand_total);
                txtOrderTotalValue.setTextColor(Color.parseColor(AppController.SECONDARY_COLOR));
                txtStatus.append(orders.order_status);
                final String entity_id = orders.order_entity_id;

                layRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), OrderDetailsActivity.class);
                        i.putExtra("order_id", entity_id);
                        //i.putExtra("index", i);
                        startActivity(i);
                    }
                });

                layOrderListLayout.addView(orderView);



            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        DashboardActivity.animateToggle(0, 1);
    }

    public interface MyOrdersInterface {
        public void setSelectedFragment(Fragment fragment);
    }
}

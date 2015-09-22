package com.fashion.krish.options;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fashion.krish.AppController;
import com.fashion.krish.R;
import com.fashion.krish.model.ProductDetails;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.TimePickerDialog;
import com.rey.material.widget.EditText;
import com.rey.material.widget.RadioButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by amit.thakkar on 8/10/2015.
 */
public class TypeTimeView {

    ProductDetails.ProductOptions productOptions;
    ActionBarActivity activity;
    private LayoutInflater inflate;
    private View timeRootview;
    private TextView txtHeader,txtError;
    private EditText editText,etQty;
    public static LinearLayout container;
    int date,month,year,minute,second,hour;

    public TypeTimeView(ActionBarActivity activity, ProductDetails.ProductOptions productOptions, LinearLayout container){
        this.activity = activity;
        this.productOptions = productOptions;
        this.container = container;
        initLayout();
    }

    public void initLayout(){
        inflate = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        timeRootview = inflate.inflate(R.layout.layout_time, null);

        ((View) timeRootview.findViewById(R.id.time_separator)).setBackgroundColor(Color.parseColor(AppController.PRIMARY_COLOR));

        String price = productOptions.option_formatted_price;
        if(price.length() > 0){
            price = " + " + price;
        }

        txtHeader = (TextView) timeRootview.findViewById(R.id.txt_time_title);
        txtHeader.setText(productOptions.option_label + price);
        if (productOptions.option_is_required.equals("1"))
            txtHeader.append(" *");

        txtError = (TextView) timeRootview.findViewById(R.id.txt_time_error);
        txtError.setVisibility(View.GONE);
        txtError.setTag("error" + productOptions.option_code);
        txtError.setVisibility(View.GONE);

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        date  = today.monthDay;
        month = today.month;
        year = today.year;
        minute = today.minute;
        second = today.second;
        hour = today.hour;

        final TextView etTime = (TextView) timeRootview.findViewById(R.id.edt_time);
        etTime.setTag("time"+productOptions.option_code);
        etTime.setText("HH:mm AA");

        final int primaryColor = Color.parseColor(AppController.PRIMARY_COLOR);
        final int secondaryColor = Color.parseColor(AppController.SECONDARY_COLOR);
        TimePickerDialog.primaryColor = primaryColor;
        TimePickerDialog.secondaryColor = secondaryColor;

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.Builder timePickerDialog = new TimePickerDialog.Builder(R.style.Material_App_Dialog_TimePicker_Light, hour, minute) {
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {

                        TimePickerDialog dialog = (TimePickerDialog) fragment.getDialog();
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                        String time = dialog.getFormattedTime(sdf);

                        etTime.setText(time.split(" ")[0].split(":")[0] + ":" +
                                time.split(" ")[0].split(":")[1] +
                                " " + time.split(" ")[1]);

                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }
                };

                timePickerDialog.positiveAction("OK").negativeAction("Cancel");

                DialogFragment fragment = DialogFragment.newInstance(timePickerDialog);
                fragment.show(activity.getSupportFragmentManager(), "");


            }
        });


        container.addView(timeRootview);
    }

    public static boolean isValidated(String code){
        boolean isValidate = false;
        TextView txtTime= (TextView) container.findViewWithTag("time"+code);
        TextView txtEr = (TextView) container.findViewWithTag("error" + code);

        if(txtTime.getText().toString().equals("HH:mm AA")){
            txtEr.setVisibility(View.VISIBLE);
            isValidate = false;
        }else{
            txtEr.setVisibility(View.GONE);
            isValidate = true;
        }

        return isValidate;
    }


}

package com.fashion.krish.options;

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
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.TimePickerDialog;
import com.rey.material.widget.EditText;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by amit.thakkar on 8/10/2015.
 */
public class TypeDateView {

    ProductDetails.ProductOptions productOptions;
    ActionBarActivity activity;
    private LayoutInflater inflate;
    private View dateRootview;
    private TextView txtHeader,txtError;
    public static LinearLayout container;
    int date,month,year,minute,second,hour;

    public TypeDateView(ActionBarActivity activity, ProductDetails.ProductOptions productOptions, LinearLayout container){
        this.activity = activity;
        this.productOptions = productOptions;
        this.container = container;
        initLayout();
    }

    public void initLayout(){
        inflate = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dateRootview = inflate.inflate(R.layout.layout_date, null);

        ((View) dateRootview.findViewById(R.id.date_separator)).setBackgroundColor(Color.parseColor(AppController.PRIMARY_COLOR));
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        date  = today.monthDay;
        month = today.month;
        year = today.year;
        minute = today.minute;
        second = today.second;
        hour = today.hour;

        String price = productOptions.option_formatted_price;
        if(price.length() > 0){
            price = " + " + price;
        }

        txtHeader = (TextView) dateRootview.findViewById(R.id.txt_date_title);
        txtHeader.setText(productOptions.option_label + price);
        if (productOptions.option_is_required.equals("1"))
            txtHeader.append(" *");

        txtError = (TextView) dateRootview.findViewById(R.id.txt_date_error);
        txtError.setVisibility(View.GONE);
        txtError.setTag("error" + productOptions.option_code);
        txtError.setVisibility(View.GONE);

        final TextView etDate = (TextView) dateRootview.findViewById(R.id.edt_date);
        etDate.setTag("date"+productOptions.option_code);
        etDate.setText("YYYY/MM/DD");

        final int primaryColor = Color.parseColor(AppController.PRIMARY_COLOR);
        final int secondaryColor = Color.parseColor(AppController.SECONDARY_COLOR);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.Builder datePickerDialog = new DatePickerDialog.Builder(R.style.Material_App_Dialog_TimePicker_Light,
                   primaryColor, secondaryColor) {
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {

                        DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String date = dialog.getFormattedDate(sdf);

                        etDate.setText(date.split("-")[0] + "/" +
                                date.split("-")[1] +
                                "/" + date.split("-")[2]);

                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);

                    }
                };

                datePickerDialog.positiveAction("OK").negativeAction("Cancel");
                //timePickerDialog.getDialog().show();

                DialogFragment fragment = DialogFragment.newInstance(datePickerDialog);
                fragment.show(activity.getSupportFragmentManager(), "");


            }
        });


        container.addView(dateRootview);
    }

    public static boolean isValidated(String code){
        boolean isValidate = false;
        TextView txtDt= (TextView) container.findViewWithTag("date"+code);
        TextView txtEr = (TextView) container.findViewWithTag("error" + code);

        if(txtDt.getText().toString().equals("YYYY/MM/DD")){
            txtEr.setVisibility(View.VISIBLE);
            isValidate = false;
        }else{
            txtEr.setVisibility(View.GONE);
            isValidate = true;
        }

        return isValidate;
    }


}

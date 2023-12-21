package com.smiligenceUAT1.metrozsellerpartner.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smiligenceUAT1.metrozsellerpartner.R;
import com.smiligenceUAT1.metrozsellerpartner.bean.ItemDetails;

import java.util.ArrayList;

public class OrderDetailsAdapter extends ArrayAdapter<ItemDetails> {
    public Activity context;

    public ArrayList<ItemDetails> orderReportsList;

    TextView  itemName, qty, Total_price,itemPrice;

    public OrderDetailsAdapter(Activity context, ArrayList<ItemDetails> billreportsList) {
        super(context, R.layout.orderdetails_list, billreportsList);
        this.context = context;
        this.orderReportsList = billreportsList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listview = inflater.inflate(R.layout.orderdetails_list, null, true);

        itemPrice = (TextView) listview.findViewById(R.id.itemPrice);
        itemName = (TextView) listview.findViewById(R.id.items);
        qty = (TextView) listview.findViewById(R.id.qty);
        Total_price = listview.findViewById(R.id.total);

        ItemDetails billDetails = orderReportsList.get(position);
        itemName.setText(String.valueOf(billDetails.getItemName()));
        itemPrice.setText(String.valueOf(billDetails.getItemPrice()));
        qty.setText(String.valueOf(billDetails.getItemQuantity()));
        Total_price.setText(String.valueOf(billDetails.getTotalItemQtyPrice()));

        return listview;
    }
}

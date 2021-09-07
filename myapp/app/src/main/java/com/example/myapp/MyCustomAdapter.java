package com.example.myapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyCustomAdapter extends ArrayAdapter {
    private final Activity activity;
    private final Data smsMessages;

    public MyCustomAdapter(Activity activity, Data smsData) {
        super(activity, R.layout.row , smsData.getMessages());
        this.activity = activity;
        this.smsMessages = smsData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        RowView sqView = null;

        if(rowView == null)
        {
            // Get a new instance of the row layout view
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.row, null);

            // Hold the view objects in an object,
            // so they don't need to be re-fetched
            sqView = new RowView();
            sqView.trAmount = (TextView) rowView.findViewById(R.id.tr_amount);
            sqView.trDate = (TextView) rowView.findViewById(R.id.tr_date);

            // Cache the view objects in the tag,
            // so they can be re-accessed later
            rowView.setTag(sqView);
        } else {
            sqView = (RowView) rowView.getTag();
        }

        // Transfer the stock data from the data object
        // to the view objects
        sqView.trAmount.setText("Amount");
        sqView.trDate.setText(smsMessages.getAmount(position));

        return rowView;
    }

    protected static class RowView {
        protected TextView trDate;
        protected TextView trAmount;
    }
}
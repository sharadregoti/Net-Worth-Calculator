package com.sharad.myapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sharad.myapp.R;

public class StockBrokerSelector extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;

    public StockBrokerSelector() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stock_broker_selector, container, false);

        ImageView zerodha = v.findViewById(R.id.fragment_stock_broker_selector_zerodha_image);
        // ImageView upstox = v.findViewById(R.id.fragment_stock_broker_selector_upstox_image);

        zerodha.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://d2d9-120-138-2-205.ngrok.io/v1/zerodha-oauth-redirect"));
            startActivity(browserIntent);
            dismiss();
        });
        // upstox.setOnClickListener(view -> {
        //     Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://d2d9-120-138-2-205.ngrok.io/v1/zerodha-oauth-redirect"));
        //     startActivity(browserIntent);
        //     dismiss();
        // });

        return v;
    }

    public interface BottomSheetListener {
        void onSaveButtonClicked(String msg);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (StockBrokerSelector.BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement bottom sheet listener");
        }
    }
}

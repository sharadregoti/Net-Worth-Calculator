package com.example.myapp;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class TransactionFilterBottomSheetFragment extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_transaction_filter_bottom_sheet, container, false);
//        ChipGroup cg = v.findViewById(R.id.filter_date);
//        ChipGroup transactionType = v.findViewById(R.id.filter_transaction_type);
//        ChipGroup bank = v.findViewById(R.id.filter_bank);
//        ChipGroup paymentType = v.findViewById(R.id.filter_payment_type);

        Chip filter_date_last_week = v.findViewById(R.id.filter_date_last_week);
        Chip filter_date_last_2_week = v.findViewById(R.id.filter_date_last_2_week);
        Chip filter_date_last_month = v.findViewById(R.id.filter_date_last_month);
        Chip filter_date_last_3_month = v.findViewById(R.id.filter_date_last_3_month);
        Chip filter_transaction_type_credit = v.findViewById(R.id.filter_transaction_type_credit);
        Chip filter_transaction_type_debit = v.findViewById(R.id.filter_transaction_type_debit);
        Chip filter_payment_type_cash = v.findViewById(R.id.filter_payment_type_cash);
        Chip filter_payment_type_online = v.findViewById(R.id.filter_payment_type_online);

        ArrayList<Chip> myChips = new ArrayList<Chip>();
        myChips.add(filter_date_last_week);
        myChips.add(filter_date_last_2_week);
        myChips.add(filter_date_last_month);
        myChips.add(filter_date_last_3_month);
        myChips.add(filter_transaction_type_credit);
        myChips.add(filter_transaction_type_debit);
        myChips.add(filter_payment_type_cash);
        myChips.add(filter_payment_type_online);

        myChips.forEach(chip -> {
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Chip text = ((Chip) view);
                    text.setChecked(text.isChecked());
                }
            });
        });

        MaterialButton clear = v.findViewById(R.id.filter_button_clear);
        MaterialButton save = v.findViewById(R.id.filter_button_save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> banks = new ArrayList<>(), transactionType = new ArrayList<>(), paymentType = new ArrayList<>();

                String T_CREDITED = "credited";
                String T_DEBITED = "debited";
                String T_CASH = "cash";
                String T_ONLINE = "online";


                if (filter_payment_type_cash.isChecked()) {
                    paymentType.add(T_CASH);
                }
                if (filter_payment_type_online.isChecked()) {
                    paymentType.add(T_ONLINE);
                }

                if (filter_transaction_type_credit.isChecked()) {
                    transactionType.add(T_CREDITED);
                }
                if (filter_transaction_type_debit.isChecked()) {
                    transactionType.add(T_DEBITED);
                }

                Long endDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
                Long startDate = 0L;
                if (filter_date_last_week.isChecked()) {
                    startDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).minusWeeks(1).toEpochSecond() * 1000;
                } else if (filter_date_last_2_week.isChecked()) {
                    startDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).minusWeeks(2).toEpochSecond() * 1000;
                } else if (filter_date_last_month.isChecked()) {
                    startDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).minusMonths(1).toEpochSecond() * 1000;
                } else if (filter_date_last_3_month.isChecked()) {
                    startDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).minusMonths(3).toEpochSecond() * 1000;
                } else {
                    startDate = LocalDateTime.now().withDayOfMonth(1).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
                }

                Log.d("Mother fucker","filter 11");
                mListener.onSaveButtonClicked(String.valueOf(startDate), String.valueOf(endDate), banks, paymentType, transactionType);
                dismiss();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long endDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
                Long startDate = LocalDateTime.now().withDayOfMonth(1).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
                // The default filter is applied
                mListener.onSaveButtonClicked(String.valueOf(startDate), String.valueOf(endDate),  new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                dismiss();
            }
        });

        return v;
    }

    public interface BottomSheetListener {
        void onSaveButtonClicked(String startDate, String endDate, ArrayList<String> banks, ArrayList<String> paymentType, ArrayList<String> transactionType);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement bottom sheet listener");
        }
    }
}
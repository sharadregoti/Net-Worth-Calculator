package com.example.myapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.util.Pair;

import com.example.myapp.Utils.Constants;
import com.example.myapp.Utils.DatabaseHelper;
import com.example.myapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class TransactionFilterBottomSheetFragment extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;

    private ArrayList<String> currentBankFilter = new ArrayList<>(), currentTxnTypeFilter = new ArrayList<>(), currentPaymentTypeFilter = new ArrayList<>();
    private String currentDateFilter = Constants.FILTER_DEFAULT_DATE;
    private String currentStartDate = "";
    private String currentEndDate = "";
    private String currentCustomDateText = "";

    public TransactionFilterBottomSheetFragment(String dateFilterType, String customDateText, String startDate, String endDate, ArrayList<String> banks, ArrayList<String> paymentType, ArrayList<String> transactionType) {
        this.currentDateFilter = dateFilterType;
        this.currentStartDate = startDate;
        this.currentEndDate = endDate;
        this.currentBankFilter = banks;
        this.currentPaymentTypeFilter = paymentType;
        this.currentTxnTypeFilter = transactionType;
        this.currentCustomDateText = customDateText;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_transaction_filter_bottom_sheet, container, false);

        // Get all the views in the filter
        Chip filter_date_last_week = v.findViewById(R.id.filter_date_last_week);
        Chip filter_date_last_2_week = v.findViewById(R.id.filter_date_last_2_week);
        Chip filter_date_last_month = v.findViewById(R.id.filter_date_last_month);
        Chip filter_date_last_3_month = v.findViewById(R.id.filter_date_last_3_month);
        Chip filter_date_custom_date = v.findViewById(R.id.filter_date_custom_date);
        Chip filter_transaction_type_credit = v.findViewById(R.id.filter_transaction_type_credit);
        Chip filter_transaction_type_debit = v.findViewById(R.id.filter_transaction_type_debit);
        Chip filter_payment_type_cash = v.findViewById(R.id.filter_payment_type_cash);
        Chip filter_payment_type_online = v.findViewById(R.id.filter_payment_type_online);
        ChipGroup filter_bank = v.findViewById(R.id.filter_bank);
        MaterialButton btnClear = v.findViewById(R.id.filter_button_clear);
        MaterialButton btnSave = v.findViewById(R.id.filter_button_save);

        // Initialize prev values
        switch (currentDateFilter) {
            case Constants.FILTER_LAST_WEEK:
                filter_date_last_week.setChecked(true);
                break;
            case Constants.FILTER_LAST_2_WEEK:
                filter_date_last_2_week.setChecked(true);
                break;
            case Constants.FILTER_LAST_MONTH:
                filter_date_last_month.setChecked(true);
                break;
            case Constants.FILTER_LAST_3_MONTH:
                filter_date_last_3_month.setChecked(true);
                break;
            case Constants.FILTER_CUSTOM_DATE:
                filter_date_custom_date.setChecked(true);
                break;
        }

        for (int i = 0; i < currentPaymentTypeFilter.size(); i++) {
            switch (currentPaymentTypeFilter.get(i)) {
                case Constants.PAYMENT_TYPE_CASH:
                    filter_payment_type_cash.setChecked(true);
                    continue;
                case Constants.PAYMENT_TYPE_ONLINE:
                    filter_payment_type_online.setChecked(true);
            }
        }

        for (int i = 0; i < currentTxnTypeFilter.size(); i++) {
            switch (currentTxnTypeFilter.get(i)) {
                case Constants.TXN_TYPE_CREDITED:
                    filter_transaction_type_credit.setChecked(true);
                    continue;
                case Constants.TXN_TYPE_DEBITED:
                    filter_transaction_type_debit.setChecked(true);
            }
        }


        // Dynamically set the list of banks in filter page as chip view
        DatabaseHelper dh = new DatabaseHelper(getContext());
        ArrayList<String> arr = dh.getListOfBanks();
        filter_bank.removeAllViews();
        for (int i = 0; i < arr.size(); i++) {
            Chip chip = new Chip(getContext());
            chip.setText(arr.get(i));
            chip.setCheckable(true);

            // Set previous filter if applicable
            for (int j = 0; j < currentBankFilter.size(); j++) {
                if (chip.getText().equals(currentBankFilter.get(j))) {
                    chip.setChecked(true);
                }
            }
            filter_bank.addView(chip);
        }

        // Date picker
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        // MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().build();
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setCalendarConstraints(constraintsBuilder.build());
        if (currentDateFilter.equals(Constants.FILTER_CUSTOM_DATE)) {
            Pair<Long, Long> drp = new Pair<Long, Long>(Long.valueOf(currentStartDate), Long.valueOf(currentEndDate));
            builder.setSelection(drp);
            filter_date_custom_date.setText(currentCustomDateText);
        }
        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();

        // Show date range picker when custom date chip is clicked
        filter_date_custom_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Chip myChip = ((Chip) view);
                materialDatePicker.show(getParentFragmentManager(), "DATE_PICKER");
                filter_date_custom_date.setText("Custom date");
            }
        });

        String[] customDateText = {""};
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                filter_date_custom_date.setText(materialDatePicker.getHeaderText());
                filter_date_custom_date.setChecked(true);
                customDateText[0] = materialDatePicker.getHeaderText();
            }
        });
        materialDatePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter_date_custom_date.setText("Custom date");
                filter_date_custom_date.setChecked(false);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> banks = new ArrayList<>(), transactionType = new ArrayList<>(), paymentType = new ArrayList<>();

                List<Integer> chipList = filter_bank.getCheckedChipIds();
                chipList.forEach(integer -> {
                    Chip c = v.findViewById(integer);
                    banks.add((String) c.getText());
                });

                if (filter_payment_type_cash.isChecked()) {
                    paymentType.add(Constants.PAYMENT_TYPE_CASH);
                }
                if (filter_payment_type_online.isChecked()) {
                    paymentType.add(Constants.PAYMENT_TYPE_ONLINE);
                }

                if (filter_transaction_type_credit.isChecked()) {
                    transactionType.add(Constants.TXN_TYPE_CREDITED);
                }
                if (filter_transaction_type_debit.isChecked()) {
                    transactionType.add(Constants.TXN_TYPE_DEBITED);
                }

                String dateFilterType = "";
                Long endDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
                Long startDate = 0L;
                if (filter_date_last_week.isChecked()) {
                    dateFilterType = Constants.FILTER_LAST_WEEK;
                    startDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).minusWeeks(1).toEpochSecond() * 1000;
                } else if (filter_date_last_2_week.isChecked()) {
                    dateFilterType = Constants.FILTER_LAST_2_WEEK;
                    startDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).minusWeeks(2).toEpochSecond() * 1000;
                } else if (filter_date_last_month.isChecked()) {
                    dateFilterType = Constants.FILTER_LAST_MONTH;
                    startDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).minusMonths(1).toEpochSecond() * 1000;
                } else if (filter_date_last_3_month.isChecked()) {
                    dateFilterType = Constants.FILTER_LAST_3_MONTH;
                    startDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).minusMonths(3).toEpochSecond() * 1000;
                } else if (filter_date_custom_date.isChecked()) {
                    dateFilterType = Constants.FILTER_CUSTOM_DATE;
                    Pair<Long, Long> da = (Pair<Long, Long>) materialDatePicker.getSelection();
                    startDate = da.first;
                    endDate = da.second;
                } else {
                    dateFilterType = Constants.FILTER_DEFAULT_DATE;
                    startDate = LocalDate.now().atTime(0, 0, 0).withDayOfMonth(1).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
                }

                mListener.onSaveButtonClicked(dateFilterType, customDateText[0], String.valueOf(startDate), String.valueOf(endDate), banks, paymentType, transactionType);
                dismiss();
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long endDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
                Long startDate = LocalDate.now().atTime(0, 0, 0).withDayOfMonth(1).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
                // The default filter is applied
                mListener.onSaveButtonClicked(Constants.FILTER_DEFAULT_DATE, "", String.valueOf(startDate), String.valueOf(endDate), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                dismiss();
            }
        });

        return v;
    }

    public interface BottomSheetListener {
        void onSaveButtonClicked(String dateFilterType, String customDateText, String startDate, String endDate, ArrayList<String> banks, ArrayList<String> paymentType, ArrayList<String> transactionType);
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
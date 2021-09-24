package com.example.myapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ActivityAddCashTransaction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_add_cash_transaction);



        MaterialToolbar mt = (MaterialToolbar) findViewById(R.id.top_action_bar);
        mt.setTitle("Add Cash");
        setSupportActionBar(mt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Transaction type options
        String[] transactionTypes = new String[]{"Credited", "Debited"};
        ArrayAdapter ttArrayAdapter = new ArrayAdapter(this, R.layout.transaction_type_drop_down_item, transactionTypes);
        AutoCompleteTextView actv = findViewById(R.id.add_cash_transaction_transactiontype);
        actv.setAdapter(ttArrayAdapter);

        // Category options
        String[] categories = new String[]{"Beauty & Fitness", "Bills", "EMI", "Eating Out", "Education", "Entertainment", "Grocery", "Household", "Insurance", "Investments", "Medical", "Miscellaneous", "Rent", "Shopping", "Transport", "Travel"};
        ArrayAdapter cArrayAdapter = new ArrayAdapter(this, R.layout.edit_text_drop_down_style, categories);
        AutoCompleteTextView categoryAutoComplete = findViewById(R.id.add_cash_transaction_expense_category);
        categoryAutoComplete.setAdapter(cArrayAdapter);

        // Date picker
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select A Date");
        final MaterialDatePicker materialDatePicker = builder.build();

        // Set date on, date edit text field
        TextInputEditText tied = findViewById(R.id.add_cash_transaction_date);
        setCurrentDate(tied);

        tied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                tied.setText(materialDatePicker.getHeaderText());
            }
        });

        TextInputEditText amountEditText = findViewById(R.id.add_cash_transaction_amount);
        AutoCompleteTextView ttAutoText = findViewById(R.id.add_cash_transaction_transactiontype);
        AutoCompleteTextView categoryAutoText = findViewById(R.id.add_cash_transaction_expense_category);
        TextInputEditText expenseMerchEditText = findViewById(R.id.add_cash_transaction_expense_merchant);
        TextInputEditText noteEditText = findViewById(R.id.add_cash_transaction_note);

        TextInputLayout tilAmount = findViewById(R.id.add_cash_transaction_layout_amount);
        TextInputLayout tilExpenseMerch = findViewById(R.id.add_cash_transaction_layout_expense_merchant);
        TextInputLayout tilTT = findViewById(R.id.add_cash_transaction_layout_transactiontype);


        amountEditText.setOnFocusChangeListener((view, b) -> {
            if (b) {
                tilAmount.setErrorEnabled(false);
            }
        });

        expenseMerchEditText.setOnFocusChangeListener((view, b) -> {
            if (b) {
                tilExpenseMerch.setErrorEnabled(false);
            }
        });

        // Hide soft keyboard while selecting from drop down menus
        ttAutoText.setOnFocusChangeListener((view, b) -> {
            if (b) {
                tilTT.setErrorEnabled(false);
                try {
                    InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(ttAutoText.getWindowToken(), 0);
                } catch (RuntimeException e) {
                    Log.d("Exception occured", e.toString());
                }
            }
        });
        categoryAutoText.setOnFocusChangeListener((view, b) -> {
            if (b) {
                try {
                    InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(categoryAutoText.getWindowToken(), 0);
                } catch (RuntimeException e) {
                    Log.d("Exception occured", e.toString());
                }
            }
        });

        MaterialButton savebtn = findViewById(R.id.add_cash_transaction_save_button);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Input validation
                boolean isValid = true;

                if (amountEditText.getText().toString().isEmpty()) {
                    tilAmount.setError("Amount is Mandatory");
                    isValid = false;
                }

                if (expenseMerchEditText.getText().toString().isEmpty()) {
                    tilExpenseMerch.setError("Merchant is Mandatory");
                    isValid = false;
                }

                if (ttAutoText.getText().toString().isEmpty()) {
                    tilTT.setError("Transaction Type is Mandatory");
                    isValid = false;
                }

                if (!isValid) {
                    return;
                }

                String amount = amountEditText.getText().toString();
                // TODO: We are not take care of time
                String date = tied.getText().toString();
                String tt = ttAutoText.getText().toString();
                String category = categoryAutoText.getText().toString();
                String expenseMerch = expenseMerchEditText.getText().toString();
                String note = noteEditText.getText().toString();
                DateFormat formatter = new SimpleDateFormat("dd MMM yyyy"); // Make sure user insert date into edittext in this format.
                Long dateL = 0L;
                try {
                    dateL = formatter.parse(date).getTime();
                    System.out.println(dateL);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Intent intent = getIntent();
                intent.putExtra("amount", Float.parseFloat(amount));
                intent.putExtra("date", dateL);
                intent.putExtra("transaction_type", tt);
                intent.putExtra("category", category);
                intent.putExtra("expense_merch", expenseMerch);
                intent.putExtra("note", note);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setCurrentDate(TextInputEditText tied) {
        Long endDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMM YYYY");
        tied.setText(sdf1.format(endDate));
    }
}
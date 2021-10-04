package com.example.myapp.Activities;

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

import com.example.myapp.R;
import com.example.myapp.Utils.Constants;
import com.example.myapp.Utils.Functions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
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

        // Get all views
        TextInputEditText etAmount = findViewById(R.id.add_cash_transaction_amount);
        TextInputEditText etTxnDate = findViewById(R.id.add_cash_transaction_date);
        TextInputEditText etExpenseMerch = findViewById(R.id.add_cash_transaction_expense_merchant);
        TextInputEditText etNotes = findViewById(R.id.add_cash_transaction_note);
        // TextInputEditText photoEditText = findViewById(R.id.add_cash_transaction_photo);
        AutoCompleteTextView actTxnType = findViewById(R.id.add_cash_transaction_transactiontype);
        AutoCompleteTextView actCategory = findViewById(R.id.add_cash_transaction_expense_category);
        TextInputLayout tilAmount = findViewById(R.id.add_cash_transaction_layout_amount);
        TextInputLayout tilExpenseMerch = findViewById(R.id.add_cash_transaction_layout_expense_merchant);
        TextInputLayout tilTxnType = findViewById(R.id.add_cash_transaction_layout_transactiontype);
        MaterialButton btnSave = findViewById(R.id.add_cash_transaction_save_button);

        String title = "Add Cash";
        Long myNewStartDate = 0L;
        String sMyNewStartDate = "";

        // Date picker
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select A Date");
        builder.setCalendarConstraints(constraintsBuilder.build());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // If extras are present that means that the parent activity is detailed view of a txn
            // Now, initialize the activity elements with these extra values
            title = extras.getString("activity_title");
            String amount = extras.getString("amount");
            etAmount.setText(amount);
            sMyNewStartDate = extras.getString("date");
            Long endDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
            DateFormat formatter = new SimpleDateFormat("dd MMM yyyy"); // Make sure user insert date into edittext in this format.
            try {
                myNewStartDate = formatter.parse(sMyNewStartDate).getTime();
                // TODO: There is a offset of 1 day, hacky solution implemented
                myNewStartDate += 86400000;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            builder.setSelection(myNewStartDate);
            String transaction_type = extras.getString("transaction_type");
            actTxnType.setText(transaction_type);
            String category = extras.getString("category");
            actCategory.setText(category);
            String expense_merchant = extras.getString("expense_merchant");
            etExpenseMerch.setText(expense_merchant);
            String notes = extras.getString("notes");
            etNotes.setText(notes);
            // String photo = extras.getString("photo");
            // photoEditText.setText(photo);
        }

        final MaterialDatePicker<Long> materialDatePicker = builder.build();
        if (myNewStartDate != 0) {
            // If parent activity is detailed txn, then set the date got from extra value
            etTxnDate.setText(sMyNewStartDate);
        } else {
            // Set date on, date edit text field
            setCurrentDate(etTxnDate);
        }

        // Set top app bar with title depending upon the parent activity
        MaterialToolbar mt = (MaterialToolbar) findViewById(R.id.top_action_bar);
        mt.setTitle(title);
        setSupportActionBar(mt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Transaction type options
        String[] transactionTypes = new String[]{Constants.TXN_TYPE_CREDITED, Constants.TXN_TYPE_DEBITED};
        ArrayAdapter ttArrayAdapter = new ArrayAdapter(this, R.layout.transaction_type_drop_down_item, transactionTypes);
        actTxnType.setAdapter(ttArrayAdapter);

        // Category options
        String[] categories = new String[]{Constants.TXN_CATEGORY_BEAUTY_FITNESS, Constants.TXN_CATEGORY_BILLS, Constants.TXN_CATEGORY_EMI, Constants.TXN_CATEGORY_EATING, Constants.TXN_CATEGORY_EDUCATION, Constants.TXN_CATEGORY_ENTERTAINMENT, Constants.TXN_CATEGORY_GROCERY, Constants.TXN_CATEGORY_HOUSEHOLD, Constants.TXN_CATEGORY_INSURANCE, Constants.TXN_CATEGORY_INVESTMENTS, Constants.TXN_CATEGORY_MEDICAL, Constants.TXN_CATEGORY_MISCELLANEOUS, Constants.TXN_CATEGORY_RENT, Constants.TXN_CATEGORY_SHOPPING, Constants.TXN_CATEGORY_TRANSPORT, Constants.TXN_CATEGORY_TRAVEL,};
        ArrayAdapter cArrayAdapter = new ArrayAdapter(this, R.layout.edit_text_drop_down_style, categories);
        actCategory.setAdapter(cArrayAdapter);


        // ************* Listeners ***************

        // Show date picker when date edit text is clicked
        etTxnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });

        // When a date is selected in date picker by pressing ok button, set that date in edit text
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                etTxnDate.setText(materialDatePicker.getHeaderText());
            }
        });


        // Disable validation errors on focus
        etAmount.setOnFocusChangeListener((view, b) -> {
            if (b) {
                tilAmount.setErrorEnabled(false);
            }
        });

        etExpenseMerch.setOnFocusChangeListener((view, b) -> {
            if (b) {
                tilExpenseMerch.setErrorEnabled(false);
            }
        });

        // Hide soft keyboard while selecting from drop down menus
        actTxnType.setOnFocusChangeListener((view, b) -> {
            if (b) {
                tilTxnType.setErrorEnabled(false);
                try {
                    InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(actTxnType.getWindowToken(), 0);
                } catch (RuntimeException e) {
                    Log.d("Exception occured", e.toString());
                }
            }
        });
        actCategory.setOnFocusChangeListener((view, b) -> {
            if (b) {
                try {
                    InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(actCategory.getWindowToken(), 0);
                } catch (RuntimeException e) {
                    Log.d("Exception occured", e.toString());
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Input validation
                boolean isValid = true;
                String amount = etAmount.getText().toString();
                // TODO: We are not take care of time
                String date = etTxnDate.getText().toString();
                String tt = actTxnType.getText().toString();
                String category = actCategory.getText().toString();
                String expenseMerch = Functions.toTitleCase(etExpenseMerch.getText().toString());
                String note = etNotes.getText().toString();

                if (amount.isEmpty()) {
                    tilAmount.setError("Amount is Mandatory");
                    isValid = false;
                } else if (amount.length() > 20) {
                    tilAmount.setError("Cannot have more than 20 characters");
                }

                if (expenseMerch.isEmpty()) {
                    tilExpenseMerch.setError("Merchant is Mandatory");
                    isValid = false;
                } else if (expenseMerch.length() > 500) {
                    tilExpenseMerch.setError("Cannot have more than 500 characters");
                }

                if (note.length() > 500) {
                    etNotes.setError("Cannot have more than 500 characters");
                }

                if (tt.isEmpty()) {
                    tilTxnType.setError("Transaction Type is Mandatory");
                    isValid = false;
                }

                if (!isValid) {
                    return;
                }

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
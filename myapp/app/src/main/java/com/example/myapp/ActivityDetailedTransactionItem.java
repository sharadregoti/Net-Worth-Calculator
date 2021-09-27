package com.example.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class ActivityDetailedTransactionItem extends AppCompatActivity {

    private boolean isDataUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_detailed_transaction_item_page);

        // Set top app bar
        MaterialToolbar mt = findViewById(R.id.top_action_bar);
        mt.setTitle("");
        setSupportActionBar(mt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle extras = getIntent().getExtras();
        String amount = extras.getString("amount");
        String date = extras.getString("date");
        String expenseMerchant = extras.getString("expense_merchant");
        String tags = extras.getString("tags");
        String bank = extras.getString("bank");
        String byFromPerson = extras.getString("by_from_person");
        String smsMessage = extras.getString("sms_message");
        String transactionType = extras.getString("transaction_type");
        String paymentType = extras.getString("payment_type");
        String category = extras.getString("category");
        String notes = extras.getString("notes");
        String photo = extras.getString("photo");
        String transactionSMSId = extras.getString("id");
        int amountTextColor = extras.getInt("amount_text_color");

        TextView tAmount = findViewById(R.id.reusable_card_view_sms_amount);
        TextView tDate = findViewById(R.id.reusable_card_view_sms_date);
        TextView tExpMerch = findViewById(R.id.reusable_card_view_sms_expense_merchant);
        TextView tBank = findViewById(R.id.detailed_transaction_activity_bank_name);
        TextView tByFromWhom = findViewById(R.id.reusable_card_view_sms_by_from_whom);
        TextView tSmsBody = findViewById(R.id.detailed_transaction_activity_sms_body);
        TextView tNotes = findViewById(R.id.detailed_transaction_activity_notes);
        ChipGroup cgTags = findViewById(R.id.reusable_card_view_tags_chip_group);

        MaterialCardView mcvSms = findViewById(R.id.detailed_transaction_activity_sms_material_card_view);

        MaterialButton deleteButton = findViewById(R.id.detailed_transaction_activity_delete_button);
        MaterialButton editButton = findViewById(R.id.detailed_transaction_activity_edit_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.putExtra("id", transactionSMSId);
                setResult(ActivityTransactions.RESULT_TXN_DELETED, intent);
                finish();
            }
        });

        DatabaseHelper dh = new DatabaseHelper(this);
        ActivityResultLauncher<Intent> handleUpdateTxnActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Float amount = data.getFloatExtra("amount", 0);
                            Long date = data.getLongExtra("date", 0L);
                            String transaction_type = data.getStringExtra("transaction_type");
                            String category = data.getStringExtra("category");
                            String expense_merch = data.getStringExtra("expense_merch");
                            String note = data.getStringExtra("note");

                            if (dh.updateProcessedTxnSMS(transactionSMSId, amount, date, transaction_type, expense_merch, note, category) != -1) {
                                Log.i("Txn update", "success");
                            } else {
                                Log.i("Txn update", "fail");
                            }

                            String sAmount = "";
                            String byFromPerson = "";
                            Integer amountTextColor = 0;
                            SimpleDateFormat sDate = new SimpleDateFormat("dd MMM YYYY");
                            DecimalFormat df = new DecimalFormat("##,##,##,##,##,##,##0.#");
                            if (transaction_type.equals(Utils.TXN_TYPE_CREDITED)) {
                                sAmount = "+ ₹" + df.format(amount);
                                byFromPerson = "Credited by";
                                amountTextColor = getColor(R.color.green_money);
                            } else if (transaction_type.equals(Utils.TXN_TYPE_DEBITED)) {
                                sAmount = "- ₹" + df.format(amount);
                                byFromPerson = "Debited to";
                                amountTextColor = getColor(R.color.red_money);
                            }


                            tAmount.setText(sAmount);
                            tAmount.setTextColor(amountTextColor);
                            tByFromWhom.setText(byFromPerson);
                            tDate.setText(sDate.format(date));
                            tExpMerch.setText(expense_merch);
                            tNotes.setText(note);

                            Snackbar sb = Snackbar.make(editButton, "Item Updated Successfully", Snackbar.LENGTH_SHORT);
                            sb.show();

                            isDataUpdated = true;
                        }
                    }
                });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityDetailedTransactionItem.this, ActivityAddCashTransaction.class);
                intent.putExtra("activity_title", "Edit transaction");
                // 3 is index required to remove +/- space ruppe symbol from text
                intent.putExtra("amount", amount.substring(3).replace(",", ""));
                intent.putExtra("date", date);
                intent.putExtra("transaction_type", Utils.toTitleCase(transactionType));
                intent.putExtra("category", category);
                intent.putExtra("expense_merchant", expenseMerchant);
                intent.putExtra("notes", notes);
                intent.putExtra("photo", photo);
                handleUpdateTxnActivityResult.launch(intent);
            }
        });

        // Set values on the views of activity
        tAmount.setText(amount);
        tAmount.setTextColor(amountTextColor);
        tDate.setText(date);
        tExpMerch.setText(expenseMerchant);
        tBank.setText(bank);
        tByFromWhom.setText(byFromPerson);
        if (paymentType.equals(Utils.PAYMENT_TYPE_CASH)) {
            mcvSms.setVisibility(View.INVISIBLE);
        }
        tSmsBody.setText(smsMessage);
        tNotes.setText(notes);

        // Remove old tags in chip group (happens when filter is rendered)
        cgTags.removeAllViews();
        String[] arr = new String[]{};
        arr = tags.split(" ", -1);
        for (int i = 0; i < arr.length; i++) {
            Chip chip = new Chip(this);
            chip.setText(arr[i]);
            cgTags.addView(chip);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra("is_data_updated", isDataUpdated);
        setResult(ActivityTransactions.RESULT_TXN_UPDATED, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
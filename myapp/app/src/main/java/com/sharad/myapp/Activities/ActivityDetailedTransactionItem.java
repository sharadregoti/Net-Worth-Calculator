package com.sharad.myapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.sharad.myapp.R;
import com.sharad.myapp.Utils.Constants;
import com.sharad.myapp.Utils.DatabaseHelper;
import com.sharad.myapp.Utils.Functions;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class ActivityDetailedTransactionItem extends AppCompatActivity {
    private final HashMap<String, Integer> supportedBanks = new HashMap<>();
    private boolean isDataUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_detailed_transaction_item_page);

        supportedBanks.put(Constants.BANK_STATE_BANK_OF_INDIA, R.drawable.bank_logo_sbi);
        supportedBanks.put(Constants.BANK_ICICI, R.drawable.bank_logo_icici);
        supportedBanks.put(Constants.BANK_HDFC_BANK, R.drawable.bank_logo_hdfc);
        supportedBanks.put(Constants.BANK_BANK_OF_INDIA, R.drawable.bank_logo_boi);
        supportedBanks.put(Constants.BANK_UNKNOWN, R.drawable.bank_logo_unknown_bank);

        // Set top app bar
        MaterialToolbar mt = findViewById(R.id.top_action_bar);
        mt.setTitle("");
        setSupportActionBar(mt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Bundle extras = getIntent().getExtras();
        String amount = extras.getString("amount");
        String nonFormattedAmount = extras.getString("non_formatted_amount");
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
        int transactionSMSId = extras.getInt("id");
        int amountTextColor = extras.getInt("amount_text_color");

        TextView tAmount = findViewById(R.id.reusable_card_view_sms_amount);
        TextView tDate = findViewById(R.id.reusable_card_view_sms_date);
        TextView tExpMerch = findViewById(R.id.reusable_card_view_sms_expense_merchant);
        TextView tBank = findViewById(R.id.detailed_transaction_activity_sms_head_text);
        TextView tByFromWhom = findViewById(R.id.reusable_card_view_sms_by_from_whom);
        TextView tSmsBody = findViewById(R.id.detailed_transaction_activity_sms_body);
        TextView tNotes = findViewById(R.id.detailed_transaction_activity_notes);
        TextView tSmsBankName = findViewById(R.id.detailed_transaction_activity_sms_bank_name);
        ImageView iTxnPhoto = findViewById(R.id.detailed_transaction_activity_image);
        ImageView iBank = findViewById(R.id.detailed_transaction_activity_bank_image);
        ChipGroup cgTags = findViewById(R.id.reusable_card_view_tags_chip_group);

        MaterialCardView mcvSms = findViewById(R.id.detailed_transaction_activity_sms_material_card_view);

        MaterialButton deleteButton = findViewById(R.id.detailed_transaction_activity_delete_button);
        MaterialButton editButton = findViewById(R.id.detailed_transaction_activity_edit_button);

        if (!bank.equals("")) {
            if (supportedBanks.get(bank) == null) {
                iBank.setImageResource(supportedBanks.get(Constants.BANK_UNKNOWN));
            } else {
                iBank.setImageResource(supportedBanks.get(bank));
            }
        }

        deleteButton.setOnClickListener(view -> {
            Intent intent = getIntent();
            intent.putExtra("id", transactionSMSId);
            setResult(ActivityTransactions.RESULT_TXN_DELETED, intent);
            finish();
        });

        DatabaseHelper dh = new DatabaseHelper(this);
        ActivityResultLauncher<Intent> handleUpdateTxnActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        float amount1 = data.getFloatExtra("amount", 0);
                        Long date1 = data.getLongExtra("date", 0L);
                        String transaction_type = data.getStringExtra("transaction_type");
                        String category1 = data.getStringExtra("category");
                        String expense_merch = data.getStringExtra("expense_merch");
                        String note = data.getStringExtra("note");
                        String intentPhoto = data.getStringExtra("photo");

                        if (dh.updateProcessedTxnSMS(transactionSMSId, amount1, date1, transaction_type, expense_merch, note, category1, intentPhoto) != -1) {
                            Log.i("Txn update", "success");
                        } else {
                            Log.i("Txn update", "fail");
                        }

                        String sAmount = "";
                        String byFromPerson1 = "";
                        Integer amountTextColor1 = 0;
                        SimpleDateFormat sDate = new SimpleDateFormat("dd MMM YYYY");
                        if (transaction_type.equals(Constants.TXN_TYPE_CREDITED)) {
                            sAmount = "+ ₹" + Functions.format((long) (amount1));
                            byFromPerson1 = "Credited by";
                            amountTextColor1 = getColor(R.color.green_money);
                        } else if (transaction_type.equals(Constants.TXN_TYPE_DEBITED)) {
                            sAmount = "- ₹" + Functions.format((long) (amount1));
                            byFromPerson1 = "Debited to";
                            amountTextColor1 = getColor(R.color.red_money);
                        }


                        tAmount.setText(sAmount);
                        tAmount.setTextColor(amountTextColor1);
                        tByFromWhom.setText(byFromPerson1);
                        tDate.setText(sDate.format(date1));
                        tExpMerch.setText(expense_merch);
                        tNotes.setText(note);
                        if (intentPhoto.isEmpty()) {
                            iTxnPhoto.setVisibility(View.GONE);
                        } else {
                            iTxnPhoto.setImageURI(Uri.parse(intentPhoto));
                        }

                        Snackbar sb = Snackbar.make(editButton, "Item Updated Successfully", Snackbar.LENGTH_SHORT);
                        sb.show();

                        isDataUpdated = true;
                    }
                });

        editButton.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityDetailedTransactionItem.this, ActivityAddCashTransaction.class);
            intent.putExtra("activity_title", "Edit transaction");
            // 3 is index required to remove +/- space ruppe symbol from text
            intent.putExtra("amount", amount.substring(3).replace(",", ""));
            intent.putExtra("non_formatted_amount", nonFormattedAmount);
            intent.putExtra("date", date);
            intent.putExtra("transaction_type", Functions.toTitleCase(transactionType));
            intent.putExtra("category", category);
            intent.putExtra("expense_merchant", expenseMerchant);
            intent.putExtra("notes", notes);
            intent.putExtra("photo", photo);
            handleUpdateTxnActivityResult.launch(intent);
        });

        // Set values on the views of activity
        tAmount.setText(amount);
        tAmount.setTextColor(amountTextColor);
        tDate.setText(date);
        tExpMerch.setText(expenseMerchant);
        tSmsBankName.setText(bank);
        tByFromWhom.setText(byFromPerson);
        if (paymentType.equals(Constants.PAYMENT_TYPE_CASH)) {
            mcvSms.setVisibility(View.INVISIBLE);
        }
        tSmsBody.setText(smsMessage);
        tNotes.setText(notes);
        if (!photo.isEmpty()) {
            iTxnPhoto.setVisibility(View.VISIBLE);
            iTxnPhoto.setImageURI(Uri.parse(photo));
        }

        iTxnPhoto.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(photo))));

        // Remove old tags in chip group (happens when filter is rendered)
        cgTags.removeAllViews();
        String[] arr = new String[]{};
        arr = tags.split(" ", -1);
        for (String s : arr) {
            Chip chip = new Chip(this);
            chip.setText(s);
            cgTags.addView(chip);
        }
        Chip showChipInCategory = new Chip(this);
        showChipInCategory.setText(category);
        showChipInCategory.setChipIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.detailed_transaction_category_chip_icon, null));
        cgTags.addView(showChipInCategory);
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
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
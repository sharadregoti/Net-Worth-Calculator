package com.sharad.myapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.sharad.myapp.Adapters.TransactionsRecycleAdapter;
import com.sharad.myapp.Fragments.TransactionFilterBottomSheetFragment;
import com.sharad.myapp.R;
import com.sharad.myapp.Utils.Constants;
import com.sharad.myapp.Utils.DatabaseHelper;
import com.sharad.myapp.Utils.Functions;
import com.sharad.myapp.Utils.ProcessSMS;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class ActivityTransactions extends AppCompatActivity implements TransactionFilterBottomSheetFragment.BottomSheetListener {

    public static final int RESULT_TXN_DELETED = 100;
    public static final int RESULT_TXN_UPDATED = 101;

    // Save bottom sheet filter state in this variables
    private String currentDateFilter = Constants.FILTER_DEFAULT_DATE;
    private String currentCustomDateText = "";
    private String currentStartDate = String.valueOf(LocalDate.now().atTime(0, 0, 0).withDayOfMonth(1).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
    private String currentEndDate = String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
    private ArrayList<String> currentBankFilter = new ArrayList<>(), currentTxnTypeFilter = new ArrayList<>(), currentPaymentTypeFilter = new ArrayList<>();
    // Indicates the count of total filter applied (Max 4)
    private int appliedFilterCount = 0;

    DatabaseHelper.StoreProcessedTransactionResult processedTxns;
    // ProcessSMS smsProcessor;
    DatabaseHelper dh;
    TransactionsRecycleAdapter transactionsRecycleAdapter;
    LinearProgressIndicator lpi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        // Get SMS permission
        ActivityCompat.requestPermissions(
                ActivityTransactions.this,
                new String[]{Manifest.permission.READ_SMS},
                PackageManager.PERMISSION_GRANTED
        );

        SharedPreferences c = getSharedPreferences("transactionDialogText", MODE_PRIVATE);
        if (!c.getBoolean("is_dialog_shown", false)) {
            MaterialAlertDialogBuilder madb = new MaterialAlertDialogBuilder(this);
            madb.setTitle("Note");
            madb.setMessage("We consider your ATM withdrawals as in hand cash and not as an expense");
            madb.setPositiveButton(R.string.transaction_note_atm, (dialogInterface, i) -> {
                SharedPreferences sharedPreferences = getSharedPreferences("transactionDialogText", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putBoolean("is_dialog_shown", true);
                myEdit.apply();
            });
            madb.show();
        }

        RecyclerView transactionRecyclerView;
        ExtendedFloatingActionButton addCashTxnBtn = findViewById(R.id.transaction_page_add_cash_transaction);
        TextView tvInHandCashAmount = findViewById(R.id.transaction_activity_set_in_hand_cash_amount_text);
        TextView tvInHandCashUpdate = findViewById(R.id.transaction_activity_set_in_hand_cash_update_text);
        transactionRecyclerView = findViewById(R.id.transaction_activity_recycler_view);

        new ProcessSMS(this);
        dh = new DatabaseHelper(this);
        processedTxns = dh.getProcessedTransactions("", "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        // changeNoTxnFoundText();

        // Set back button
        MaterialToolbar mt = findViewById(R.id.top_action_bar);
        mt.setTitle("");
        setSupportActionBar(mt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Use Default filter of current month, change the filter & summary text
        changeTransactionFilterText(Long.parseLong(currentStartDate), Long.parseLong(currentEndDate));
        changeTransactionSummaryText();

        ActivityResultLauncher<Intent> handleDetailedTxnActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (data == null) {
                        Log.i("Delete transaction activity, didn't set required extras", "");
                        return;
                    }

                    if (result.getResultCode() == RESULT_TXN_UPDATED) {
                        boolean isDataUpdated = data.getBooleanExtra("is_data_updated", false);
                        if (isDataUpdated) {
                            onSaveButtonClicked(currentDateFilter, currentCustomDateText, currentStartDate, currentEndDate, currentBankFilter, currentPaymentTypeFilter, currentTxnTypeFilter);
                        }
                    } else if (result.getResultCode() == RESULT_TXN_DELETED) {
                        int id = data.getIntExtra("id", 0);
                        if (id > 0) {

                            dh.deleteProcessedTxnSMS(id);

                            Snackbar sb = Snackbar.make(addCashTxnBtn, "Item Deleted Successfully", Snackbar.LENGTH_SHORT);
                            sb.show();

                            onSaveButtonClicked(currentDateFilter, currentCustomDateText, currentStartDate, currentEndDate, currentBankFilter, currentPaymentTypeFilter, currentTxnTypeFilter);
                        }
                    }
                });

        // Set recycle view adapter
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionsRecycleAdapter = new TransactionsRecycleAdapter(this, processedTxns, handleDetailedTxnActivityResult);
        transactionRecyclerView.setAdapter(transactionsRecycleAdapter);

        // Process the edit text fields of add cash txn activity, whenever save button is clicked
        ActivityResultLauncher<Intent> handleAddCashActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Float amount = data.getFloatExtra("amount", 0);
                        Long date = data.getLongExtra("date", 0L);
                        String transaction_type = data.getStringExtra("transaction_type");
                        String category = data.getStringExtra("category");
                        String expense_merch = data.getStringExtra("expense_merch");
                        String note = data.getStringExtra("note");
                        String photoURI = data.getStringExtra("photo");
                        String is_mismatch_manual_msg = data.getStringExtra("is_mismatch_manual_msg");

                        Float inHandAmt = dh.getInHandCashAmount();
                        if (amount > inHandAmt && transaction_type.equals(Constants.TXN_TYPE_DEBITED)) {
                            if (is_mismatch_manual_msg.isEmpty()) {
                                is_mismatch_manual_msg = Constants.NOT_AVAILABLE;
                            }
                            long insertId = dh.addTransactionSMS(amount - inHandAmt, date - 10, 0, "", Constants.TXN_TYPE_CREDITED, Constants.PAYMENT_TYPE_CASH, is_mismatch_manual_msg, "Offline", "", "", "", Constants.TXN_CATEGORY_DEFAULT_Other);
                            dh.insertInHandCashAmount(insertId, (amount - inHandAmt), Constants.IN_HAND_CASH_AUTOMATICALLY_AMOUNT_ADJUSTED);
                            // in hand adjustment made, in hand equals current amount
                            inHandAmt = inHandAmt + (amount-inHandAmt);
                        }

                        long insertId = dh.addTransactionSMS(amount, date, 0, "", transaction_type, Constants.PAYMENT_TYPE_CASH, expense_merch, "Offline", "", note, photoURI, category);
                        if (insertId != -1) {
                            Log.d("Add cash txn: ", "Added data successfully");

                            if (transaction_type.equals(Constants.TXN_TYPE_DEBITED)) {
                                dh.insertInHandCashAmount(insertId, inHandAmt - amount, Constants.IN_HAND_CASH_AUTOMATICALLY_DEBITED);
                            } else if (transaction_type.equals(Constants.TXN_TYPE_CREDITED)) {
                                dh.insertInHandCashAmount(insertId, inHandAmt + amount, Constants.IN_HAND_CASH_AUTOMATICALLY_CREDITED);
                            }
                            changeTransactionSummaryText();
                        } else {
                            Log.d("Add cash txn: ", "Failed to add data");
                        }

                        onSaveButtonClicked(currentDateFilter, currentCustomDateText, currentStartDate, currentEndDate, currentBankFilter, currentPaymentTypeFilter, currentTxnTypeFilter);
                    }
                });

        // Show add cash txn page, when FAB is clicked
        addCashTxnBtn.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityTransactions.this, ActivityAddCashTransaction.class);
            intent.putExtra("in_hand_cash", processedTxns.getInHandCash());
            handleAddCashActivityResult.launch(intent);
        });

        // Process the edit text fields of update txn activity (opened from detailed row view), whenever save button is clicked
        ActivityResultLauncher<Intent> handleUpdateInHandCashActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String amount = data.getStringExtra("amount");
                        if (amount.isEmpty()) {
                            amount = "0";
                        }
                        float inHandAmt = dh.getInHandCashAmount();
                        float currentAmt = Float.parseFloat(amount);
                        if (inHandAmt > currentAmt) {
                            // minus
                            dh.insertInHandCashAmount(0, (inHandAmt - currentAmt), Constants.IN_HAND_CASH_MANUAL_AMOUNT_ADJUSTED);
                        } else if (inHandAmt < currentAmt) {
                            // plus
                            dh.insertInHandCashAmount(0, (currentAmt - inHandAmt), Constants.IN_HAND_CASH_MANUAL_AMOUNT_ADJUSTED);
                        }


                        // 0 means no txn is asscociated with this update
                        dh.insertInHandCashAmount(0, Float.parseFloat(amount), Constants.IN_HAND_CASH_MANUALLY_UPDATED);
                        tvInHandCashAmount.setText("+ ₹" + Functions.format((long) Float.parseFloat(amount)));
                    }
                });

        tvInHandCashUpdate.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityTransactions.this, ActivityUpdateInHandCash.class);
            intent.putExtra("amount", dh.getInHandCashAmount());
            handleUpdateInHandCashActivityResult.launch(intent);
        });

        // Handle bottom app bar clicks
        BottomNavigationView bnv = findViewById(R.id.bottom_app_bar_navigation_view);
        bnv.setSelectedItemId(R.id.bottom_menu_activities);
        bnv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_menu_home:
                        startActivity(new Intent(getApplicationContext(), ActivityHome.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.bottom_menu_activities:
                        return true;
                    case R.id.bottom_menu_privacy:
                        startActivity(new Intent(getApplicationContext(), ActivityPrivacy.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.bottom_menu_settings:
                        startActivity(new Intent(getApplicationContext(), ActivitySettings.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.app_bar_filter:
                // onclick, show bottom page for filter selection. Also set the state of filter with
                // variables
                TransactionFilterBottomSheetFragment bottomSheetFragment = new TransactionFilterBottomSheetFragment(
                        currentDateFilter, currentCustomDateText, currentStartDate, currentEndDate,
                        currentBankFilter, currentPaymentTypeFilter, currentTxnTypeFilter);
                bottomSheetFragment.setCancelable(false);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add filter icon in the top action bar
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_bar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Show the count of filters applies beside filter text in top app bar
        MenuItem item = menu.findItem(R.id.app_bar_filter);
        if (item.getTitle().equals("Filter") && appliedFilterCount > 0) {
            item.setTitle("Filter (" + appliedFilterCount + ")");
        } else {
            item.setTitle("Filter");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onSaveButtonClicked(String dateFilterType, String customDateText, String startDate, String endDate, ArrayList<String> banks, ArrayList<String> paymentType, ArrayList<String> transactionType) {
        this.currentDateFilter = dateFilterType;
        this.currentCustomDateText = customDateText;
        this.currentStartDate = startDate;
        this.currentEndDate = endDate;
        this.currentBankFilter = banks;
        this.currentPaymentTypeFilter = paymentType;
        this.currentTxnTypeFilter = transactionType;
        this.appliedFilterCount = 0;

        if (!dateFilterType.equals(Constants.FILTER_DEFAULT_DATE)) {
            this.appliedFilterCount++;
        }
        if (banks.size() > 0) {
            this.appliedFilterCount++;
        }
        if (paymentType.size() > 0) {
            this.appliedFilterCount++;
        }
        if (transactionType.size() > 0) {
            this.appliedFilterCount++;
        }

        processedTxns.applyFilter(startDate, endDate, banks, paymentType, transactionType);
        transactionsRecycleAdapter.notifyDataSetChanged();
        changeTransactionFilterText(Long.parseLong(startDate), Long.parseLong(endDate));
        changeTransactionSummaryText();
        invalidateOptionsMenu();
    }

    private void changeTransactionFilterText(Long startDate, Long endDate) {
        TextView smsFilterText = findViewById(R.id.transaction_page_sms_list_text);
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMM YYYY");
        smsFilterText.setText(String.format("For time period: %s - %s", sdf1.format(startDate), sdf1.format(endDate)));
    }

    private void changeTransactionSummaryText() {
        TextView tNoTxnFound;
        tNoTxnFound = findViewById(R.id.transaction_page_no_transaction_found_text);
        // tNoTxnFound.setVisibility(View.VISIBLE);
        if (processedTxns.getTransactionCount() == 0) {
            Snackbar sb = Snackbar.make(tNoTxnFound, "No Transaction Found", Snackbar.LENGTH_SHORT);
            sb.setAction("Close", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call your action method here
                    sb.dismiss();
                }
            });
            sb.show();
        } else {
            // tNoTxnFound.setVisibility(View.INVISIBLE);
        }
        TextView inHandCashView = findViewById(R.id.transaction_activity_set_in_hand_cash_amount_text);
        inHandCashView.setText(String.format("+ ₹%s", Functions.format((long) processedTxns.getInHandCash())));
        TextView incomeView = findViewById(R.id.transaction_activity_set_income_amount_text);
        incomeView.setText(String.format("+ ₹%s", Functions.format((long) processedTxns.getIncome())));
        TextView expenseView = findViewById(R.id.transaction_activity_set_expense_amount_text);
        expenseView.setText(String.format("- ₹%s", Functions.format((long) processedTxns.getExpense())));
    }
}

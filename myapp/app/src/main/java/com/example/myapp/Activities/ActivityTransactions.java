package com.example.myapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.Adapters.TransactionsRecycleAdapter;
import com.example.myapp.Fragments.TransactionFilterBottomSheetFragment;
import com.example.myapp.R;
import com.example.myapp.Utils.Constants;
import com.example.myapp.Utils.DatabaseHelper;
import com.example.myapp.Utils.Functions;
import com.example.myapp.Utils.ProcessSMS;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
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

    ProcessSMS smsProcessSMS;
    DatabaseHelper dh;
    TransactionsRecycleAdapter transactionsRecycleAdapter;

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

        RecyclerView transactionRecyclerView;
        ExtendedFloatingActionButton addCashTxnBtn = findViewById(R.id.transaction_page_add_cash_transaction);
        TextView tvInHandCashAmount = findViewById(R.id.transaction_activity_set_in_hand_cash_amount_text);
        TextView tvInHandCashUpdate = findViewById(R.id.transaction_activity_set_in_hand_cash_update_text);
        transactionRecyclerView = findViewById(R.id.transaction_activity_recycler_view);

        dh = new DatabaseHelper(this);
        smsProcessSMS = new ProcessSMS(ActivityTransactions.this);

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
                        throw new NullPointerException("Delete transaction activity, didn't set required extras");
                    }

                    if (result.getResultCode() == RESULT_TXN_UPDATED) {
                        boolean isDataUpdated = data.getBooleanExtra("is_data_updated", false);
                        if (isDataUpdated) {
                            onSaveButtonClicked(currentDateFilter, currentCustomDateText, currentStartDate, currentEndDate, currentBankFilter, currentPaymentTypeFilter, currentTxnTypeFilter);
                        }
                    } else if (result.getResultCode() == RESULT_TXN_DELETED) {
                        String id = data.getStringExtra("id");
                        if (!id.isEmpty()) {

                            dh.deleteProcessedTxnSMS(id);

                            Snackbar sb = Snackbar.make(addCashTxnBtn, "Item Deleted Successfully", Snackbar.LENGTH_SHORT);
                            sb.show();

                            onSaveButtonClicked(currentDateFilter, currentCustomDateText, currentStartDate, currentEndDate, currentBankFilter, currentPaymentTypeFilter, currentTxnTypeFilter);
                        }
                    }
                });

        // Set recycle view adapter
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionsRecycleAdapter = new TransactionsRecycleAdapter(this, smsProcessSMS, handleDetailedTxnActivityResult);
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

                        // TODO: take care of filters & name lowercase & uppsercase
                        if (smsProcessSMS.addTransaction(amount, date, 0, "", transaction_type, Constants.PAYMENT_TYPE_CASH, expense_merch, "Offline", "", note, "", category)) {
                            Log.d("Add cash txn: ", "Added data successfully");
                            if (transaction_type.equals(Constants.TXN_TYPE_DEBITED)) {
                                String amt = dh.getInHandCashAmount();
                                if (amt.equals("")) {
                                    amt = "0";
                                }
                                // TODO: What if the amount is more that actual in hand cash
                                dh.insertInHandCashAmount(Float.parseFloat(amt) - amount, Constants.IN_HAND_CASH_AUTOMATICALLY_DEBITED);
                                changeTransactionSummaryText();
                            }
                        } else {
                            Log.d("Add cash txn: ", "Failed to add data");
                        }

                        // TODO: extract current filter
                        onSaveButtonClicked(currentDateFilter, currentCustomDateText, currentStartDate, currentEndDate, currentBankFilter, currentPaymentTypeFilter, currentTxnTypeFilter);
                    }
                });

        // Show add cash txn page, when FAB is clicked
        addCashTxnBtn.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityTransactions.this, ActivityAddCashTransaction.class);
            handleAddCashActivityResult.launch(intent);
        });

        // Process the edit text fields of update txn activity (opened from detailed row view), whenever save button is clicked
        ActivityResultLauncher<Intent> handleUpdateInHandCashActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String amount = data.getStringExtra("amount");

                        DecimalFormat df = new DecimalFormat("##,##,##,##,##,##,##0.#");
                        Float price = Float.parseFloat(amount);
                        dh.insertInHandCashAmount(price, Constants.IN_HAND_CASH_MANUALLY_UPDATED);
                        tvInHandCashAmount.setText("+ ₹" + df.format(price));
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
                        return true;
                    case R.id.bottom_menu_activities:
                        return true;
                    case R.id.bottom_menu_privacy:
                        startActivity(new Intent(getApplicationContext(), ActivityPrivacy.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.bottom_menu_settings:
                        startActivity(new Intent(getApplicationContext(), ActivitySettings.class));
                        overridePendingTransition(0, 0);
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

        smsProcessSMS.filterList(startDate, endDate, banks, paymentType, transactionType);
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
        DecimalFormat df = new DecimalFormat("##,##,##,##,##,##,###");
        // Float amt = this.smsProcessSMS.calculateInHandCash();
        // if (amt.equals("")) {
        //     amt = "0";
        // }
        // Long price = Long.parseLong(amt);
        // CompactDecimalFormat formattedNumber = CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT);
        TextView inHandCashView = findViewById(R.id.transaction_activity_set_in_hand_cash_amount_text);
        inHandCashView.setText(String.format("+ ₹%s", Functions.format((long) this.smsProcessSMS.calculateInHandCash())));
        TextView incomeView = findViewById(R.id.transaction_activity_set_income_amount_text);
        incomeView.setText(String.format("+ ₹%s", Functions.format((long) this.smsProcessSMS.calculateIncome())));
        TextView expenseView = findViewById(R.id.transaction_activity_set_expense_amount_text);
        expenseView.setText(String.format("- ₹%s", Functions.format((long) this.smsProcessSMS.calculateExpense())));
    }
}

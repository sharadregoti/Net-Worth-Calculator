package com.example.myapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class ActivityTransactions extends AppCompatActivity implements TransactionFilterBottomSheetFragment.BottomSheetListener {

    public static final int RESULT_TXN_DELETED = 100;
    public static final int RESULT_TXN_UPDATED = 101;

    private ArrayList<String> currentBankFilter = new ArrayList<>(), currentTxnTypeFilter = new ArrayList<>(), currentPaymentTypeFilter = new ArrayList<>();
    private String currentDateFilter = Utils.FILTER_DEFAULT_DATE;
    private String currentStartDate = "";
    private String currentCustomDateText = "";
    private String currentEndDate = "";

    ProcessSMS smsProcessSMS;
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
        DatabaseHelper dh = new DatabaseHelper(this);

        // Set back button
        MaterialToolbar mt = findViewById(R.id.top_action_bar);
        mt.setTitle("");
        setSupportActionBar(mt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Process & store the sms in database
        smsProcessSMS = new ProcessSMS(ActivityTransactions.this);
        ExtendedFloatingActionButton addCashTxnBtn = findViewById(R.id.transaction_page_add_cash_transaction);

        ActivityResultLauncher<Intent> handleDeleteTxnActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_TXN_UPDATED) {
                            Intent data = result.getData();
                            Boolean isDataUpdated = data.getBooleanExtra("is_data_updated", false);
                            if (isDataUpdated) {
                                // TODO: extract current filter
                                // Long endDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
                                // Long startDate = LocalDateTime.now().withDayOfMonth(1).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
                                onSaveButtonClicked(currentDateFilter, currentCustomDateText, currentStartDate, currentEndDate, currentBankFilter, currentPaymentTypeFilter, currentTxnTypeFilter);
                            }
                        } else if (result.getResultCode() == RESULT_TXN_DELETED) {
                            Intent data = result.getData();
                            String id = data.getStringExtra("id");
                            if (!id.isEmpty()) {

                                // TODO: take care of filters & name lowercase & uppsercase
                                if (dh.deleteProcessedTxnSMS(id) != -1) {
                                    Log.i("Txn delete", "success");
                                } else {
                                    Log.i("Txn delete", "fail");
                                }

                                Snackbar sb = Snackbar.make(addCashTxnBtn, "Item Deleted Successfully", Snackbar.LENGTH_SHORT);
                                sb.show();

                                // TODO: extract current filter
                                // Long endDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
                                // Long startDate = LocalDateTime.now().withDayOfMonth(1).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
                                onSaveButtonClicked(currentDateFilter, currentCustomDateText, currentStartDate, currentEndDate, currentBankFilter, currentPaymentTypeFilter, currentTxnTypeFilter);
                                // onSaveButtonClicked(String.valueOf(startDate), String.valueOf(endDate), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                            }
                        }
                    }
                });

        // Process the edit text fields of add cash txn activity, whenever save button is clicked
        ActivityResultLauncher<Intent> handleAddCashActivityResult = registerForActivityResult(
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

                            // TODO: take care of filters & name lowercase & uppsercase
                            if (smsProcessSMS.addTransaction(amount, date, 0, "", transaction_type, Utils.PAYMENT_TYPE_CASH, expense_merch, "Offline", "", note, "", category)) {
                                Log.d("Add cash txn: ", "Added data successfully");
                            } else {
                                Log.d("Add cash txn: ", "Failed to add data");
                            }

                            // TODO: extract current filter
                            // Long endDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
                            // Long startDate = LocalDateTime.now().withDayOfMonth(1).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
                            onSaveButtonClicked(currentDateFilter, currentCustomDateText, currentStartDate, currentEndDate, currentBankFilter, currentPaymentTypeFilter, currentTxnTypeFilter);

                            // onSaveButtonClicked(String.valueOf(startDate), String.valueOf(endDate), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                        }
                    }
                });

        // Show add cash txn page, when FAB is clicked
        addCashTxnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityTransactions.this, ActivityAddCashTransaction.class);
                handleAddCashActivityResult.launch(intent);
            }
        });


        // Set recycle view adapter
        transactionRecyclerView = findViewById(R.id.transaction_activity_recycler_view);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionsRecycleAdapter = new TransactionsRecycleAdapter(this, smsProcessSMS, handleDeleteTxnActivityResult);
        transactionRecyclerView.setAdapter(transactionsRecycleAdapter);

        // Use Default filter of current month, change the filter & summary text
        Long startDate = LocalDateTime.now().withDayOfMonth(1).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
        Long endDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
        changeTransactionFilterText(startDate, endDate);
        changeTransactionSummaryText();

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
                // onclick, show bottom page for filter selection
                TransactionFilterBottomSheetFragment bottomSheetFragment = new TransactionFilterBottomSheetFragment(
                        currentDateFilter,
                        currentCustomDateText,
                        currentStartDate,
                        currentEndDate,
                        currentBankFilter,
                        currentPaymentTypeFilter,
                        currentTxnTypeFilter
                );
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
    public void onSaveButtonClicked(String dateFilterType, String customDateText, String startDate, String endDate, ArrayList<String> banks, ArrayList<String> paymentType, ArrayList<String> transactionType) {
        this.currentDateFilter = dateFilterType;
        this.currentCustomDateText = customDateText;
        this.currentStartDate = startDate;
        this.currentEndDate = endDate;
        this.currentBankFilter = banks;
        this.currentPaymentTypeFilter = paymentType;
        this.currentTxnTypeFilter = transactionType;

        smsProcessSMS.filterList(startDate, endDate, banks, paymentType, transactionType);
        transactionsRecycleAdapter.notifyDataSetChanged();
        changeTransactionFilterText(Long.parseLong(startDate), Long.parseLong(endDate));
        changeTransactionSummaryText();
    }

    public void changeTransactionFilterText(Long startDate, Long endDate) {
        TextView smsFilterText = findViewById(R.id.transaction_page_sms_list_text);
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMM YYYY");
        smsFilterText.setText("For time period: " + sdf1.format(startDate) + " - " + sdf1.format(endDate));
    }

    public void changeTransactionSummaryText() {
        DecimalFormat df = new DecimalFormat("##,##,##,##,##,##,###");
        TextView inHandCashView = findViewById(R.id.transaction_activity_set_in_hand_cash_amount_text);
        inHandCashView.setText(String.valueOf(df.format(this.smsProcessSMS.calculateInHandCash())));
        TextView incomeView = findViewById(R.id.transaction_activity_set_income_amount_text);
        incomeView.setText(String.valueOf("+ ₹" + df.format(this.smsProcessSMS.calculateIncome())));
        TextView expenseView = findViewById(R.id.transaction_activity_set_expense_amount_text);
        expenseView.setText(String.valueOf("- ₹" + df.format(this.smsProcessSMS.calculateExpense())));
    }
}

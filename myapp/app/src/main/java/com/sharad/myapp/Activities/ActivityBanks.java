package com.sharad.myapp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sharad.myapp.Adapters.BankRecycleAdapter;
import com.sharad.myapp.R;
import com.sharad.myapp.Utils.DatabaseHelper;
import com.sharad.myapp.Utils.Functions;
import com.google.android.material.appbar.MaterialToolbar;

public class ActivityBanks extends AppCompatActivity implements BankRecycleAdapter.BankAdapterListener {

    public static final int RESULT_BANK_DELETED = 100;
    public static final int RESULT_BANK_REFRESH = 101;

    public DatabaseHelper.BankAccountsHelper bankData;
    DatabaseHelper dh;
    BankRecycleAdapter bankRecycleAdapter;
    TextView tCombinedBankBalance;

    ActivityResultLauncher<Intent> handleBankPopupMenuResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if (data == null) {
                    Log.d("Bank popup activity, didn't set required extras", "");
                    return;
                }

                if (result.getResultCode() == RESULT_BANK_REFRESH) {
                    boolean isDataUpdated = data.getBooleanExtra("is_data_updated", false);
                    if (isDataUpdated) {
                        refreshRecyclerView();
                    }
                } else if (result.getResultCode() == RESULT_BANK_DELETED) {
                    String id = data.getStringExtra("id");
                    if (!id.isEmpty()) {

                        // dh.deleteProcessedTxnSMS(id);
                        //
                        // Snackbar sb = Snackbar.make(addCashTxnBtn, "Item Deleted Successfully", Snackbar.LENGTH_SHORT);
                        // sb.show();

                        refreshRecyclerView();
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banks);

        dh = new DatabaseHelper(this);
        bankData = dh.getAllBankInfo();

        tCombinedBankBalance = findViewById(R.id.bank_activity_bank_balance_amount_text);
        RecyclerView bankRecyclerView = findViewById(R.id.bank_activity_recycler_view);
        // Set recycle view adapter
        bankRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bankRecycleAdapter = new BankRecycleAdapter(bankData, handleBankPopupMenuResult, this);
        bankRecyclerView.setAdapter(bankRecycleAdapter);

        // Set back button
        MaterialToolbar mt = findViewById(R.id.top_action_bar);
        mt.setTitle("");
        setSupportActionBar(mt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tCombinedBankBalance.setText(Functions.format((long) bankData.getCombinedBankBalance()));
        changeNoBankInfo();
    }

    public void refreshRecyclerView() {
        bankData.refreshAccounts();
        bankRecycleAdapter.notifyDataSetChanged();
        tCombinedBankBalance.setText(Functions.format((long) bankData.getCombinedBankBalance()));
        changeNoBankInfo();
    }

    public void changeNoBankInfo() {
        TextView tNoTxnFound = findViewById(R.id.activity_banks_no_transaction_found_text);
        TextView tStarBankInfo = findViewById(R.id.bank_activity_bottom_warning_text);
        if (bankData.getBankAccountsCount() == 0) {
            tNoTxnFound.setVisibility(View.VISIBLE);
            tNoTxnFound.setMovementMethod(LinkMovementMethod.getInstance());
            tNoTxnFound.setLinkTextColor(Color.BLUE);
            tStarBankInfo.setVisibility(View.INVISIBLE);
        } else {
            tNoTxnFound.setVisibility(View.INVISIBLE);
            tStarBankInfo.setVisibility(View.VISIBLE);
        }
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

    @Override
    public void onDeleteButtonPressed(String bankName) {
        dh.deleteBankAccount(bankName);
        refreshRecyclerView();
    }
}
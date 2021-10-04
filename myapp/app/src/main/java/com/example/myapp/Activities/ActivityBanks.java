package com.example.myapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.Adapters.BankRecycleAdapter;
import com.example.myapp.R;
import com.example.myapp.Utils.DatabaseHelper;
import com.google.android.material.appbar.MaterialToolbar;

public class ActivityBanks extends AppCompatActivity implements BankRecycleAdapter.BankAdapterListener {

    public static final int RESULT_BANK_DELETED = 100;
    public static final int RESULT_BANK_REFRESH = 101;

    public DatabaseHelper.BankAccountsHelper bankData;
    DatabaseHelper dh;
    BankRecycleAdapter bankRecycleAdapter;

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
    }

    public void refreshRecyclerView() {

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
        bankData = dh.getAllBankInfo();

        bankRecycleAdapter.notifyDataSetChanged();
    }
}
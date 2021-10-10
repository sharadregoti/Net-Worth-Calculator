package com.sharad.myapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.sharad.myapp.R;
import com.sharad.myapp.Utils.Constants;
import com.sharad.myapp.Utils.Functions;
import com.sharad.myapp.banks.Bank;
import com.sharad.myapp.banks.ICICI;
import com.sharad.myapp.banks.StateBankOfIndia;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class ActivityBanksRefreshBalance extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banks_refresh_balance);

        Intent intent = getIntent();
        String bankName = intent.getStringExtra("bank_name");

        TextView tBankName = findViewById(R.id.activity_bank_refresh_balance_bank_name_text);
        ImageView iBankLogo = findViewById(R.id.activity_bank_refresh_balance_bank_logo_image);
        MaterialButton mbSmsButton = findViewById(R.id.activity_bank_refresh_balance_sms_button);
        MaterialButton mbCallButton = findViewById(R.id.activity_bank_refresh_balance_call_button);

        tBankName.setText(bankName);
        iBankLogo.setImageResource(Functions.supportedBanks.get(bankName));

        Bank bankObj;
        switch (bankName) {
            case Constants.BANK_STATE_BANK_OF_INDIA:
                bankObj = new StateBankOfIndia();
                break;
            case Constants.BANK_ICICI:
                bankObj = new ICICI();
                break;
            default:
                throw new IllegalStateException("Unexpected bank: " + bankName);
        }

        mbSmsButton.setOnClickListener(view -> {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:"));
            sendIntent.putExtra("address", bankObj.getBankBalanceSmsAddress());
            sendIntent.putExtra("sms_body", bankObj.getBankBalanceSmsBody());
            startActivity(sendIntent);
        });
        mbCallButton.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + bankObj.getBankBalanceCallPhoneAddress()));
            startActivity(i);
        });
        // Set back button
        MaterialToolbar mt = findViewById(R.id.top_action_bar);
        mt.setTitle("");
        setSupportActionBar(mt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
}
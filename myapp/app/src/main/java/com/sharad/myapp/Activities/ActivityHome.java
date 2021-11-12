package com.sharad.myapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sharad.myapp.R;
import com.sharad.myapp.Utils.DatabaseHelper;
import com.sharad.myapp.Utils.Functions;

public class ActivityHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set Night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        LinearLayout llMutualFund = findViewById(R.id.home_activity_mutual_fund_layout);
        LinearLayout llBanks = findViewById(R.id.home_activity_banks_layout);
        LinearLayout llCrypto = findViewById(R.id.home_activity_crypto_layout);
        LinearLayout llStocks = findViewById(R.id.home_activity_stocks_layout);
        TextView tNetWorthAmount = findViewById(R.id.home_activity_net_worth_amount_text);
        TextView tInflationNetWorthAmount = findViewById(R.id.home_activity_net_worth_inflation_adjusted_amount_text);
        TextView tAssetAmount = findViewById(R.id.home_activity_asset_amount_text);

        DatabaseHelper dh = new DatabaseHelper(this);

        int totalAmount = (int) dh.getNetWorth() + (int) dh.getCurrentStockValue() + (int) dh.getCurrentMutualFundValue();
        double compoundedValue = totalAmount - calculateCompoundInterest(totalAmount, 5, .04);

        tNetWorthAmount.setText("₹"+Functions.format(totalAmount));
        tAssetAmount.setText("₹"+Functions.format(totalAmount));
        tInflationNetWorthAmount.setText("₹"+Functions.format((long) compoundedValue));

        // HashMap<String,String> myMap = Functions.getCoFormat((long) totalCurrentAmount);

        // double inflationAdjustedAmount = Float.parseFloat(myMap.get("value").toString()) * Math.pow((1 + 15.0), 10.0);
        // tInfalationAdjustedAmount.setText("₹" + inflationAdjustedAmount + myMap.get("suffix"));
        llMutualFund.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityHome.this, ActivityMutualFund.class);
            startActivity(intent);
        });
        llBanks.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityHome.this, ActivityBanks.class);
            startActivity(intent);
        });
        llCrypto.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityHome.this, ActivityCrypto.class);
            startActivity(intent);
        });
        llStocks.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityHome.this, ActivityStocks.class);
            startActivity(intent);
        });

        // Set bottom app bar
        BottomNavigationView bnv = findViewById(R.id.bottom_app_bar_navigation_view);
        bnv.setSelectedItemId(R.id.bottom_menu_home);
        bnv.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                // To remove the warning you might use if/else
                case R.id.bottom_menu_home:
                    return true;
                case R.id.bottom_menu_activities:
                    startActivity(new Intent(getApplicationContext(), ActivityTransactions.class));
                    overridePendingTransition(0, 0);
                    finish();
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
        });
    }

    public double calculateCompoundInterest(int p, int t, double r) {
        return p * Math.pow(1 + (r), t) - p;
    }
}
package com.sharad.myapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.sharad.myapp.Adapters.StocksRecycleAdapter;
import com.sharad.myapp.Fragments.StockBrokerSelector;
import com.sharad.myapp.R;
import com.sharad.myapp.Utils.DatabaseHelper;
import com.sharad.myapp.Utils.Functions;
import com.sharad.myapp.Utils.HTTPAPICalls;
import com.sharad.myapp.Utils.StockHolding;
import com.sharad.myapp.Utils.StockSummary;
import com.sharad.myapp.Utils.StocksFundsAndMargins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityStocks extends AppCompatActivity implements StockBrokerSelector.BottomSheetListener {

    TextView tCurrentamount;
    TextView tInfalationAdjustedAmount;
    TextView tInvestedAmount;
    TextView tGrowthText;
    TextView tWalletBalance;
    TextView tNoStocksFoundText;
    ImageView iArrowImage;

    StocksRecycleAdapter stocksRecycleAdapter;
    RecyclerView stocksRecyclerView;

    DatabaseHelper dh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks);

        dh = new DatabaseHelper(this);

        tCurrentamount = findViewById(R.id.activity_stocks_current_amount_text);
        tInvestedAmount = findViewById(R.id.activity_stocks_invested_amount_value_text);
        tGrowthText = findViewById(R.id.activity_stocks_growth_text);
        tWalletBalance = findViewById(R.id.activity_stocks_wallet_balance_amount_text);
        tNoStocksFoundText = (TextView) findViewById(R.id.activity_stocks_no_stockss_found_text);
        iArrowImage = findViewById(R.id.activity_stocks_arrow_image);
        stocksRecyclerView = findViewById(R.id.stocks_activity_recycler_view);

        // Set back button
        MaterialToolbar mt = findViewById(R.id.top_action_bar);
        mt.setTitle("");
        setSupportActionBar(mt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set recycle view adapter
        stocksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        String token = "";

        Uri uri = getIntent().getData();
        if (uri != null) {
            String path = uri.toString();
            token = uri.getQueryParameter("accessToken");
            String brokerName = uri.getQueryParameter("brokerName");
            Toast.makeText(ActivityStocks.this, "Path" + path + "::" + token, Toast.LENGTH_LONG).show();
            SharedPreferences sharedPreferences = getSharedPreferences("zerodhaAccessToken", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("accessToken", token);
            myEdit.apply();
            sharedPreferences = getSharedPreferences("stockBroker", MODE_PRIVATE);
            myEdit = sharedPreferences.edit();
            myEdit.putString("name", brokerName);
            myEdit.apply();
        } else {
            SharedPreferences c = getSharedPreferences("zerodhaAccessToken", MODE_PRIVATE);
            token = c.getString("accessToken", "");
        }

        SharedPreferences sb = getSharedPreferences("stockBroker", MODE_PRIVATE);
        String stockBroker = sb.getString("name", "");

        if (stockBroker.isEmpty()) {
            StockBrokerSelector bottomSheetFragment = new StockBrokerSelector();
            bottomSheetFragment.setCancelable(false);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            return;
        } else if (token.isEmpty()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://d2d9-120-138-2-205.ngrok.io/v1/zerodha-oauth-redirect"));
            startActivity(browserIntent);
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.kite.trade/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HTTPAPICalls jsonPlaceholder = retrofit.create(HTTPAPICalls.class);
        final float[] walletBalance = new float[1];


        // validate token
        Call<StocksFundsAndMargins> fundsAndMargins = jsonPlaceholder.getFundsAndMargins("token " + token);
        String finalToken = token;
        fundsAndMargins.enqueue(new Callback<StocksFundsAndMargins>() {
            @Override
            public void onResponse(Call<StocksFundsAndMargins> call, Response<StocksFundsAndMargins> response) {
                if (response.code() == 200) {
                    StocksFundsAndMargins sfm = response.body();
                    walletBalance[0] = sfm.getData().getEquity().getNet();
                    tWalletBalance.setText("₹" + Functions.format((long) walletBalance[0]));
                } else {
                    Toast.makeText(ActivityStocks.this, "Token validation failed" + finalToken, Toast.LENGTH_LONG).show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<StocksFundsAndMargins> call, Throwable t) {
            }
        });


        Call<StockSummary> mfSummary = jsonPlaceholder.getPortfolioHoldings("token " + token);
        mfSummary.enqueue(new Callback<StockSummary>() {
            @Override
            public void onResponse(Call<StockSummary> call, Response<StockSummary> response) {
                if (response.code() == 200) {
                    tNoStocksFoundText.setVisibility(View.GONE);
                    StockSummary mfs = response.body();
                    ArrayList<StockHolding> mfhs = new ArrayList(Arrays.asList(mfs.getData()));
                    stocksRecycleAdapter = new StocksRecycleAdapter(mfhs);
                    stocksRecyclerView.setAdapter(stocksRecycleAdapter);
                    setMFSummary(mfhs, walletBalance[0]);
                } else {
                    tNoStocksFoundText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<StockSummary> call, Throwable t) {

            }
        });
    }

    void setMFSummary(List<StockHolding> mfhs, float walletBalance) {
        float totalInvestedAmount = 0, totalCurrentAmount = 0, growthAmount = 0, growthPercentage = 0;
        for (int i = 0; i < mfhs.size(); i++) {
            totalCurrentAmount += mfhs.get(i).getQuantity() * mfhs.get(i).getLast_price();
            totalInvestedAmount += mfhs.get(i).getQuantity() * mfhs.get(i).getAverage_price();
        }

        growthAmount = totalCurrentAmount - totalInvestedAmount;
        if (growthAmount < 0) {
            iArrowImage.setImageResource(R.drawable.arrow_down);
            tGrowthText.setTextColor(ContextCompat.getColor(this, R.color.red_money));
        } else {
            iArrowImage.setImageResource(R.drawable.arrow_up);
            tGrowthText.setTextColor(ContextCompat.getColor(this, R.color.green_money));
        }

        growthPercentage = (growthAmount / totalInvestedAmount) * 100;

        tInvestedAmount.setText("₹" + Functions.format((long) totalInvestedAmount));
        tCurrentamount.setText("₹" + Functions.format((long) totalCurrentAmount));
        tGrowthText.setText(String.format("%.2f%% (₹ %s)", growthPercentage, Functions.format((long) growthAmount)));
        dh.upsertCurrentStockValue(totalCurrentAmount, totalInvestedAmount, walletBalance);
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
    public void onSaveButtonClicked(String msg) {

    }
}
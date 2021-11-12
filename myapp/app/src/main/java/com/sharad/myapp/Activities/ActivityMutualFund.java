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
import com.sharad.myapp.Adapters.MutualFundRecycleAdapter;
import com.sharad.myapp.Fragments.StockBrokerSelector;
import com.sharad.myapp.R;
import com.sharad.myapp.Utils.DatabaseHelper;
import com.sharad.myapp.Utils.Functions;
import com.sharad.myapp.Utils.HTTPAPICalls;
import com.sharad.myapp.Utils.MutualFundHolding;
import com.sharad.myapp.Utils.MutualFundSummary;
import com.sharad.myapp.Utils.StocksFundsAndMargins;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityMutualFund extends AppCompatActivity implements StockBrokerSelector.BottomSheetListener{

    TextView tCurrentamount;
    TextView tInvestedAmount;
    TextView tGrowthText;
    TextView tCurrentNavDateText;
    TextView tNoMutualFundFoundText;
    ImageView iArrowImage;

    MutualFundRecycleAdapter mutualFundRecycleAdapter;
    RecyclerView mutualFundRecyclerView;

    DatabaseHelper dh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutual_fund);

        dh = new DatabaseHelper(this);

        tCurrentNavDateText = findViewById(R.id.activity_mutual_fund_current_value_text);
        tCurrentamount = findViewById(R.id.activity_mutual_fund_current_amount_text);
        tInvestedAmount = findViewById(R.id.activity_mutual_fund_invested_amount_value_text);
        tGrowthText = findViewById(R.id.activity_mutual_fund_growth_text);
        tNoMutualFundFoundText = (TextView) findViewById(R.id.activity_mutual_fund_no_mutual_funds_found_text);
        iArrowImage = findViewById(R.id.activity_mutual_fund_arrow_image);
        mutualFundRecyclerView = findViewById(R.id.mutual_fund_activity_recycler_view);

        // Set back button
        MaterialToolbar mt = findViewById(R.id.top_action_bar);
        mt.setTitle("");
        setSupportActionBar(mt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set recycle view adapter
        mutualFundRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        settCurrentNavDateText("");

        SharedPreferences c = getSharedPreferences("zerodhaAccessToken", MODE_PRIVATE);
        String token = c.getString("accessToken", "");

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
                } else {
                    Toast.makeText(ActivityMutualFund.this, "Token validation failed" + finalToken, Toast.LENGTH_LONG).show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<StocksFundsAndMargins> call, Throwable t) {
            }
        });

        Call<MutualFundSummary> mfSummary = jsonPlaceholder.getMutualFundSummary("token " + token);
        mfSummary.enqueue(new Callback<MutualFundSummary>() {
            @Override
            public void onResponse(Call<MutualFundSummary> call, Response<MutualFundSummary> response) {
                if (response.code() == 200) {
                    tNoMutualFundFoundText.setVisibility(View.GONE);
                    MutualFundSummary mfs = response.body();
                    ArrayList<MutualFundHolding> mfhs = new ArrayList(Arrays.asList(mfs.getData()));
                    if (mfhs.size() > 0) {
                        settCurrentNavDateText(mfhs.get(0).getLast_price_date());
                    }
                    mutualFundRecycleAdapter = new MutualFundRecycleAdapter(mfhs);
                    mutualFundRecyclerView.setAdapter(mutualFundRecycleAdapter);
                    setMFSummary(mfhs);
                } else {
                    tNoMutualFundFoundText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<MutualFundSummary> call, Throwable t) {

            }
        });
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

    void settCurrentNavDateText(String d) {
        if (d.isEmpty()) {
            tCurrentNavDateText.setText("Current value");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d", Locale.ENGLISH);
            LocalDate date = LocalDate.parse(d, formatter);
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
            String text = String.format("Current value (NAV as on %s)", date.format(formatter2));
            tCurrentNavDateText.setText(text);
        }
    }

    void setMFSummary(List<MutualFundHolding> mfhs) {
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

        dh.upsertCurrentMutualFundValue(totalCurrentAmount, totalInvestedAmount);
    }

    @Override
    public void onSaveButtonClicked(String msg) {

    }
}
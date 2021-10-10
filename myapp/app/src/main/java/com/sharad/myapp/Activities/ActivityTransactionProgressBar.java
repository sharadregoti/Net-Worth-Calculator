package com.sharad.myapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.sharad.myapp.R;
import com.sharad.myapp.Utils.ProcessSMS;

public class ActivityTransactionProgressBar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_progress_bar);

        new ProcessSMS(this);

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            onBoardingFinished();
            Intent intent = new Intent(this, ActivityHome.class);
            startActivity(intent);
            finish();
        }, 1);
    }

    private void onBoardingFinished() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putBoolean("is_on_boarding_finished", true);
        myEdit.apply();
    }
}
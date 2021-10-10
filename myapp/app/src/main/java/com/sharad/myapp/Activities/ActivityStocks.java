package com.sharad.myapp.Activities;

import android.os.Bundle;

import com.sharad.myapp.R;

import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.ui.AppBarConfiguration;

import com.sharad.myapp.databinding.ActivityStocksBinding;

public class ActivityStocks extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityStocksBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks);
    }
}
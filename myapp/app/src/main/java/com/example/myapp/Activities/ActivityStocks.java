package com.example.myapp.Activities;

import android.os.Bundle;

import com.example.myapp.R;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapp.databinding.ActivityStocksBinding;

public class ActivityStocks extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityStocksBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks);
    }
}
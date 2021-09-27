package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ActivityPrivacy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        // Set bottom app bar
        BottomNavigationView bnv = findViewById(R.id.bottom_app_bar_navigation_view);
        bnv.setSelectedItemId(R.id.bottom_menu_privacy);
        bnv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_menu_home:
                        startActivity(new Intent(getApplicationContext(), ActivityHome.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.bottom_menu_activities:
                        startActivity(new Intent(getApplicationContext(), ActivityTransactions.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.bottom_menu_privacy:
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

}
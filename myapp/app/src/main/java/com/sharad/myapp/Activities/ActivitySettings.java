package com.sharad.myapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.sharad.myapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ActivitySettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set bottom app bar
        BottomNavigationView bnv = findViewById(R.id.bottom_app_bar_navigation_view);
        bnv.setSelectedItemId(R.id.bottom_menu_settings);
        bnv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_menu_home:
                        startActivity(new Intent(getApplicationContext(), ActivityHome.class));
                        overridePendingTransition(0, 0);
                        finish();
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
                        return true;
                }
                return false;
            }
        });
    }
}
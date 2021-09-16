package com.example.myapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReadSms extends AppCompatActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        ActivityCompat.requestPermissions(
                ReadSms.this,
                new String[] {Manifest.permission.READ_SMS},
                PackageManager.PERMISSION_GRANTED
        );

        Data smsData = new Data(ReadSms.this);

        RecyclerView myRecView = findViewById(R.id.sms_msg_list);
        myRecView.setLayoutManager(new LinearLayoutManager(this));
        myRecView.setAdapter(new RecycleAdapter(this,smsData));
    }
}

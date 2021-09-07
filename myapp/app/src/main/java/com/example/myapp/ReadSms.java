package com.example.myapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class ReadSms extends AppCompatActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.column);

        ActivityCompat.requestPermissions(
                ReadSms.this,
                new String[] {Manifest.permission.READ_SMS},
                PackageManager.PERMISSION_GRANTED
        );

        Data smsData = new Data(ReadSms.this);

        ListView myListView;
        myListView = (ListView) findViewById(R.id.list_view);
        MyCustomAdapter adapter = new MyCustomAdapter(ReadSms.this, smsData);
        myListView.setAdapter(adapter);
    }
}

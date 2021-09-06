package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private TextView myTextView;
    private ListView myListView;
    List list = new ArrayList();
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myTextView = findViewById(R.id.textView);
        ActivityCompat.requestPermissions(
                MainActivity.this,
                new String[] {Manifest.permission.READ_SMS},
                PackageManager.PERMISSION_GRANTED
        );

        myListView = (ListView)findViewById(R.id.list_view);
        read_sms();
        adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,list);
        myListView.setAdapter(adapter);

    }

    public void read_sms() {
        String[] columns = new String[] {"_id","address","body","date"};
        String where = "address LIKE \"%ICICIB\"";
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms"),columns,where,null,null);

        Integer totalCount = 0;
        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
//            Log.d("Yo Yo", "1" + cursor.getString(2).startsWith("ICICI Bank Acc") + cursor.getString(2));
            if (cursor.getColumnName(2).equals("body") && cursor.getString(2).startsWith("ICICI Bank Acc")) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    map.put(cursor.getColumnName(i),cursor.getString(i));
                }
                Pattern p = Pattern.compile("-?\\d+");
                Matcher m = p.matcher(cursor.getString(2));
                Integer count = 0;
                while (m.find()) {
                    count++;
                    if (count == 2) {
                        Log.d("Number",m.group());
                        map.put("Amount",m.group());
                        totalCount = totalCount + Integer.parseInt(m.group());
                    }
                }
                list.add(map);

                Log.d("One row finished",
                        "**************************************************");
            }
        }
        myTextView.setText(totalCount.toString());

//        cursor.moveToFirst();
//        myTextView.setText(cursor.getString(12));
    }
}
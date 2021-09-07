package com.example.myapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Data {
    private List list = new ArrayList();
    private Context ctx;

    public Data(Context ctx) {
        this.ctx = ctx;
        read_sms();
    }

    public List getMessages() {
        return list;
    }

    public String getDate(Integer position) {
        HashMap d = new HashMap();
        d = (HashMap) list.get(position);
        Log.d("Hello :",d.get("date").toString());
        return d.get("date").toString();
    }

    public String getAmount(Integer position) {
        HashMap d = new HashMap();
        d = (HashMap) list.get(position);
        Log.d("Hello A:",d.get("Amount").toString());
        return d.get("Amount").toString();
    }

    private void read_sms() {
        String[] columns = new String[] {"_id","address","body","date"};
        String where = "address LIKE \"%ICICIB\"";
        Cursor cursor = ctx.getContentResolver().query(Uri.parse("content://sms"),columns,where,null,null);

        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();

            if (cursor.getColumnName(2).equals("body"))  {

                if (cursor.getString(2).contains("requested")) {
                    continue;
                } else if (cursor.getString(2).contains("debited")) {
                    map.put("Type","debited");
                } else if (cursor.getString(2).contains("credited")) {
                    map.put("Type","credited");
                } else {
                    continue;
                }

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
                    }
                }

                String obj= null;
                try {
                    obj = new JSONObject(map).toString(2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                list.add(map);

                Log.d("One row finished", "**************************************************");
            }
        }
    }
}

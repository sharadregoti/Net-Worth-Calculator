package com.example.myapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.myapp.banks.Bank;
import com.example.myapp.banks.FalseAlarmException;
import com.example.myapp.banks.ICICI;
import com.example.myapp.banks.NotTransactionSMSException;
import com.example.myapp.banks.StateBankOfIndia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {
    static final String ICICI = "ICICI";
    static final String STATE_BANK_OF_INDIA = "SBI";

    private final HashMap<String, String[]> supportedBanks = new HashMap<>();

    private final List<HashMap<String,String>> list = new ArrayList<>();
    private final Context ctx;

    public Data(Context ctx) {
        this.ctx = ctx;
        this.supportedBanks.put(ICICI, new String[]{"ICICIB"});
        this.supportedBanks.put(STATE_BANK_OF_INDIA, new String[]{"SBIUPI", "ATMSBI", "CBSSBI", "SBIPSG", "SBIDGT"});
        read_sms();
    }

    public List getMessages() {
        return list;
    }

    public String getDate(Integer position) {
        HashMap d = (HashMap) list.get(position);
        String createdTime = d.get("date").toString();
        Date date1 = new Date(Long.parseLong(createdTime)); // right here
        System.out.println(date1.toString()); // this is what you are looking online
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMM YYYY"); // here you would have to customize the output format you are looking for
        System.out.println(sdf1.format(date1));
        return sdf1.format(date1);
    }

    public String getAmount(Integer position) {
        HashMap d = (HashMap) list.get(position);
        return d.get("amount").toString();
    }

    public String getType(Integer position) {
        HashMap d = (HashMap) list.get(position);
        return d.get("type").toString();
    }

    public String getBank(Integer position) {
        HashMap d = (HashMap) list.get(position);
        return d.get("bank").toString();
    }

    public String getTransactionPerson(Integer position) {
        HashMap d = (HashMap) list.get(position);
        return d.get("transaction_person").toString();
    }

    public String getTags(Integer position) {
        HashMap d = (HashMap) list.get(position);
        return d.get("tags").toString();
    }

    private void read_sms() {
        String[] columns = new String[]{"_id", "address", "body", "date"};

        for (Map.Entry<String, String[]> bank : this.supportedBanks.entrySet()) {
            StringBuilder whereClause = new StringBuilder("");
            for (Integer i = 0; i < bank.getValue().length; i++) {
                whereClause.append("address LIKE \"%").append(bank.getValue()[i]).append("\"");
                if (i != bank.getValue().length - 1) {
                    whereClause.append(" or ");
                }
            }

            Cursor cursor = ctx.getContentResolver().query(Uri.parse("content://sms"), columns, whereClause.toString(), null, null);

            Bank bankObj;
            switch (bank.getKey()) {
                case STATE_BANK_OF_INDIA:
                    bankObj = new StateBankOfIndia();
                    break;
                case ICICI:
                    bankObj = new ICICI();
                    break;
                default:
                    throw new IllegalStateException("Unexpected bank: " + bank.getKey());
            }

            while (cursor.moveToNext()) {
                HashMap<String, String> map = new HashMap<>();

                if (cursor.getColumnName(2).equals("body")) {
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        map.put(cursor.getColumnName(i), cursor.getString(i));
                    }

                    map.put("bank", bank.getKey());
                    map.put("transaction_person", "N/A");
                    map.put("tags", "N/A");

                    String smsBody = cursor.getString(2);
                    HashMap<String,String> newMap;
                    try {
                        newMap = bankObj.parse(smsBody);
                        HashMap<String, String> map3 = new HashMap<>(map);
                        newMap.forEach((key, value) -> map3.merge(key, value, (v1,v2) -> v1+v2));
                        list.add(map3);
                    } catch (NotTransactionSMSException | FalseAlarmException e) {
                        Log.d("User Exception", e.getMessage());
                    } catch (Exception e){
                        Log.d("System Exception", e.getMessage());
                    }
                }
            }

        }
    }
}

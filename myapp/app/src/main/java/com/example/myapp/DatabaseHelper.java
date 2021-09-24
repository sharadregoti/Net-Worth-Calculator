package com.example.myapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String SQL_FILE_NAME = "green_money";

    private static final String LAST_PROCESSED_TRANSACTION_SMS_TABLE = "last_processed_transaction_sms";
    private static final String LAST_UPDATED_TIMESTAMP = "last_update_ts";

    private static final String TRANSACTION_SMS_TABLE = "transaction_sms";
    private static final String ID_0 = "id";
    private static final String SMS_ID_1 = "sms_id";
    private static final String SMS_MESSAGE_2 = "sms_message";
    private static final String AMOUNT_3 = "amount";
    private static final String DATE_4 = "date";
    private static final String TRANSACTION_PERSON_5 = "transaction_person";
    private static final String TRANSACTION_TYPE_6 = "transaction_type";
    private static final String BANK_7 = "bank_name";
    private static final String TAGS_8 = "tags";
    private static final String NOTES_9 = "notes";
    private static final String IMAGE_REF_10 = "image_ref";
    private static final String CATEGORY_11 = "category";
    private static final String PAYMENT_TYPE_12 = "payment_type";

    private static final int s = 420;

    public DatabaseHelper(@Nullable Context context) {
        super(context, SQL_FILE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO: Change hardcoded table names with consts
        String query = "CREATE TABLE IF NOT EXISTS " + TRANSACTION_SMS_TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sms_id INTEGER, " +
                "sms_message BLOB, " +
                "amount REAL, " +
                "date INTEGER, " +
                "transaction_person TEXT, " +
                "transaction_type TEXT, " +
                "bank_name TEXT, " +
                "tags TEXT, " +
                "notes TEXT, " +
                "image_ref TEXT, " +
                "category TEXT, " +
                "payment_type TEXT)";
        db.execSQL(query.toString());

        query = "CREATE TABLE IF NOT EXISTS " + LAST_PROCESSED_TRANSACTION_SMS_TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "last_update_ts INTEGER)";
        db.execSQL(query.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // TODO: whenever a version upgrade is being made, this has to be changed to reflect the db changes
    }

    public boolean addTransactionSMS(float amount, Long date, int smsId, String smsMsg, String tt, String pt, String tp, String tags, String bankName, String notes, String imageRef, String category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(SMS_ID_1, smsId);
        cv.put(SMS_MESSAGE_2, smsMsg);
        cv.put(AMOUNT_3, amount);
        cv.put(DATE_4, date);
        cv.put(TRANSACTION_TYPE_6, tt);
        cv.put(PAYMENT_TYPE_12, pt);
        cv.put(BANK_7, bankName);
        cv.put(TAGS_8, tags);
        cv.put(TRANSACTION_PERSON_5, tp);
        cv.put(NOTES_9, notes);
        cv.put(IMAGE_REF_10, imageRef);
        cv.put(CATEGORY_11, category);

        long result = db.insert(TRANSACTION_SMS_TABLE, null, cv);
        return (result != -1);
    }

    public String getLastProcessedTxnSMSDate() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = SQLiteQueryBuilder.buildQueryString(false, LAST_PROCESSED_TRANSACTION_SMS_TABLE, new String[]{LAST_UPDATED_TIMESTAMP}, "", "", "", "", "1");
        Cursor data = db.rawQuery(query, null);
        // TODO: Some exception handling
        if (data.moveToNext()) {
            return data.getString(0);
        }
        data.close();
        return "";
    }

    public void upsertLastProcessedTxnDate() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long unixTime = System.currentTimeMillis();
        cv.put(LAST_UPDATED_TIMESTAMP, unixTime);
        cv.put(ID_0, 1);
        db.insertWithOnConflict(LAST_PROCESSED_TRANSACTION_SMS_TABLE, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public float calculateIncome(String whereClause) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT SUM(amount) FROM " + TRANSACTION_SMS_TABLE + " WHERE transaction_type = ? and " + whereClause;
        Cursor data = db.rawQuery(query, new String[]{"credited"});
        float income = 0;
        while (data.moveToNext()) {
            income = data.getFloat(0);
        }
        data.close();
        return income;
    }

    public float calculateExpense(String whereClause) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT SUM(amount) FROM " + TRANSACTION_SMS_TABLE + " WHERE transaction_type = ? and " + whereClause;
        Cursor data = db.rawQuery(query, new String[]{"debited"});
        float expense = 0;
        while (data.moveToNext()) {
            expense = data.getFloat(0);
        }
        data.close();
        return expense;
    }

    public float calculateInHandCash(String whereClause) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT SUM(amount) FROM " + TRANSACTION_SMS_TABLE + " WHERE transaction_type = ? and " + whereClause;
        Cursor data = db.rawQuery(query, new String[]{"in_hand_cash"});
        float inHandCash = 0;
        while (data.moveToNext()) {
            inHandCash = data.getFloat(0);
        }
        data.close();
        return inHandCash;
    }

    public StoreFilteredTransactionResult getFilteredTransactions(String startDate, String endDate, ArrayList<String> banks, ArrayList<String> paymentType, ArrayList<String> transactionType) {
        String dateQuery = "", ptQuery = "", ttQuery = "";
        ArrayList<String> finalArr = new ArrayList<String>();
        if (!Objects.equals(startDate, "") && !Objects.equals(endDate, "")) {
            dateQuery += "(date > \"" + startDate + "\" AND date < \"" + endDate + "\")";
            finalArr.add(dateQuery);
        } else {
            //      Use default filter of current month
            Long sl = LocalDateTime.now().withDayOfMonth(1).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
            Long el = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
            dateQuery += "(date > \"" + sl + "\" AND date < \"" + el + "\")";
            finalArr.add(dateQuery);
        }

        if (paymentType.size() == 1) {
            ptQuery += "(payment_type = \"" + paymentType.get(0) + "\")";
            finalArr.add(ptQuery);
        } else if (paymentType.size() > 1) {
            String temp = "";
            for (int i = 0; i < paymentType.size(); i++) {
                temp += " payment_type = \"" + paymentType.get(i) + "\" OR";
            }
            // remove the last or
            temp = temp.substring(0, temp.length() - 2);
            ptQuery += "(" + temp + ")";
            finalArr.add(ptQuery);
        }

        if (transactionType.size() == 1) {
            ttQuery += "(transaction_type = \"" + transactionType.get(0) + "\")";
            finalArr.add(ttQuery);
        } else if (transactionType.size() > 1) {
            String temp = "";
            for (int i = 0; i < transactionType.size(); i++) {
                temp += " transaction_type = \"" + transactionType.get(i) + "\" OR";
            }
            // remove the last or
            temp = temp.substring(0, temp.length() - 2);
            ttQuery += "(" + temp + ")";
            finalArr.add(ttQuery);
        }

        String whereClause = "";
        if (finalArr.size() == 1) {
            whereClause += "(" + finalArr.get(0) + ")";
        } else if (finalArr.size() > 1) {
            String temp = "";
            for (int i = 0; i < finalArr.size(); i++) {
                if (finalArr.get(i) != null) {
                    temp += finalArr.get(i) + " and";
                }
            }
            // remove the last or
            temp = temp.substring(0, temp.length() - 3);
            whereClause += "(" + temp + ")";
        }

        SQLiteDatabase db = this.getWritableDatabase();
        String myfinalQuery = SQLiteQueryBuilder.buildQueryString(false, TRANSACTION_SMS_TABLE, new String[]{"*"}, whereClause, "", "", "date DESC", "");
        Cursor data = db.rawQuery(myfinalQuery, null);

        // Iterate & store data in list
        List<HashMap<String, Object>> list = new ArrayList<>();
        while (data.moveToNext()) {

            HashMap<String, Object> map = new HashMap<>();
            map.put(SMS_ID_1, data.getInt(1));
            map.put(SMS_MESSAGE_2, data.getBlob(2).toString());
            map.put(AMOUNT_3, data.getFloat(3));
            map.put(DATE_4, data.getLong(4));
            map.put(TRANSACTION_PERSON_5, data.getString(5));
            map.put(TRANSACTION_TYPE_6, data.getString(6));
            map.put(BANK_7, data.getString(7));
            map.put(TAGS_8, data.getString(8));
            map.put(NOTES_9, data.getString(9));
            map.put(IMAGE_REF_10, data.getString(10));
            map.put(CATEGORY_11, data.getString(11));
            map.put(PAYMENT_TYPE_12, data.getString(12));
            list.add(map);

        }
        data.close();

        return new StoreFilteredTransactionResult(this.calculateIncome(whereClause), this.calculateExpense(whereClause), list);
    }

}

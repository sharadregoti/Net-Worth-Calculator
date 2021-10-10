
package com.sharad.myapp.Utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.sharad.myapp.banks.BOI;
import com.sharad.myapp.banks.Bank;
import com.sharad.myapp.banks.FalseAlarmException;
import com.sharad.myapp.banks.GenericBank;
import com.sharad.myapp.banks.HDFC;
import com.sharad.myapp.banks.ICICI;
import com.sharad.myapp.banks.NotTransactionSMSException;
import com.sharad.myapp.banks.SmsParseResult;
import com.sharad.myapp.banks.StateBankOfIndia;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProcessSMS {

    // List of supported banks
    private final HashMap<String, String[]> supportedBanks = new HashMap<>();

    private final Context ctx;

    private DatabaseHelper dh;

    public ProcessSMS(Context ctx) {
        this.ctx = ctx;
        this.dh = new DatabaseHelper(ctx);
        this.supportedBanks.put(Constants.BANK_ICICI, new String[]{"ICICIB"});
        this.supportedBanks.put(Constants.BANK_STATE_BANK_OF_INDIA, new String[]{"SBIUPI", "ATMSBI", "SBIATM", "CBSSBI", "SBIPSG", "SBIDGT"});
        this.supportedBanks.put(Constants.BANK_HDFC_BANK, new String[]{"HDFC"});
        this.supportedBanks.put(Constants.BANK_BANK_OF_INDIA, new String[]{"BOI"});

        this.supportedBanks.put(Constants.BANK_OF_BARODA, new String[]{Constants.BANK_OF_BARODA});
        this.supportedBanks.put(Constants.BANK_OF_MAHARASHTRA, new String[]{Constants.BANK_OF_MAHARASHTRA});
        this.supportedBanks.put(Constants.BANK_CANARA_BANK, new String[]{Constants.BANK_CANARA_BANK});
        this.supportedBanks.put(Constants.BANK_CENTRAL_BANK_OF_INDIA, new String[]{Constants.BANK_CENTRAL_BANK_OF_INDIA});
        this.supportedBanks.put(Constants.BANK_INDIAN_OVERSEAS_BANK, new String[]{Constants.BANK_INDIAN_OVERSEAS_BANK});
        this.supportedBanks.put(Constants.BANK_PUNJAB_AND_SIND_BANK, new String[]{Constants.BANK_PUNJAB_AND_SIND_BANK});
        this.supportedBanks.put(Constants.BANK_PUNJAB_NATIONAL_BANK, new String[]{Constants.BANK_PUNJAB_NATIONAL_BANK});
        this.supportedBanks.put(Constants.BANK_UCO_BANK, new String[]{Constants.BANK_UCO_BANK});
        this.supportedBanks.put(Constants.BANK_UNION_BANK_OF_INDIA, new String[]{Constants.BANK_UNION_BANK_OF_INDIA});
        this.supportedBanks.put(Constants.BANK_AXIS_BANK, new String[]{Constants.BANK_AXIS_BANK});
        this.supportedBanks.put(Constants.BANK_BANDHAN_BANK, new String[]{Constants.BANK_BANDHAN_BANK});
        this.supportedBanks.put(Constants.BANK_CSB_BANK, new String[]{Constants.BANK_CSB_BANK});
        this.supportedBanks.put(Constants.BANK_DCB_BANK, new String[]{Constants.BANK_DCB_BANK});
        this.supportedBanks.put(Constants.BANK_DHANLAXMI_BANK, new String[]{Constants.BANK_DHANLAXMI_BANK});
        this.supportedBanks.put(Constants.BANK_FEDERAL_BANK, new String[]{Constants.BANK_FEDERAL_BANK});
        this.supportedBanks.put(Constants.BANK_IDFC_FIRST_BANK, new String[]{Constants.BANK_IDFC_FIRST_BANK});
        this.supportedBanks.put(Constants.BANK_KARNATAKA_BANK, new String[]{Constants.BANK_KARNATAKA_BANK});
        this.supportedBanks.put(Constants.BANK_KARUR_VYSYA_BANK, new String[]{Constants.BANK_KARUR_VYSYA_BANK});
        this.supportedBanks.put(Constants.BANK_KOTAK_MAHINDRA_BANK, new String[]{Constants.BANK_KOTAK_MAHINDRA_BANK});
        this.supportedBanks.put(Constants.BANK_LAKSHMI_VILAS_BANK, new String[]{Constants.BANK_LAKSHMI_VILAS_BANK});
        this.supportedBanks.put(Constants.BANK_RBL_BANK, new String[]{Constants.BANK_RBL_BANK});
        this.supportedBanks.put(Constants.BANK_SOUTH_INDIAN_BANK, new String[]{Constants.BANK_SOUTH_INDIAN_BANK});
        this.supportedBanks.put(Constants.BANK_TAMILNAD_MERCANTILE_BANK, new String[]{Constants.BANK_TAMILNAD_MERCANTILE_BANK});
        this.supportedBanks.put(Constants.BANK_YES_BANK, new String[]{Constants.BANK_YES_BANK});
        this.supportedBanks.put(Constants.BANK_IDBI_BANK, new String[]{Constants.BANK_IDBI_BANK});

        processAndStoreSMS();
    }

    private void processAndStoreSMS() {
        // For now, remove previous db & start fresh
//        this.dh.clean();
        String[] columns = new String[]{"_id", "address", "body", "date"};

        String lastProcessedDate = this.dh.getLastProcessedTxnSMSDate();
        if (lastProcessedDate.isEmpty()) {
            // This happens during first initialization
            // Use the default of past 10 yrs
            Long startDate = LocalDate.now().atTime(0, 0, 0).minusYears(10).withDayOfMonth(1).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
            lastProcessedDate = String.valueOf(startDate);
            Log.d("Last processed date is empty: ", lastProcessedDate);
        }

        for (Map.Entry<String, String[]> bank : this.supportedBanks.entrySet()) {

            // Build where clause
            // TODO: Check descending on Ascending in database table new values should be on top
            // TODO: Change this to simple where clause with like operator
            StringBuilder whereClause = new StringBuilder(String.format("(date > %s) and ( ", lastProcessedDate));
            for (int i = 0; i < bank.getValue().length; i++) {
                whereClause.append("address LIKE \"%").append(bank.getValue()[i]).append("\"");
                if (i != bank.getValue().length - 1) {
                    whereClause.append(" or ");
                }
            }
            whereClause.append(")");

            Log.d("SMS where clause: ", whereClause.toString());
            Cursor cursor = ctx.getContentResolver().query(Uri.parse("content://sms"), columns, whereClause.toString(), null, "date ASC");

            Bank bankObj;
            switch (bank.getKey()) {
                case Constants.BANK_STATE_BANK_OF_INDIA:
                    bankObj = new StateBankOfIndia();
                    break;
                case Constants.BANK_ICICI:
                    bankObj = new ICICI();
                    break;
                case Constants.BANK_BANK_OF_INDIA:
                    bankObj = new BOI();
                    break;
                case Constants.BANK_HDFC_BANK:
                    bankObj = new HDFC();
                    break;
                default:
                    bankObj = new GenericBank();
            }

            while (cursor.moveToNext()) {
                HashMap<String, String> map = new HashMap<>();

                if (cursor.getColumnName(2).equals("body")) {
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        map.put(cursor.getColumnName(i), cursor.getString(i));
                    }

                    String smsBody = cursor.getString(2);
                    SmsParseResult result;
                    try {
                        result = bankObj.parse(smsBody);
                        if (result.isBankBalanceSMS()) {
                            this.dh.upsertBankAccount(result.getAmount(), bank.getKey());
                            continue;
                        }

                        int dSmsId = Integer.parseInt(Objects.requireNonNull(map.get("_id")));
                        String dBank = bank.getKey();
                        String dtp = result.getMerchantName();
                        String dTags = result.getTags();
                        float dAmount = result.getAmount();
                        String dpt = Constants.PAYMENT_TYPE_ONLINE;
                        String dType = result.getTxnType();
                        Long dDate = Long.parseLong(Objects.requireNonNull(map.get("date")));

                        if (this.dh.addTransactionSMS(dAmount, dDate, dSmsId, smsBody, dType, dpt, dtp, dTags, dBank, "", "", Constants.TXN_CATEGORY_DEFAULT_Other) != -1) {
                            Log.d("Transaction Data: ", "************ Successful Insert ************");
                        } else {
                            Log.d("Transaction Data: ", "************ Fail Insert ************");
                        }
                    } catch (NotTransactionSMSException | FalseAlarmException e) {
                        Log.d("User Exception", "" + e.getMessage());
                    } catch (Exception e) {
                        Log.d("System Exception", "" + e.getMessage());
                    }
                }
            }
            cursor.close();
        }
        // Done
        this.dh.upsertLastProcessedTxnDate();
    }
}
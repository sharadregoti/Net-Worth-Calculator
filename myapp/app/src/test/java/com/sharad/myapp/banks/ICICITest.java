package com.sharad.myapp.banks;

import android.util.Log;

import com.sharad.myapp.Utils.Constants;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class ICICITest extends TestCase {

    class DataHolder {
        float amount;
        String txnType, merchantName, tags, smsBody, testName;
        boolean isBankSMS;

        public DataHolder(String name, float amount, String txnType, String merchantName, String tags, boolean isBankSMS, String smsBody) {
            this.testName = name;
            this.amount = amount;
            this.txnType = txnType;
            this.merchantName = merchantName;
            this.tags = tags;
            this.isBankSMS = isBankSMS;
            this.smsBody = smsBody;
        }
    }

    List<ICICITest.DataHolder> arr = Arrays.asList(
            // Check bank balance SMS
            new ICICITest.DataHolder("Bank Balance", 44283, "", "", "", true, "Balances for Ac XXXXXXXX3705 on 08/05/2021 11:20:29 AM ISTTotal Avbl. Bal: INR|44283.0Avbl. Bal: INR 44283.0Linked FD bal: INR|0.0"),
            // UPI Debited TODO: Improvement can be made in merchant name
            new ICICITest.DataHolder("UPI Debited", 1, Constants.TXN_TYPE_DEBITED, "swamiregoti42@okicici ", "UPI", false, "ICICI Bank Acct XXX705 debited for INR 1.00 on 16-May-21 and swamiregoti42@okicici credited.UPI:113612289014.Call 18002662 for dispute or SMS BLOCK 705 to 9215676766."),
            // UPI Credited TODO: We can extract merchant name
            new ICICITest.DataHolder("UPI Credited", 6510, Constants.TXN_TYPE_CREDITED, "sharadregoti15@okaxis ", "UPI", false, "Dear Customer, Account XXX705 is credited with INR 6510.00 on 05-Jul-21 from sharadregoti15@okaxis. UPI Ref. no. 118617777432 - ICICI Bank."),
            // IMPS Debited TODO: Improvement can be made in merchant name
            new ICICITest.DataHolder("IMPS Debited", 120000, Constants.TXN_TYPE_DEBITED, "Acct XX426 ", "IMPS", false, "ICICI Bank Acct XX705 debited with Rs 120,000.00 on 29-Jul-21 & Acct XX426 credited.IMPS:121011216856. Call 18002662 for dispute or SMS BLOCK 705 to 9215676766"),
            // IMPS Credited TODO: Improvement can be made in merchant name
            new ICICITest.DataHolder("IMPS Credited", 1, Constants.TXN_TYPE_CREDITED, "XXXXX82020", "IMPS", false, "ICICI Bank Account XX705 is credited with Rs 1.00 on 16-May-21 by Account linked to mobile number XXXXX82020. IMPS Ref. no. 113612405761."),
            // NEFT Debited TODO: No data available
            // NEFT Credited TODO: Improvement can be made in merchant name
            new ICICITest.DataHolder("NEFT Credited", 235000, Constants.TXN_TYPE_CREDITED, "N176210639001294 ZERODHA BROKI ", "NEFT", false, "Dear Customer, your ICICI Bank Account XX705 has been credited with INR 2,35,000.00 on 25-Jun-21. Info:NEFT-N176210639001294-ZERODHA BROKI. The Available Balance is INR 2,69,674.00.")
            // ATM Debited TODO: No data available
            // ATM Credited TODO: No data available
            // Cash debited (withdrawal) from bank branch TODO: No data available
            // Cash credited (deposit) from bank branch TODO: No data available
            // Check Book debited TODO: No data available
            // Check Book credited TODO: No data available
            // RTGS debited TODO: No data available
            // RTGS credited TODO: No data available
            // NACH debited TODO: No data available
            // NACH credited TODO: No data available
    );

    public void testParse() {
        ICICI sbi = new ICICI();
        for (int i = 0; i < arr.size(); i++) {
            try {
                ICICITest.DataHolder d = arr.get(i);
                SmsParseResult result = sbi.parse(d.smsBody);
                assertEquals(d.testName, d.amount, result.getAmount());
                assertEquals(d.testName, d.txnType, result.getTxnType());
                assertEquals(d.testName, d.merchantName, result.getMerchantName());
                assertEquals(d.testName, d.tags, result.getTags());
                assertEquals(d.testName, d.isBankSMS, result.isBankBalanceSMS());
            } catch (FalseAlarmException | NotTransactionSMSException e) {
                Log.d("Msg", e.getMessage());
            }
        }
    }
}
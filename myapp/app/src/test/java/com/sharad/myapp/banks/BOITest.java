package com.sharad.myapp.banks;

import android.util.Log;

import com.sharad.myapp.Utils.Constants;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class BOITest extends TestCase {

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

    List<BOITest.DataHolder> arr = Arrays.asList(
            // Check bank balance SMS TODO: No data available
            // UPI Debited TODO: Improvement can be made in merchant name
            new BOITest.DataHolder("UPI Debited", 75, Constants.TXN_TYPE_DEBITED, "XXXXXXXXXX0007 ", "UPI", false, "Your a/c no. XXXXXXXXXXX7858 is debited for Rs. 75.00 on 16-09-2019 08:24 and credited to a/c no. XXXXXXXXXX0007 (UPI Ref no 925908273809)."),
            // UPI Credited
            new BOITest.DataHolder("UPI Credited", 20000, Constants.TXN_TYPE_CREDITED, Constants.NOT_AVAILABLE, "UPI", false, "BOI -  Rs.20000.00 Credited to your Ac XX7858 on 15-04-21 by UPI ref No.110522204956.Avl Bal 22459.34"),
            // IMPS Debited TODO: No data available
            // IMPS Credited TODO: Improvement can be made in merchant name
            new BOITest.DataHolder("IMPS Credited", 200, Constants.TXN_TYPE_CREDITED, "ABOLI JAGANNATH CHAU ", "IMPS", false, "BOI-You have received Rs.200.00 from ABOLI JAGANNATH CHAU to your A/C xx7858 on 11/09/19 by IMPS RRN 925418606677"),
            // NEFT Debited
            new BOITest.DataHolder("NEFT Debited", 5500, Constants.TXN_TYPE_DEBITED, Constants.NOT_AVAILABLE, "NEFT", false, "BOI -  Your NEFT transaction with reference number BKIDN20006914815 for Rs 5500 has been credited to beneficiary account on 06-01-20 at time 21:31:22 hrs"),
            // NEFT Credited
            new BOITest.DataHolder("NEFT Credited", 13740, Constants.TXN_TYPE_CREDITED, Constants.NOT_AVAILABLE, "NEFT", false, "BOI -  Rs 13740.00 Credited in your Ac XX7858 on 23-09-2020 By NEFTINWARD N267201252543650 .Avl Bal 14386.20"),
            // ATM Debited
            new BOITest.DataHolder("ATM Debited", 200, Constants.TXN_TYPE_DEBITED, Constants.BANK_TXN_SELF, "ATM", false, "BOI -  Rs 200 Debited to Ac-XX7858 from ATMID:SACWC493 on 24-10-20. Avl. Bal 1540.20. Call 18004251112 if txn not done."),
            // ATM Credited TODO: No data available
            // Cash debited (withdrawal) from bank branch TODO: No data available
            // Cash credited (deposit) from bank branch TODO: No data available
            // Check Book debited TODO: No data available
            // Check Book credited TODO: No data available
            // RTGS debited TODO: No data available
            // RTGS credited TODO: No data available
            // NACH debited TODO: No data available
            // NACH credited
            new BOITest.DataHolder("NACH Credited", 484.25F, Constants.TXN_TYPE_CREDITED, Constants.NOT_AVAILABLE, "NACH", false, "BOI -  Rs 484.25 has been Credited (NACH) in your account XXXX7858 - APBS CR INW - CPSMSICIC 62863471 on 30-03-2020.Avl Bal 17660.59")
    );

    public void testParse() {
        BOI sbi = new BOI();
        for (int i = 0; i < arr.size(); i++) {
            try {
                BOITest.DataHolder d = arr.get(i);
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
package com.sharad.myapp.banks;

import android.util.Log;

import com.sharad.myapp.Utils.Constants;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class HDFCTest extends TestCase {

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

    List<HDFCTest.DataHolder> arr = Arrays.asList(
            // Check bank balance SMS TODO: No data available
            // UPI Debited
            new HDFCTest.DataHolder("UPI Debited", 99, Constants.TXN_TYPE_DEBITED, "7498227618@paytm", "UPI", false, "HDFC Bank: Rs 99.00 debited from a/c **3704 on 28-09-21 to VPA 7498227618@paytm(UPI Ref No 127129291829). Not you? Call on 18002586161 to report"),
            // UPI Credited TODO: Improvement can be made in merchant name
            new HDFCTest.DataHolder("UPI Credited", 1570, Constants.TXN_TYPE_CREDITED, "irctceticketing@yesbank ", "UPI", false, "Rs. 1570.00 credited to a/c XXXXXX3704 on 20-04-21 by a/c linked to VPA irctceticketing@yesbank (UPI Ref No  111017936710)."),
            // IMPS Debited TODO: Improvement we can extract merch name
            new HDFCTest.DataHolder("IMPS Debited", 10000, Constants.TXN_TYPE_DEBITED, Constants.NOT_AVAILABLE, "IMPS", false, "UPDATE: A/c XX3704 debited for INR 10,000.00 on 05-01-21 & A/c xxxxxxxx0737 credited (IMPS Ref No.100500321351).Avl bal:INR 14,022.45.Not you?Call 18002586161"),
            // IMPS Credited TODO: Improvement can be made in merchant name
            new HDFCTest.DataHolder("IMPS Credited", 2900, Constants.TXN_TYPE_CREDITED, Constants.NOT_AVAILABLE, "IMPS", false, "UPDATE: Your A/c XX3704 credited with INR 2,900.00 on 26-02-21 by A/c linked to mobile no XX0173 (IMPS Ref No. 105721391106) Available bal: INR 17,416.85"),
            // NEFT Debited TODO: No data available
            // NEFT Credited
            new HDFCTest.DataHolder("NEFT Credited", 5000, Constants.TXN_TYPE_CREDITED, Constants.NOT_AVAILABLE, "NEFT", false, "UPDATE: INR 5,000.00 deposited in A/c XX3704 on 21-DEC-20 for NEFT Cr-NKGS0000001-PRAJWAL V SHINDE-MEHANK VISHAL JAIN-NKGSH20356363400.Avl bal:INR 18,402.45 subject to clearing")
            // ATM Debited TODO: No data available
            // ATM Credited TODO: No data available
            // Cash debited (withdrawal) from bank branch TODO: No data available
            // Cash credited (deposit) from bank branch TODO: No data available
            // Check Book debited TODO: No data available
            // Check Book credited TODO: No data available
            // RTGS debited TODO: No data available
            // RTGS credited TODO: No data available
            // NACH debited TODO: No data available
            // NACH credited
            // new HDFCTest.DataHolder("NACH Credited", 484.25F, Constants.TXN_TYPE_CREDITED, Constants.NOT_AVAILABLE, "NACH", false, "BOI -  Rs 484.25 has been Credited (NACH) in your account XXXX7858 - APBS CR INW - CPSMSICIC 62863471 on 30-03-2020.Avl Bal 17660.59")
    );

    public void testParse() {
        HDFC sbi = new HDFC();
        for (int i = 0; i < arr.size(); i++) {
            try {
                HDFCTest.DataHolder d = arr.get(i);
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
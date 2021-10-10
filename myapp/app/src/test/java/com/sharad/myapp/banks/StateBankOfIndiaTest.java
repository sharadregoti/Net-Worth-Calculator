package com.sharad.myapp.banks;

import android.util.Log;

import com.sharad.myapp.Utils.Constants;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class StateBankOfIndiaTest extends TestCase {

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

    List<DataHolder> arr = Arrays.asList(
            // Check bank balance SMS TODO: No data available
            // UPI Debited
            new DataHolder("UPI Debited", 750, Constants.TXN_TYPE_DEBITED, Constants.NOT_AVAILABLE, "UPI", false, "Dear SBI UPI User, ur A/cX1711 - debited by Rs750.0 on 06Mar20 Ref No 006608124569. Download YONO @ www.yonosbi.com"),
            // UPI Credited TODO: We can extract merchant name
            new DataHolder("UPI Credited", 25000, Constants.TXN_TYPE_CREDITED, Constants.NOT_AVAILABLE, "UPI", false, "Dear SBI UPI User, ur A/cX1711 credited by Rs25000 on 28Jul20 by SWANY SIDDAYYA REGOTI (Ref no 021011474770)"),
            // IMPS Debited TODO: Improvement can be made in merchant name
            new DataHolder("IMPS Debited", 1000, Constants.TXN_TYPE_DEBITED, " no. XXXXXXXX1711 is debited for Rs.1000.00 on 10-03-21 and a/c XXXXXXX797 credited ", "IMPS", false, "Your a/c no. XXXXXXXX1711 is debited for Rs.1000.00 on 10-03-21 and a/c XXXXXXX797 credited (IMPS Ref no 106920990071). Download YONO @ www.yonosbi.com."),
            // IMPS Credited TODO: Improvement can be made in merchant name
            new DataHolder("IMPS Credited", 1, Constants.TXN_TYPE_CREDITED, " 9XXXXXX020-ZERODHA BROKING LTD- ", "IMPS", false, "Your a/c no. XXXXXXXX1711 is credited by Rs.1.00 on 10-07-20 by a/c linked to mobile 9XXXXXX020-ZERODHA BROKING LTD- (IMPS Ref no 019223418470)."),
            // NEFT Debited TODO: No data available
            // NEFT Credited
            new DataHolder("NEFT Credited", 10000, Constants.TXN_TYPE_CREDITED, "SPACE UP TECHNOLOGIES LLP", "NEFT", false, "INR 10,000.00 credited to your A/c No XX1711 on 18/08/2020 through NEFT with UTR SDC25156074 by SPACE UP TECHNOLOGIES LLP, INFO: /URGENT/"),
            // ATM Debited
            new DataHolder("ATM Debited", 500, Constants.TXN_TYPE_DEBITED, Constants.BANK_TXN_SELF, "ATM", false, "Rs500w/d@SBI ATM S5NE005352621 fm A/cx1711 on06Sep20Txn#2731Avlbal Rs27448 If not w/d,fwd this SMS to9223008333/call1800111109or09449112211 to block card"),
            // ATM Credited TODO: No data available
            // Cash debited (withdrawal) from bank branch TODO: No data available
            // Cash credited (deposit) from bank branch
            new DataHolder("Cash credited", 30000, Constants.TXN_TYPE_CREDITED, Constants.BANK_TXN_SELF, "BRANCH", false, "Your A/C XXXXX751711 Credited INR 30,000.00 on 12/06/20 -Deposited by Cash by SELF. Avl Bal INR 55,709.90"),
            // Check book debited TODO: No data available
            // Check credited
            new DataHolder("Check credited", 10000, Constants.TXN_TYPE_CREDITED, Constants.NOT_AVAILABLE, "", false, "Your A/C XXXXX751711 credited by Cheque no 000123 dated 25/02/20 of DCB of Rs 10,000.00 on 26/02/20. Avl Bal Rs 27,551.40.")
            // RTGS debited TODO: No data available
            // RTGS credited TODO: No data available
            // NACH debited TODO: No data available
            // NACH credited TODO: No data available
    );

    public void testParse() {
        StateBankOfIndia sbi = new StateBankOfIndia();
        for (int i = 0; i < arr.size(); i++) {
            try {
                DataHolder d = arr.get(i);
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
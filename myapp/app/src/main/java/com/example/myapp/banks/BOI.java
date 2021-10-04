package com.example.myapp.banks;

import com.example.myapp.Utils.Constants;
import com.example.myapp.Utils.Functions;

import java.util.HashMap;

public class BOI implements Bank {

    // Source of data (https://www.bankbazaar.com/debit-card/how-to-check-bank-of-india-balance-using-mobile-phone.html)
    @Override
    public String getBankBalanceSmsBody() {
        return "BAL 1111";
    }

    @Override
    public String getBankBalanceSmsAddress() {
        return "9810558585";
    }

    @Override
    public String getBankBalanceCallPhoneAddress() {
        return "9015135135";
    }

    @Override
    public HashMap<String, String> parse(String smsBody) throws NotTransactionSMSException, FalseAlarmException {
        HashMap<String, String> map = new HashMap<>();

        if (smsBody.contains("debited") || smsBody.contains("Debited")) {
            map.put("type", Constants.TXN_TYPE_DEBITED);
        } else if (smsBody.contains("credited") || smsBody.contains("Credited") || smsBody.contains("You have received")) {
            map.put("type", Constants.TXN_TYPE_CREDITED);
        } else {
            throw new NotTransactionSMSException("Not a bank transaction");
        }

        if (smsBody.contains("UPI")) {
            map.put("tags", "UPI");
            int initialIndex = smsBody.indexOf("to a/c no. ");
            if (initialIndex == -1) {
                initialIndex = smsBody.indexOf("credited to ") + 12;
            } else {
                initialIndex += 11;
            }
            int lastIndex = smsBody.indexOf("(UPI Ref");
            if (lastIndex != -1) {
                map.put("transaction_person", smsBody.substring(initialIndex, lastIndex));
            }
        } else if (smsBody.contains("IMPS")) {
            map.put("tags", "IMPS");
            int initialIndex = smsBody.indexOf("from") + 5;
            int lastIndex = smsBody.indexOf("to your");
            map.put("transaction_person", smsBody.substring(initialIndex, lastIndex));
        } else if (smsBody.contains("w/d") || smsBody.contains("withdrawn")) {
            map.put("tags", "ATM");
        } else if (smsBody.contains("NEFT")) {
            map.put("tags", "NEFT");
        } else if (smsBody.contains("ATMID")) {
            map.put("tags", "ATM");
            map.put("transaction_person", "ATM Withdrawal");
        }

        String amt = Functions.extractAmountFromSMS(smsBody);
        map.put("amount", amt);
        return map;
    }
}

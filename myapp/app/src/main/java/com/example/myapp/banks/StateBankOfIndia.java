package com.example.myapp.banks;

import com.example.myapp.Utils.Constants;
import com.example.myapp.Utils.Functions;

import java.util.HashMap;

public class StateBankOfIndia implements Bank {

    // Source of data (https://www.paisabazaar.com/banking/sbi-balance-enquiry-toll-free-number/#:~:text=A.%20Account%2Dholders%20can%20SMS,SMS%20%E2%80%9CMSTMT%E2%80%9D%20to%2009223866666.)
    @Override
    public String getBankBalanceSmsBody() {
        return "BAL";
    }

    @Override
    public String getBankBalanceSmsAddress() {
        return "9223766666";
    }

    @Override
    public String getBankBalanceCallPhoneAddress() {
        return "9223766666";
    }

    @Override
    public HashMap<String, String> parse(String smsBody) throws NotTransactionSMSException, FalseAlarmException {
        HashMap<String, String> map = new HashMap<>();

        if (smsBody.contains("debited") || smsBody.contains("Debited") || smsBody.contains("debit") || smsBody.contains("1st UPI") || smsBody.toLowerCase().contains("sbidrcard")) {
            // tx# is for sbidrcard
            map.put("type", Constants.TXN_TYPE_DEBITED);
        } else if (smsBody.contains("w/d@") || smsBody.contains("withdrawn")) {
            map.put("tags", "ATM Cash_Withdrawal");
            map.put("transaction_person", "SELF");
            map.put("type", Constants.TXN_TYPE_DEBITED);
        } else if (smsBody.contains("credited") || smsBody.contains("Credited") || smsBody.contains("credit")) {
            map.put("type", Constants.TXN_TYPE_CREDITED);
        } else if (smsBody.contains("CR -SBI")) {
            map.put("bank_balance", "yes");
        } else {
            throw new NotTransactionSMSException("Not a bank transaction");
        }

        if (smsBody.contains("UPI")) {
            map.put("tags", "UPI");
        } else if (smsBody.contains("IMPS")) {
            map.put("tags", "IMPS");
            int initialIndex = smsBody.indexOf("to mobile") + 9;
            int lastIndex = smsBody.indexOf("(IMPS Ref");
            map.put("transaction_person", smsBody.substring(initialIndex, lastIndex));
        } else if (smsBody.contains("NEFT")) {
            map.put("tags", "NEFT");
            int initialIndex = smsBody.indexOf("by ") + 3;
            int lastIndex = smsBody.indexOf(", INFO:");
            map.put("transaction_person", smsBody.substring(initialIndex, lastIndex));
        } else if (smsBody.contains("Cash by SELF")) {
            map.put("tags", "ATM Cash_Deposit");
            map.put("transaction_person", "SELF");
        }


        String amt = Functions.extractAmountFromSMS(smsBody);
        map.put("amount", amt);
        return map;
    }
}

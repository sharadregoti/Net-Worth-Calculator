package com.sharad.myapp.banks;

import com.sharad.myapp.Utils.Constants;
import com.sharad.myapp.Utils.Functions;

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
    public SmsParseResult parse(String smsBody) throws NotTransactionSMSException, FalseAlarmException {
        String merchName = "", tags = "", txnType = "", amount = "";
        boolean isBankBalanceSMS = false;

        if (smsBody.contains("debited") || smsBody.contains("Debited") || smsBody.contains("debit") || smsBody.contains("1st UPI") || smsBody.toLowerCase().contains("sbidrcard")) {
            // tx# is for sbidrcard
            txnType = Constants.TXN_TYPE_DEBITED;
        } else if (smsBody.contains("w/d@") || smsBody.contains("withdrawn")) {
            tags = "ATM";
            merchName = Constants.BANK_TXN_SELF;
            txnType = Constants.TXN_TYPE_DEBITED;
        } else if (smsBody.contains("credited") || smsBody.contains("Credited") || smsBody.contains("credit")) {
            txnType = Constants.TXN_TYPE_CREDITED;
        } else if (smsBody.contains("CR -SBI")) {
            isBankBalanceSMS = true;
        } else {
            throw new NotTransactionSMSException("Not a bank transaction");
        }

        if (smsBody.contains("UPI")) {
            tags = "UPI";
        } else if (smsBody.contains("IMPS")) {
            tags = "IMPS";
            int initialIndex = smsBody.indexOf("to mobile") + 9;
            int lastIndex = smsBody.indexOf("(IMPS Ref");
            merchName = smsBody.substring(initialIndex, lastIndex);
        } else if (smsBody.contains("NEFT")) {
            tags = "NEFT";
            int initialIndex = smsBody.indexOf("by ") + 3;
            int lastIndex = smsBody.indexOf(", INFO:");
            merchName = smsBody.substring(initialIndex, lastIndex);
        } else if (smsBody.contains("Cash by SELF")) {
            tags = "BRANCH";
            merchName = Constants.BANK_TXN_SELF;
        }


        String amt = Functions.extractAmountFromSMS(smsBody);
        amount = amt;
        return new SmsParseResult(merchName, tags, txnType, amount, smsBody, isBankBalanceSMS);
    }
}

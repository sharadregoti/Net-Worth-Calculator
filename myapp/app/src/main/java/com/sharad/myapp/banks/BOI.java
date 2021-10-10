package com.sharad.myapp.banks;

import com.sharad.myapp.Utils.Constants;
import com.sharad.myapp.Utils.Functions;

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
    public SmsParseResult parse(String smsBody) throws NotTransactionSMSException, FalseAlarmException {
        String merchName = "", tags = "", txnType = "", amount = "";
        boolean isBankBalanceSMS = false;

        if (smsBody.contains("debited") || smsBody.contains("Debited") || smsBody.contains("Your NEFT transaction with reference")) {
            txnType = Constants.TXN_TYPE_DEBITED;
        } else if (smsBody.contains("credited") || smsBody.contains("Credited") || smsBody.contains("You have received")) {
            txnType = Constants.TXN_TYPE_CREDITED;
        } else {
            throw new NotTransactionSMSException("Not a bank transaction");
        }

        if (smsBody.contains("UPI")) {
            tags = "UPI";
            int initialIndex = smsBody.indexOf("to a/c no. ");
            if (initialIndex == -1) {
                initialIndex = smsBody.indexOf("credited to ") + 12;
            } else {
                initialIndex += 11;
            }
            int lastIndex = smsBody.indexOf("(UPI Ref");
            if (lastIndex != -1) {
                merchName = smsBody.substring(initialIndex, lastIndex);
            }
        } else if (smsBody.contains("IMPS")) {
            tags = "IMPS";
            int initialIndex = smsBody.indexOf("from") + 5;
            int lastIndex = smsBody.indexOf("to your");
            merchName = smsBody.substring(initialIndex, lastIndex);
        } else if (smsBody.contains("w/d") || smsBody.contains("withdrawn")) {
            tags = "ATM";
            merchName = Constants.BANK_TXN_SELF;
        } else if (smsBody.contains("NEFT")) {
            tags = "NEFT";
        } else if (smsBody.contains("ATMID")) {
            tags = "ATM";
            merchName = Constants.BANK_TXN_SELF;
        } else if (smsBody.contains("NACH")) {
            tags = "NACH";
        }

        String amt = Functions.extractAmountFromSMS(smsBody);
        amount = amt;
        return new SmsParseResult(merchName, tags, txnType, amount, smsBody, isBankBalanceSMS);
    }
}

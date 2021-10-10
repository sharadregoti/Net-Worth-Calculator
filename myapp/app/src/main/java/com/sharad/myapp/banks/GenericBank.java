package com.sharad.myapp.banks;

import com.sharad.myapp.Utils.Constants;
import com.sharad.myapp.Utils.Functions;

public class GenericBank implements Bank {
    @Override
    public String getBankBalanceSmsBody() {
        return "";
    }

    @Override
    public String getBankBalanceSmsAddress() {
        return "";
    }

    @Override
    public String getBankBalanceCallPhoneAddress() {
        return "";
    }

    @Override
    public SmsParseResult parse(String smsBody) throws NotTransactionSMSException, FalseAlarmException {
        String merchName = "", tags = "", txnType = "", amount = "";
        boolean isBankBalanceSMS = false;

        smsBody = smsBody.toLowerCase();
        if (smsBody.contains("emi")) {
            throw new NotTransactionSMSException("EMI are not supported yet");
        } else if (smsBody.contains("requested")) {
            throw new FalseAlarmException("False transactional SMS contains requested in it");
        } else if (smsBody.contains("credit card")) {
            throw new FalseAlarmException("Credit card sms are not supported");
        } else if (smsBody.contains("debited") || smsBody.contains("debit")) {
            txnType = Constants.TXN_TYPE_DEBITED;
        } else if (smsBody.contains("credited") || smsBody.contains("you have received") || smsBody.contains("deposited") || smsBody.contains("credit")) {
            txnType = Constants.TXN_TYPE_CREDITED;
        }

        if (smsBody.contains("upi")) {
            tags = "UPI";
        } else if (smsBody.contains("imps")) {
            tags = "IMPS";
        } else if (smsBody.contains("w/d") || smsBody.contains("withdrawn")) {
            tags = "ATM";
            merchName = Constants.BANK_TXN_SELF;
        } else if (smsBody.contains("neft")) {
            tags = "NEFT";
        } else if (smsBody.contains("atmid")) {
            tags = "ATM";
        } else if (smsBody.contains("nach")) {
            tags = "NACH";
        }

        String amt = Functions.extractAmountFromSMS(smsBody);
        amount = amt;
        return new SmsParseResult(merchName, tags, txnType, amount, smsBody, isBankBalanceSMS);
    }
}

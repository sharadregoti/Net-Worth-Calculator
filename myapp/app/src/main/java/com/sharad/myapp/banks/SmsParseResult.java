package com.sharad.myapp.banks;

import com.sharad.myapp.Utils.Constants;

public class SmsParseResult {

    String merchName, tags, txnType, amount, smsBody;
    boolean isBankBalanceSMS;

    public SmsParseResult(String merchName, String tags, String txnType, String amount, String smsBody, boolean isBankBalanceSMS) {
        this.merchName = merchName;
        this.tags = tags;
        this.txnType = txnType;
        this.amount = amount;
        this.smsBody = smsBody;
        this.isBankBalanceSMS = isBankBalanceSMS;
    }


    public float getAmount() {
        return Float.parseFloat(amount);
    }

    public String getTags() {
        return tags;
    }

    public String getMerchantName() {
        return (merchName == "" && !isBankBalanceSMS()) ? Constants.NOT_AVAILABLE : merchName;
    }

    public String getTxnType() {
        return txnType;
    }

    public boolean isBankBalanceSMS() {
        return isBankBalanceSMS;
    }
}

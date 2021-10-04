package com.example.myapp.banks;

import java.util.HashMap;

public interface Bank {
    String getBankBalanceSmsBody();

    String getBankBalanceSmsAddress();

    String getBankBalanceCallPhoneAddress();

    HashMap<String, String> parse(String smsBody) throws NotTransactionSMSException, FalseAlarmException;

    class BankConstants {
        static final String ICICI = "ICICI";
        static final String STATE_BANK_OF_INDIA = "SBI";
        static final String HDFC_BANK = "HDFC";
        static final String BANK_OF_INDIA = "BOI";
    }
}
package com.sharad.myapp.banks;

public interface Bank {
    String getBankBalanceSmsBody();

    String getBankBalanceSmsAddress();

    String getBankBalanceCallPhoneAddress();

    SmsParseResult parse(String smsBody) throws NotTransactionSMSException, FalseAlarmException;

    class BankConstants {
        static final String ICICI = "ICICI";
        static final String STATE_BANK_OF_INDIA = "SBI";
        static final String HDFC_BANK = "HDFC";
        static final String BANK_OF_INDIA = "BOI";
    }
}


package com.example.myapp.banks;

import java.util.HashMap;

public interface Bank {
    public HashMap<String,String> parse(String smsBody) throws NotTransactionSMSException,FalseAlarmException;
}

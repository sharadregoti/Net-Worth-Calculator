package com.example.myapp.banks;

import com.example.myapp.Utils;

import java.util.HashMap;

public class StateBankOfIndia implements Bank {
    @Override
    public HashMap<String, String> parse(String smsBody) throws NotTransactionSMSException,FalseAlarmException{
        HashMap<String, String> map = new HashMap<>();

        if (smsBody.contains("debited")) {
            map.put("type", Utils.TXN_TYPE_DEBITED);
        } else if (smsBody.contains("credited")) {
            map.put("type", Utils.TXN_TYPE_CREDITED);
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
        } else if (smsBody.contains("w/d") || smsBody.contains("withdrawn")) {
            map.put("tags", "ATM");
        } else if (smsBody.contains("NEFT")) {
            map.put("tags", "NEFT");
            int initialIndex = smsBody.indexOf("by ") + 3;
            int lastIndex = smsBody.indexOf(", INFO:");
            map.put("transaction_person", smsBody.substring(initialIndex, lastIndex));
        }


        Integer index = smsBody.indexOf("Rs. ");
        if (index == -1) {
            index = smsBody.indexOf("Rs ");
        }
        if (index == -1) {
            index = smsBody.indexOf("Rs");
        }
        if (index == -1) {
            index = smsBody.indexOf("INR ");
        }
        if (index == -1) {
            index = smsBody.indexOf("INR");
        }
        if (index == -1) {
            map.put("amount", smsBody);
        } else {
            StringBuilder amount = new StringBuilder();
            Integer spaceCount = 0;
            for (int i = index; i < smsBody.length(); i++) {
                if (smsBody.charAt(i) == 'I' || smsBody.charAt(i) == 'N' || smsBody.charAt(i) == 'R' || smsBody.charAt(i) == 's') {
                    continue;
                }

                if (smsBody.charAt(i) == ' ' && !Character.isDigit(smsBody.charAt(i + 1))) {
                    break;
                }

                if (smsBody.charAt(i) == 'w') {
                    break;
                }
                amount.append(smsBody.charAt(i));
            }
            String am = "";
            if (amount.toString().startsWith(".")) {
                am = amount.substring(1);
            } else {
                am = amount.toString();
            }
            String[] arr = am.split("\\.");
            if (arr.length >= 2) {
                int foo = Integer.parseInt(arr[1]);
                if (foo == 0) {
                    am = arr[0];
                }
            }
            map.put("amount", am.trim());
        }
        return null;
    }
}

package com.example.myapp.banks;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ICICI implements Bank {

    @Override
    public HashMap<String, String> parse(String smsBody) throws NotTransactionSMSException, FalseAlarmException {
        HashMap<String, String> map = new HashMap<>();

        if (smsBody.contains("Credit Card")) {
//          TODO: Skipping credit cards for now
            throw new FalseAlarmException("Credit card sms are not supported");
        } else if (smsBody.contains("requested")) {
            /* Some banks send a pre payment message which has payment amount int
               this if block avoids duplication of amounts
               */
            throw new FalseAlarmException("False transactional SMS");
        } else if (smsBody.contains("debited")) {
            map.put("type", "debited");
            if (smsBody.contains("credited.UPI")) {
                Integer index = smsBody.indexOf("credited.UPI");
                String subString = smsBody.substring(0, index);
                subString = subString.trim();

                String[] arr = subString.split(" ");
                String finalStr = "";
                for (Integer i = arr.length - 1; i >= 0; i--) {
                    if (arr[i].equals("&") || arr[i].equals("and")) {
                        break;
                    }
                    finalStr = arr[i] + " " + finalStr;
                }

                map.put("transaction_person", finalStr);
                map.put("tags", "UPI");
            } else if (smsBody.contains("credited.IMPS")) {
                Integer index = smsBody.indexOf("credited.IMPS");
                String subString = smsBody.substring(0, index);
                subString = subString.trim();
                String[] arr = subString.split(" ");
                String finalStr = "";
                for (Integer i = arr.length - 1; i >= 0; i--) {
                    if (arr[i].equals("&") || arr[i].equals("and")) {
                        break;
                    }
                    finalStr = arr[i] + " " + finalStr;
                }

                map.put("transaction_person", finalStr);
                map.put("tags", "IMPS");
            } else if (smsBody.contains("Info:")) {
                Integer initialIndex = smsBody.lastIndexOf("Info:");
                Integer lastIndex = smsBody.indexOf('.', initialIndex);
//                        5 is the lenght of Info:
                String subString = smsBody.substring(initialIndex + 5, lastIndex);
                subString = subString.trim();
                subString = subString.replace("*", " ");
                subString = subString.replace("-", " ");
                String[] arr = subString.split(" ");
                String finalStr = "";
                for (Integer i = 1; i < arr.length; i++) {
                    finalStr += arr[i] + " ";
                }
                map.put("transaction_person", finalStr);
                map.put("tags", arr[0].toUpperCase());
            }
        } else if (smsBody.contains("credited")) {
            map.put("type", "credited");
            if (smsBody.contains(". UPI Ref.")) {
                Integer index = smsBody.indexOf(". UPI Ref.");
                String subString = smsBody.substring(0, index);
                subString = subString.trim();

                String[] arr = subString.split(" ");
                String finalStr = "";
                for (Integer i = arr.length - 1; i >= 0; i--) {
                    if (arr[i].equals("from")) {
                        break;
                    }
                    finalStr = arr[i] + " " + finalStr;
                }

                map.put("transaction_person", finalStr);
                map.put("tags", "UPI");
            } else if (smsBody.contains(". IMPS Ref.")) {
                Integer index = smsBody.indexOf(". IMPS Ref.");
                String subString = smsBody.substring(0, index);
                subString = subString.trim();

                String[] arr = subString.split(" ");

                map.put("transaction_person", arr[arr.length - 1]);
                map.put("tags", "IMPS");
            } else if (smsBody.contains("Info:")) {
                Integer initialIndex = smsBody.lastIndexOf("Info:");
                Integer lastIndex = smsBody.indexOf('.', initialIndex);
//                        5 is the lenght of Info:
                String subString = smsBody.substring(initialIndex + 5, lastIndex);
                subString = subString.trim();
                subString = subString.replace("*", " ");
                subString = subString.replace("-", " ");
                String[] arr = subString.split(" ");
                String finalStr = "";
                for (Integer i = 1; i < arr.length; i++) {
                    finalStr += arr[i] + " ";
                }
                map.put("transaction_person", finalStr);
                map.put("tags", arr[0].toUpperCase());
            }
        } else {
            throw new NotTransactionSMSException("Not a bank transaction");
        }

//      TODO: Instead of pattern use INR or Rs search to be consistent with other bank implementation
        Pattern p = Pattern.compile("[-]?\\d[\\d,]*[\\.]?[\\d{2}]*");
        Matcher m = p.matcher(smsBody);
        Integer count = 0;
        while (m.find()) {
            count++;
            if (count == 2) {
                String str = m.group();
                String[] arr = str.split("\\.");
                if (arr.length >= 2) {
                    int foo = Integer.parseInt(arr[1]);
                    if (foo == 0) {
                        str = arr[0];
                    }
                }
                str = str.trim().replace(",", "");
                map.put("amount", str);
            }
        }
        return map;
    }

}

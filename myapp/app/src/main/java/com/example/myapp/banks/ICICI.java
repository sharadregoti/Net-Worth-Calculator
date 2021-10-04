package com.example.myapp.banks;

import com.example.myapp.Utils.Constants;
import com.example.myapp.Utils.Functions;

import java.util.HashMap;

public class ICICI implements Bank {

    // Source of data (https://www.icicibank.com/mobile-banking/sms-banking.page)
    @Override
    public String getBankBalanceSmsBody() {
        return "IBAL";
    }

    @Override
    public String getBankBalanceSmsAddress() {
        return "9215676766";
    }

    @Override
    public String getBankBalanceCallPhoneAddress() {
        return "9594612612";
    }

    @Override
    public HashMap<String, String> parse(String smsBody) throws NotTransactionSMSException, FalseAlarmException {
        HashMap<String, String> map = new HashMap<>();

        if (smsBody.contains("Credit Card")) {
            // TODO: Skipping credit cards for now
            throw new FalseAlarmException("Credit card sms are not supported");
        } else if (smsBody.contains("requested")) {
            /* Some banks send a pre payment message which has payment amount init
               this if block avoids duplication of amounts
               */
            throw new FalseAlarmException("False transactional SMS contains requested in it");
        } else if (smsBody.contains("debited") || smsBody.contains("transaction of")) {
            map.put("type", Constants.TXN_TYPE_DEBITED);
            // Extract info about, to whom the amount has been debited to
            // Also add tags, that refers to the type of electronic payment made
            if (smsBody.contains("credited.UPI")) {
                // ******* Sample *********
                // ICICI Bank Acct XXX705 debited for INR 1.00 on 16-May-21 and
                // swamiregoti42@okicici credited.UPI:113612289014.Call 18002662
                // for dispute or SMS BLOCK 705 to 9215676766.
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
                // ******* Sample *********
                // ICICI Bank Acct XX705 debited with Rs 100.00 on 29-Jul-21 & Acct XX426
                // credited.IMPS:121011210158. Call 18002662 for dispute or SMS BLOCK 705 to 9215676766
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
                // ******* Sample *********
                // Dear Customer, ICICI Bank Account XX705 is debited with INR 9,757.00 on 06-Sep-21.
                // Info: BIL*000202192. The Available Balance is INR 5,606.10. Call 18002662
                // for dispute or SMS BLOCK 705 to 9215676766
                Integer initialIndex = smsBody.lastIndexOf("Info:");
                Integer lastIndex = smsBody.indexOf('.', initialIndex);
                // 5 is the lenght of Info:
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
            map.put("type", Constants.TXN_TYPE_CREDITED);
            if (smsBody.contains(". UPI Ref.")) {
                // ******* Sample *********
                // Dear Customer, Account XXX705 is credited with INR 9678.00 on 06-Sep-21
                // from swamiregoti42@okicici. UPI Ref. no. 124997082022 - ICICI Bank.
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
                // ******* Sample *********
                // ICICI Bank Account XX705 is credited with Rs 1.00 on 16-May-21
                // by Account linked to mobile number XXXXX59977. IMPS Ref. no. 113612904504.
                Integer index = smsBody.indexOf(". IMPS Ref.");
                String subString = smsBody.substring(0, index);
                subString = subString.trim();

                String[] arr = subString.split(" ");

                map.put("transaction_person", arr[arr.length - 1]);
                map.put("tags", "IMPS");
            } else if (smsBody.contains("Info:")) {
                // ******* Sample *********
                // Dear Customer, your ICICI Bank Account XX705 has been credited with INR 44,283.00 on 05-May-21.
                // Info:INF*INFT*52154000067G*BULD8352154  . The Available Balance is INR 44,283.00.
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
        } else if (smsBody.contains("Balances for Ac")) {
            // Balances for Ac XXXXXXXX3705 on 20/05/2021 10:12:53 PM ISTTotal Avbl. Bal: INR|99284.0Avbl. Bal: INR 99284.0Linked FD bal: INR|0.0
            map.put("bank_balance", "yes");
        } else {
            throw new NotTransactionSMSException("Not a bank transaction");
        }

        String amt = Functions.extractAmountFromSMS(smsBody);
        map.put("amount", amt);
        return map;
    }

}

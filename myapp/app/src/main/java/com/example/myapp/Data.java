package com.example.myapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Data {
    static final String BANK_OF_INDIA = "BOI";
    static final String ICICI = "ICICI";
    static final String STATE_BANK_OF_INDIA = "SBI";

//    enum Banks {
//        HDFC_Bank,
//        State_Bank_of_India,
//        ICICI_Bank,
//        Axis_Bank,
//        Kotak_Mahindra_Bank,
//        IndusInd_Bank,
//        Yes_Bank,
//        Punjab_National_Bank,
//        Bank_Of_Baroda,
//        Bank_Of_India,
//    }

    private HashMap<String, String[]> supportedBanks = new HashMap<String, String[]>();

    private List list = new ArrayList();
    private List jsonList = new ArrayList();
    private Context ctx;

    public Data(Context ctx) {
        this.ctx = ctx;
//        this.supportedBanks.put(ICICI, new String[]{"ICICIB"});
        this.supportedBanks.put(STATE_BANK_OF_INDIA, new String[]{"SBIUPI", "ATMSBI", "CBSSBI", "SBIPSG", "SBIDGT"});
        read_sms();
    }

    public List getMessages() {
        return list;
    }

    public String getDate(Integer position) {
        HashMap d = (HashMap) list.get(position);
        String createdTime = d.get("date").toString();
        Date date1 = new Date(Long.parseLong(createdTime)); // right here
        System.out.println(date1.toString()); // this is what you are looking online
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-YYYY"); // here you would have to customize the output format you are looking for
        System.out.println(sdf1.format(date1));
        return sdf1.format(date1);
    }

    public String getAmount(Integer position) {
        HashMap d = (HashMap) list.get(position);
        return d.get("amount").toString();
    }

    public String getType(Integer position) {
        HashMap d = (HashMap) list.get(position);
        return d.get("type").toString();
    }

    public String getBank(Integer position) {
        HashMap d = (HashMap) list.get(position);
        return d.get("bank").toString();
    }

    public String getTransactionPerson(Integer position) {
        HashMap d = (HashMap) list.get(position);
        return d.get("transaction_person").toString();
    }

    public String getTags(Integer position) {
        HashMap d = (HashMap) list.get(position);
        return d.get("tags").toString();
    }

    private void read_sms() {
        String[] columns = new String[]{"_id", "address", "body", "date"};

        for (Map.Entry<String, String[]> bank : this.supportedBanks.entrySet()) {
            StringBuilder whereClause = new StringBuilder("");
            for (Integer i = 0; i < bank.getValue().length; i++) {
                whereClause.append("address LIKE \"%").append(bank.getValue()[i]).append("\"");
                if (i != bank.getValue().length - 1) {
                    whereClause.append(" or ");
                }
            }

            Cursor cursor = ctx.getContentResolver().query(Uri.parse("content://sms"), columns, whereClause.toString(), null, null);

            while (cursor.moveToNext()) {
                HashMap<String, String> map = new HashMap<>();

                if (cursor.getColumnName(2).equals("body")) {
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        map.put(cursor.getColumnName(i), cursor.getString(i));
                    }

                    map.put("bank", bank.getKey());
                    map.put("transaction_person", "N/A");
                    map.put("tags", "N/A");

                    String smsBody = cursor.getString(2);

                    if (smsBody.contains("debited")) {
                        map.put("type", "debited");
                    } else if (smsBody.contains("credited")) {
                        map.put("type", "credited");
                    } else {
                        continue;
                    }

                    if (smsBody.contains("UPI")) {
                        map.put("tags","UPI");
                    } else if (smsBody.contains("IMPS")) {
                        map.put("tags","IMPS");
                        int initialIndex = smsBody.indexOf("to mobile") + 9;
                        int lastIndex = smsBody.indexOf("(IMPS Ref");
                        map.put("transaction_person", smsBody.substring(initialIndex,lastIndex));
                    } else if (smsBody.contains("w/d") || smsBody.contains("withdrawn")) {
                        map.put("tags","ATM");
                    } else if (smsBody.contains("NEFT")) {
                        map.put("tags","NEFT");
                        int initialIndex = smsBody.indexOf("by ") + 3;
                        int lastIndex = smsBody.indexOf(", INFO:");
                        map.put("transaction_person", smsBody.substring(initialIndex,lastIndex));
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

                            if (smsBody.charAt(i) == ' ' && !Character.isDigit(smsBody.charAt(i+1))) {
                                break;
                            }

                            if (smsBody.charAt(i) == 'w') {
                                break;
                            }
                            amount.append(smsBody.charAt(i));
                        }
                        String am = "";
                        if (amount.toString().startsWith(".")) {
                            am = amount.toString().substring(1);
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
                        map.put("amount", am);
                    }

                    /*
                    if (smsBody.contains("Credit Card")) {
//              TODO:      Skipping credit cards for now
                        continue;
                    } else if (smsBody.contains("requested")) {
                        continue;
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
                        continue;
                    }

                    Pattern p = Pattern.compile("[-]?\\d[\\d,]*[\\.]?[\\d{2}]*");
                    Matcher m = p.matcher(cursor.getString(2));
                    Integer count = 0;
                    while (m.find()) {
                        count++;
                        if (count == 2) {
                            map.put("amount", m.group());
                        }
                    }*/

//                Convert hash map to json
                    String obj = null;
                    try {
                        obj = new JSONObject(map).toString(2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonList.add(obj);
                    list.add(map);
                }
            }

        }
    }
}

package com.sharad.myapp.Utils;

import androidx.appcompat.app.AppCompatActivity;

import com.sharad.myapp.R;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Functions extends AppCompatActivity {

    public static HashMap<String, Integer> supportedBanks = new HashMap<>();

    static {
        supportedBanks.put("SBI", R.drawable.bank_logo_sbi);
        supportedBanks.put("ICICI", R.drawable.bank_logo_icici);
    }

    // Copied from here https://stackoverflow.com/questions/4753251/how-to-go-about-formatting-1200-to-1-2k-in-java/4754243
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "K");
        suffixes.put(1_000_00L, "L");
        suffixes.put(1_000_000_0L, "Cr");
        // suffixes.put(1_000_000_000_000L, "T");
        // suffixes.put(1_000_000_000_000_000L, "P");
        // suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        float truncated = (float) value / (divideBy); //the number part of the output times 10
        String temp = String.format("%.2f", truncated);
        int tempLen = temp.length();
        if (temp.endsWith(".00")) {
            // remove last 3 characters include dot & 2 00s
            temp = temp.replace(".00", "");
        } else if (temp.endsWith("0")) {
            // remove last 1 character
            temp = temp.substring(0, tempLen - 1);
        }
        return temp + suffix;
    }

    public static HashMap<String, String> getCoFormat(long value) {
        HashMap<String, String> myMap = new HashMap<>();

        String myValue = format(value);
        if (myValue.endsWith("Cr")) {
            myMap.put("suffix", "Cr");
            myMap.put("value", myValue.substring(0, myValue.length() - 2));
        } else if (myValue.endsWith("K") || (myValue.endsWith("L"))) {
            myMap.put("suffix", myValue.substring(myValue.length() - 1));
            myMap.put("value", myValue.substring(0, myValue.length() - 1));
        } else {
            myMap.put("suffix", "");
            myMap.put("value", myValue);
        }
        return myMap;
    }

    public static String toTitleCase(String string) {

        // Check if String is null
        if (string == null) {

            return null;
        }

        boolean whiteSpace = true;

        StringBuilder builder = new StringBuilder(string); // String builder to store string
        final int builderLength = builder.length();

        // Loop through builder
        for (int i = 0; i < builderLength; ++i) {

            char c = builder.charAt(i); // Get character at builders position

            if (whiteSpace) {

                // Check if character is not white space
                if (!Character.isWhitespace(c)) {

                    // Convert to title case and leave whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    whiteSpace = false;
                }
            } else if (Character.isWhitespace(c)) {

                whiteSpace = true; // Set character is white space

            } else {

                builder.setCharAt(i, Character.toLowerCase(c)); // Set character to lowercase
            }
        }

        return builder.toString(); // Return builders text
    }

    public static String extractAmountFromSMS(String smsBody) {

        // Get the starting index of Rs or INR
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
            // TODO: This is wrong
            return smsBody;
        } else {
            StringBuilder amount = new StringBuilder();
            for (int i = index; i < smsBody.length(); i++) {
                // Skip the initial key words
                if (smsBody.charAt(i) == 'I' || smsBody.charAt(i) == 'N' || smsBody.charAt(i) == 'R' || smsBody.charAt(i) == 's') {
                    continue;
                }

                // Break the for loop if after the initial keywords there is a space and the next character after space is not a digit
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
                // Remove the starting dot (.)
                am = amount.substring(1);
            } else {
                am = amount.toString();
            }
            // replace , with empty value
            am = am.replace(",", "");
            String[] arr = am.split("\\.");
            if (arr.length >= 2) {
                try {
                    int foo = Integer.parseInt(arr[1]);
                    if (foo == 0) {
                        am = arr[0];
                    }
                } catch (NumberFormatException e) {
                    // Log.d("There might be some character with decimal place in the string", "");
                    am = arr[0];
                }
            }
            return am.trim();
        }
    }
}

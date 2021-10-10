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

    public static float deFormat(String v) {
        if (v.length() > 0) {
            Character lastElement = v.charAt(v.length() - 1);
            if (lastElement == 'K') {
                float remainElem = Float.valueOf(v.substring(0, v.length() - 2));
                return remainElem * 10000;
            }
        }
        return Float.valueOf(v);
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
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
            am = am.replace(",","");
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

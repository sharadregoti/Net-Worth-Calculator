package com.example.myapp;

public class Utils {

    public static final String FILTER_LAST_WEEK = "0";
    public static final String FILTER_LAST_2_WEEK = "1";
    public static final String FILTER_LAST_MONTH = "2";
    public static final String FILTER_LAST_3_MONTH = "3";
    public static final String FILTER_CUSTOM_DATE = "4";
    public static final String FILTER_DEFAULT_DATE = "5";

    public static final String TXN_TYPE_CREDITED = "Credited";
    public static final String TXN_TYPE_DEBITED = "Debited";
    public static final String TXN_TYPE_IN_HAND_CASH = "In Hand Cash";

    public static final String PAYMENT_TYPE_CASH = "Cash";
    public static final String PAYMENT_TYPE_ONLINE = "Online";

    public static final String TXN_CATEGORY_BEAUTY_FITNESS = "Beauty & Fitness";
    public static final String TXN_CATEGORY_BILLS = "Bills";
    public static final String TXN_CATEGORY_EMI = "EMI";
    public static final String TXN_CATEGORY_EATING = "Eating Out";
    public static final String TXN_CATEGORY_EDUCATION = "Education";
    public static final String TXN_CATEGORY_ENTERTAINMENT = "Entertainment";
    public static final String TXN_CATEGORY_GROCERY = "Grocery";
    public static final String TXN_CATEGORY_HOUSEHOLD = "Household";
    public static final String TXN_CATEGORY_INSURANCE = "Insurance";
    public static final String TXN_CATEGORY_INVESTMENTS = "Investments";
    public static final String TXN_CATEGORY_MEDICAL = "Medical";
    public static final String TXN_CATEGORY_MISCELLANEOUS = "Miscellaneous";
    public static final String TXN_CATEGORY_RENT = "Rent";
    public static final String TXN_CATEGORY_SHOPPING = "Shopping";
    public static final String TXN_CATEGORY_TRANSPORT = "Transport";
    public static final String TXN_CATEGORY_TRAVEL = "Travel";

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
}

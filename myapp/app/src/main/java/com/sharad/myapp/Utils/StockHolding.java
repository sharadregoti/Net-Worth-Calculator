package com.sharad.myapp.Utils;

public class StockHolding {
    private String tradingsymbol;
    private String exchange;
    private float instrument_token;
    private String isin;
    private String product;
    private float price;
    private float quantity;
    private float used_quantity;
    private float t1_quantity;
    private float realised_quantity;
    private float authorised_quantity;
    private String authorised_date;
    private float opening_quantity;
    private float collateral_quantity;
    private String collateral_type;
    private boolean discrepancy;
    private float average_price;
    private float last_price;
    private float close_price;
    private float pnl;
    private float day_change;
    private float day_change_percentage;

    public String getTradingsymbol() {
        return tradingsymbol;
    }

    public String getExchange() {
        return exchange;
    }

    public float getInstrument_token() {
        return instrument_token;
    }

    public String getIsin() {
        return isin;
    }

    public String getProduct() {
        return product;
    }

    public float getPrice() {
        return price;
    }

    public float getQuantity() {
        return quantity;
    }

    public float getUsed_quantity() {
        return used_quantity;
    }

    public float getT1_quantity() {
        return t1_quantity;
    }

    public float getRealised_quantity() {
        return realised_quantity;
    }

    public float getAuthorised_quantity() {
        return authorised_quantity;
    }

    public String getAuthorised_date() {
        return authorised_date;
    }

    public float getOpening_quantity() {
        return opening_quantity;
    }

    public float getCollateral_quantity() {
        return collateral_quantity;
    }

    public String getCollateral_type() {
        return collateral_type;
    }

    public boolean isDiscrepancy() {
        return discrepancy;
    }

    public float getAverage_price() {
        return average_price;
    }

    public float getLast_price() {
        return last_price;
    }

    public float getClose_price() {
        return close_price;
    }

    public float getPnl() {
        return pnl;
    }

    public float getDay_change() {
        return day_change;
    }

    public float getDay_change_percentage() {
        return day_change_percentage;
    }
}

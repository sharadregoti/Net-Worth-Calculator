package com.sharad.myapp.Utils;

public class MutualFundHolding {
    private String fund;
    private String folio;
    private float pnl;
    private float xirr;
    private float average_price;
    private float last_price;
    private String last_price_date;
    private float quantity;

    public String getFund() {
        return fund;
    }

    public String getFolio() {
        return folio;
    }

    public float getPnl() {
        return pnl;
    }

    public float getXirr() {
        return xirr;
    }

    public float getAverage_price() {
        return average_price;
    }

    public float getLast_price() {
        return last_price;
    }

    public String getLast_price_date() {
        return last_price_date;
    }

    public float getQuantity() {
        return quantity;
    }
}

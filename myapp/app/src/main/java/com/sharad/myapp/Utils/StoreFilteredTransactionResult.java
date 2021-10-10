package com.sharad.myapp.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoreFilteredTransactionResult {
    public float income = 0;
    public float expense = 0;
    public float inHandCash = 0;
    public List<HashMap<String, Object>> dbList = new ArrayList<>();

    public StoreFilteredTransactionResult(float income, float expense, float inHandCash, List<HashMap<String, Object>> filterdList) {
        this.income = income;
        this.expense = expense;
        this.inHandCash = inHandCash;
        this.dbList = filterdList;
    }

    public float getIncome() {
        return this.income;
    }

    public float getExpense() {
        return this.expense;
    }

    public float getInHandCash() {
        return this.inHandCash;
    }

    public List<HashMap<String, Object>> getFilteredList() {
        return this.dbList;
    }
}

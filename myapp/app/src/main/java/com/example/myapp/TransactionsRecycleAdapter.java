package com.example.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class TransactionsRecycleAdapter extends RecyclerView.Adapter<TransactionsRecycleAdapter.RecycleViewHolder> {

    private Context context;
    private final ProcessSMS processSMS;

    private static final boolean isDefaultFilterEnabled = true;

    private DatabaseHelper dh;
    private ActivityResultLauncher<Intent> hdta;


    public TransactionsRecycleAdapter(Context context, ProcessSMS processSMS, ActivityResultLauncher<Intent> hdta) {
        this.processSMS = processSMS;
        this.context = context;
        this.hdta = hdta;
        this.dh = new DatabaseHelper(context);
    }

    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_transactions_item_page, parent, false);
        return new RecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransactionsRecycleAdapter.RecycleViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat("##,##,##,##,##,##,##0.#");
        Float price = Float.parseFloat(processSMS.getAmount(position));

        String amount = "";
        String date = processSMS.getDate(position);
        String expenseMerch = processSMS.getTransactionPerson(position);
        String tags = processSMS.getTags(position);
        String bank = processSMS.getBank(position);
        String byFromPerson = "";
        int amountTextColor = 0;

        if (processSMS.getType(position).equals(Utils.TXN_TYPE_CREDITED)) {
            amount = "+ ₹" + df.format(price);
            byFromPerson = "Credited by";
            amountTextColor = this.context.getColor(R.color.green_money);
            holder.amount.setText(amount);
            holder.amount.setTextColor(amountTextColor);
            holder.byFromPerson.setText(byFromPerson);
        } else {
            amount = "- ₹" + df.format(price);
            byFromPerson = "Debited to";
            amountTextColor = this.context.getColor(R.color.red_money);
            holder.amount.setText(amount);
            holder.amount.setTextColor(amountTextColor);
            holder.byFromPerson.setText(byFromPerson);
        }
        holder.date.setText(date);
        holder.expenseMerch.setText(expenseMerch);

        holder.tChipGroup.removeAllViews();
        String[] arr = new String[]{};
        arr = tags.split(" ", -1);
        for (int i = 0; i < arr.length; i++) {
            Chip chip = new Chip(this.context);
            chip.setText(arr[i]);
            holder.tChipGroup.addView(chip);
        }

        String finalAmount = amount;
        String finalByFromPerson = byFromPerson;
        int finalAmountTextColor = amountTextColor;

        holder.parentLayout.setOnClickListener((view -> {
            Intent intent = new Intent(context, ActivityDetailedTransactionItem.class);
            intent.putExtra("amount", finalAmount);
            intent.putExtra("date", date);
            intent.putExtra("expense_merchant", expenseMerch);
            intent.putExtra("tags", tags);
            intent.putExtra("bank", bank);
            intent.putExtra("by_from_person", finalByFromPerson);
            intent.putExtra("amount_text_color", finalAmountTextColor);
            intent.putExtra("transaction_type", processSMS.getType(position));
            intent.putExtra("payment_type", processSMS.getPaymentType(position));
            intent.putExtra("category", processSMS.getCategory(position));
            intent.putExtra("notes", processSMS.getNotes(position));
            intent.putExtra("photo", processSMS.getPhoto(position));
            intent.putExtra("sms_message", processSMS.getSMSBody(position));
            intent.putExtra("id", processSMS.getRowId(position));
            hdta.launch(intent);
//            this.processSMS.filterList();
        }));
    }

    @Override
    public int getItemCount() {
        return processSMS.getMessages().size();
    }

    public class RecycleViewHolder extends RecyclerView.ViewHolder {
        TextView amount, date, expenseMerch, byFromPerson;
        LinearLayout parentLayout;
        ChipGroup tChipGroup;

        public RecycleViewHolder(View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.reusable_card_view_sms_amount);
            date = itemView.findViewById(R.id.reusable_card_view_sms_date);
            tChipGroup = itemView.findViewById(R.id.reusable_card_view_tags_chip_group);
            expenseMerch = itemView.findViewById(R.id.reusable_card_view_sms_expense_merchant);
            byFromPerson = itemView.findViewById(R.id.reusable_card_view_sms_by_from_whom);
            parentLayout = itemView.findViewById(R.id.row_item_layout);
        }
    }
}

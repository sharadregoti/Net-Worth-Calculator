package com.example.myapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;

public class TransactionsRecycleAdapter extends RecyclerView.Adapter<TransactionsRecycleAdapter.RecycleViewHolder> {

    private Context context;
    private final ProcessSMS processSMS;

    private static final boolean isDefaultFilterEnabled = true;

    private DatabaseHelper dh;


    public TransactionsRecycleAdapter(Context context, ProcessSMS processSMS) {
        this.processSMS = processSMS;
        this.context = context;
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
        if (processSMS.getType(position).equals("credited")) {
            holder.amount.setText("+ ₹" + df.format(price));
            holder.amount.setTextColor(Color.parseColor("#118C4F"));
            holder.byFromPeron.setText("Credited by");
        } else {
            holder.amount.setText("- ₹" + df.format(price));
            holder.amount.setTextColor(Color.parseColor("#db1115"));
            holder.byFromPeron.setText("Debited to");
        }
        holder.date.setText(processSMS.getDate(position));
//        holder.bank.setText(data.getBank(position));
        holder.transactionPerson.setText(processSMS.getTransactionPerson(position));
//        holder.tags.setText(data.getTags(position));
        holder.parentLayout.setOnClickListener((view -> {
            Intent intent = new Intent(context, ActivityDetailedTransactionItem.class);
            context.startActivity(intent);
        }));
    }

    @Override
    public int getItemCount() {
        return processSMS.getMessages().size();
    }

    public class RecycleViewHolder extends RecyclerView.ViewHolder {
        TextView amount, date, bank, transactionPerson, tags, byFromPeron;
        LinearLayout parentLayout;

        public RecycleViewHolder(View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.sms_amount);
            date = itemView.findViewById(R.id.sms_date);
//            bank = itemView.findViewById(R.id.sms_bank);
            transactionPerson = itemView.findViewById(R.id.sms_transaction_person);
            byFromPeron = itemView.findViewById(R.id.sms_by_from_whom);
//            tags = itemView.findViewById(R.id.sms_tags);
            parentLayout = itemView.findViewById(R.id.row_item_layout);
        }
    }
}

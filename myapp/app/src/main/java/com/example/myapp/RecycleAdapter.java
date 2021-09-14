package com.example.myapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.RecycleViewHolder> {

    private final Data data;
    public RecycleAdapter(Data data) {
        this.data = data;
    }

    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_layout,parent,false);
        return new RecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecycleAdapter.RecycleViewHolder holder, int position) {
        if (data.getType(position) == "credited") {
            holder.amount.setText("+ ₹"+data.getAmount(position));
            holder.amount.setTextColor(Color.GREEN);
        } else {
            holder.amount.setText("- ₹"+data.getAmount(position));
            holder.amount.setTextColor(Color.RED);
        }
        holder.date.setText(data.getDate(position));
        holder.bank.setText(data.getBank(position));
        holder.transactionPerson.setText(data.getTransactionPerson(position));
        holder.tags.setText(data.getTags(position));
    }

    @Override
    public int getItemCount() {
        return data.getMessages().size();
    }

    public class RecycleViewHolder extends RecyclerView.ViewHolder {
        TextView amount,date,bank,transactionPerson,tags;
        public RecycleViewHolder(View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.sms_amount);
            date = itemView.findViewById(R.id.sms_date);
            bank = itemView.findViewById(R.id.sms_bank);
            transactionPerson = itemView.findViewById(R.id.sms_transaction_person);
            tags = itemView.findViewById(R.id.sms_tags);
        }
    }
}

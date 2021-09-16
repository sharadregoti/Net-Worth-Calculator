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

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.RecycleViewHolder> {

    private Context context;
    private final Data data;
    public RecycleAdapter(Context context, Data data) {
        this.data = data;
        this.context = context;
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
            holder.byFromPeron.setText("Credited by");
        } else {
            holder.amount.setText("- ₹"+data.getAmount(position));
            holder.amount.setTextColor(Color.RED);
            holder.byFromPeron.setText("Debited to");
        }
        holder.date.setText(data.getDate(position));
//        holder.bank.setText(data.getBank(position));
        holder.transactionPerson.setText(data.getTransactionPerson(position));
//        holder.tags.setText(data.getTags(position));
        holder.parentLayout.setOnClickListener((view -> {
            Intent intent = new Intent(context, ViewRowItemActivity.class);
//            intent.putExtra("")
            context.startActivity(intent);
        }));
    }

    @Override
    public int getItemCount() {
        return data.getMessages().size();
    }

    public class RecycleViewHolder extends RecyclerView.ViewHolder {
        TextView amount,date,bank,transactionPerson,tags, byFromPeron;
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

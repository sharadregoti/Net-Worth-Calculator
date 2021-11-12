package com.sharad.myapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sharad.myapp.R;
import com.sharad.myapp.Utils.Functions;
import com.sharad.myapp.Utils.MutualFundHolding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MutualFundRecycleAdapter extends RecyclerView.Adapter<MutualFundRecycleAdapter.RecycleViewHolder> {

    private List<MutualFundHolding> mfhs;

    public MutualFundRecycleAdapter(List<MutualFundHolding> mfhs) {
        this.mfhs = mfhs;
    }

    @Override
    public @NotNull
    MutualFundRecycleAdapter.RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_mutal_fund_list_page, parent, false);
        return new RecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MutualFundRecycleAdapter.RecycleViewHolder holder, int position) {
        holder.mutualFundName.setText(mfhs.get(position).getFund());
        float totalCurrentAmount = mfhs.get(position).getQuantity() * mfhs.get(position).getLast_price();
        float totalInvestedAmount = mfhs.get(position).getQuantity() * mfhs.get(position).getAverage_price();

        float growthAmount = totalCurrentAmount - totalInvestedAmount;
        float growthPercentage = (growthAmount / totalInvestedAmount) * 100;
        if (growthAmount < 0) {
            holder.iArrow.setImageResource(R.drawable.arrow_down);
            holder.growthAmount.setTextColor(ContextCompat.getColor(holder.growthAmount.getContext(), R.color.red_money));

        } else {
            holder.iArrow.setImageResource(R.drawable.arrow_up);
            holder.growthAmount.setTextColor(ContextCompat.getColor(holder.growthAmount.getContext(), R.color.green_money));
        }

        holder.currentBalance.setText("₹" + Functions.format((long) totalCurrentAmount));
        holder.investedAmount.setText("₹" + Functions.format((long) totalInvestedAmount));
        holder.growthAmount.setText(String.format("%.2f%% (₹ %s)", growthPercentage, Functions.format((long) growthAmount)));
    }

    @Override
    public int getItemCount() {
        return mfhs.size();
    }


    public class RecycleViewHolder extends RecyclerView.ViewHolder {
        TextView mutualFundName, currentBalance, investedAmount, growthAmount;
        ImageView iArrow;

        public RecycleViewHolder(View itemView) {
            super(itemView);
            mutualFundName = itemView.findViewById(R.id.activity_mutual_fund_item_page_fund_name);
            currentBalance = itemView.findViewById(R.id.activity_mutual_fund_item_page_current_value_amount_text);
            investedAmount = itemView.findViewById(R.id.activity_mutual_fund_item_page_invested_value_amount_text);
            growthAmount = itemView.findViewById(R.id.activity_mutual_fund_item_page_current_value_growth_amount_text);
            iArrow = itemView.findViewById(R.id.activity_mutual_item_page_arrow_image);
        }
    }
}

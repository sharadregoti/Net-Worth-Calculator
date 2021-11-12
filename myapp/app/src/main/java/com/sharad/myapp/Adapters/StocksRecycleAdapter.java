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
import com.sharad.myapp.Utils.StockHolding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StocksRecycleAdapter extends RecyclerView.Adapter<StocksRecycleAdapter.RecycleViewHolder>{
    private List<StockHolding> sh;

    public StocksRecycleAdapter(List<StockHolding> sh) {
        this.sh = sh;
    }

    @Override
    public @NotNull
    StocksRecycleAdapter.RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_stock_list_page, parent, false);
        return new RecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StocksRecycleAdapter.RecycleViewHolder holder, int position) {
        holder.stockName.setText(sh.get(position).getTradingsymbol());
        float totalCurrentAmount = sh.get(position).getQuantity() * sh.get(position).getLast_price();
        float totalInvestedAmount = sh.get(position).getQuantity() * sh.get(position).getAverage_price();

        float growthAmount = totalCurrentAmount - totalInvestedAmount;
        float growthPercentage = (growthAmount / totalInvestedAmount) * 100;
        if (growthAmount < 0) {
            holder.iArrow.setImageResource(R.drawable.arrow_down);
            holder.growthAmount.setTextColor(ContextCompat.getColor(holder.growthAmount.getContext(), R.color.red_money));

        } else {
            holder.iArrow.setImageResource(R.drawable.arrow_up);
            holder.growthAmount.setTextColor(ContextCompat.getColor(holder.growthAmount.getContext(), R.color.green_money));
        }

        holder.currentValueText.setText("₹" + Functions.format((long) totalCurrentAmount));
        holder.quantityText.setText(String.format("%d",(int) sh.get(position).getQuantity()));
        holder.growthAmount.setText(String.format("%.2f%% (₹ %s)", growthPercentage, Functions.format((long) growthAmount)));
    }


    @Override
    public int getItemCount() {
        return sh.size();
    }

    public class RecycleViewHolder extends RecyclerView.ViewHolder {
        TextView stockName, quantityText, currentValueText, growthAmount;
        ImageView iArrow;

        public RecycleViewHolder(View itemView) {
            super(itemView);
            stockName = itemView.findViewById(R.id.activity_stocks_item_page_fund_name);
            quantityText = itemView.findViewById(R.id.activity_stocks_item_page_quantity_amount_text);
            currentValueText = itemView.findViewById(R.id.activity_stocks_item_page_current_value_amount_text);
            growthAmount = itemView.findViewById(R.id.activity_stocks_item_page_gain_or_loss_amount_text);
            iArrow = itemView.findViewById(R.id.activity_stock_page_arrow_image);
        }
    }

}

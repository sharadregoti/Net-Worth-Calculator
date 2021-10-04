package com.example.myapp.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.Activities.ActivityBanksRefreshBalance;
import com.example.myapp.R;
import com.example.myapp.Utils.DatabaseHelper;
import com.example.myapp.Utils.Functions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

public class BankRecycleAdapter extends RecyclerView.Adapter<BankRecycleAdapter.RecycleViewHolder> {

    private ActivityResultLauncher<Intent> arl;
    private DatabaseHelper.BankAccountsHelper data;
    private BankAdapterListener mListener;

    public BankRecycleAdapter(DatabaseHelper.BankAccountsHelper data, ActivityResultLauncher<Intent> arl, BankAdapterListener mList) {
        this.mListener = mList;
        this.data = data;
        this.arl = arl;
    }

    @Override
    public @NotNull RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_banks_item_page, parent, false);
        return new RecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BankRecycleAdapter.RecycleViewHolder holder, int position) {
        String bankName = data.getBankName(position);
        holder.bankName.setText(bankName);
        holder.bankBalance.setText("₹" + Functions.format((long) data.getBankBalance(position)));
        holder.imBankLogo.setImageResource(Functions.supportedBanks.get(bankName));

        // Menu button onClickListener
        holder.imPopUpMenu.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(holder.imPopUpMenu.getContext(), view);
            popup.inflate(R.menu.bank_overflow_menu);
            popup.show();

            //Set on click listener for the menu
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.bank_overflow_delete_account) {
                    MaterialAlertDialogBuilder madb = new MaterialAlertDialogBuilder(holder.imPopUpMenu.getContext());
                    madb.setTitle("Delete this account").setMessage("Are you sure you want to delete this acount?\n\nAll the transactions linked to this account will be deleted from the app").setNegativeButton(R.string.bank_delete_account_no, (dialogInterface, i) -> {

                    }).setPositiveButton(R.string.bank_delete_account_yes, (dialogInterface, i) -> {
                        mListener.onDeleteButtonPressed(bankName);
                    }).show();
                }
                if (item.getItemId() == R.id.bank_overflow_refresh_balance) {
                    Intent intent = new Intent(holder.imPopUpMenu.getContext(), ActivityBanksRefreshBalance.class);
                    intent.putExtra("bank_name", bankName);
                    arl.launch(intent);
                }
                return false;
            });
        });
    }

    @Override
    public int getItemCount() {
        return data.getBankAccountsCount();
    }

    public interface BankAdapterListener {
        void onDeleteButtonPressed(String bankName);
    }

    public class RecycleViewHolder extends RecyclerView.ViewHolder {
        TextView bankName, bankBalance;
        ImageView imPopUpMenu, imBankLogo;

        public RecycleViewHolder(View itemView) {
            super(itemView);
            bankName = itemView.findViewById(R.id.activity_banks_item_page_bank_name_text);
            bankBalance = itemView.findViewById(R.id.activity_banks_item_page_amount_text);
            imPopUpMenu = itemView.findViewById(R.id.activity_banks_item_page_popup_menu_image);
            imBankLogo = itemView.findViewById(R.id.activity_banks_item_page_bank_logo_image);
        }
    }
}
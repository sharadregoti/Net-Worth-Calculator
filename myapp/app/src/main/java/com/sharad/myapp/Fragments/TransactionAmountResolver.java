package com.sharad.myapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.sharad.myapp.R;

public class TransactionAmountResolver extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;

    int amount = 0;

    public TransactionAmountResolver(int amount) {
        this.amount = amount;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transaction_amount_resolver, container, false);

        MaterialButton mb = v.findViewById(R.id.mismatch_resolver_save_button);
        TextView tMsg = v.findViewById(R.id.mismatch_resolver_text);
        tMsg.setText(String.format("Specify how did you receive extra ₹%d", amount));
        EditText etMsg = v.findViewById(R.id.mismatch_resolver_edit_text);
        TextInputLayout tilMsg = v.findViewById(R.id.mismatch_resolver_text_input_layout);

        // Disable validation errors on focus
        etMsg.setOnFocusChangeListener((view, b) -> {
            if (b) {
                tilMsg.setErrorEnabled(false);
            }
        });

        mb.setOnClickListener(view -> {
            String msg = etMsg.getText().toString();

            if (msg.isEmpty()) {
                tilMsg.setError("Message is Mandatory");
                return;
            }
            dismiss();
            mListener.onSaveButtonClicked(msg);
        });

        return v;
    }

    public interface BottomSheetListener {
        void onSaveButtonClicked(String msg);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (TransactionAmountResolver.BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement bottom sheet listener");
        }
    }
}
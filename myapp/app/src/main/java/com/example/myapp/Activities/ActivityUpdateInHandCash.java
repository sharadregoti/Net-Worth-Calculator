package com.example.myapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.Utils.DatabaseHelper;
import com.example.myapp.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class ActivityUpdateInHandCash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_in_hand_cash);

        DatabaseHelper dh = new DatabaseHelper(this);

        Intent intent = getIntent();
        String amt = intent.getStringExtra("amount");

        EditText etAmount = findViewById(R.id.update_in_hand_cash_amount_edit_text);
        etAmount.setText(amt);
        MaterialButton mbSaveBtn = findViewById(R.id.update_in_hand_cash_save_button);

        mbSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = etAmount.getText().toString();
                if (amount.length() == 0) {
                    Snackbar sb = Snackbar.make(mbSaveBtn, "Amount cannot be empty", Snackbar.LENGTH_SHORT);
                    sb.show();
                    return;
                }

                Intent intent = getIntent();
                intent.putExtra("amount", amount);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        // Set back button
        MaterialToolbar mt = findViewById(R.id.top_action_bar);
        mt.setTitle("Update In Hand Cash");
        setSupportActionBar(mt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
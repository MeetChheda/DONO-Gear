package com.example.payment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ShippingActivity extends AppCompatActivity {

    EditText nameText, emailText, addressText, stateText, zipcodeText;
    Button doneButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping);
        init();

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!filled()) {
                    Toast.makeText(ShippingActivity.this, "Please fill out all the details", Toast.LENGTH_SHORT).show();
                    return;
                }
                MainActivity.params.put("name", nameText.getText().toString());
                MainActivity.params.put("email", emailText.getText().toString());
                MainActivity.params.put("address", addressText.getText().toString());
                MainActivity.params.put("city_state", stateText.getText().toString());
                MainActivity.params.put("zip", zipcodeText.getText().toString());
                onBackPressed();
            }
        });
    }

    private boolean filled() {
        return nameText.getText().toString().length() > 0 &&
                emailText.getText().toString().length() > 0 &&
                addressText.getText().toString().length() > 0 &&
                stateText.getText().toString().length() > 0 &&
                zipcodeText.getText().toString().length() > 0;
    }

    private void init() {
        doneButton = findViewById(R.id.done);
        nameText = findViewById(R.id.input_name);
        emailText = findViewById(R.id.input_email);
        addressText = findViewById(R.id.input_address);
        stateText = findViewById(R.id.input_state);
        zipcodeText = findViewById(R.id.input_zipcode);
    }
}

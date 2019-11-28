package com.example.donogear.actionpages;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.donogear.R;

public class ShippingDetailsActivity extends AppCompatActivity {

    EditText nameText, emailText, addressText, stateText, zipcodeText;
    Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_details);
        init();

        doneButton.setOnClickListener(view -> {
            if (!filled()) {
                Toast.makeText(this, "Please fill out all the details", Toast.LENGTH_SHORT).show();
                return;
            }
            PaymentActivity.params.put("name", nameText.getText().toString());
            PaymentActivity.params.put("email", emailText.getText().toString());
            PaymentActivity.params.put("address", addressText.getText().toString());
            PaymentActivity.params.put("city_state", stateText.getText().toString());
            PaymentActivity.params.put("zip", zipcodeText.getText().toString());
            onBackPressed();
        });
    }

    /**
     * Check if all fields have been filled
     * @return - whether all fields are filled or not
     */
    private boolean filled() {
        return nameText.getText().toString().length() > 0 &&
                emailText.getText().toString().length() > 0 &&
                addressText.getText().toString().length() > 0 &&
                stateText.getText().toString().length() > 0 &&
                zipcodeText.getText().toString().length() > 0;
    }

    /**
     * Initialize layout and variables
     */
    private void init() {
        doneButton = findViewById(R.id.done);
        nameText = findViewById(R.id.input_name);
        emailText = findViewById(R.id.input_email);
        addressText = findViewById(R.id.input_address);
        stateText = findViewById(R.id.input_state);
        zipcodeText = findViewById(R.id.input_zipcode);

        if (PaymentActivity.params.containsKey("name")) {
            nameText.setText(PaymentActivity.params.get("name").toString());
            nameText.setEnabled(false);
        }
        if (PaymentActivity.params.containsKey("email")) {
            emailText.setText(PaymentActivity.params.get("email").toString());
            emailText.setEnabled(false);
        }
        if (PaymentActivity.params.containsKey("address")) {
            addressText.setText(PaymentActivity.params.get("address").toString());
        }
        if (PaymentActivity.params.containsKey("zip")) {
            zipcodeText.setText(PaymentActivity.params.get("zip").toString());
        }
        if (PaymentActivity.params.containsKey("city_state")) {
            stateText.setText(PaymentActivity.params.get("address").toString());
        }
    }
}


package com.example.donogear.actionpages;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.donogear.R;
import com.parse.ParseCloud;
import com.parse.ParseUser;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;

import java.util.HashMap;
import java.util.Map;

import static com.example.donogear.utils.Constants.CARD_TOKEN;
import static com.example.donogear.utils.Constants.CUSTOMER_ID;
import static com.example.donogear.utils.Constants.PUBLISHABLE_KEY;

public class PaymentInfoActivity extends AppCompatActivity {
    private static final String NAME = "name";
    private static final String SAVE_CREDIT_CARD_SERVICE_NAME = "saveCreditCard";

    private ProgressDialog progress;
    private CardMultilineWidget cardMultilineWidget;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_info);
        cardMultilineWidget = findViewById(R.id.savedCardWidget);
        Bundle bundle = getIntent().getExtras();

        progress = new ProgressDialog(this);

        Button saveBtn = findViewById(R.id.save_payment_btn);
        saveBtn.setOnClickListener(v -> {
            if (cardMultilineWidget.getCard() != null && cardMultilineWidget.getCard().validateCard()) {
                startProgress();

                new Stripe(this, PUBLISHABLE_KEY).createToken(
                        cardMultilineWidget.getCard(),
                        new ApiResultCallback<Token>() {

                            public void onError(Exception e) {
                                Log.e("PaymentInfoActivity", "Error saving card: " + e.getMessage(), e);
                                Toast.makeText(PaymentInfoActivity.this,
                                        "Error saving card", Toast.LENGTH_LONG).show();
                            }

                            public void onSuccess(Token token) {
                                saveCreditCard(token);
                            }
                        });


            } else {
                Toast.makeText(getApplicationContext(), "Must fill all required fields",
                        Toast.LENGTH_LONG).show();
            }
        });

        Button cancelBtn = findViewById(R.id.cancel_payment_update_btn);
        cancelBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyAccountActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

    }

    /**
     * Returns the user to the My Account page
     */
    private void returnToMyAccount() {
        Intent intent = new Intent(this, MyAccountActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Saves the user's credit card to the payment service
     *
     * @param cardToken The token holding the credit card information
     */
    private void saveCreditCard(Token cardToken) {
        ParseUser user = ParseUser.getCurrentUser();
        Map<String, Object> params = new HashMap<>();
        params.put(CARD_TOKEN, cardToken.getId());
        params.put(NAME, user.getUsername());
        String customerId = (String) user.get(CUSTOMER_ID);

        // If the user has not previously saved a credit card, they do not have a customer ID. A new
        // one will be generated if no param is passed
        if (customerId != null) {
            params.put(CUSTOMER_ID, customerId);
        }

        ParseCloud.callFunctionInBackground(SAVE_CREDIT_CARD_SERVICE_NAME, params, (response, e) -> {
            progress.dismiss();

            if (e == null) {
                Log.d("Cloud Response", "Successfully saved card " + response.toString());
                // Add the customer ID to the Parse user to be used in the future
                user.put(CUSTOMER_ID, response.toString());
                user.saveInBackground();
                Toast.makeText(getApplicationContext(),
                        "Payment Successfully Saved",
                        Toast.LENGTH_LONG).show();
            } else {
                Log.e("Cloud Response", "Error saving card: " + e, e);
                Toast.makeText(getApplicationContext(),
                        "Failed to Save Payment",
                        Toast.LENGTH_LONG).show();
            }

            returnToMyAccount();
        });
    }

    /**
     * Progress dialog for UI
     */
    private void startProgress(){
        progress.setTitle("Saving Card Information");
        progress.setMessage("Please Wait");
        progress.show();
    }
}

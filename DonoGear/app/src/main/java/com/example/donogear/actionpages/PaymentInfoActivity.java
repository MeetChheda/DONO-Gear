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

import static com.example.donogear.utils.Constants.CUSTOMER_ID;
import static com.example.donogear.utils.Constants.PUBLISHABLE_KEY;

public class PaymentInfoActivity extends AppCompatActivity {
    private ProgressDialog progress;
    private CardMultilineWidget cardMultilineWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_info);
        cardMultilineWidget = findViewById(R.id.savedCardWidget);
        Bundle bundle = getIntent().getExtras();
        String customerId = bundle.getString(CUSTOMER_ID);
        String cardId = bundle.getString(CUSTOMER_ID);

        progress = new ProgressDialog(this);

        Button saveBtn = findViewById(R.id.save_payment_btn);
        saveBtn.setOnClickListener(v -> {
            if (cardMultilineWidget.getCard() != null && cardMultilineWidget.getCard().validateCard()) {
                new Stripe(this, PUBLISHABLE_KEY).createToken(
                        cardMultilineWidget.getCard(),
                        new ApiResultCallback<Token>() {

                            public void onError(Exception error) {
                                Toast.makeText(PaymentInfoActivity.this,
                                        "Stripe -" + error.toString(),
                                        Toast.LENGTH_LONG).show();
                            }

                            public void onSuccess(Token token) {
                                handleCustomer(customerId, cardId, token);
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

    private void returnToMyAccount() {
        Intent intent = new Intent(this, MyAccountActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void handleCustomer(String customerId, String previousCardId, Token cardToken) {
        Map<String, Object> customerParams = new HashMap<>();
        customerParams.put("cardToken", cardToken.getId());
        customerParams.put("name", ParseUser.getCurrentUser().getUsername());

        if (customerId != null) {
            customerParams.put("customerId", customerId);
        }

        ParseCloud.callFunctionInBackground("saveCard", customerParams, (response, e) -> {
            progress.dismiss();

            if (e == null) {
                Log.d("Cloud Response", "Successfully saved card " + response.toString());
                ParseUser user = ParseUser.getCurrentUser();
                user.put("customerId", response.toString());
                Toast.makeText(getApplicationContext(),
                        "Payment Successfully Saved",
                        Toast.LENGTH_LONG).show();
            } else {

                Log.d("Cloud Response", "Error saving card: " + e);
                Toast.makeText(getApplicationContext(),
                        "Failed to Save Payment",
                        Toast.LENGTH_LONG).show();
            }

            returnToMyAccount();
        });

//        try {
//            final Customer customer = null;
//
//            if (customer != null && previousCardId != null) {
//                savedCard = (Card) customer.getSources().retrieve(previousCardId);
//
//                new AlertDialog.Builder(PaymentInfoActivity.this)
//                        .setTitle("Delete Payment")
//                        .setMessage("Are you sure you want to overwrite the saved payment?")
//                        .setPositiveButton("YES", (dialogInterface, i) -> overwriteSavedCard(customer, customerParams))
//                        .setNegativeButton("NO", null)
//                        .show();
//
//            } else {
//                Customer.create(customerParams);
////                Map<String, Object> cardParams = new HashMap<>();
////                cardParams.put("payment_method", cardMultilineWidget.getCard());
////                customer.getSources().create(cardParams);
//                returnToMyAccount();
//            }
//
//        } catch (StripeException e) {
//            Log.e("PaymentInfoActivity", "Error saving payment information: " + e.getMessage(), e);
//            Toast.makeText(getApplicationContext(), "Unable to save payment",
//                    Toast.LENGTH_LONG).show();
//        }
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

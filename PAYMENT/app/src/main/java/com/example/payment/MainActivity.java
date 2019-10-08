package com.example.payment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private Card card;
    private CardMultilineWidget cardMultilineWidget;
    public static final String PUBLISHABLE_KEY = "pk_test_BS3lFguklXYlFf8oc5Ul9kg300mSOJkSYT";
    public static HashMap<String, Object> params;
    private ProgressDialog progress;
    private Button purchase, reset, shipping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardMultilineWidget = findViewById(R.id.card_widget);
        progress = new ProgressDialog(this);

        init();
        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buy();
            }
        });
        shipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card = cardMultilineWidget.getCard();
                startActivity(new Intent(getBaseContext(), ShippingActivity.class));
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardMultilineWidget.clear();
            }
        });
    }

    public void onSaveInstanceState (Bundle savedInstancestate) {
        super.onSaveInstanceState(savedInstancestate);
        card = cardMultilineWidget.getCard();
        if (card != null) {
            savedInstancestate.putString("cardNumber", card.getNumber());
            savedInstancestate.putInt("month", card.getExpMonth());
            savedInstancestate.putInt("year", card.getExpYear());
            savedInstancestate.putString("CVV", card.getCVC());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (cardMultilineWidget.getCard() != null) {
            cardMultilineWidget.setCardNumber(savedInstanceState.getString("cardNumber"));
            cardMultilineWidget.setCvcCode(savedInstanceState.getString("CVV"));
            int month = savedInstanceState.getInt("month");
            int year = savedInstanceState.getInt("year");
            cardMultilineWidget.setExpiryDate(month, year);
        }
    }

    private void init() {
        purchase = findViewById(R.id.purchase);
        shipping = findViewById(R.id.shipping);
        reset = findViewById(R.id.reset);

        params = new HashMap<>();
        params.put("name","Meet Chheda");
        params.put("email","meet@gmail.com");
        params.put("address","Los Angeles");
        params.put("zip","90007");
        params.put("city_state","CA");
        params.put("ItemName", "test");
    }

    private void buy(){
        card = cardMultilineWidget.getCard();
        if (card == null) {
            Toast.makeText(MainActivity.this, "Enter valid details", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean validation = card.validateCard();
        if(validation){
            startProgress("Validating Credit Card");
            new Stripe(this, PUBLISHABLE_KEY).createToken(
                    card,
                    new ApiResultCallback<Token>() {

                        public void onError(Exception error) {
                            Toast.makeText(MainActivity.this,
                                    "Stripe -" + error.toString(),
                                    Toast.LENGTH_LONG).show();
                        }

                        public void onSuccess(Token token) {
                            System.out.println("Token: " + token);
                            finishProgress();
                            charge(token);
                        }
                    });
        } else if (!card.validateNumber()) {
            Toast.makeText(MainActivity.this,
                    "Stripe - The card number that you entered is invalid",
                    Toast.LENGTH_LONG).show();
        } else if (!card.validateExpiryDate()) {
            Toast.makeText(MainActivity.this,
                    "Stripe - The expiration date that you entered is invalid",
                    Toast.LENGTH_LONG).show();
        } else if (!card.validateCVC()) {
            Toast.makeText(MainActivity.this,
                    "Stripe - The CVC code that you entered is invalid",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this,
                    "Stripe - The card details that you entered are invalid",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void charge(Token cardToken){
        params.put("cardToken", cardToken.getId());
        startProgress("Purchasing Item");
        ParseCloud.callFunctionInBackground("purchaseItem", params, new FunctionCallback<Object>() {
            public void done(Object response, ParseException e) {
                finishProgress();
                if (e == null) {
                    Log.d("Cloud Response", "There were no exceptions! " + response.toString());
                    Toast.makeText(getApplicationContext(),
                            "Item Purchased Successfully ",
                            Toast.LENGTH_LONG).show();
                } else {
                    System.out.println(e);
                    Log.d("Cloud Response", "Exception: " + e);
                    Toast.makeText(getApplicationContext(),
                            e.getMessage().toString(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void startProgress(String title){
        progress.setTitle(title);
        progress.setMessage("Please Wait");
        progress.show();
    }

    private void finishProgress(){
        progress.dismiss();


    }
}

package com.example.donogear.actionpages;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donogear.R;
import com.parse.ParseCloud;
import com.parse.ParseUser;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.example.donogear.utils.Constants.BUY_NOW;
import static com.example.donogear.utils.Constants.ERROR_BID_MESSAGE;
import static com.example.donogear.utils.Constants.ERROR_BID_TITLE;
import static com.example.donogear.utils.Constants.ERROR_SHIP;
import static com.example.donogear.utils.Constants.INCOMPLETE_DETAILS;
import static com.example.donogear.utils.Constants.INVALID_CARD_CVC;
import static com.example.donogear.utils.Constants.INVALID_CARD_DETAILS;
import static com.example.donogear.utils.Constants.INVALID_CARD_EXP;
import static com.example.donogear.utils.Constants.INVALID_CARD_NUMBER;
import static com.example.donogear.utils.Constants.ITEM_ID;
import static com.example.donogear.utils.Constants.ITEM_NAME;
import static com.example.donogear.utils.Constants.PUBLISHABLE_KEY;
import static com.example.donogear.utils.Constants.RAFFLE_COUNT;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "PaymentActivity";
    private static String itemName;
    private static String itemId;
    private static int raffle_count;
    private static int itemPrice;

    private Card card;
    private CardMultilineWidget cardMultilineWidget;
    public static HashMap<String, Object> params;
    private ProgressDialog progress;
    private Button purchase, reset, shipping;
    private List<String> keys;
    private List<String> missingKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        itemId = bundle.getString(ITEM_ID);
        itemName = bundle.getString(ITEM_NAME);
        itemPrice = bundle.getInt(BUY_NOW);
        raffle_count = bundle.getInt(RAFFLE_COUNT);
        initializeLayout();
        purchase.setOnClickListener(v -> buy());
        shipping.setOnClickListener(view -> {
            card = cardMultilineWidget.getCard();
            startActivity(new Intent(getApplicationContext(), ShippingDetailsActivity.class));
        });
        reset.setOnClickListener(view -> cardMultilineWidget.clear());
    }

    private void initializeLayout() {
        TextView title = findViewById(R.id.payment_title);
        TextView amount = findViewById(R.id.payment_amount);
        title.setText(itemName);
        amount.setText("$" +itemPrice);

        keys = Arrays.asList(
                "email", "name", "address", "zip", "city_state"
        );

        TextView cancel = findViewById(R.id.cancel_checkout);
        cancel.setOnClickListener(view -> finish());

        cardMultilineWidget = findViewById(R.id.card_widget);
        progress = new ProgressDialog(this);

        purchase = findViewById(R.id.purchase);
        shipping = findViewById(R.id.shipping);
        reset = findViewById(R.id.clear_form);
        getUserDetails();
        if (!validShippingDetails()) {
            purchase.setAlpha(0.25f);
        }
    }

    @Override
    public void onResume() {
        if (!validShippingDetails()) {
            purchase.setAlpha(0.25f);
        } else {
            purchase.setAlpha(1f);
        }
        super.onResume();
    }

    /**
     * Validate if user has entered all fields
     * @return
     */
    private boolean validShippingDetails() {
        missingKeys = new ArrayList<>();
        for (String key: keys) {
            if (!params.containsKey(key)) {
                missingKeys.add(key);
            }
        }
        return missingKeys.isEmpty();
    }

    /**
     * Save user details (params) for stripe cloud
     */
    private static void getUserDetails() {
        params = new HashMap<>();
        params.put("collectibleId", itemId);
        params.put("price", itemPrice);
        params.put(RAFFLE_COUNT, raffle_count);
        ParseUser user = ParseUser.getCurrentUser();
        if (user == null) {
            return;
        }
        params.put("userId", user.getObjectId());
        params.put("email",user.getEmail());
        params.put("name", user.getUsername());
    }

    /**
     * Stripe method to facilitate purchase
     */
    private void buy() {
        if (!validShippingDetails()) {
            String missingItems = String.join(", ", missingKeys).trim();
            missingItems = "\nThe missing field(s) is / are: " + missingItems;
            new AlertDialog.Builder(this)
                    .setTitle(ERROR_SHIP)
                    .setMessage(ERROR_BID_MESSAGE + missingItems)
                    .setNegativeButton("OK", null)
                    .show();
            return;
        }
        card = cardMultilineWidget.getCard();
        if (card == null) {
            Toast.makeText(this, "Enter valid details", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, params.toString());
        boolean validation = card.validateCard();
        if(validation) {
            startProgress("Validating Credit Card");
            new Stripe(this, PUBLISHABLE_KEY).createToken(
                    card,
                    new ApiResultCallback<Token>() {

                        public void onError(Exception error) {
                            Toast.makeText(PaymentActivity.this,
                                    "Stripe -" + error.toString(),
                                    Toast.LENGTH_LONG).show();
                        }

                        public void onSuccess(Token token) {
                            finishProgress();
                            charge(token);
                        }
                    });
        } else if (!card.validateNumber()) {
            Toast.makeText(this, INVALID_CARD_NUMBER, Toast.LENGTH_LONG).show();
        } else if (!card.validateExpiryDate()) {
            Toast.makeText(this, INVALID_CARD_EXP, Toast.LENGTH_LONG).show();
        } else if (!card.validateCVC()) {
            Toast.makeText(this, INVALID_CARD_CVC, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, INVALID_CARD_DETAILS, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Stripe method to charge the user
     * @param cardToken - card token
     */
    private void charge(Token cardToken){
        params.put("cardToken", cardToken.getId());
        startProgress("Purchasing Item");
        ParseCloud.callFunctionInBackground("purchaseItem", params, (response, e) -> {
            finishProgress();
            if (e == null) {
                Log.d("Cloud Response", "There were no exceptions! " + response.toString());
                Toast.makeText(getApplicationContext(),
                        "Item Purchased Successfully ",
                        Toast.LENGTH_LONG).show();
            } else {
                Log.d("Cloud Response", "Exception: " + e);
                Toast.makeText(getApplicationContext(),
                        e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Progress dialog for UI
     * @param title - title for dialog
     */
    private void startProgress(String title){
        progress.setTitle(title);
        progress.setMessage("Please Wait");
        progress.show();
    }

    private void finishProgress(){
        progress.dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstancestate) {
        super.onSaveInstanceState(savedInstancestate);
        card = cardMultilineWidget.getCard();
        if (card != null) {
            savedInstancestate.putString("cardNumber", card.getNumber());
            savedInstancestate.putInt("month", card.getExpMonth());
            savedInstancestate.putInt("year", card.getExpYear());
            savedInstancestate.putString("CVV", card.getCvc());
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

}

package com.example.donogear.actionpages;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.braintreepayments.cardform.view.CardForm;
import com.example.donogear.R;

public class PaymentInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_info);

        CardForm cardForm = (CardForm) findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .cardholderName(CardForm.FIELD_REQUIRED)
                .postalCodeRequired(true)
                .mobileNumberRequired(true)
                .mobileNumberExplanation("SMS is required on this number")
                .actionLabel("Purchase")
                .setup(this);
    }
}

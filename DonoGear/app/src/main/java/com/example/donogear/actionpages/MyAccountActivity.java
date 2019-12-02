package com.example.donogear.actionpages;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.donogear.R;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.stripe.exception.StripeException;
import com.stripe.model.CardCollection;
import com.stripe.model.Customer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.example.donogear.utils.Constants.CUSTOMER_ID;
import static com.example.donogear.utils.Constants.EMAIL_PATTERN;
import static com.example.donogear.utils.Constants.SAVE_USER_DETAILS;
import static com.example.donogear.utils.Constants.UPDATE_USER_DETAILS;

/**
 * Maintains my account page
 */
public class MyAccountActivity extends AppCompatActivity {
    private static final String GET_CREDIT_CARD_SERVICE_NAME = "getCreditCard";
    private static final String PHONE_NUM = "phoneNumber";
    private static final String EXP_DATE_FORMAT = "%s/%s";
    private static final String CARD_NUMBER_FORMAT = "XXXX-XXXX-XXXX-%s";

    private String currentUserName;
    private String currentEmail;
    private String currentPhoneNumber;
    private String currentLast4Digits;
    private String currentExpDate;
    private EditText userNameInput;
    private EditText emailInput;
    private EditText phoneNumberInput;
    private Button updateUserSettingsButton;
    private Button cancelSettingsUpdateButton;
    private TextView guestUserMessage;
    private boolean isUpdatingUserSettings = false;
    private Button editPaymentInfoButton;
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        updateUserSettingsButton = findViewById(R.id.update_settings_button);
        initializeLayout();


        updateUserSettingsButton.setOnClickListener(v -> {

            if (!isUpdatingUserSettings) {
                enableUserSettingsLayout();
                isUpdatingUserSettings = true;
            } else {
                boolean isValidInputs = validateUserInputs();
                if (isValidInputs) {
                    updateSavedUserInformation();
                    disableUserSettingsLayout();
                    isUpdatingUserSettings = false;
                }
            }
        });
    }

    /**
     * Initialize the My Account page layout
     */
    private void initializeLayout() {
        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            initializeAuthenticatedUserLayout(user);
        } else {
            // This is a guest user. Disable all controls
            initializeGuestUserLayout();
        }
    }

    /**
     * Validates the user inputs page. Sets input errors if the inputs are invalid
     *
     * @return True if the inputs are valid. Otherwise false
     */
    private boolean validateUserInputs() {
        boolean isValid = true;
        clearInputErrors();

        if (TextUtils.isEmpty(userNameInput.getText())){
            userNameInput.setError("User name is required");
            isValid = false;
        }

        if (! pattern.matcher(emailInput.getText()).matches()) {
            emailInput.setError("Please enter valid email");
            isValid = false;
        }

        if (! TextUtils.isEmpty(phoneNumberInput.getText()) &&
                !PhoneNumberUtils.isGlobalPhoneNumber(phoneNumberInput.getText().toString())) {
            phoneNumberInput.setError("Please enter a valid phone number");
            isValid = false;
        }
        return isValid;
    }

    /**
     * Initializes the layout for a non-guest user
     * @param user The user object
     */
    private void initializeAuthenticatedUserLayout(ParseUser user) {
        updateCurrentUserInfo(user);

        userNameInput = findViewById(R.id.fullName);
        emailInput = findViewById(R.id.emailAddress);
        phoneNumberInput = findViewById(R.id.phoneNum);
        cancelSettingsUpdateButton = findViewById(R.id.cancel_user_profile_update_button);

        userNameInput.setText(currentUserName);
        emailInput.setText(currentEmail);
        phoneNumberInput.setText(currentPhoneNumber);

        editPaymentInfoButton = findViewById(R.id.edit_payment_btn);

        editPaymentInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentInfoActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Initializes the layout for a guest user
     */
    private void initializeGuestUserLayout() {
        guestUserMessage = findViewById(R.id.guest_user_message);
        guestUserMessage.setVisibility(VISIBLE);

        LinearLayout userSettings = findViewById(R.id.user_settings);
        userSettings.setVisibility(GONE);

        LinearLayout editPaymentLayout = findViewById(R.id.edit_payment_layout);
        editPaymentLayout.setVisibility(GONE);

        LinearLayout paymentDetails = findViewById(R.id.existing_payment_layout);
        paymentDetails.setVisibility(GONE);
    }

    /**
     * Enables the edit user settings components
     */
    private void enableUserSettingsLayout() {
        userNameInput.setEnabled(true);
        phoneNumberInput.setEnabled(true);
        emailInput.setEnabled(true);
        updateUserSettingsButton.setText(SAVE_USER_DETAILS);
        cancelSettingsUpdateButton.setVisibility(VISIBLE);
        editPaymentInfoButton.setVisibility(INVISIBLE);

        cancelSettingsUpdateButton.setOnClickListener(v -> {
            userNameInput.setText(currentUserName);
            emailInput.setText(currentEmail);
            phoneNumberInput.setText(currentPhoneNumber);
            clearInputErrors();
            disableUserSettingsLayout();
        });
    }

    /**
     * Disables the edit user settings components
     */
    private void disableUserSettingsLayout() {
        userNameInput.setEnabled(false);
        phoneNumberInput.setEnabled(false);
        emailInput.setEnabled(false);
        updateUserSettingsButton.setText(UPDATE_USER_DETAILS);
        cancelSettingsUpdateButton.setVisibility(INVISIBLE);
        editPaymentInfoButton.setVisibility(VISIBLE);
    }

    /**
     * Clears the errors for the input fields
     */
    private void clearInputErrors() {
        userNameInput.setError(null);
        emailInput.setError(null);
        phoneNumberInput.setError(null);
    }

    /**
     * Updates the user information in the database
     */
    private void updateSavedUserInformation() {
        ParseUser user = ParseUser.getCurrentUser();
        user.setUsername(userNameInput.getText().toString());
        user.setEmail(emailInput.getText().toString());

        String phoneNum = !TextUtils.isEmpty(phoneNumberInput.getText())
                ? phoneNumberInput.getText().toString().trim() : "";
        user.put(PHONE_NUM, phoneNum);

        try {
            user.saveInBackground();
            user = user.fetch();
            updateCurrentUserInfo(user);
        } catch (ParseException e) {
            Log.e("MyAccountPage", "Unable to save user info:" + e.getMessage(), e);
        }
    }

    /**
     * Updates the current user information displayed on the page
     * @param user The current user
     */
    private void updateCurrentUserInfo(ParseUser user) {
        currentUserName = user.getUsername();
        currentEmail = user.getEmail();
        Object phoneNum = user.get(PHONE_NUM);

        if (phoneNum != null) {
            currentPhoneNumber = (String) phoneNum;
        }

        String customerId = (String) user.get(CUSTOMER_ID);

        if (customerId != null) {
            initializePaymentInfo(customerId);
        } else {
            LinearLayout paymentDetails = findViewById(R.id.existing_payment_layout);
            paymentDetails.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Initializes the payment details layout. If the user does not have any saved payment, this
     * layout is invisible
     * @param customerId The customer ID
     */
    private void initializePaymentInfo(String customerId) {
        LinearLayout paymentDetails = findViewById(R.id.existing_payment_layout);

        Map<String, Object> params = new HashMap<>();
        params.put(CUSTOMER_ID, customerId);

        ParseCloud.callFunctionInBackground(GET_CREDIT_CARD_SERVICE_NAME, params, (response, e) -> {

            if (e == null) {
                Log.d("Cloud Response", "Successfully retrieved card");

                if (response != null) {
                    HashMap<String, Object> cardInfo = (HashMap<String, Object>) response;
                    currentLast4Digits = (String) cardInfo.get("last4");
                    TextView last4CardDigits = findViewById(R.id.cardNumberLabel);
                    last4CardDigits.setText(String.format(CARD_NUMBER_FORMAT, currentLast4Digits));

                    currentExpDate = String.format(EXP_DATE_FORMAT, cardInfo.get("exp_month"),
                            cardInfo.get("exp_year"));
                    TextView expDataTextView = findViewById(R.id.expDateLabel);
                    expDataTextView.setText(currentExpDate);
                    paymentDetails.setVisibility(VISIBLE);
                } else {
                    paymentDetails.setVisibility(GONE);
                }

            } else {
                Log.e("Cloud Response", "Error retrieving card: " + e, e);
            }
        });
    }

    @Override
    public void onResume() {
        updateSavedUserInformation();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
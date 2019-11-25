package com.example.donogear.actionpages;

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
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.regex.Pattern;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static com.example.donogear.utils.Constants.EMAIL_PATTERN;
import static com.example.donogear.utils.Constants.SAVE_USER_DETAILS;
import static com.example.donogear.utils.Constants.UPDATE_USER_DETAILS;

/**
 * Maintains my account page
 */
public class MyAccountActivity extends AppCompatActivity {

    private static final String PHONE_NUM = "phoneNumber";
    private String currentUserName;
    private String currentEmail;
    private String currentPhoneNumber;
    private EditText userNameInput;
    private EditText emailInput;
    private EditText phoneNumberInput;
    private Button updateUserSettingsButton;
    private Button cancelSettingsUpdateButton;
    private TextView guestUserMessage;
    private boolean isUpdatingUserSettings = false;
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
    }

    /**
     * Initializes the layout for a guest user
     */
    private void initializeGuestUserLayout() {
        guestUserMessage = findViewById(R.id.guest_user_message);
        guestUserMessage.setVisibility(View.VISIBLE);

        LinearLayout userSettings = findViewById(R.id.user_settings);
        userSettings.setVisibility(GONE);
    }

    /**
     * Enables the edit user settings components
     */
    private void enableUserSettingsLayout() {
        userNameInput.setEnabled(true);
        phoneNumberInput.setEnabled(true);
        emailInput.setEnabled(true);
        updateUserSettingsButton.setText(SAVE_USER_DETAILS);
        cancelSettingsUpdateButton.setVisibility(View.VISIBLE);

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
    }
}

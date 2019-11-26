package com.example.donogear.actionpages;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.donogear.R;
import com.example.donogear.registeration.LauncherActivity;
import com.facebook.login.LoginManager;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.regex.Pattern;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static com.example.donogear.utils.Constants.EMAIL_PATTERN;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {

    private static final String PHONE_NUM = "phoneNumber";
    private static final String TAG = "UserProfileActivity";


    private View view;
    private MainActivity activity;
    private String currentUserName;
    private String currentEmail;
    private String currentPhoneNumber;
    private EditText userNameInput;
    private EditText emailInput;
    private EditText phoneNumberInput;
    private Button updateUserSettingsButton;
    private Button cancelSettingsUpdateButton;
    private boolean isUpdatingUserSettings = false;
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public UserProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
        initializeLayout();

        activity = (MainActivity) getActivity();
        initializeLayout();
        return view;
    }

    /**
     * Function added for logging out
     *
     */
    private void initializeLayout() {

        ParseUser user = ParseUser.getCurrentUser();

        if (user != null) {
            initializeAuthenticatedUserLayout(user);
        } else {
            // This is a guest user. Disable all controls
            initializeGuestUserLayout();
        }

        updateUserSettingsButton = (Button) view.findViewById(R.id.update_settings_button);

        updateUserSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (! isUpdatingUserSettings) {
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
            }
        });

        Button logout = view.findViewById(R.id.userlogout_btn);

        logout.setOnClickListener(v -> {
            LoginManager.getInstance().logOut();
            ParseUser.logOut();
            Intent intent = new Intent(activity, LauncherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        });
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

        if (! TextUtils.isEmpty(phoneNumberInput.getText()) && ! PhoneNumberUtils.isGlobalPhoneNumber(phoneNumberInput.getText().toString())) {
            phoneNumberInput.setError("Please enter a valid phone number");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Initializes the layout for a non-guest user
     *
     * @param user The user object
     */
    private void initializeAuthenticatedUserLayout(ParseUser user) {
        updateCurrentUserInfo(user);

        userNameInput = (EditText) view.findViewById(R.id.fullName);
        emailInput = (EditText) view.findViewById(R.id.emailAddress);
        phoneNumberInput = (EditText) view.findViewById(R.id.phoneNum);
        cancelSettingsUpdateButton = (Button) view.findViewById(R.id.cancel_user_profile_update_button);

        userNameInput.setText(currentUserName);
        emailInput.setText(currentEmail);
        phoneNumberInput.setText(currentPhoneNumber);
    }

    /**
     * Initializes the layout for a guest user
     */
    private void initializeGuestUserLayout() {
        LinearLayout guestUserLayout = (LinearLayout) view.findViewById(R.id.guest_user_settings);
        guestUserLayout.setVisibility(View.VISIBLE);

        LinearLayout userSettings = (LinearLayout) view.findViewById(R.id.user_settings);
        userSettings.setVisibility(GONE);
    }

    /**
     * Enables the edit user settings components
     */
    private void enableUserSettingsLayout() {
        userNameInput.setEnabled(true);
        phoneNumberInput.setEnabled(true);
        emailInput.setEnabled(true);
        updateUserSettingsButton.setText("Save Settings");
        cancelSettingsUpdateButton.setVisibility(View.VISIBLE);

        cancelSettingsUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameInput.setText(currentUserName);
                emailInput.setText(currentEmail);
                phoneNumberInput.setText(currentPhoneNumber);
                clearInputErrors();
                disableUserSettingsLayout();
            }
        });
    }

    /**
     * Disables the edit user settings components
     */
    private void disableUserSettingsLayout() {
        userNameInput.setEnabled(false);
        phoneNumberInput.setEnabled(false);
        emailInput.setEnabled(false);
        updateUserSettingsButton.setText("Update Settings");
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

        String phoneNum = ! TextUtils.isEmpty(phoneNumberInput.getText()) ? phoneNumberInput.getText().toString().trim() : "";
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
     *
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

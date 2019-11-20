package com.example.donogear.actionpages;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.donogear.R;
import com.parse.ParseUser;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;

public class MyAccountActivity extends AppCompatActivity {
    private static final String PHONE_NUM = "phoneNumber";

    private View view;
    private MainActivity activity;
    private String currentUserName;
    private String currentEmail;
    private String currentPhoneNumber;
    private EditText userNameInput;
    private EditText emailInput;
    private EditText phoneNumberInput;
    private EditText oldPasswordInput;
    private EditText newPasswordInput;
    private Button updateUserSettingsButton;
    private Button updatePasswordButton;
    private Button cancelSettingsUpdateButton;
    private Button cancelPasswordUpdateButton;
    private TextView guestUserMessage;
    private boolean isUpdatingUserSettings = false;
    private boolean isUpdatingPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        updateUserSettingsButton = (Button) findViewById(R.id.update_settings_button);
        updatePasswordButton = (Button) findViewById(R.id.change_password_button);
        initializeLayout();

        updateUserSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (! isUpdatingUserSettings) {
                    enableUserSettingsLayout();
                    isUpdatingUserSettings = true;
                } else {
                    disableUserSettingsLayout();
                    isUpdatingUserSettings = false;
                }
            }
        });

        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (! isUpdatingPassword) {
                    enablePasswordLayout();
                    isUpdatingPassword = true;
                } else {
                    disablePasswordLayout();
                    isUpdatingPassword = false;
                }
            }
        });
    }

    private void initializeLayout() {
        ParseUser user = ParseUser.getCurrentUser();

        if (user != null) {
            initializeAuthenticatedUserLayout(user);
        } else {
            // This is a guest user. Disable all controls
            initializeGuestUserLayout();
        }
    }

    private void initializeAuthenticatedUserLayout(ParseUser user) {
        currentUserName = user.getUsername();
        currentEmail = user.getEmail();
        currentPhoneNumber = (String) user.get(PHONE_NUM);

        userNameInput = (EditText) findViewById(R.id.fullName);
        emailInput = (EditText) findViewById(R.id.emailAddress);
        phoneNumberInput = (EditText) findViewById(R.id.phoneNum);

        userNameInput.setText(currentUserName);
        emailInput.setText(currentEmail);
        phoneNumberInput.setText(currentPhoneNumber);
    }

    private void initializeGuestUserLayout() {
        guestUserMessage = (TextView) findViewById(R.id.guest_user_message);
        guestUserMessage.setVisibility(View.VISIBLE);
//        userNameInput.setVisibility(INVISIBLE);
//        emailInput.setVisibility(INVISIBLE);
//        phoneNumberInput.setVisibility(INVISIBLE);
//        updateUserSettingsButton.setVisibility(INVISIBLE);
//        updatePasswordButton.setVisibility(INVISIBLE);
    }

    private void enableUserSettingsLayout() {
        userNameInput.setEnabled(true);
        phoneNumberInput.setEnabled(true);
        emailInput.setEnabled(true);
        updateUserSettingsButton.setText("Save Settings");
        cancelSettingsUpdateButton.setVisibility(View.VISIBLE);
    }

    private void disableUserSettingsLayout() {
        userNameInput.setEnabled(true);
        phoneNumberInput.setEnabled(true);
        emailInput.setEnabled(true);
        updateUserSettingsButton.setText("Update Settings");
        cancelSettingsUpdateButton.setVisibility(INVISIBLE);
    }

    private void enablePasswordLayout() {
        newPasswordInput.setEnabled(true);
        oldPasswordInput.setEnabled(true);
        updateUserSettingsButton.setText("Save Password");
        cancelPasswordUpdateButton.setVisibility(View.VISIBLE);
    }

    private void disablePasswordLayout() {
        newPasswordInput.setEnabled(false);
        oldPasswordInput.setEnabled(false);
        updateUserSettingsButton.setText("Update Password");
        cancelPasswordUpdateButton.setVisibility(INVISIBLE);
    }
}

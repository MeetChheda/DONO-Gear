package com.example.donogear.registeration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.example.donogear.actionpages.MainActivity;
import com.example.donogear.R;
import com.parse.ParseUser;

import static com.example.donogear.utils.Constants.USER_DETAILS;
import static com.example.donogear.utils.Constants.USER_NAME;

public class LauncherActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        Button loginButton = findViewById(R.id.login_btn);
        Button guestButton = findViewById(R.id.guest_btn);
        loginButton.setOnClickListener(view -> loginClicked());
        guestButton.setOnClickListener(view -> guestAccess());

        ParseUser.logOut();
        checkSharedPreferences();
    }

    private void checkSharedPreferences() {
        sharedPreferences = getSharedPreferences(USER_DETAILS, MODE_PRIVATE);
        if (sharedPreferences.contains(USER_NAME)) {
            loginClicked();
        }
    }

    public void loginClicked() {
        Intent intent = new Intent(LauncherActivity.this, LoginPageActivity.class);
        startActivity(intent);
    }

    public void guestAccess() {
        Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
        startActivity(intent);
    }
}

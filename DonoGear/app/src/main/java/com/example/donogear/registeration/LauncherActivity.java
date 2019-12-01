package com.example.donogear.registeration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.donogear.actionpages.MainActivity;
import com.example.donogear.R;
import com.parse.ParseUser;

public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = "LauncherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        Button loginButton = findViewById(R.id.login_btn);
        Button guestButton = findViewById(R.id.guest_btn);
        loginButton.setOnClickListener(view -> loginClicked());
        guestButton.setOnClickListener(view -> guestAccess());

        ParseUser parseUser = ParseUser.getCurrentUser();
        if (parseUser != null) {
            Log.e(TAG,"Parse User not null");
            System.out.println(parseUser.getUsername());
            loginClicked();
        } else {
            Log.e(TAG, "Parse User null");
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

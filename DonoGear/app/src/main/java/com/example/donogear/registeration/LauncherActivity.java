package com.example.donogear.registeration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.example.donogear.actionpages.MainActivity;
import com.example.donogear.R;
import com.parse.ParseUser;
import com.stripe.Stripe;

public class LauncherActivity extends AppCompatActivity {
    private ParseUser parseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        ParseUser.logOut();
        Button loginButton = findViewById(R.id.login_btn);
        Button guestButton = findViewById(R.id.guest_btn);
        loginButton.setOnClickListener(view -> loginClicked());
        guestButton.setOnClickListener(view -> guestAccess());

        parseUser = ParseUser.getCurrentUser();
        if (parseUser != null) {
            System.out.println("Parse User not null");
            System.out.println(parseUser.getUsername());
            loginClicked();
        } else {
            System.out.println("Parse User currently null");
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

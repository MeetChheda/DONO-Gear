package com.example.donogear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.facebook.login.LoginManager;
import com.parse.Parse;
import com.parse.ParseUser;
import com.parse.facebook.ParseFacebookUtils;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                // if defined
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );

        ParseFacebookUtils.initialize(this);
        LoginManager.getInstance().logOut();
        ParseUser.logOut();
    }

    public void loginClicked(View view) {
        Intent intent = new Intent(LauncherActivity.this, LoginPageActivity.class);
        startActivity(intent);
    }

    public void guestAccess(View view) {
        Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
        startActivity(intent);
    }
}

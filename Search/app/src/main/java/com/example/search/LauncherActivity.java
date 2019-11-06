package com.example.search;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
    }

    public void LoginOrCreateAccountBtnClicked(View view) {
        Intent intent = new Intent(LauncherActivity.this, LoginPageActivity.class);
        startActivity(intent);
    }

    public void guestAccess(View view) {
        Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
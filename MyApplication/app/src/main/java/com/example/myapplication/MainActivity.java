package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void LoginOrCreateAccountBtnClicked(View view) {
        Intent intent = new Intent(MainActivity.this, LoginPageActivity.class);
        startActivity(intent);
    }

    public void guestAccess(View view) {
        Intent intent = new Intent(MainActivity.this, NavbarActivity.class);
        startActivity(intent);
    }

}


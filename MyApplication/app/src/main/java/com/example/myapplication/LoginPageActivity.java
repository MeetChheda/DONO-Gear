package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class LoginPageActivity extends AppCompatActivity {

    TabItem createAccountTab;
    TabItem loginTab;
    TabLayout tabs;
    LinearLayout login_layout;
    LinearLayout createAccount_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        tabs = findViewById(R.id.tabs);
        login_layout = findViewById(R.id.login_layout);
        createAccount_layout = findViewById(R.id.createAccount_layout);
//        createAccountTab = findViewById(R.id.createAccount_tab);
//        loginTab = findViewById(R.id.login_tab);
//        loginTab.setBackgroundColor(Color.parseColor("#2FD6D6"));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0: {
                        createAccount_layout.setVisibility(View.GONE);
                        login_layout.setVisibility(View.VISIBLE);
//                        loginTab.setBackgroundColor(Color.parseColor("#2FD6D6"));
//                        createAccountTab.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        break;
                    }
                    case 1: {
                        login_layout.setVisibility(View.GONE);
                        createAccount_layout.setVisibility(View.VISIBLE);
//                        createAccountTab.setBackgroundColor(Color.parseColor("#2FD6D6"));
//                        loginTab.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void createAccountBtnClicked(View view) {

    }

    public void loginBtnClicked(View view) {

    }

//    public void createAccountTabClicked(View view) {
//        System.out.println("Create Tab clicked");
//    }
//
//    public void loginTabClicked(View view) {
//        System.out.println("Login Tab clicked");
//    }
}

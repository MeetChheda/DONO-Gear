package com.example.donogear;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.facebook.ParseFacebookUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;

public class LoginPageActivity extends AppCompatActivity {
    private String TAG = "LoginPageActivity";
    TabItem createAccountTab;
    TabItem loginTab;
    TabLayout tabs;
    LinearLayout login_layout;
    LinearLayout createAccount_layout;
    ProgressBar pgsBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        pgsBar = (ProgressBar)findViewById(R.id.pBar);
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

        ImageView loginButton = (ImageView) findViewById(R.id.facebook_icon);

        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                // TODO request more user permissions when determining what is needed
                Collection<String> permissions = Arrays.asList("public_profile", "email");
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginPageActivity.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (err != null) {
                            ParseUser.logOut();
                            Log.e(TAG, "An error occurred", err);
                        }

                        if (user == null) {
                            ParseUser.logOut();
                            Log.d(TAG, "The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            Log.d(TAG, "Successfully logged in new user via Facebook");
                            getUserDetailFromFB();
                        } else {
                            Toast.makeText(LoginPageActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Successfully logged in existing user via Facebook");
                            directSuccessfulUserLogin();
                        }
                    }
                });
            }
        });
    }

    /**
     * Retrieves user information from Facebook
     */
    void getUserDetailFromFB() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),new  GraphRequest.GraphJSONObjectCallback(){
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                ParseUser user = ParseUser.getCurrentUser();
                try{
                    // TODO store needed user data and route to main page for user account data
                    user.setUsername(object.getString("name"));
                    user.setEmail(object.getString("email"));
                } catch(JSONException e){
                    Log.e(TAG, "Error retrieving user data", e);
                }

                // Add user to Back4App registry
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d(TAG, "Successfully added user to App user registry");
                        Toast.makeText(LoginPageActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                        directSuccessfulUserLogin();
                    }
                });
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","name, email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    /**
     * Routes a successful user login to the main page
     */
    private void directSuccessfulUserLogin() {
        pgsBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(LoginPageActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void createAccountBtnClicked(View view) {

    }

    public void loginBtnClicked(View view) {

    }
}

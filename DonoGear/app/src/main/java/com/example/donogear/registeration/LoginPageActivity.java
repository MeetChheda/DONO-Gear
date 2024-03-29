package com.example.donogear.registeration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.donogear.R;
import com.example.donogear.actionpages.MainActivity;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseUser;
import com.parse.facebook.ParseFacebookUtils;
import com.parse.twitter.ParseTwitterUtils;

import org.json.JSONException;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.donogear.utils.Constants.EMAIL_PATTERN;


public class LoginPageActivity extends AppCompatActivity {
    private String TAG = "LoginPageActivity";
    TabLayout tabs;
    LinearLayout login_layout;
    LinearLayout createAccount_layout;
    EditText usernameEditTextSignup;
    EditText passwordEditTextSignup;
    EditText usernameEditTextLogin;
    EditText passwordEditTextLogin;
    EditText emailEditTextSignUp;
    TextView invalid;
    String username;
    String password;
    String email;
    ProgressBar pgsBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        pgsBar = findViewById(R.id.pBar);
        invalid = findViewById(R.id.invalid);
        tabs = findViewById(R.id.tabs);
        login_layout = findViewById(R.id.login_layout);
        createAccount_layout = findViewById(R.id.createAccount_layout);
        usernameEditTextSignup = findViewById(R.id.usernameCreateAccount_editText);
        emailEditTextSignUp = findViewById(R.id.emailCreateAccount_editText);
        passwordEditTextSignup = findViewById(R.id.passwordCreateAccount_editText);
        usernameEditTextLogin = findViewById(R.id.username_editText);
        passwordEditTextLogin = findViewById(R.id.password_editText);
        checkForLoggedInStatus();

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0: {
                        createAccount_layout.setVisibility(View.GONE);
                        login_layout.setVisibility(View.VISIBLE);
                        break;
                    }
                    case 1: {
                        login_layout.setVisibility(View.GONE);
                        createAccount_layout.setVisibility(View.VISIBLE);
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

        ImageView facebookLoginButton =  findViewById(R.id.facebook_icon);

        facebookLoginButton.setOnClickListener(view -> {
            // TODO request more user permissions when determining what is needed
            Collection<String> permissions = Arrays.asList("public_profile", "email");
            ParseFacebookUtils.logInWithReadPermissionsInBackground(this,
                    permissions, (user, err) -> {
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
            });
        });

        ImageView twitterLoginButton = findViewById(R.id.twitter_icon);
        twitterLoginButton.setOnClickListener(view -> {
            ParseTwitterUtils.logIn(LoginPageActivity.this, (user, err) -> {
                if (err != null) {
                    ParseUser.logOut();
                    Log.e(TAG, "An error occured", err);
                }
                if (user == null) {
                    ParseUser.logOut();
                    Log.d(TAG, "Uh oh. The user cancelled the Twitter login.");
                } else if (user.isNew()) {
                    Log.d(TAG, "User signed up and logged in through Twitter!");
                    getUserDetailsFromTwitter();
                } else {
                    Log.d(TAG, "User logged in through Twitter!");
                    directSuccessfulUserLogin();
                }
            });
        });

    }

    /**
     * Retrieves user information from Twitter
     */
    private void getUserDetailsFromTwitter() {
        ParseUser user = ParseUser.getCurrentUser();
        try{
            user.setUsername(ParseTwitterUtils.getTwitter().getScreenName());
        } catch(Exception e){
            Log.e(TAG, "Error retrieving user data", e);
        }

        user.saveInBackground(e -> {
            Log.d(TAG, "Successfully added user to App user registry");
            Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show();
            directSuccessfulUserLogin();
        });
    }

    private void checkForLoggedInStatus() {
        if (ParseUser.getCurrentUser() != null) {
            directSuccessfulUserLogin();
        }
    }

    /**
     * Retrieves user information from Facebook
     */
    void getUserDetailFromFB() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                (object, response) -> {
            ParseUser user = ParseUser.getCurrentUser();
            try{
                // TODO store needed user data and route to main page for user account data
                user.setUsername(object.getString("name"));
                user.setEmail(object.getString("email"));
            } catch(JSONException e){
                Log.e(TAG, "Error retrieving user data", e);
            }

            // Add user to Back4App registry
            user.saveInBackground(e -> {
                Log.d(TAG, "Successfully added user to App user registry");
                Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show();
                directSuccessfulUserLogin();
            });
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
        username = usernameEditTextSignup.getText().toString();
        email = emailEditTextSignUp.getText().toString();
        password = passwordEditTextSignup.getText().toString();

        if (username.length() == 0) {
            usernameEditTextSignup.setError("Username cannot be empty");
        }
        if (!emailValidator(email)) {
            emailEditTextSignUp.setError("Please enter valid email");
        }
        if (password.length() == 0) {
            passwordEditTextSignup.setError("Password is required");
        }
        else if (password.length() < 8) {
            passwordEditTextSignup.setError("Password length should be at-least 8 characters");
        }

        else {
            ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);


            user.signUpInBackground(e -> {
                if (e == null) {
                    Intent intent = new Intent(this, MainActivity.class);
                    Log.d(TAG, "Sign up finish");
                    startActivity(intent);
                } else {
                    if (e.getMessage().equals("Account already exists for this email address.")) {
                        emailEditTextSignUp.setError("Please enter different email");
                    }
                    else {
                        usernameEditTextSignup.setError("Please enter different username");
                    }
                    Log.e(TAG, "Sign up error");
                    ParseUser.logOut();
                }
            });
        }
    }

    public boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void loginBtnClicked(View view) {

        invalid.setVisibility(View.GONE);
        username = usernameEditTextLogin.getText().toString();
        password = passwordEditTextLogin.getText().toString();
        if (username.length() == 0) {
            usernameEditTextLogin.setError("Username cannot be empty");
        }
        if (password.length() == 0) {
            passwordEditTextLogin.setError("Please enter password");
        }

        ParseUser.logInInBackground(username, password, (parseUser, e) -> {
            if (parseUser != null) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                invalid.setVisibility(View.VISIBLE);
                usernameEditTextLogin.setText("");
                passwordEditTextLogin.setText("");
                Log.e(TAG, e.getMessage());
                ParseUser.logOut();
            }
        });
    }


}

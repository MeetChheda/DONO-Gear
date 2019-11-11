package com.example.donogear;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.google.android.material.tabs.TabLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.LogInCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginPageActivity extends AppCompatActivity {

    TabLayout tabs;
    LinearLayout login_layout;
    LinearLayout createAccount_layout;
    EditText usernameEditTextSignup;
    EditText passwordEditTextSignup;
    EditText usernameEditTextLogin;
    EditText passwordEditTextLogin;
    EditText emailEditTextSignUp;
    String username;
    String password;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );

//        boolean finish = getIntent().getBooleanExtra("finish", false);
        tabs = findViewById(R.id.tabs);
        login_layout = findViewById(R.id.login_layout);
        createAccount_layout = findViewById(R.id.createAccount_layout);
        usernameEditTextSignup = (EditText) findViewById(R.id.usernameCreateAccount_editText);
        emailEditTextSignUp = (EditText)findViewById(R.id.emailCreateAccount_editText);
        passwordEditTextSignup = (EditText)findViewById(R.id.passwordCreateAccount_editText);
        usernameEditTextLogin = (EditText)findViewById(R.id.username_editText);
        passwordEditTextLogin = (EditText)findViewById(R.id.password_editText);

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
    }

//    private void alertDisplayer(String title,String message){
//        AlertDialog.Builder builder = new AlertDialog.Builder(LoginPageActivity.this)
//                .setTitle(title)
//                .setMessage(message)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
////                        Intent intent = new Intent(LoginPageActivity.this, MainActivity.class);
////                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
////                        startActivity(intent);
//                    }
//                });
//        AlertDialog ok = builder.create();
//        ok.show();
//    }

    public void createAccountBtnClicked(View view) {

        username = usernameEditTextSignup.getText().toString();
        email = emailEditTextSignUp.getText().toString();
        password = passwordEditTextSignup.getText().toString();

        if (!emailValidator(email)) {
            emailEditTextSignUp.setError("Please enter valid email");
        }
        else if (password.length() == 0) {
            passwordEditTextSignup.setError("Password is required");
        }
        else if (password.length() < 8) {
            passwordEditTextSignup.setError("Password length should be atleast 8 characters");
        }

        else {
            ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);


            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
//                    alertDisplayer("Sucessful Sign Up!","Welcome " + username + "!");
                        Intent intent = new Intent(LoginPageActivity.this, MainActivity.class);
                        System.out.println("Signup finish");
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        usernameEditTextSignup.setError("Please enter different username");
                        System.out.println("Signup error");
                        ParseUser.logOut();
//                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();


    }

    public void loginBtnClicked(View view) {


        username = usernameEditTextLogin.getText().toString();
        System.out.println(username);
        password = passwordEditTextLogin.getText().toString();
        System.out.println(password);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                System.out.println(parseUser);
                if (parseUser != null) {
                    Intent intent = new Intent(LoginPageActivity.this, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    ParseUser.logOut();
                    Toast.makeText(LoginPageActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}

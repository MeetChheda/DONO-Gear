package com.example.donogear.registration;

import com.example.donogear.registeration.LoginPageActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import java.util.HashMap;

public class LoginPageActivityTest {

    HashMap<String, String> userCredentials;
    private LoginPageActivity loginPageActivity;

    @Before
    public void initLogin() {
        loginPageActivity = new LoginPageActivity();
        userCredentials = new HashMap<String, String>();
        userCredentials.put("user1", "12345678");
        userCredentials.put("user2", "12345678");
        userCredentials.put("user3", "12345678");
        userCredentials.put("user4", "12345678");
        userCredentials.put("user5", "12345678");
        userCredentials.put("user6", "12345678");
        userCredentials.put("user7", "12345678");
    }

    @Test
    public void usernameInvalidOnLoginTest() {
        String username = "user8";
        String password = "12345678";
        Assert.assertEquals("Username is incorrect", loginPageActivity.loginCheck(userCredentials, username, password));
    }

    @Test
    public void passwordInvalidOnLoginTest() {
        String username = "user1";
        String password = "12345679";
        Assert.assertEquals("Password is incorrect", loginPageActivity.loginCheck(userCredentials, username, password));
    }

    @Test
    public void userValidOnLoginTest() {
        String username = "user1";
        String password = "12345678";
        Assert.assertEquals("SUCCESS", loginPageActivity.loginCheck(userCredentials, username, password));
    }

    @Test
    public void userExistsOnSignUpTest() {
        String username = "user1";
        String password = "12345678";
        String email = "user1@gmail.com";
        Assert.assertEquals("Username already exists", loginPageActivity.signUpCheck(userCredentials, username, email, password));
    }

    @Test
    public void emailInvalidOnSignUpTest() {
        String username = "user8";
        String password = "12345678";
        String email = "user1.gmail.com";
        Assert.assertEquals("Email invalid", loginPageActivity.signUpCheck(userCredentials, username, email, password));
    }

    @Test
    public void passwordInvalidOnSignUpTest() {
        String username = "user8";
        String password = "12345";
        String email = "user1@gmail.com";
        Assert.assertEquals("Password is invalid", loginPageActivity.signUpCheck(userCredentials, username, email, password));
    }

    @Test
    public void userValidOnSignUpTest() {
        String username = "user9";
        String password = "12345678";
        String email = "user1@gmail.com";
        Assert.assertEquals("SUCCESS", loginPageActivity.signUpCheck(userCredentials, username, email, password));
    }

}

package com.example.donogear.actionpages;

import com.parse.ParseFile;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PaymentActivityTest {

    private HashMap<String, Object> paramsTest;
    private PaymentActivity paymentActivity;
    private List<String> keys;

    @Before
    public void initData() {
        keys = Arrays.asList(
                "email", "name", "address", "zip", "city_state"
        );
        paymentActivity = new PaymentActivity();
    }

    // TC-05-06
    @Test
    public void emailKeyNotPresentTest() {
        paramsTest = new HashMap<>();
        paramsTest.put("name", "params1");
        paramsTest.put("address", "Los Angeles");
        paramsTest.put("city_state", "California");
        paramsTest.put("zip", "90007");

        Assert.assertEquals(false, paymentActivity.validShippingDetails(paramsTest, new ArrayList<>(), keys));

    }

    // TC-05-04
    @Test
    public void addressKeyNotPresentTest() {
        paramsTest = new HashMap<>();
        paramsTest.put("name", "params1");
        paramsTest.put("email", "params1@gmail.com");
        paramsTest.put("city_state", "California");
        paramsTest.put("zip", "90007");

        Assert.assertEquals(false, paymentActivity.validShippingDetails(paramsTest, new ArrayList<>(), keys));

    }

    // TC-05-02
    @Test
    public void stateKeyNotPresentTest() {
        paramsTest = new HashMap<>();
        paramsTest.put("name", "params1");
        paramsTest.put("email", "params1@gmail.com");
        paramsTest.put("address", "Los Angeles");
        paramsTest.put("zip", "90007");

        Assert.assertEquals(false, paymentActivity.validShippingDetails(paramsTest, new ArrayList<>(), keys));

    }

    // TC-05-05
    @Test
    public void zipKeyNotPresentTest() {
        paramsTest = new HashMap<>();
        paramsTest.put("name", "params1");
        paramsTest.put("email", "params1@gmail.com");
        paramsTest.put("address", "Los Angeles");
        paramsTest.put("city_state", "California");

        Assert.assertEquals(false, paymentActivity.validShippingDetails(paramsTest, new ArrayList<>(), keys));

    }

    // TC-05-03
    @Test
    public void nameKeyNotPresentTest() {
        paramsTest = new HashMap<>();
        paramsTest.put("email", "params1@gmail.com");
        paramsTest.put("address", "Los Angeles");
        paramsTest.put("city_state", "California");
        paramsTest.put("zip", "90007");

        Assert.assertEquals(false, paymentActivity.validShippingDetails(paramsTest, new ArrayList<>(), keys));

    }

    // TC-05-01
    @Test
    public void allKeyPresentTest() {
        paramsTest = new HashMap<>();
        paramsTest.put("name", "params1");
        paramsTest.put("email", "params1@gmail.com");
        paramsTest.put("address", "Los Angeles");
        paramsTest.put("city_state", "California");
        paramsTest.put("zip", "90007");

        Assert.assertEquals(true, paymentActivity.validShippingDetails(paramsTest, new ArrayList<>(), keys));

    }

}

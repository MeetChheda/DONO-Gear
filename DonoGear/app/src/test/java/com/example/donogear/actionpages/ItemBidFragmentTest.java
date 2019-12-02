package com.example.donogear.actionpages;

import com.example.donogear.actionpages.ItemBidFragment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import static com.example.donogear.utils.Constants.ADD_TO_BID;
import static com.example.donogear.utils.Constants.SUBTRACT_FROM_BID;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
/**
 * Tests to validate if the bid amount is correctly updated or not. This class runs using
 * RobolectricTestRunner and mocks a method as would be actually implemented in the app.
 */
public class ItemBidFragmentTest {

    private int startBid;
    private int currentBid;
    private int steps;

    private ItemBidFragment itemBidFragment;

    @Before
    public void init() {
        itemBidFragment = new ItemBidFragment();
    }

    /**
     * TC-03-04
     * Test to check if a the bid amount updates correctly when increased
     */
    @Test
    public void addingToBidTest() {
        startBid = 2000;
        currentBid = 0;
        steps = 100;
        int value = 2000;
        int latestValue = Math.max(startBid, currentBid);
        int newAmount = itemBidFragment.changeAmount(value, steps, latestValue, ADD_TO_BID);
        Assert.assertEquals(2100, newAmount);
    }

    /**
     * TC-03-05
     * Test to check if a the bid amount updates correctly when decreased and is within bounds
     * (higher than the current bid)
     */
    @Test
    public void subtractingFromBidLegallyTest() {
        startBid = 2000;
        currentBid = 0;
        steps = 100;
        int value = 2400;
        int latestValue = Math.max(startBid, currentBid);
        int newAmount = itemBidFragment.changeAmount(value, steps, latestValue, SUBTRACT_FROM_BID);
        Assert.assertEquals(2300, newAmount);
    }

    /**
     * TC-03-01
     * Test to check if entered bid is not a number
     */
    @Test
    public void nonIntegerEnteredAsBidTest()  {
        boolean actual = itemBidFragment.validationBid("abc",100);
        Assert.assertEquals(false, actual);
    }

    /**
     * TC-03-03
     * Test to check if entered bid is less than latest bid
     */
    @Test
    public void valueBidLessThanLatestBidTest()  {
        boolean actual = itemBidFragment.validationBid("50",100);
        Assert.assertEquals(false, actual);
    }

    /**
     * TC-03-02
     * Test to check if entered bid is valid
     */
    @Test
    public void validBidEntered()  {
        boolean actual = itemBidFragment.validationBid("500",100);
        Assert.assertEquals(true, actual);
    }
}

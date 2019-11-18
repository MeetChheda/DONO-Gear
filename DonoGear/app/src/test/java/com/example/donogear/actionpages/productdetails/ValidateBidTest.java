package com.example.donogear.actionpages.productdetails;

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

public class ValidateBidTest {

    private int startBid;
    private int currentBid;
    private int steps;

    private ItemBidFragment itemBidFragment;

    @Before
    public void init() {
        itemBidFragment = new ItemBidFragment();
    }

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

    @Test
    public void subtractingFromBidIllegallyTest() {
        startBid = 2000;
        currentBid = 0;
        steps = 100;
        int value = 2000;
        int latestValue = Math.max(startBid, currentBid);
        int newAmount = itemBidFragment.changeAmount(value, steps, latestValue, SUBTRACT_FROM_BID);
        Assert.assertTrue(ShadowToast.showedToast("Your bid should be higher than the current bid"));
        Assert.assertEquals(2000, newAmount);
    }
}

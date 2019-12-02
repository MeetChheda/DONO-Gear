package com.example.donogear.actionpages;

import com.example.donogear.models.ItemDetails;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePageFragmentTest {

    private List<ItemDetails> itemDetailsList;
    private HomePageFragment homePageFragment;
    /**
     * Initializing data
     */
    @Before
    public void initData() {
        InitializeItems initializeItems = new InitializeItems();
        initializeItems.init();
        itemDetailsList = initializeItems.getItemDetailsList();
        homePageFragment = new HomePageFragment();
    }

    // TC-01-01
    @Test
    public void getTrendingItemListTest() {
        List<ItemDetails> trendingItemList = homePageFragment.displayTrendingItems(itemDetailsList, new ArrayList<ItemDetails>());
        Assert.assertEquals(3, trendingItemList.size());
    }
}

package com.example.donogear.actionpages.mainactivity;

import com.example.donogear.actionpages.InitializeItems;
import com.example.donogear.actionpages.MainActivity;
import com.example.donogear.models.ItemDetails;

import org.junit.*;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.donogear.utils.Constants.AUCTION_IDENTIFIER;
import static com.example.donogear.utils.Constants.DROP_IDENTIFIER;
import static com.example.donogear.utils.Constants.RAFFLE_IDENTIFIER;

public class FilterByCategoryTest {

    private List<ItemDetails> itemDetailsList;
    private MainActivity mainActivity;

    @Before
    public void initData() {
        InitializeItems initializeItems = new InitializeItems();
        initializeItems.init();
        itemDetailsList = initializeItems.getItemDetailsList();
        mainActivity = new MainActivity();
    }

    @Test
    public void filterButEmptyListTest() {
        List<ItemDetails> myList = mainActivity.filterItemsByCategory(new ArrayList<>(),
                AUCTION_IDENTIFIER);
        Assert.assertEquals(0, myList.size());
    }

    @Test
    public void filterForAuctionTest() {
        List<ItemDetails> myList = mainActivity.filterItemsByCategory(itemDetailsList, AUCTION_IDENTIFIER);
        Assert.assertEquals(3, myList.size());
        List<String> possibleNames = Arrays.asList(
                "item1", "item2", "item5"
        );
        Assert.assertTrue(possibleNames.contains(myList.get(0).itemName));
        Assert.assertTrue(possibleNames.contains(myList.get(1).itemName));
        Assert.assertTrue(possibleNames.contains(myList.get(2).itemName));
    }

    @Test
    public void filterForRafflesTest() {
        List<ItemDetails> myList = mainActivity.filterItemsByCategory(itemDetailsList, RAFFLE_IDENTIFIER);
        Assert.assertEquals(1, myList.size());
        Assert.assertEquals("item3", myList.get(0).itemName);
    }

    @Test
    public void filterForDropsTest() {
        List<ItemDetails> myList = mainActivity.filterItemsByCategory(itemDetailsList, DROP_IDENTIFIER);
        Assert.assertEquals(1, myList.size());
        Assert.assertEquals("item4", myList.get(0).itemName);
    }

}

package com.example.donogear.actionpages;

import com.example.donogear.actionpages.MainActivity;
import com.example.donogear.models.ItemDetails;

import org.junit.Before;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InitializeItems {

    private String id;
    private String itemName;
    private String itemDescription;
    private String highestBidder;
    private int currentPrice;
    private int startBid;
    private int buyItNowPrice;
    private String category;
    private Date endDate;
    private int costPerEntry;
    private List<File> listOfImages;

    private List<ItemDetails> itemDetailsList;

    @Before
    public void init() {
        itemDetailsList = new ArrayList<>();

        id = "01";
        itemName = "item1"; itemDescription = "dummy for an auction item - 1";
        highestBidder = null; currentPrice = 0;
        startBid = 2000; buyItNowPrice = 0;
        category = "auction"; endDate = new Date();
        listOfImages = null;
        addToList();

        id = "02";
        itemName = "item2"; itemDescription = "dummy for an auction item - 2";
        highestBidder = null; currentPrice = 0;
        startBid = 4000; buyItNowPrice = 0;
        category = "auction"; endDate = new Date();
        listOfImages = null;
        addToList();

        id = "03";
        itemName = "item3"; itemDescription = "dummy for a raffle item - 3";
        highestBidder = null; costPerEntry = 10;
        category = "raffle"; endDate = new Date();
        listOfImages = null;
        addToList();

        id = "04";
        itemName = "item4"; itemDescription = "dummy for a drop item - 4";
        highestBidder = null; costPerEntry = 10;
        category = "drop"; endDate = new Date();
        listOfImages = null;
        addToList();

        id = "05";
        itemName = "item5"; itemDescription = "dummy for an auction item - 5";
        highestBidder = null; currentPrice = 0;
        startBid = 3000; buyItNowPrice = 0;
        category = "auction"; endDate = new Date();
        listOfImages = null;
        addToList();
    }

    private void addToList() {
        ItemDetails item = new ItemDetails(id, itemName, itemDescription, startBid, buyItNowPrice,
                currentPrice, highestBidder, category, endDate, costPerEntry, listOfImages);
        itemDetailsList.add(item);
    }

    public List<ItemDetails> getItemDetailsList() {
        return itemDetailsList;
    }
}

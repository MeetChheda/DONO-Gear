package com.example.search;

import java.io.File;
import java.util.Date;
import java.util.List;

public class ItemDetails {
    String id;
    String itemName;
    int buyNowPrice;
    int currentPrice;
    String highestBidder;
    String category;
    Date endDate;
    List<File> listOfImages;

    public ItemDetails(String id, String itemName, int buyNowPrice, int currentPrice, String highestBidder,
                       String category, Date endDate, List<File> listOfImages) {
        this.id = id;
        this.itemName = itemName;
        this.buyNowPrice = buyNowPrice;
        this.currentPrice = currentPrice;
        this.highestBidder = highestBidder;
        this.category = category;
        this.endDate = endDate;
        this.listOfImages = listOfImages;
    }
}

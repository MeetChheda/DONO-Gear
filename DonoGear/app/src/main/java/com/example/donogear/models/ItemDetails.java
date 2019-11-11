package com.example.donogear.models;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ItemDetails implements Serializable {
    public String id;
    public String itemName;
    public String itemDescription;
    public int buyNowPrice;
    public int currentPrice;
    public int costPerEntry;
    public String highestBidder;
    public String category;
    public Date endDate;
    public List<File> listOfImages;

    /**
     * Constructor for AUCTION items - params are self explanatory
     */
    public ItemDetails(String id, String itemName, String itemDescription, int buyNowPrice,
                       int currentPrice, String highestBidder, String category, Date endDate,
                       List<File> listOfImages) {
        this.id = id;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.buyNowPrice = buyNowPrice;
        this.currentPrice = currentPrice;
        this.highestBidder = highestBidder;
        this.category = category;
        this.endDate = endDate;
        this.listOfImages = listOfImages;
    }

    /**
     * Constructor for RAFFLE items - params are self explanatory
     */
    public ItemDetails(String id, String itemName, String itemDescription, int costPerEntry, String category,
                       Date endDate, List<File> listOfImages) {
        this.id = id;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.costPerEntry = costPerEntry;
        this.category = category;
        this.endDate = endDate;
        this.listOfImages = listOfImages;
    }

    public String printData() {
        return "Name: " + this.itemName + " category: " + this.category;
    }
}
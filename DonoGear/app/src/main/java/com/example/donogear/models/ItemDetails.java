package com.example.donogear.models;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * Storing details for each item
 * TODO: isTrending variable will be dynamic in future
 */
public class ItemDetails implements Serializable {
    public String id;
    public String itemName;
    public String itemDescription;
    public int startBid;
    public int buyNowPrice;
    public int currentPrice;
    public int costPerEntry;
    public String highestBidder;
    public String category;
    public Date endDate;
    public List<File> listOfImages;
    public boolean isTrending;

    /**
     * Default constructor for ItemDetails (Auction / Raffle items)
     * @param id - id of item
     * @param itemName - name of the item
     * @param itemDescription - description of the item
     * @param startBid - start bid amount of the item
     * @param buyNowPrice - buyItNow price (for eligible items)
     * @param currentPrice - current price for the item (for auction-able items)
     * @param highestBidder - highest current bidder
     * @param category - category of the item (raffle, auction, drop)
     * @param endDate - endDate for the item (for raffles, auctions)
     * @param costPerEntry - cost per entry for raffle items
     * @param listOfImages - list Of images of the item
     * @param isTrending - check if the item is trending
     */
    public ItemDetails(String id, String itemName, String itemDescription, int startBid, int buyNowPrice,
                       int currentPrice, String highestBidder, String category, Date endDate,
                       int costPerEntry, List<File> listOfImages, boolean isTrending) {
        this.id = id;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.startBid = startBid;
        this.buyNowPrice = buyNowPrice;
        this.currentPrice = currentPrice;
        this.highestBidder = highestBidder;
        this.category = category;
        this.endDate = endDate;
        this.costPerEntry = costPerEntry;
        this.listOfImages = listOfImages;
        this.isTrending = isTrending;
    }

    /**
     * Printing item details for debugging
     * @return - item details; printed
     */
    public String printData() {
        return "Name: " + this.itemName + " category: " + this.category + " costPerEntry: " +
                this.costPerEntry + " startBid: " + this.startBid + " highest bidder: " +
                this.highestBidder + " with images: " + this.listOfImages.size() + " and endtime: "
                + (this.endDate == null ? "N/A" : this.endDate);
    }
}
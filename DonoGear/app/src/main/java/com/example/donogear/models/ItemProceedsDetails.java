package com.example.donogear.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Storing details for proceeds for a specific item
 */
public class ItemProceedsDetails {
    public String itemId;
    public String title;
    public String description;
    public List<File> proceedsImagesList;
    public List<String> proceedsVideosList;

    public ItemProceedsDetails() { }

    /**
     * Constructor
     * @param itemId - id of the item
     * @param title - title of the proceed (for the item)
     * @param description - description of the proceed
     * @param proceedsImages - list of images for the proceed
     * @param proceedsVideos - list of videos for the proceed
     */
    public ItemProceedsDetails(String itemId, String title, String description, List<File> proceedsImages,
                               List<String> proceedsVideos) {
        this.itemId = itemId;
        this.title = title;
        this.description = description;
        this.proceedsImagesList = new ArrayList<>(proceedsImages);
        this.proceedsVideosList = new ArrayList<>(proceedsVideos);
    }

    /**
     * Printing item-proceed-details for debugging
     * @return - item-proceed-details; printed
     */
    public String printProceeds() {
        return this.title + ": " + this.description + " and has " +
                this.proceedsImagesList.size() + " images and " + this.proceedsVideosList.size() +
                " videos";
    }
}

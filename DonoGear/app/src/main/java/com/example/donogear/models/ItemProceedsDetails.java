package com.example.donogear.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ItemProceedsDetails {
    public String itemId;
    public String title;
    public String description;
    public List<File> proceedsImagesList;
    public List<String> proceedsVideosList;

    public ItemProceedsDetails() { }

    public ItemProceedsDetails(String itemId, String title, String description, List<File> proceedsImages,
                               List<String> proceedsVideos) {
        this.itemId = itemId;
        this.title = title;
        this.description = description;
        this.proceedsImagesList = new ArrayList<>(proceedsImages);
        this.proceedsVideosList = new ArrayList<>(proceedsVideos);
    }

    public String printProceeds() {
        return this.title + ": " + this.description + " and has " +
                this.proceedsImagesList.size() + " images and " + this.proceedsVideosList.size() +
                " videos";
    }
}

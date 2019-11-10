package com.example.donogear;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ItemProceedsDetails {
    String itemId;
    String title;
    String description;
    List<File> proceedsImagesList;
    List<String> proceedsVideosList;

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

package com.example.donogear.models;

import java.io.File;
import java.util.List;

public class CausesDetails {
    public String id;
    public String causeTitle;
    public String causeCategory;
    public List<File> images;
    public String websiteUrl;

    public CausesDetails(String id, String causeTitle, String causeCategory, List<File> images, String websiteUrl) {
        this.id = id;
        this.causeTitle = causeTitle;
        this.causeCategory = causeCategory;
        this.images = images;
        this.websiteUrl = websiteUrl;
    }


}

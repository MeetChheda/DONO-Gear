package com.example.donogear.models;

import java.io.File;
import java.util.List;

/**
 * Storing details for each cause
 */
public class CausesDetails {
    public String id;
    public String causeTitle;
    public String causeCategory;
    public List<File> images;
    public String websiteUrl;


    /**
     * Default constructor for cause details
     * @param id - id of cause
     * @param causeTitle - title of a cause
     * @param causeCategory - category of cause
     * @param images - images of a cause
     * @param websiteUrl - url of cause
     */
    public CausesDetails(String id, String causeTitle, String causeCategory, List<File> images, String websiteUrl) {
        this.id = id;
        this.causeTitle = causeTitle;
        this.causeCategory = causeCategory;
        this.images = images;
        this.websiteUrl = websiteUrl;
    }
}

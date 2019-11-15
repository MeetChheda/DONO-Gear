package com.example.donogear.models;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * Storing details for each donor
 */
public class DonorDetails implements Serializable {
    public String id;
    public String donorName;
    public String category;
    public List<File> images;

    /**
     * Default constructor for donor details
     * @param id - id of donor
     * @param donorName - name of donor
     * @param category - category of donor
     * @param images - images of donor
     */
    public DonorDetails(String id, String donorName, String category, List<File> images) {
        this.id = id;
        this.donorName = donorName;
        this.category = category;
        this.images = images;
    }
}

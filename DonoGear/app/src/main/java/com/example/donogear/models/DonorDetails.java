package com.example.donogear.models;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class DonorDetails implements Serializable {
    public String id;
    public String donorName;
    public String category;
    public List<File> images;

    public DonorDetails(String id, String donorName, String category, List<File> images) {
        this.id = id;
        this.donorName = donorName;
        this.category = category;
        this.images = images;
    }
}

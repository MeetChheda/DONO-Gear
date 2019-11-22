package com.example.donogear.models;

import java.io.File;
import java.util.List;

public class AnnouncementDetails {
    public String id;
    public String announcementTitle;
    public String announcementDescription;
    public List<File> images;

    public AnnouncementDetails(String id, String announcementTitle, String announcementDescription, List<File> images) {
        this.id = id;
        this.announcementTitle = announcementTitle;
        this.announcementDescription = announcementDescription;
        this.images = images;
    }
}



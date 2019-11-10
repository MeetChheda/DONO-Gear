package com.example.donogear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.donogear.MainActivity.COLLECTIBLE_VIDEOS;
import static com.example.donogear.MainActivity.PROCEEDS;

public class ProductDetails extends AppCompatActivity {

    private String itemId;
    private String itemName;
    private List<File> itemImages;
    private List<String> itemVideosUrl;
    private ItemProceedsDetails proceedsDetails;

    private ParseQuery<ParseObject> itemVideosQuery, itemProceedsQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Intent intent = getIntent();
        ItemDetails itemDetails = (ItemDetails) intent.getSerializableExtra("item_details");
        itemId = itemDetails.id;
        itemVideosUrl = getItemVideos();
        Toast.makeText(this, itemVideosUrl.size() + "", Toast.LENGTH_SHORT).show();
        getItemProceedsDetails();
        //System.out.println(proceedsDetails.printProceeds());
    }

    private List<String> getItemVideos() {
        itemVideosQuery = ParseQuery.getQuery(COLLECTIBLE_VIDEOS);
        itemVideosQuery.whereEqualTo("collectibleId", itemId);
        System.out.println(itemId);
        final List<String> allVideos = new ArrayList<>();
        itemVideosQuery.getFirstInBackground((object, e) -> {
            if (e == null) {
                for (int i = 1; i < 4; i++) {
                    if (object.has("video" + i)) {
                        if (object.getString("video" + i) != null) {
                            String videoUrl = object.getString("video" + i);
                            System.out.println(videoUrl);
                            allVideos.add(videoUrl);
                        }
                    }
                }
            }
        });
        return allVideos;
    }

    private void getItemProceedsDetails() {
        proceedsDetails = new ItemProceedsDetails();
        itemProceedsQuery = ParseQuery.getQuery(PROCEEDS);
        itemProceedsQuery.whereEqualTo("collectibleId", itemId);
        itemProceedsQuery.getFirstInBackground((object, e) -> {
            if (e == null) {
                final String proceedTitle = object.getString("proceedTitle");
                System.out.println("Title " + proceedTitle);
                final String proceedDescription = object.getString("proceedDescription");
                System.out.println("Desc " + proceedDescription);
                final List<File> proceedsImages = new ArrayList<>();
                final List<String> proceedVideo = new ArrayList<>();
                for (int i = 1; i < 3; i++) {
                    if (object.getParseFile("proceedImage" + i) != null) {
                        try {
                            if (object.getParseFile("proceedImage" + i).getFile() != null) {
                                File image = object.getParseFile("proceedImage" + i).getFile();
                                System.out.println("Added image " + i);
                                proceedsImages.add(image);
                            }
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                for (int i = 1; i < 2; i++) {
                    if (object.has("proceedVideo" + i)) {
                        if (object.getString("proceedVideo" + i) != null) {
                            String videoUrl = object.getString("proceedVideo" + i);
                            System.out.println("Added video " + i);
                            proceedVideo.add(videoUrl);
                        }
                    }
                }
                proceedsDetails = new ItemProceedsDetails(itemId, proceedTitle, proceedDescription,
                        proceedsImages, proceedVideo);
            }
        });
    }
}

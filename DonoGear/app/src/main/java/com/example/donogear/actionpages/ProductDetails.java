package com.example.donogear.actionpages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.donogear.R;
import com.example.donogear.interfaces.TickTime;
import com.example.donogear.models.ItemDetails;
import com.example.donogear.models.ItemProceedsDetails;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.donogear.utils.Constants.COLLECTIBLE_VIDEOS;
import static com.example.donogear.utils.Constants.PROCEEDS;

public class ProductDetails extends AppCompatActivity {

    private String itemId, itemName, itemDescription, itemHighestBidder;
    private int itemBidAmount;
    private Date itemTime;
    private List<File> itemImages;
    private List<String> itemVideosUrl;
    private ItemProceedsDetails proceedsDetails;
    private ParseQuery<ParseObject> itemVideosQuery, itemProceedsQuery;
    private Handler handler;
    private Context context;

    private boolean hasVideos, hasProceeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        initData();
        itemVideosUrl = getItemVideos();
        getItemProceedsDetails();
        LinearLayout horizontalScrollViewContainer = findViewById(R.id.inner_layout);
        LinearLayout itemVideosLayout = findViewById(R.id.videoButtons);
        displayImages(itemImages, horizontalScrollViewContainer);
        displayRemainingTime();
        displayItemDetails();
        Runnable videoRunnable = new Runnable() {
            @Override
            public void run() {
                if(hasVideos) {
                    displayVideo(itemVideosUrl, itemVideosLayout);
                }
                else {
                    System.out.println("Video? : " + hasVideos);
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(videoRunnable);

        Runnable proceedsRunnable = new Runnable() {
            @Override
            public void run() {
                if(hasProceeds) {
                    displayProceedsDetails();
                }
                else {
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(proceedsRunnable);
    }

    private void initData() {
        handler = new Handler();
        context = getBaseContext();

        Intent intent = getIntent();
        ItemDetails itemDetails = (ItemDetails) intent.getSerializableExtra("item_details");
        itemId = itemDetails.id;
        itemName = itemDetails.itemName;
        itemDescription = itemDetails.itemDescription;
        itemImages = itemDetails.listOfImages;
        itemTime = itemDetails.endDate;
        itemHighestBidder = itemDetails.highestBidder;
        itemBidAmount = itemDetails.currentPrice;

        ImageButton back =  findViewById(R.id.backbtn);
        back.setOnClickListener(view -> finish());
        hasProceeds = false;
        hasVideos = false;
    }

    /**
     * Displays the video snipper
     * TODO - Working code for snippet / better UI to show video display button
     */
    private void displayVideo(List<String> videoList, LinearLayout videoContainer) {
//        final VideoView videoView = findViewById(R.id.videoView);
        if (videoList == null || videoList.size() == 0) {
            return;
        }
//        videoView.setVisibility(View.VISIBLE);
//        MediaController mediaController = new MediaController(context);
//        mediaController.setAnchorView(videoView);
//        mediaController.setMediaPlayer(videoView);
//        videoView.setMediaController(mediaController);
        for (int i = 0; i < videoList.size(); i += 3) {
            LinearLayout newLayout = new LinearLayout(getBaseContext());
            newLayout.setOrientation(LinearLayout.HORIZONTAL);
            newLayout.setBaselineAligned(false);

            for (int j = 0; j < 3 && j + i < videoList.size(); j++) {
                final Button button = new Button(getBaseContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        320, 130
                );
                layoutParams.setMargins(15, 15, 15, 0);
                layoutParams.gravity = Gravity.TOP;
                button.setLayoutParams(layoutParams);
                button.setTextColor(Color.BLACK);
                String number = "Video " + (i + j + 1);
                button.setText(number);
                newLayout.addView(button);

                Uri video = Uri.parse(videoList.get(i + j));
                button.setOnClickListener(view ->
                        startActivity(new Intent(Intent.ACTION_VIEW, video)));
            }
            videoContainer.addView(newLayout);
        }
    }

    /**
     * Displays all other details for an item after data has been read in background
     */
    private void displayItemDetails() {
        TextView titleText = findViewById(R.id.title);
        titleText.setText(itemName);
        TextView descriptionText = findViewById(R.id.description);
        descriptionText.setText(itemDescription);
        TextView bidderText = findViewById(R.id.bidder);
        if (itemHighestBidder.equals("Null")) {
            bidderText.setVisibility(View.VISIBLE);
            bidderText.setText("No current bids. Be the first one to bid!");
        }
        TextView bidAmount = findViewById(R.id.bid_holder);
        if (itemBidAmount > 0) {
            String text = bidAmount.getText().toString();
            text += "<font color='#2fd6d6'>" + itemBidAmount + "</font>";
            bidAmount.setText(Html.fromHtml(text));
            bidAmount.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Uses TickTime interface to display time left for the current auction / raffle to finish
     */
    private void displayRemainingTime() {
        TextView time_remaining = findViewById(R.id.time_remaining);
        long timeInMilliSec = itemTime.getTime() - Calendar.getInstance().getTimeInMillis();
        new CountDownTimer(timeInMilliSec, 1000) {
            @Override
            public void onTick(long l) {
                String newTime = TickTime.displayTime(l);
                time_remaining.setText(newTime);
            }

            @Override
            public void onFinish() {
                time_remaining.setText("TIME UP");
                time_remaining.setTextColor(Color.RED);
                //itemHolder.timeHolder.setVisibility(View.GONE);
            }
        }.start();
    }

    /**
     * Display the proceed details for the selected item after it has been read in background. It
     * reuses the methods to displayImages and Videos for the proceeds
     */
    private void displayProceedsDetails() {
        System.out.println(proceedsDetails.printProceeds());
        System.out.println(proceedsDetails.printProceeds());
        TextView proceedsTitle = findViewById(R.id.proceeds_title);
        proceedsTitle.setText(proceedsDetails.title);

        TextView proceedsDescription = findViewById(R.id.proceeds_description);
        proceedsDescription.setText(proceedsDetails.description);
        LinearLayout proceeds_images_layout = findViewById(R.id.proceeds_images);
        displayImages(proceedsDetails.proceedsImagesList, proceeds_images_layout);

        LinearLayout proceeds_video_layout = findViewById(R.id.proceed_video_buttons);
        displayVideo(proceedsDetails.proceedsVideosList, proceeds_video_layout);
    }


    /**
     * Display the images in a layout.
     * TODO: Would have to change use separate methods to incorporate horizontal scroll of images
     *       (for item) and one with normal vertical layout (for proceeds)
     */
    private void displayImages(List<File> imagesList, LinearLayout layout) {
        for (File image: imagesList) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            params.gravity = Gravity.CENTER;
            params.setMargins(5, 5, 5, 5);
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(params);
            Bitmap bitmap = BitmapFactory.decodeFile(image.toString());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageBitmap(bitmap);
            layout.addView(imageView);
        }
    }

    /**
     * Queries the database to get videos for the selected item
     * @return - list of URL's of all videos for items
     */
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
            hasVideos = true;
        });
        return allVideos;
    }

    /**
     * Get the proceed details for a single item
     */
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
            hasProceeds = true;
        });

    }
}

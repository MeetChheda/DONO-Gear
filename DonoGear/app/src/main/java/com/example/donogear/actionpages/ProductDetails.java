package com.example.donogear.actionpages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donogear.R;
import com.example.donogear.interfaces.TickTime;
import com.example.donogear.models.ItemDetails;
import com.example.donogear.models.ItemProceedsDetails;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.donogear.utils.Constants.COLLECTIBLE_VIDEOS;
import static com.example.donogear.utils.Constants.PRIMARY_COLOR;
import static com.example.donogear.utils.Constants.PROCEEDS;
import static com.example.donogear.utils.Constants.RAFFLE_IDENTIFIER;

public class ProductDetails extends AppCompatActivity {

    private String itemId, itemName, itemDescription, itemHighestBidder, category;
    private int itemBidAmount, startBid, costPerEntry;
    private Date itemTime;
    private List<File> itemImages;
    private List<String> itemVideosUrl;
    private ItemProceedsDetails proceedsDetails;
    private Handler handler;
    private Context context;
    private Button button;
    private boolean flag = false;
    private boolean hasVideos, hasProceeds;
    private LinearLayout raffle_buttons;
    private RelativeLayout full_layout;

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
        if (category.equals(RAFFLE_IDENTIFIER)) {
            displayRaffleButtons();
            button.setText("Enter");
        } else {
            displayBidDetails();
        }

        Runnable videoRunnable = new Runnable() {
            @Override
            public void run() {
                if(hasVideos) {
                    displayVideo(itemVideosUrl, itemVideosLayout);
                }
                else {
//                    System.out.println("Video? : " + hasVideos);
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

    private void displayRaffleButtons() {
        List<Integer> ticketDenominations = Arrays.asList(
                100, 250, 500, 1000, 2500, 5000, 10000, 25000
        );
        for (int j = 0; j <= ticketDenominations.size(); j += 2) {
            LinearLayout newLayout = new LinearLayout(this);
            newLayout.setOrientation(LinearLayout.HORIZONTAL);
            newLayout.setBaselineAligned(false);
            for (int i = 0; i < 2 && j + i < ticketDenominations.size(); i++) {
                final Button button = new Button(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        400, 200
                );
                layoutParams.setMargins(70, 30, 50, 0);
                layoutParams.gravity = Gravity.TOP;
                button.setLayoutParams(layoutParams);
                button.setTextColor(Color.BLACK);
                String price = "$" + (1.0 * ticketDenominations.get(i + j) / costPerEntry);
                price += "\n" + ticketDenominations.get(i + j) + " Entries";
                setButtonLayout(button, PRIMARY_COLOR, Color.WHITE);
                button.setText(price);

                newLayout.addView(button);
                button.setOnClickListener(v -> Toast.makeText(context,
                        button.getText(), Toast.LENGTH_SHORT).show());
            }
            raffle_buttons.addView(newLayout);
        }
    }

    /**
     * Initialized variables and layout views
     */
    private void initData() {
        handler = new Handler();
        context = getBaseContext();

        Intent intent = getIntent();
        ItemDetails itemDetails = (ItemDetails) intent.getSerializableExtra("item_details");
        category = intent.getStringExtra("typeOfSearch");
        itemId = itemDetails.id;
        itemName = itemDetails.itemName;
        itemDescription = itemDetails.itemDescription;
        itemImages = itemDetails.listOfImages;
        itemTime = itemDetails.endDate;
        itemHighestBidder = itemDetails.highestBidder;
        itemBidAmount = itemDetails.currentPrice;
        startBid = itemDetails.startBid;
        costPerEntry = itemDetails.costPerEntry;

        full_layout = findViewById(R.id.full_item_layout);
        raffle_buttons = findViewById(R.id.raffle_buttons);
        ImageButton back =  findViewById(R.id.backbtn);
        back.setOnClickListener(view -> {
            if (flag) {
                flag = false;
                full_layout.setVisibility(View.VISIBLE);
                raffle_buttons.setVisibility(View.GONE);
                return;
            }
            finish();
        });

        button = findViewById(R.id.enter);
        button.setOnClickListener(view -> {
            full_layout.setVisibility(View.GONE);
            flag = true;
            raffle_buttons.setVisibility(View.VISIBLE);
        });
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
    }

    private void displayBidDetails() {
        TextView bidderText = findViewById(R.id.bidder);
        if (itemHighestBidder == null) {
            bidderText.setVisibility(View.VISIBLE);
            bidderText.setText("No current bids. Be the first one to bid!");
        }
        TextView bidAmount = findViewById(R.id.bid_holder);
        String text = bidAmount.getText().toString();
        if (itemBidAmount > 0) {
            text += "<font color='#2fd6d6'>$" + itemBidAmount + "</font>";
        } else {
            text = "Starting Bid: ";
            text += "<font color='#2fd6d6'>$" + startBid + "</font>";
        }
        bidAmount.setText(Html.fromHtml(text));
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
                    850
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
        ParseQuery<ParseObject> itemVideosQuery = ParseQuery.getQuery(COLLECTIBLE_VIDEOS);
        itemVideosQuery.whereEqualTo("collectibleId", itemId);
        final List<String> allVideos = new ArrayList<>();
        itemVideosQuery.getFirstInBackground((object, e) -> {
            if (e == null) {
                for (int i = 1; i < 4; i++) {
                    if (object.has("video" + i)) {
                        if (object.getString("video" + i) != null) {
                            String videoUrl = object.getString("video" + i);
//                            System.out.println(videoUrl);
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
        ParseQuery<ParseObject> itemProceedsQuery = ParseQuery.getQuery(PROCEEDS);
        itemProceedsQuery.whereEqualTo("collectibleId", itemId);
        itemProceedsQuery.getFirstInBackground((object, e) -> {
            if (e == null) {
                final String proceedTitle = object.getString("proceedTitle");
//                System.out.println("Title " + proceedTitle);
                final String proceedDescription = object.getString("proceedDescription");
//                System.out.println("Desc " + proceedDescription);
                final List<File> proceedsImages = new ArrayList<>();
                final List<String> proceedVideo = new ArrayList<>();
                for (int i = 1; i < 3; i++) {
                    if (object.getParseFile("proceedImage" + i) != null) {
                        try {
                            if (object.getParseFile("proceedImage" + i).getFile() != null) {
                                File image = object.getParseFile("proceedImage" + i).getFile();
//                                System.out.println("Added image " + i);
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
//                            System.out.println("Added video " + i);
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



    /**
     * Defines layout for a button, such as color, position, size etc
     * @param button - a single button
     * @param borderColor - color for the border
     * @param bgColor - background color, for the button
     */
    private void setButtonLayout(Button button, int borderColor, int bgColor) {
        button.setTextColor(borderColor);
        float[] outerRadii = new float[]{75,75,75,75,75,75,75,75};
        float[] innerRadii = new float[]{75,75,75,75,75,75,75,75};
        ShapeDrawable borderDrawable = new ShapeDrawable(new RoundRectShape(
                outerRadii,
                null,
                innerRadii
        ));
        borderDrawable.getPaint().setColor(borderColor);
        borderDrawable.getPaint().setStyle(Paint.Style.FILL);
        // Define the border width
        borderDrawable.setPadding(5,5,5,5);
        // Set the shape background
        ShapeDrawable backgroundShape = new ShapeDrawable(new RoundRectShape(
                outerRadii,
                null,
                innerRadii
        ));
        backgroundShape.getPaint().setColor(bgColor); // background color
        backgroundShape.getPaint().setStyle(Paint.Style.FILL); // Define background
        backgroundShape.getPaint().setAntiAlias(true);

        // Initialize an array of drawables
        Drawable[] drawables = new Drawable[]{ borderDrawable, backgroundShape };
        backgroundShape.setPadding(10,10,10,10);
        LayerDrawable layerDrawable = new LayerDrawable(drawables);

        button.setBackground(layerDrawable);
    }
}

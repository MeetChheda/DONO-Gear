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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donogear.R;
import com.example.donogear.interfaces.ButtonDesign;
import com.example.donogear.interfaces.TickTime;
import com.example.donogear.interfaces.onSavePressed;
import com.example.donogear.models.ItemDetails;
import com.example.donogear.models.ItemProceedsDetails;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.view.View.GONE;
import static com.example.donogear.utils.Constants.AUCTION_IDENTIFIER;
import static com.example.donogear.utils.Constants.COLLECTIBLE_VIDEOS;
import static com.example.donogear.utils.Constants.DEFAULT_BID_MESSAGE;
import static com.example.donogear.utils.Constants.DROP_IDENTIFIER;
import static com.example.donogear.utils.Constants.PRIMARY_COLOR;
import static com.example.donogear.utils.Constants.PROCEEDS;
import static com.example.donogear.utils.Constants.RAFFLE_IDENTIFIER;
import static com.example.donogear.utils.Constants.TIME_UP;

public class ProductDetails extends AppCompatActivity implements ButtonDesign,
        View.OnClickListener, onSavePressed {

    private String itemId, itemName, itemDescription, itemHighestBidder, category;
    private int itemBidAmount, startBid, costPerEntry;
    private Date itemTime;
    private List<File> itemImages;
    private List<String> itemVideosUrl;
    private ItemProceedsDetails proceedsDetails;
    private Handler handler;
    private Context context;
    private boolean flag = false;
    private boolean hasVideos, hasProceeds;
    private LinearLayout raffle_buttons;
    private RelativeLayout full_layout;
    private Button raffle;
    private Button auction;
    private Button drop;
    private TextView startBidAmount;
    private TextView currentBidAmount;
    private TextView bidderText;

    private BottomSheetDialogFragment dialogFragment;

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
        checkCategory(category);

        /**
         * Delay timers (2) to facilitate populating of layout only after background tasks of fetching
         * the item-specific videos and proceeds details have been finished
         */
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

    /**
     * Displays the different denominations of buying raffle-tickets as buttons. Clicking on this
     * will take the user directly to the payment page
     */
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
                ButtonDesign.setButtonLayout(button, PRIMARY_COLOR, Color.WHITE);
//                setButtonLayout(button, PRIMARY_COLOR, Color.WHITE);
                button.setText(price);

                newLayout.addView(button);
                button.setOnClickListener(v -> Toast.makeText(context,
                        button.getText(), Toast.LENGTH_SHORT).show());
            }
            raffle_buttons.addView(newLayout);
        }
    }

    /**
     * Initialized variables and layout views along with behaviour for buttons
     */
    private void initData() {
        handler = new Handler();
        context = getBaseContext();

        Intent intent = getIntent();
        ItemDetails itemDetails = (ItemDetails) intent.getSerializableExtra("item_details");
        category = intent.getStringExtra("category");
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
        startBidAmount = findViewById(R.id.start_bid_amount);
        currentBidAmount = findViewById(R.id.current_bid_amount);
        bidderText = findViewById(R.id.no_current_bids);

        raffle = findViewById(R.id.enter);
        auction = findViewById(R.id.bid);
        drop = findViewById(R.id.buy);

        raffle.setOnClickListener(this);
        auction.setOnClickListener(this);
        drop.setOnClickListener(this);
        findViewById(R.id.backbtn).setOnClickListener(this);
        hasProceeds = false;
        hasVideos = false;
    }

    /**
     * Displays the video snipper
     * TODO - Working code for snippet / better UI to show video display button
     */
    private void displayVideo(List<String> videoList, LinearLayout videoContainer) {
//        final VideoView videoView = findViewById(R.id.videoView);
        if (videoList == null || videoList.size() == 0)
            return;
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

    /**
     * Displays the bid details for auction items ONLY
     */
    private void displayBidDetails() {
        findViewById(R.id.bidLayout).setVisibility(View.VISIBLE);
        startBidAmount.setText("$" + startBid);
        currentBidAmount.setText("$" + itemBidAmount);

        bidderText = findViewById(R.id.no_current_bids);
        if (itemBidAmount == 0) {
            bidderText.setVisibility(View.VISIBLE);
            bidderText.setText(DEFAULT_BID_MESSAGE);
            currentBidAmount.setVisibility(GONE);
            findViewById(R.id.current_bid_holder).setVisibility(GONE);
        }
    }

    /**
     * Uses TickTime interface to display time left for the current auction / raffle to finish
     */
    private void displayRemainingTime() {
        if (category.equals(DROP_IDENTIFIER))
            return;
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
                time_remaining.setText(TIME_UP);
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
        if (imagesList == null)
            return;
        for (File image: imagesList) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    759
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
     * Checks whoch item was clicked on the previous search / home page. Behaviour of buttons and
     * layouts is changed accordingly
     * @param category - thr category of the selected item
     */
    private void checkCategory(String category) {
        switch (category) {
            case RAFFLE_IDENTIFIER:
                drop.setVisibility(GONE);
                auction.setVisibility(GONE);
                displayRaffleButtons();
                break;

            case AUCTION_IDENTIFIER:
                drop.setVisibility(GONE);
                raffle.setVisibility(GONE);
                displayBidDetails();
                break;

            case DROP_IDENTIFIER:
                auction.setVisibility(GONE);
                raffle.setVisibility(GONE);
                break;

            default:
                break;
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
                final String proceedDescription = object.getString("proceedDescription");
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
     * Check if 'Enter' button is pressed for raffles. This method is used to tweak back button
     * pressed behaviour based on the current view
     * @return - whether the back button should return to previous activity or not
     */
    private boolean checkButtonPressed(boolean check) {
        if (check) {
            flag = false;
            full_layout.setVisibility(View.VISIBLE);
            raffle_buttons.setVisibility(GONE);
            raffle.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    private void bottomSheetForItem() {
        Bundle bundle = new Bundle();
        bundle.putInt("currentBid", itemBidAmount);
        bundle.putInt("startBid", startBid);
        bundle.putString("highestBidder", itemHighestBidder);
        dialogFragment = new ItemBidFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getSupportFragmentManager(), dialogFragment.getTag());


        final View bottomSheetLayout = getLayoutInflater().inflate(R.layout.item_bottom_sheet, null);
        (bottomSheetLayout.findViewById(R.id.cancel)).setOnClickListener(view ->
                dialogFragment.dismiss());
        (bottomSheetLayout.findViewById(R.id.done)).setOnClickListener(v ->
                Toast.makeText(getApplicationContext(), "Ok button clicked", Toast.LENGTH_SHORT).show());
    }

    //TODO - Implement functionality for place bid button
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backbtn:
                if (!checkButtonPressed(flag)) {
                    finish();
                }
                break;

            case R.id.enter:
                flag = true;
                raffle_buttons.setVisibility(View.VISIBLE);
                full_layout.setVisibility(GONE);
                raffle.setVisibility(GONE);
                break;

            case R.id.buy:
                //TODO - button for drops
                break;

            case R.id.bid:
                bottomSheetForItem();
                break;

            default:
                break;

        }
    }

    @Override
    public void onBackPressed() {
        if (!checkButtonPressed(flag)) {
            super.onBackPressed();
        }
    }

    @Override
    public void passData(Bundle bundle) {
        int newBidAmount = bundle.getInt("userBid");
        Toast.makeText(context, "New value is " + newBidAmount, Toast.LENGTH_SHORT).show();
        bidderText.setVisibility(GONE);
        currentBidAmount.setText("$" + newBidAmount);
        if (itemBidAmount == 0) {
            currentBidAmount.setVisibility(View.VISIBLE);
            findViewById(R.id.current_bid_holder).setVisibility(View.VISIBLE);
        }
        itemBidAmount = newBidAmount;
    }
}

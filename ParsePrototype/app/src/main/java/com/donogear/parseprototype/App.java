package com.donogear.parseprototype;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class App extends Activity {
    private Timer myTimer;
    private int currentPrice = 0;
    private String itemName = "MyItem";
    int secondsLeft = 16800;
    ImageButton imageButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        // Set up configurations with Back4App server
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                // if defined
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Item");
        query.whereEqualTo("name", itemName);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> items, ParseException e) {
                TextView currentBidTextView = findViewById(R.id.currentBid);
                TextView bidby = findViewById(R.id.bidby);
                TextView timeLeft = findViewById(R.id.timeleft);
                imageButton = findViewById(R.id.pricebutton);
                if (e == null) {
                    if (!items.isEmpty()) {
                        currentPrice = (Integer) items.get(0).get("price");
                        currentBidTextView.setText("$"+ String.valueOf(currentPrice));
                        if (String.valueOf(currentPrice).length() == 3) {
                            imageButton.setLayoutParams(new RelativeLayout.LayoutParams(160, 110));
                        }
                        if (String.valueOf(currentPrice).length() == 4) {
                            imageButton.setLayoutParams(new RelativeLayout.LayoutParams(210, 110));
                        }
                        bidby.setText(String.valueOf(items.get(0).get("user")));
                        float hoursLeft = (float)secondsLeft / 3600;
                        float minsLeft = hoursLeft - (int)hoursLeft;
                        minsLeft = minsLeft * 60;
                        timeLeft.setText((int)hoursLeft+"h " + (int)minsLeft+"m");
                    }
                } else {
                    // object does not exist
                    currentPrice = 0;
                }


            }
        });

        // Create a timer to check for updated price every second
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                secondsLeft = secondsLeft - 1;
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Item");
                query.whereEqualTo("name", itemName);
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> items, ParseException e) {
                        TextView currentBidTextView = findViewById(R.id.currentBid);
                        TextView bidby = findViewById(R.id.bidby);
                        TextView timeLeft = findViewById(R.id.timeleft);
                        if (e == null) {
                            if (!items.isEmpty()) {
                                currentPrice = (Integer) items.get(0).get("price");
                                //currentBidTextView.setText("Current Bid: " + String.valueOf(currentPrice) + " by " + items.get(0).get("user"));
                                currentBidTextView.setText("$"+ String.valueOf(currentPrice));
                                bidby.setText(String.valueOf(items.get(0).get("user")));
                                float hoursLeft = (float)secondsLeft / 3600;
                                float minsLeft = hoursLeft - (int)hoursLeft;
                                minsLeft = minsLeft * 60;
                                timeLeft.setText((int)hoursLeft+"h " + (int)minsLeft+"m");
                            }
                        } else {
                            // object does not exist
                            currentPrice = 0;
                        }
                    }
                });
            }

        }, 0, 1000);
    }

    public void sendData(View view) {
        // Get the input new bid
        TextView newBidTextView = (TextView)findViewById(R.id.newBid);
        String newBid = newBidTextView.getText().toString().trim();

        // Get the input user name
        TextView userTextView = (TextView)findViewById(R.id.user_name_edit_text);
        final String user = userTextView.getText().toString().trim();

        // Only proceed if the bid is provided
        if (!newBid.isEmpty()) {

            // Only proceed if the bid is greater than the current price
            if (Integer.valueOf(newBid) > currentPrice) {
                final int bid = Integer.valueOf(newBid);

                // Check to see if the object already exists before creating a new object
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Item");
                query.whereEqualTo("name", itemName);
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> items, ParseException e) {
                        ParseObject item;

                        if (e == null) {
                            if (!items.isEmpty()) {
                                // item exists
                                item = items.get(0);
                            } else {
                                // new item
                                item = new ParseObject("Item");
                                item.put("name", itemName);
                            }

                            item.put("user", user);
                            item.put("price", bid);
                            item.saveInBackground();
                        } else {

                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Please bid higher", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

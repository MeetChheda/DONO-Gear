package com.example.donogear.interfaces;

import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import static com.example.donogear.utils.Constants.COLLECTIBLES;

public interface RealTimeUpdate {

    static void writeNewBid(final String itemId, final int newBidAmount, final ParseUser user) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(COLLECTIBLES);
        query.whereEqualTo("objectId", itemId);
        query.getFirstInBackground((object, e) -> {
            if (e == null) {
                object.put("currentBid", newBidAmount);
                String userName = "guest";
                if (user != null) {
                    userName = user.getUsername();
                }
                object.put("highestBidder", userName);
                object.saveInBackground();
                Log.d("Interface" , "Writing value: " + newBidAmount);
            }
        });
    }
}

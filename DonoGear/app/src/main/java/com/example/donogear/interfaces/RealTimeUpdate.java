package com.example.donogear.interfaces;

import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import static com.example.donogear.utils.Constants.COLLECTIBLES;

public interface RealTimeUpdate {

    static void writeNewBid(final String itemId, final int newBidAmount, final String userName) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(COLLECTIBLES);
        query.whereEqualTo("objectId", itemId);
        query.getFirstInBackground((object, e) -> {
            if (e == null) {
                object.put("currentBid", newBidAmount);
                object.put("highestBidder", userName);
                object.saveInBackground();
                Log.d("Interface" , "Writing value: " + newBidAmount);
            }
        });
    }
}

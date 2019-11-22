package com.example.donogear.utils;

import android.graphics.Color;

public class Constants {

    //Table name in database
    public static final String COLLECTIBLES = "Collectible";
    public static final String COLLECTIBLE_IMAGES = "CollectibeImages";
    public static final String COLLECTIBLE_VIDEOS = "CollectibleVideos";
    public static final String DONOR = "Donor";
    public static final String PROCEEDS = "Proceeds";
    public static final String TAGS = "Tags";
    public static final String ANNOUNCEMENTS = "Announcement";

    public static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    //constant values
    public static final String AUCTION_IDENTIFIER = "auction";
    public static final String RAFFLE_IDENTIFIER = "raffle";
    public static final String DROP_IDENTIFIER = "drop";
    public static final String DONOR_IDENTIFIER = "donor";
    public static final String CAUSES_IDENTIFIER = "causes";
    public static final String HOME_IDENTIFIER = "home";
    public static final String MY_INTERESTS = "myinterests";
    public static final String TRENDING = "trending";
    public static final String SEARCH = "search";
    public static final String PROFILE = "profile";


    public static final int PRIMARY_COLOR = Color.parseColor("#2fd6d6");
    public static final int ADD_TO_BID = 1;
    public static final int SUBTRACT_FROM_BID = -1;

    public static final String TIME_UP = "TIME UP";
    public static final String DEFAULT_BID_MESSAGE = "No current bids. Be the first one!";

    public static final String USER_DETAILS = "user_details";
    public static final String USER_NAME = "user_name";
}

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
    public static final String ITEM_NAME = "itemName";
    public static final String ITEM_ID = "collectibleId";
    public static final String BUY_NOW = "buyNowPrice";
    public static final String RAFFLE_COUNT = "raffleTicketCount";

    public static final int PRIMARY_COLOR = Color.parseColor("#2fd6d6");
    public static final int ADD_TO_BID = 1;
    public static final int SUBTRACT_FROM_BID = -1;

    public static final String TIME_UP = "TIME UP";

    public static final String NO_CURRENT_BIDS = "N / A";

    public static final String READ_MORE = "Read more";
    public static final String READ_LESS = "Read less";

    public static final String ERROR_BID_TITLE = "Error placing your bid";
    public static final String ERROR_BID_MESSAGE =
            "The most recent bid amount was changed when you were trying to bid. Do you want to " +
                    "bid again?";

    public static final String ALERT_MESSAGE = "Are you sure?";
    public static final String HIGHEST_BID_MESSAGE = "You are already the highest bidder. Do you " +
            "still want to continue?";

    public static final String LOGIN_PROMPT = "Please login to place a bid";
    public static final String SAVE_USER_DETAILS = "Save details";
    public static final String UPDATE_USER_DETAILS = "Update details";
    public static final String SAVE_USER_INTERESTS = "Saved user preferences";
    public static final String LOGIN_ERROR = "Unknown error occurred. Try logging in again";
    public static final String LOGIN_FOR_DETAILS = "Log in / register with DONO-Gear to create a " +
            "profile and use many exciting features";

    public static final String INVALID_CARD_NUMBER = "Stripe - The card number that you entered is invalid";
    public static final String INVALID_CARD_EXP = "Stripe - The expiration date that you entered is invalid";
    public static final String INVALID_CARD_CVC = "Stripe - The CVC code that you entered is invalid";
    public static final String INVALID_CARD_DETAILS = "Stripe - The card details that you entered are invalid";
    public static final String PUBLISHABLE_KEY = "pk_test_BS3lFguklXYlFf8oc5Ul9kg300mSOJkSYT";

    public static final String ERROR_SHIP = "There was some problem with your request";
    public static final String INCOMPLETE_DETAILS = "Please review your order details again. " +
            "Some of them might be missing / invalid\n";
    public static final String CARD_TOKEN = "cardToken";
    public static final String CUSTOMER_ID = "customerId";
}

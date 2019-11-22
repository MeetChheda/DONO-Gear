package com.example.donogear.utils;

import android.app.Application;
import android.util.Log;

import com.example.donogear.R;
import com.parse.Parse;
import com.parse.facebook.ParseFacebookUtils;
import com.parse.twitter.ParseTwitterUtils;

public class App extends Application {
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );
        try {
            ParseFacebookUtils.initialize(this);
            ParseTwitterUtils.initialize(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret));
        } catch (IllegalStateException e) {
            Log.d("DONO-Gear", "Facebook is already initialized");
        }
    }
}

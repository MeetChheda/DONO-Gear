package com.example.donogear.actionpages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.donogear.registeration.LauncherActivity;
import com.facebook.login.LoginManager;
import com.parse.ParseUser;

import com.example.donogear.R;

import static com.example.donogear.utils.Constants.USER_DETAILS;
import static com.example.donogear.utils.Constants.USER_NAME;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {

    private static final String TAG = "UserProfileActivity";
    private View view;
    private MainActivity activity;
    private SharedPreferences sharedPreferences;
    private Handler handler;

    public UserProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
        initializeLayout();

        activity = (MainActivity) getActivity();


        handler = new Handler();
        Runnable proceedsRunnable = new Runnable() {
            @Override
            public void run() {
                if(activity.hasAllImages && activity.hasAllData) {
                    initializeLayout();
                    initButtonClicks();
                }
                else {
                    handler.postDelayed(this, 100);
                }
            }
        };
        handler.post(proceedsRunnable);



        return view;
    }

    /**
     * Function added for logging out
     *
     */
    private void initializeLayout() {

        Button logout = view.findViewById(R.id.userlogout_btn);
        Button myAccountButton = view.findViewById(R.id.myaccount_btn);

        myAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity, MyAccountActivity.class);
            startActivity(intent);
        });

        logout.setOnClickListener(v -> {
            LoginManager.getInstance().logOut();
            ParseUser.logOut();

            sharedPreferences = getContext().getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String name = sharedPreferences.getString(USER_NAME, "");
                Log.d(TAG, "LOGGING OUT: " + name);
                editor.clear();
                editor.apply();
            }
            Intent intent = new Intent(activity, LauncherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        });
    }

    /**
     * Helper function to initialise buttons and define behaviour of the clicks
     */
    private void initButtonClicks() {


    }
}

package com.example.donogear.actionpages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
        return view;
    }

    /**
     * Function added for logging out
     *
     */
    private void initializeLayout() {

        Button logout = view.findViewById(R.id.userlogout_btn);

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
}

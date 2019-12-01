package com.example.donogear.actionpages;

import android.content.Intent;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.donogear.registeration.LauncherActivity;
import com.facebook.login.LoginManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseUser;

import com.example.donogear.R;

import java.util.ArrayList;
import static com.example.donogear.utils.Constants.LOGIN_FOR_DETAILS;
import static com.example.donogear.utils.Constants.MY_INTERESTS;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {

    private static final String TAG = "UserProfileActivity";
    private View view;
    private MainActivity activity;
    private ArrayList<String> userInterests;

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
        initializeLayout();
        return view;
    }

    /**
     * Function added for logging out, my interests and user details page
     */
    private void initializeLayout() {
        userInterests = new ArrayList<>();
        CoordinatorLayout relativeLayout = view.findViewById(R.id.profile_snackbar);
        Button logout = view.findViewById(R.id.userlogout_btn);
        Button myAccountButton = view.findViewById(R.id.myaccount_btn);
        Button myInterestsButton  = view.findViewById(R.id.myinterests_btn);
        ParseUser user = ParseUser.getCurrentUser();
        if (user == null) {
            Snackbar.make(relativeLayout, LOGIN_FOR_DETAILS, 4000).show();
            logout.setEnabled(false);
            logout.setAlpha(0.25f);
            myAccountButton.setEnabled(false);
            myAccountButton.setAlpha(0.25f);
            myInterestsButton.setEnabled(false);
            myInterestsButton.setAlpha(0.25f);
        } else {
            getUserInterests(user);
        }

        myAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity, MyAccountActivity.class);
            startActivity(intent);
        });

        myInterestsButton.setOnClickListener(v -> {
            BottomSheetDialogFragment fragment = new MyInterestsFragment();
            Bundle bundle = new Bundle();
            getUserInterests(user);
            bundle.putStringArrayList(MY_INTERESTS, userInterests);
            fragment.setArguments(bundle);
            fragment.show(activity.getSupportFragmentManager(), fragment.getTag());
        });

        logout.setOnClickListener(v -> {
            LoginManager.getInstance().logOut();
            ParseUser.logOut();
            Intent intent = new Intent(activity, LauncherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    /**
     * Get user interests from the database
     * @param user - current Parse user, if logged in
     */
    private void getUserInterests(ParseUser user) {
        Object obj = user.get(MY_INTERESTS);
        userInterests = (ArrayList<String>) obj;
    }
}
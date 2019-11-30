package com.example.donogear.actionpages;


import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donogear.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.example.donogear.utils.Constants.LOGIN_ERROR;
import static com.example.donogear.utils.Constants.MY_INTERESTS;
import static com.example.donogear.utils.Constants.SAVE_USER_INTERESTS;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyInterestsFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private View view;
    private List<String> topicsTags;
    private List<String> causesTags;
    private Set<String> selectedTags;
    private LinearLayout layout;
    private int counter = -1;
    private List<SwitchCompat> allSwitches;

    public MyInterestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_interests, container, false);

        initializeLayout();
        getUserPreferences();
        Objects.requireNonNull(getDialog()).setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) bottomSheet.getParent();
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setPeekHeight(bottomSheet.getHeight());
            coordinatorLayout.getParent().requestLayout();
        });
        return view;
    }

    /**
     * Defining and initializing layout and views for the fragment
     */
    private void initializeLayout() {
        topicsTags = new ArrayList<String>(MainActivity.tags[0]);
        causesTags = new ArrayList<String>(MainActivity.tags[1]);
        Button reset = view.findViewById(R.id.reset_btn);
        Button save = view.findViewById(R.id.save_btn);
        ImageButton cancel = view.findViewById(R.id.cancel_btn);
        reset.setOnClickListener(this);
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
        selectedTags = new HashSet<>();
        allSwitches = new ArrayList<>();
        layout = view.findViewById(R.id.linear_topic_interest);
        addButtons(topicsTags);
        layout = view.findViewById(R.id.linear_causes_interest);
        addButtons(causesTags);
    }

    /**
     * Pre-sets switches based on user preferences
     */
    private void setButtons() {
        for(SwitchCompat switchCompat: allSwitches) {
            if (selectedTags.contains(switchCompat.getTextOff().toString())) {
                switchCompat.setChecked(true);
            } else {
                switchCompat.setChecked(false);
            }
        }
    }

    /**
     * Pre-sets user interests
     */
    private void getUserPreferences() {
        if (getArguments() != null) {
            List<String> interests = getArguments().getStringArrayList(MY_INTERESTS);
            selectedTags.addAll(interests);
        }
        setButtons();
    }

    /**
     * Dynamically adds all switches to the layout
     * @param tags - adds a switch for each tag
     */
    private void addButtons(List<String> tags) {
        for(String tag: tags) {
            RelativeLayout lay = new RelativeLayout(getContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(60, 30, 60, 30);
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            TextView textView = new TextView(getContext());
            textView.setText(tag.toUpperCase());
            textView.setLayoutParams(params);
            textView.setTextSize(16);
            textView.setTextColor(Color.BLACK);
            SwitchCompat switchCompat = new SwitchCompat(getContext());
            switchCompat.setId(++counter);
            switchCompat.setTextOff(tag);
            switchCompat.setOnClickListener(view -> {
                List<String> allTags = new ArrayList<>(topicsTags);
                allTags.addAll(causesTags);
                int id = view.getId();
                if (!selectedTags.add(allTags.get(id))) {
                    selectedTags.remove(allTags.get(id));
                }
            });
            allSwitches.add(switchCompat);

            params.removeRule(RelativeLayout.ALIGN_PARENT_START);
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            switchCompat.setLayoutParams(params);


            lay.addView(textView);
            lay.addView(switchCompat);
            layout.addView(lay);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_btn:
                dismiss();
                break;

            case R.id.reset_btn:
                selectedTags = new HashSet<>();
                setButtons();
                break;

            case R.id.save_btn:
                ParseUser user = ParseUser.getCurrentUser();
                if (user == null) {
                    Toast.makeText(getContext(), LOGIN_ERROR, Toast.LENGTH_SHORT).show();
                    break;
                }
                user.put(MY_INTERESTS, new ArrayList<>(selectedTags));
                user.saveInBackground();
                Toast.makeText(getContext(), SAVE_USER_INTERESTS, Toast.LENGTH_SHORT).show();
                dismiss();
        }
    }
}

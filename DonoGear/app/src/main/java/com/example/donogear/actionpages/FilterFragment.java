package com.example.donogear.actionpages;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.donogear.R;
import com.example.donogear.interfaces.ButtonDesign;
import com.example.donogear.interfaces.onSavePressed;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.donogear.utils.Constants.PRIMARY_COLOR;

public class FilterFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private LinearLayout linearLayout;
    private List<String> topicsTags, causesTags;
    private List<Button> topicButtons, causesButtons;
    private Button save, reset;
    private ImageButton cancel;
    private onSavePressed savePressedListener;
    private String category;
    private List<String> topicsSelected, causesSelected;
    private List<String> savedTags;
    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        topicsTags = new ArrayList<>(MainActivity.tags[0]);
        causesTags = new ArrayList<>(MainActivity.tags[1]);
        topicButtons = new ArrayList<>();
        causesButtons = new ArrayList<>();
        savedTags = new ArrayList<>();
        topicsSelected = new ArrayList<>();
        causesSelected = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_filter, container,
                false);
        if (getArguments() != null) {
            savedTags = getArguments().getStringArrayList("tagsSelected");
            category = getArguments().getString("category");
        }
        System.out.println("\n\n\n" + savedTags);
        save = view.findViewById(R.id.save);
        reset = view.findViewById(R.id.reset);
        cancel = view.findViewById(R.id.cancel);
        save.setOnClickListener(this);
        reset.setOnClickListener(this);
        cancel.setOnClickListener(this);
        linearLayout = view.findViewById(R.id.linear_topic);
        addButtons(topicsTags, topicButtons);
        linearLayout = view.findViewById(R.id.linear_causes);
        addButtons(causesTags, causesButtons);
        // get the views and attach the listener

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
     * Add buttons for filter tags to the layout
     * @param tags - all tags
     * @param tagButtons - buttons for all tags
     */
    private void addButtons(List<String> tags, List<Button> tagButtons) {
        for (int j = 0; j <= tags.size(); j += 3) {
            LinearLayout newLayout = new LinearLayout(getContext());
            newLayout.setOrientation(LinearLayout.HORIZONTAL);
            newLayout.setBaselineAligned(false);
            for (int i = 0; i < 3 && j + i < tags.size(); i++) {
                final Button button = new Button(getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        320, 130
                );
                layoutParams.setMargins(8, 15, 8, 0);
                layoutParams.gravity = Gravity.TOP;
                button.setLayoutParams(layoutParams);
                button.setTextColor(Color.BLACK);
                String text = tags.get(i + j), fin = text;
                String[] split = text.split("&");
                if (split.length > 1) {
                    fin = split[0] + " &\n" + split[1];
                }
                ButtonDesign.setButtonLayout(button, PRIMARY_COLOR, Color.WHITE);
                button.setText(fin);
                if (savedTags != null && savedTags.size() > 0) {
                    if (savedTags.stream().anyMatch(str -> str.startsWith(split[0]))) {
                        toggleButton(button);
                    }
                }
                tagButtons.add(button);

                newLayout.addView(button);
                button.setOnClickListener(v -> toggleButton(button));
            }
            linearLayout.addView(newLayout);
        }
    }

    /**
     * Toggle button (selected / deselected)
     * @param button - button which is clicked
     */
    private void toggleButton(Button button) {
        int color = button.getTextColors().getDefaultColor();
        if (color == PRIMARY_COLOR) {
            ButtonDesign.setButtonLayout(button, Color.WHITE, PRIMARY_COLOR);
        } else {
            ButtonDesign.setButtonLayout(button, PRIMARY_COLOR, Color.WHITE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reset:
                topicsSelected = new ArrayList<>();
                causesSelected = new ArrayList<>();
                for (Button button: topicButtons) {
                    ButtonDesign.setButtonLayout(button, PRIMARY_COLOR, Color.WHITE);
                }
                for (Button button: causesButtons) {
                    ButtonDesign.setButtonLayout(button, PRIMARY_COLOR, Color.WHITE);
                }
                break;
            case R.id.cancel:
                dismiss();
                break;
            case R.id.save:
                for (Button button: topicButtons) {
                    int color = button.getTextColors().getDefaultColor();
                    if (color == Color.WHITE) {
                        topicsSelected.add(button.getText().toString().toLowerCase());
                    }
                }
                for (Button button: causesButtons) {
                    int color = button.getTextColors().getDefaultColor();
                    if (color == Color.WHITE) {
                        causesSelected.add(button.getText().toString().toLowerCase());
                    }
                }
                System.out.println("Sending result -----------");
                savePressedListener.passData(topicsSelected, causesSelected, category);
                SearchPageFragment.fragment.dismiss();
                break;
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            savePressedListener = (onSavePressed) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSavePressed");
        }
    }
}

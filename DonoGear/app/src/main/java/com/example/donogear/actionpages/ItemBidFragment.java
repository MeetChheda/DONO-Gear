package com.example.donogear.actionpages;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donogear.R;
import com.example.donogear.interfaces.onSavePressed;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import static com.example.donogear.utils.Constants.ADD_TO_BID;
import static com.example.donogear.utils.Constants.SUBTRACT_FROM_BID;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemBidFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private int startPrice;
    private int currentBid;
    private int latestBidValue;
    private String highestBidder;
    private Button plus;
    private Button minus;
    private TextView setPrice;
    private TextView currentPrice;
    private EditText edit_price;
    private int steps;
    private onSavePressed savePressedListener;

    public ItemBidFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_item_bid, container, false);
        if (getArguments() != null) {
            startPrice = getArguments().getInt("startBid");
            currentBid = getArguments().getInt("currentBid");
            highestBidder = getArguments().getString("highestBidder");
        }
        initializeLayout(view);
        initializeData();

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

    private void initializeData() {
        String textPrice = "";
        latestBidValue = Math.max(startPrice, currentBid);
        textPrice += latestBidValue;
        steps = Math.max(0, (int) (Math.pow(10, (textPrice.length() - 2))));
        textPrice = "$" + (Integer.parseInt(textPrice));
        currentPrice.setText(textPrice);
        setPrice.setText(textPrice);
        edit_price.setText(textPrice.substring(1));

        String text = "- $" + steps;
        minus.setText(text);
        text = "+ $" + steps;
        plus.setText(text);
    }

    private void initializeLayout(final View view) {
        plus = view.findViewById(R.id.plus);
        minus = view.findViewById(R.id.minus);
        ImageButton cancel = view.findViewById(R.id.cancel);
        ImageButton done = view.findViewById(R.id.done);

        currentPrice = view.findViewById(R.id.current_bid_amount);
        setPrice = view.findViewById(R.id.price_text);
        edit_price = view.findViewById(R.id.price_edit);

        plus.setOnClickListener(this);
        minus.setOnClickListener(this);
        cancel.setOnClickListener(this);
        done.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String amount;
        int value;
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;

            case R.id.done:
                String currentText = edit_price.getText().toString();
                if(!validationBid(currentText, latestBidValue))
                    return;
                Bundle bundle = new Bundle();
                bundle.putInt("userBid", currentBid);
                bundle.putString("userName", "user_name");
                savePressedListener.passData(bundle);
                dismiss();
                break;

            case R.id.plus:
                amount = setPrice.getText().toString().substring(1);
                value = Integer.parseInt(amount);
                value = changeAmount(value, steps, latestBidValue, ADD_TO_BID);
                amount = "$" + value;
                setAmount(amount);
                break;

            case R.id.minus:
                amount = setPrice.getText().toString().substring(1);
                value = Integer.parseInt(amount);
                value = changeAmount(value, steps, latestBidValue, SUBTRACT_FROM_BID);
                amount = "$" + value;
                setAmount(amount);
                break;

            default:
                break;
        }
    }

    public int changeAmount(int value, int multiple, int latestValue, int operation) {
        int newAmount = value + (multiple * operation);
        if (operation == ADD_TO_BID)
            return newAmount;
        if (newAmount < latestValue) {
            Toast.makeText(getContext(), "Your bid should be higher than the current bid",
                    Toast.LENGTH_SHORT).show();
            return value;
        }
        return newAmount;
    }

    public void setAmount(String amount) {
        setPrice.setText(amount);
        edit_price.setText(amount.substring(1));
    }

    public boolean validationBid(String text, int latestBidValue) {
        for (char ch: text.toCharArray()) {
            if (!Character.isDigit(ch)) {
//                Toast.makeText(getContext(), "Enter a number", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        int value = Integer.parseInt(text);
        if (value <= latestBidValue) {
//            Toast.makeText(getContext(), "Your bid has to be higher than the current bid",
//                    Toast.LENGTH_SHORT).show();
            return false;
        }
        //TODO - Query for the current bid value before accepting user bid and set
        // highestBidder = currentUser if logged in.
        currentBid = value;
        //TODO - Write to database with current value
        return true;
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

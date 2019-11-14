package com.example.donogear.actionpages;


import android.os.Bundle;

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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemBidFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private int startBid;
    private int currentBid;
    private Button plus;
    private Button minus;
    private TextView setPrice;
    private TextView currentPrice;
    private EditText edit_price;
    private ImageButton cancel;
    private ImageButton done;
    private int multiple;

    public ItemBidFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_item_bid, container, false);
        if (getArguments() != null) {
            startBid = getArguments().getInt("startBid");
            currentBid = getArguments().getInt("currentBid");
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
        if (currentBid == 0) {
            textPrice += startBid;
        } else {
            textPrice += currentBid;
        }
        multiple = Math.max(0, (int) (Math.pow(10, (textPrice.length() - 2))));
        textPrice = "$" + (Integer.parseInt(textPrice) + multiple / 2);
        currentPrice.setText(textPrice);
        setPrice.setText(textPrice);
        edit_price.setText(textPrice.substring(1));

        String text = "- $" + multiple;
        minus.setText(text);
        text = "+ $" + multiple;
        plus.setText(text);
    }

    private void initializeLayout(final View view) {
        plus = view.findViewById(R.id.plus);
        minus = view.findViewById(R.id.minus);
        cancel = view.findViewById(R.id.cancel);
        done = view.findViewById(R.id.done);

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
                Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                break;

            case R.id.plus:
                amount = setPrice.getText().toString().substring(1);
                value = Integer.parseInt(amount);
                value += multiple;
                amount = "$" + value;
                setPrice.setText(amount);
                edit_price.setText(amount);
                break;

            case R.id.minus:
                amount = setPrice.getText().toString().substring(1);
                value = Integer.parseInt(amount);
                if (value - multiple <= Math.max(currentBid, startBid)) {
                    Toast.makeText(getContext(), "Bid should be higher than the current bid",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                value -= multiple;
                amount = "$" + value;
                setPrice.setText(amount);
                edit_price.setText(amount);
                break;


            default:
                break;
        }
    }
}

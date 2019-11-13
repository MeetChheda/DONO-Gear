package com.example.donogear.actionpages;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.donogear.R;
import com.parse.ParseUser;

public class MyAccountFragment extends Fragment {
    private View view;
    private MainActivity activity;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_account, container, false);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
        initializeLayout();
        return view;
    }

    private void initializeLayout() {

    }
}

package com.example.donogear.actionpages;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.donogear.R;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment {


    public HomePageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }
}
package com.example.donogear.actionpages;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.example.donogear.R;
import com.example.donogear.models.DonorDetails;
import com.example.donogear.models.ItemDetails;
import com.example.donogear.utils.DonorAdapter;
import com.example.donogear.utils.ItemAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BrowsePageFragment extends Fragment {


    private boolean searchFlag;

    private List<String> searchArray;
    private List<String> tagsSelected;
    private RecyclerView recyclerView;
    private List<DonorDetails> listOfDonors, copyList;
    private DonorAdapter donorAdapter;
    static BottomSheetDialogFragment fragment;
    private View view;
    private MainActivity activity;
    private String typeOfSearch;
    private Handler handler;


    public BrowsePageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_browse_page, container, false);
        activity = (MainActivity) getActivity();
        if (getArguments() != null) {
            typeOfSearch = getArguments().getString("type");
        }
        setHasOptionsMenu(true);
        handler = new Handler();
        Runnable proceedsRunnable = new Runnable() {
            @Override
            public void run() {
                if(activity.hasAllImages && activity.hasAllData) {
                    initializeLayout();

                }
                else {
//                    System.out.println("Getting data");
                    handler.postDelayed(this, 100);
                }
            }
        };
        handler.post(proceedsRunnable);
        return view;
    }

    private void initializeLayout() {
        recyclerView = view.findViewById(R.id.card_view_donor_recycler_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        System.out.println(manager);
        recyclerView.setLayoutManager(manager);
        listOfDonors = activity.donorList;
        System.out.println("Size " + listOfDonors.size());
        donorAdapter = activity.donorAdapter;
        donorAdapter.setDonorList(listOfDonors);
        recyclerView.setAdapter(donorAdapter);
        donorAdapter.notifyDataSetChanged();

    }
}

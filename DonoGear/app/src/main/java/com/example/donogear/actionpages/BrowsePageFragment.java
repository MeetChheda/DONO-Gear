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
import com.example.donogear.models.CausesDetails;
import com.example.donogear.models.DonorDetails;
import com.example.donogear.models.ItemDetails;
import com.example.donogear.utils.CausesAdapter;
import com.example.donogear.utils.DonorAdapter;
import com.example.donogear.utils.ItemAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static com.example.donogear.utils.Constants.DONOR_IDENTIFIER;


/**
 * A simple {@link Fragment} subclass.
 */
public class BrowsePageFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<DonorDetails> listOfDonors;
    private List<CausesDetails> listOfCauses;
    private DonorAdapter donorAdapter;
    private CausesAdapter causesAdapter;
    private View view;
    private MainActivity activity;
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

        setHasOptionsMenu(true);
        handler = new Handler();
        Runnable proceedsRunnable = new Runnable() {
            @Override
            public void run() {
                if(activity.hasAllImages && activity.hasAllData) {
                    initializeLayout();
                    getType();
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
     * Initialises all the layout basics which includes initialising recycler view
     */
    private void initializeLayout() {
        recyclerView = view.findViewById(R.id.card_view_browse_recycler_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
    }

    /**
     * Gets the type of fragment clicked and accordingly displays the fragment
     * type variable can contain donor or cause type
     */
    public void getType() {
        if (getArguments() != null) {
            String typeOfSearch = getArguments().getString("type");
            if (typeOfSearch.equals(DONOR_IDENTIFIER)) {
                displayDonor(activity.donorDetailsList);
            }
            else {
                displayCauses(activity.causesDetailsList);
            }
        }
    }

    /**
     * display donor fragment by setting appropriate adapter in recycler view
     */
    public int displayDonor(List<DonorDetails> donorDetails) {
        listOfDonors = donorDetails;
        System.out.println("Size " + listOfDonors.size());
        //donorAdapter = activity.donorAdapter;
//        donorAdapter.setDonorList(listOfDonors);
//        recyclerView.setAdapter(donorAdapter);
//        donorAdapter.notifyDataSetChanged();
        return listOfDonors.size();
    }

    /**
     * display causes fragment by setting appropriate adapter in recycler view
     */
    public int displayCauses(List<CausesDetails> causesDetailsList) {
        listOfCauses = causesDetailsList;
        System.out.println("Size " + listOfCauses.size());
//        causesAdapter = activity.causesAdapter;
//        causesAdapter.setCausesList(listOfCauses);
//        recyclerView.setAdapter(causesAdapter);
//        causesAdapter.notifyDataSetChanged();
        return listOfCauses.size();
    }

}

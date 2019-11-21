package com.example.donogear.actionpages;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.donogear.R;
import com.example.donogear.interfaces.ItemClickListener;
import com.example.donogear.models.AnnouncementDetails;
import com.example.donogear.models.ItemDetails;
import com.example.donogear.utils.AnnouncementAdapter;
import com.example.donogear.utils.ItemAdapter;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment implements ItemClickListener {

    private View view;
    private MainActivity activity;
    private Handler handler;
    private RecyclerView recyclerView;
    private RecyclerView trendingRecyclerView;
    private List<AnnouncementDetails> listOfAnnouncements;
    private AnnouncementAdapter announcementAdapter;
    private ItemAdapter itemAdapter;
    private List<ItemDetails> trendingItemList;
    private Context context;
    private Button learnMore;
    private String typeOfSearch;

    public HomePageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_page, container, false);
        activity = (MainActivity) getActivity();

        System.out.println("Home Page called");
        setHasOptionsMenu(true);
        handler = new Handler();

        Runnable proceedsRunnable = new Runnable() {
            @Override
            public void run() {
                if(activity.hasAllImages && activity.hasAllData) {
                    System.out.println("Initialize");
                    initializeLayout();
//                    getType();

                }
                else {
                    handler.postDelayed(this, 100);
                }
            }
        };
        handler.post(proceedsRunnable);
        return view;

    }

    private void initializeLayout() {
        if (getArguments() != null) {
            typeOfSearch = getArguments().getString("type");
        }
        trendingRecyclerView = view.findViewById(R.id.card_view_trending_recycler_list);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        trendingRecyclerView.setLayoutManager(horizontalLayoutManager);
        trendingItemList = new ArrayList<>();
        itemAdapter = activity.itemAdapter;
        displayTrendingItems(activity.listOfItems);
        System.out.println(trendingItemList.size());
        itemAdapter.setItemList(trendingItemList);
        System.out.println(itemAdapter.getItemCount());
        trendingRecyclerView.setAdapter(itemAdapter);
        itemAdapter.setClickListener(this);
        itemAdapter.notifyDataSetChanged();


        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView = view.findViewById(R.id.card_view_announcement_recycler_list);
        recyclerView.setLayoutManager(manager);


//        recyclerView.setLayoutManager(manager);
        listOfAnnouncements = activity.announcementDetailsList;
        System.out.println("Size " + listOfAnnouncements.size());
        announcementAdapter = activity.announcementAdapter;
        announcementAdapter.setAnnouncementList(listOfAnnouncements);
        recyclerView.setAdapter(announcementAdapter);
        announcementAdapter.notifyDataSetChanged();
        System.out.println("Initialize end");
        learnMore = view.findViewById(R.id.learn_more_id);
        learnMore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.donowebsite)));
                startActivity(browserIntent);
            }
        });

    }


    private void displayTrendingItems(List<ItemDetails> itemDetailsList) {
        for (ItemDetails itemDetails: itemDetailsList) {
            if (itemDetails.isTrending && itemDetails.listOfImages.size() > 0) {
                trendingItemList.add(itemDetails);
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        ItemDetails item = trendingItemList.get(position);
//        System.out.println(item.itemName);
        Intent intent = new Intent(activity, ProductDetails.class);
        intent.putExtra("item_details", item);
        intent.putExtra("typeOfSearch", typeOfSearch);
        startActivity(intent);
    }
}

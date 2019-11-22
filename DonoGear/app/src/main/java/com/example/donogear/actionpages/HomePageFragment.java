package com.example.donogear.actionpages;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.donogear.R;
import com.example.donogear.interfaces.ItemClickListener;
import com.example.donogear.models.AnnouncementDetails;
import com.example.donogear.models.ItemDetails;
import com.example.donogear.utils.AnnouncementAdapter;
import com.example.donogear.utils.ItemAdapter;
import com.example.donogear.utils.MyInterestsItemAdapter;

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
    private RecyclerView myInterestsRecyclerView;
    private List<AnnouncementDetails> listOfAnnouncements;
    private AnnouncementAdapter announcementAdapter;
    private ItemAdapter trendingItemAdapter;
    private MyInterestsItemAdapter myInterestsItemAdapter;
    private List<ItemDetails> trendingItemList;
    private List<ItemDetails> myInterestsItemList;
    private Context context;
    private Button learnMore;
    private String typeOfSearch;
    private TextView trendingText;
    private TextView myInterestsText;

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
     * Initializing layout and setting adapters of trending items, announcements and myInterests item
     */
    private void initializeLayout() {
        if (getArguments() != null) {
            typeOfSearch = getArguments().getString("type");
        }
        trendingText = view.findViewById(R.id.trending_text);
        trendingRecyclerView = view.findViewById(R.id.card_view_trending_recycler_list);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        trendingRecyclerView.setLayoutManager(horizontalLayoutManager);
        trendingItemList = new ArrayList<>();
        trendingItemAdapter = activity.itemAdapter;
        System.out.println("Size of copy list " + activity.copyList.size());
        trendingItemList = displayTrendingItems(activity.copyList);
        if (trendingItemList.size() == 0) {
            trendingText.setVisibility(View.GONE);
        }
        else {
            trendingText.setVisibility(View.VISIBLE);
        }
        System.out.println(trendingItemList.size());
        trendingItemAdapter.setItemList(trendingItemList);
        System.out.println(trendingItemAdapter.getItemCount());
        trendingRecyclerView.setAdapter(trendingItemAdapter);
        trendingItemAdapter.setClickListener(this);
        trendingItemAdapter.notifyDataSetChanged();

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView = view.findViewById(R.id.card_view_announcement_recycler_list);
        recyclerView.setLayoutManager(manager);
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

        myInterestsText = view.findViewById(R.id.myinterests_text);
        myInterestsRecyclerView = view.findViewById(R.id.card_view_myinterests_recycler_list);
        LinearLayoutManager horizontalInterestsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        myInterestsRecyclerView.setLayoutManager(horizontalInterestsLayoutManager);
        myInterestsItemList = new ArrayList<>();
        myInterestsItemAdapter = new MyInterestsItemAdapter(activity, activity.listOfItems);
        myInterestsItemList = activity.listOfItems;
        if (myInterestsItemList.size() > 0) {
            myInterestsText.setVisibility(View.VISIBLE);
        }
        else {
            myInterestsText.setVisibility(View.GONE);
        }
        System.out.println(myInterestsItemList.size());
        myInterestsItemAdapter.setItemList(myInterestsItemList);
        System.out.println(myInterestsItemAdapter.getItemCount());
        myInterestsRecyclerView.setAdapter(myInterestsItemAdapter);
        myInterestsItemAdapter.setClickListener(this);
        myInterestsItemAdapter.notifyDataSetChanged();
    }


    /**
     * Filter item list and set trending item list
     * @param itemDetailsList - list of all items
     * @return - list of items which are trending
     */
    private List<ItemDetails> displayTrendingItems(List<ItemDetails> itemDetailsList) {
        for (ItemDetails itemDetails: itemDetailsList) {
            if (itemDetails.isTrending && itemDetails.listOfImages.size() > 0) {
                System.out.println("Item added");
                trendingItemList.add(itemDetails);
            }
        }
        return trendingItemList;
    }

    /**
     * Overriding the interface method to perform action for onClick on any trending item. Will call
     * the next trending item details activity
     * @param view - clicked view
     * @param position - position of the item clicked
     */
    @Override
    public void onItemClick(View view, int position) {
        ItemDetails item = trendingItemList.get(position);
//        System.out.println(item.itemName);
        Intent intent = new Intent(activity, ProductDetails.class);
        intent.putExtra("item_details", item);
        intent.putExtra("typeOfSearch", typeOfSearch);
        startActivity(intent);
    }


    /**
     * Overriding the interface method to perform action for onClick on any myInterest item. Will call
     * the next myInterest item details activity
     * @param view - clicked view
     * @param position - position of the item clicked
     */
    @Override
    public void onMyInterestItemClick(View view, int position) {
        ItemDetails item = myInterestsItemList.get(position);
        Intent intent = new Intent(activity, ProductDetails.class);
        intent.putExtra("item_details", item);
        intent.putExtra("typeOfSearch", typeOfSearch);
        startActivity(intent);
    }
}

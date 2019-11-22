package com.example.donogear.actionpages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.donogear.R;
import com.example.donogear.interfaces.ItemClickListener;
import com.example.donogear.interfaces.myOnBackPressed;
import com.example.donogear.models.ItemDetails;
import com.example.donogear.utils.ItemAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.donogear.utils.Constants.SEARCH;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchPageFragment extends Fragment implements ItemClickListener, myOnBackPressed {

    private boolean searchFlag;

    private List<String> searchArray;
    private List<ItemDetails> listOfItems, copyList;
    private ItemAdapter itemAdapter;
    private FloatingActionButton filterButton;
    private AutoCompleteTextView actv;
    static BottomSheetDialogFragment fragment;
    private Button cross;
    private View view;
    private MainActivity activity;
    private ImageView search;
    private String category;
    private Handler handler;

    public SearchPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search_page, container, false);
        activity = (MainActivity) getActivity();
        if (getArguments() != null) {
            category = getArguments().getString("type");
        }
        setHasOptionsMenu(true);
        handler = new Handler();
        Runnable proceedsRunnable = new Runnable() {
            @Override
            public void run() {
                if(activity.hasAllImages && activity.hasAllData) {
                    initializeLayout();
                    initButtonClicks();
                    searchBox();
                    displayData();
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
     * Initialises all the layout basics (views, buttons etc) Also extracts all the displayable
     * data from the MainActivity.
     *
     * TODO: Cleaner logic for passing data from Activity to Fragment
     */
    private void initializeLayout() {
        RecyclerView recyclerView = view.findViewById(R.id.card_view_recycler_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        cross = view.findViewById(R.id.cross);
        filterButton = view.findViewById(R.id.filter_button);
        actv = view.findViewById(R.id.autoCompleteTextView);
        search = view.findViewById(R.id.search_img);

        listOfItems = activity.listOfItems;
        copyList = activity.copyList;
        searchArray = activity.searchArray;
        filterDataByCategory();
        List<String> tagsSelected = activity.tagsSelected;

        itemAdapter = activity.itemAdapter;
        itemAdapter.setItemList(listOfItems);
        recyclerView.setAdapter(itemAdapter);
        searchFlag = false;

        itemAdapter.setClickListener(this, SEARCH);
        itemAdapter.notifyDataSetChanged();
        Bundle bundle = new Bundle();
        bundle.putSerializable("tagsSelected", new ArrayList<>(tagsSelected));
        bundle.putString("category", category);
        fragment = new FilterFragment();
        fragment.setArguments(bundle);
    }

    /**
     * Filters from the collective list of all items to only display the items with the selected
     * category
     */
    private void filterDataByCategory() {
        listOfItems = activity.listOfItems.stream()
                .filter(item -> item.category.matches(category))
                .collect(Collectors.toList());
        copyList = activity.copyList.stream()
                .filter(item -> item.category.matches(category))
                .collect(Collectors.toList());
        searchArray = listOfItems.stream()
                .map(item -> item.itemName)
                .collect(Collectors.toList());
    }

    /**
     * Restores list of items back to it's original state; pre-search or pre-filter
     */
    private void restoreListOfItems() {
        listOfItems = new ArrayList<>(copyList);
        searchArray = listOfItems.stream().map(item -> item.itemName).collect(Collectors.toList());
    }

    /**
     * The MainActivity is responsible for filtering and passing the queried data.
     * This function checks for no result found
     */
    private void displayData() {
        TextView no_records = view.findViewById(R.id.no_records);
        no_records.setVisibility(View.GONE);
        if (listOfItems.size() == 0) {
            no_records.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Overriding the interface method to perform action for onClick on any collectible. Will call
     * the next collectible details activity
     * @param view - clicked view
     * @param position - position of the clicked item in the list
     */
    public void onItemClick(View view, int position, String type) {
        if (type.equals(SEARCH)) {
            ItemDetails item = listOfItems.get(position);
            Intent intent = new Intent(activity, ProductDetails.class);
            intent.putExtra("item_details", item);
            intent.putExtra("category", category);
            startActivity(intent);
        }
    }

    /**
     * Facilitates auto complete search based on itemName (for the time being). Filters from the list
     * of items to display items that match the name.
     */
    private void searchBox() {
        // Search auto - complete
        ArrayAdapter<String> adapter = new ArrayAdapter<> (getContext(),
                android.R.layout.select_dialog_item, searchArray);
        //Getting the instance of AutoCompleteTextView
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actv.setTextColor(Color.BLACK);
        actv.setOnItemClickListener((adapterView, view, i, l) -> {
            String item = adapter.getItem(i);
            listOfItems = copyList
                    .stream()
                    .filter(x -> x.itemName.contains(item))
                    .collect(Collectors.toList());
            itemAdapter.setItemList(listOfItems);
            itemAdapter.notifyDataSetChanged();
            InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        });
    }

    /**
     * Helper function to initialise buttons and define behaviour of the clicks
     */
    private void initButtonClicks() {
        filterButton.setOnClickListener(view ->
                fragment.show(activity.getSupportFragmentManager(), fragment.getTag())
        );

        cross.setOnClickListener(view -> {
            actv.setText("");
            restoreListOfItems();
            itemAdapter.setItemList(listOfItems);
            itemAdapter.notifyDataSetChanged();
        });

        search.setOnClickListener(view -> {
            if (!searchFlag) {
                searchFlag = true;
                cross.setVisibility(View.VISIBLE);
                actv.setVisibility(View.VISIBLE);
                actv.requestFocus();
            } else {
                searchFlag = false;
                cross.setVisibility(View.GONE);
                actv.setVisibility(View.GONE);
                cross.performClick();
            }
        });
    }

    /**
     * The original behaviour is present in the parent activity (MainActivity - onBackPressed)
     * To ensure correct behavior - onBackPressed will always go to Home fragment unless the user
     * is deep into one search. If yes, then backPress just takes him to previous view which is all
     * items without the search query (i.e. All items)
     * @return - false means to perform normal backButton press.
     */
    @Override
    public boolean onBackPressed() {
        if (searchFlag) {
            searchFlag = false;
            restoreListOfItems();
            itemAdapter.setItemList(listOfItems);
            itemAdapter.notifyDataSetChanged();
            actv.setText("");
            actv.clearFocus();
            actv.setVisibility(View.GONE);
            cross.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.search:
                searchFlag = true;
                cross.setVisibility(View.VISIBLE);
                actv.setVisibility(View.VISIBLE);
                break;
        }
        return true;
    }
}

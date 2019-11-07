package com.example.donogear;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchPageFragment extends Fragment implements ItemClickListener, myOnBackPressed {

    public static final int GET_FILTER_TAG = 1;
    private boolean searchFlag;

    private List<String> searchArray;
    public static List<String>[] tags;
    private List<String> tagsSelected;
    private RecyclerView recyclerView;
    private List<ItemDetails> listOfItems, copyList;
    private Context context;
    private ItemAdapter itemAdapter;
    private FloatingActionButton filterButton;
    private AutoCompleteTextView actv;
    static BottomSheetDialogFragment fragment;
    private Button cross;
    private View view;
    private MainActivity activity;

    public SearchPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search_page, container, false);
        activity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
        initializeLayout();
        searchBox();
        displayData();
        return view;
    }

    /**
     * Initialises all the layout basics (views, buttons etc) Also extracts all the displayable
     * data from the MainActivity.
     *
     * TO-DO: Cleaner logic for passing data from Activity to Fragment
     */
    private void initializeLayout() {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);

        recyclerView = view.findViewById(R.id.card_view_recycler_list);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        cross = view.findViewById(R.id.cross);
        filterButton = view.findViewById(R.id.filter_button);
        actv = view.findViewById(R.id.autoCompleteTextView);

        context = getContext();
        listOfItems = activity.listOfItems;

        tags = MainActivity.tags;
        tagsSelected = activity.tagsSelected;
        searchArray = activity.searchArray;
        copyList = activity.copyList;
        itemAdapter = activity.itemAdapter;
        recyclerView.setAdapter(itemAdapter);

        searchFlag = false;

        itemAdapter.setClickListener(this);
        itemAdapter.notifyDataSetChanged();
        Bundle bundle = new Bundle();
        bundle.putSerializable("tagsSelected", new ArrayList<>(tagsSelected));
        fragment = new FilterFragment();
        fragment.setTargetFragment(SearchPageFragment.this, GET_FILTER_TAG);
        fragment.setArguments(bundle);

        filterButton.setOnClickListener(view -> {
            fragment.show(activity.getSupportFragmentManager(), fragment.getTag());
        });

        cross.setOnClickListener(view -> {
            actv.setText("");
            listOfItems = copyList;
            itemAdapter.setItemList(listOfItems);
            itemAdapter.notifyDataSetChanged();
            itemAdapter.setClickListener(this);
        });
    }

    /**
     * The MainAcitivty is responsible for filtering and passing the queried data.
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
    @Override
    public void onItemClick(View view, int position) {
        ItemDetails item = listOfItems.get(position);
        System.out.println(item.itemName);
        Toast.makeText(context, item.itemName, Toast.LENGTH_SHORT).show();
    }

    /**
     * Facilitates auto complete search based on itemName (for the time being). Filters from the list
     * of items to display items that match the name.
     */
    private void searchBox() {
        // Search auto - complete
        ArrayAdapter<String> adapter = new ArrayAdapter<> (context, android.R.layout.select_dialog_item, searchArray);
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
            listOfItems = copyList;
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

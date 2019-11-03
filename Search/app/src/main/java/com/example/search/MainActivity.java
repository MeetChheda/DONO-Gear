package com.example.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements FilterFragment.onSavePressed, ItemClickListener {

    public static final String COLLECTIBLES = "Collectible";
    public static final String COLLECTIBLE_IMAGES = "CollectibeImages";
    public static final String TAGS = "Tags";

    private List<String> searchArray;
    public static List<String>[] tags;
    private List<String> tagsSelected;
    private Set<String> selectedItemsId;
    private RecyclerView recyclerView;
    private List<ItemDetails> listOfItems, copyList;
    private Context context;
    private boolean flagSearch;
    public ItemAdapter itemAdapter;
    private FloatingActionButton filterButton;
    private AutoCompleteTextView actv;
    private Map<String, List<String>> tagsToItems;
    public static BottomSheetDialogFragment fragment;
    private Button cross;

    ParseQuery<ParseObject> tagsQuery, collectibleQuery, imagesQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParseQuery.clearAllCachedResults();
        // Initialize Parse Credentials
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );
        // Initialize basic layout specifics and Adapter
        initializeLayout();
        readData();
        getFilters();
        itemAdapter.notifyDataSetChanged();
        searchBox();
        filterButton.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("tagsSelected", new ArrayList<>(tagsSelected));
            fragment = new FilterFragment();
            fragment.setArguments(bundle);
            fragment.show(getSupportFragmentManager(), fragment.getTag());
        });

        cross.setOnClickListener(view -> {
            actv.setText("");
            listOfItems = copyList;
            itemAdapter.setItemList(listOfItems);
            itemAdapter.notifyDataSetChanged();
            itemAdapter.setClickListener(this);
        });
        Toast.makeText(context, "Here", Toast.LENGTH_SHORT).show();
    }

    private void getFilters() {
        tagsQuery = ParseQuery.getQuery(TAGS);
        //Toast.makeText(context, tagsQuery.hasCachedResult() + "", Toast.LENGTH_SHORT).show();
        //tagsQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        tagsQuery.findInBackground((items, e) -> {
            if (e == null) {
                for (ParseObject item : items) {
                    String name = item.getString("tagName");
                    int tagType = item.getInt("tagClass");
                    List<String> objects = item.getList("itemIdArray");
                    if (name != null && objects != null && objects.size() > 0) {
                        tagsToItems.put(name, objects);
                    }
                    System.out.println(name + " ------- " + tagType);
                    tags[tagType].add(name);
                }
            } else {
                // Something is wrong
                Toast.makeText(MainActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                Log.e("Error", e.toString());
            }
            for (List<String> a: tags) {
                System.out.println(a);
            }
        });
    }

    private void readData() {
        collectibleQuery = ParseQuery.getQuery(COLLECTIBLES);
        //collectibleQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        collectibleQuery.findInBackground((items, e) -> {
            if (e == null) {
                for (ParseObject item : items) {
                    String itemId = item.getObjectId();
                    final List<File> itemImages = getImagesForItems(itemId);
                    final int startBid = item.getInt("startBid");
                    final int buyNowPrice = item.getInt("buyNowPrice");
                    final int currentBid = item.getInt("currentBid");
                    final String itemName = item.getString("itemName");
                    final String highestBidder = item.getString("highestBidder") != null ?
                            item.getString("highestBidder") : "Be the first one to bid!";
                    final String category = item.getString("category");
                    final Date endDate = item.getDate("auctionEndDate");
                    System.out.println(itemName + " for " + buyNowPrice + " starting at " + startBid + "---------");
                    ItemDetails itemDetails = new ItemDetails(itemId, itemName, buyNowPrice,
                            currentBid, highestBidder, category, endDate, itemImages);
                    searchArray.add(itemName);
                    listOfItems.add(itemDetails);
                    itemAdapter.notifyDataSetChanged();
                }
            } else {
                // Something is wrong
                Toast.makeText(MainActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                Log.e("Error", e.toString());
            }
            copyList.addAll(listOfItems);
        });
    }

    private List<File> getImagesForItems(String itemId) {
        imagesQuery = ParseQuery.getQuery(COLLECTIBLE_IMAGES);
        imagesQuery.whereEqualTo("collectibleId", itemId);
        final List<File> allImages = new ArrayList<>();
        imagesQuery.getFirstInBackground((object, e) -> {
            if (e == null) {
                for (int i = 1; i < 6; i++) {
                    if (object.getParseFile("image" + i) != null) {
                        try {
                            if (object.getParseFile("image" + i).getFile() != null) {
                                File image = object.getParseFile("image" + i).getFile();
                                allImages.add(image);
                                itemAdapter.notifyDataSetChanged();
                            }
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
        return allImages;
    }

    @Override
    public void onItemClick(View view, int position) {
        ItemDetails item = listOfItems.get(position);
        System.out.println(item.itemName);
        Toast.makeText(context, item.itemName, Toast.LENGTH_SHORT).show();
    }

    private void initializeLayout() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.card_view_recycler_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        cross = findViewById(R.id.cross);
        filterButton = findViewById(R.id.filter_button);
        actv = findViewById(R.id.autoCompleteTextView);

        context = getBaseContext();
        listOfItems = new ArrayList<>();
        tags = new ArrayList[2];
        tags[0] = new ArrayList<>();
        tags[1] = new ArrayList<>();
        tagsSelected = new ArrayList<>();
        searchArray = new ArrayList<>();
        copyList = new ArrayList<>();
        selectedItemsId = new HashSet<>();
        tagsToItems = new HashMap<>();
        flagSearch = false;
        setItemAdapter();

        itemAdapter.setClickListener(this);
    }

    private void searchBox() {
        // Search auto - complete
        ArrayAdapter<String> adapter = new ArrayAdapter<> (this, android.R.layout.select_dialog_item, searchArray);
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
            flagSearch = true;
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ParseQuery.clearAllCachedResults();
    }

    @Override
    public void passData(List<String> topics, List<String> causes) {
        actv.setText("");
        TextView no_records = findViewById(R.id.no_records);
        no_records.setVisibility(View.GONE);
        selectedItemsId = new HashSet<>();
        tagsSelected = new ArrayList<>(topics);
        tagsSelected.addAll(causes);
        System.out.println(tagsSelected);
        System.out.println(tagsToItems);
        for (String str: tagsSelected) {
            if (tagsToItems.containsKey(str)) {
                selectedItemsId.addAll(tagsToItems.get(str));
            }
        }
        if (topics.size() == 0 &&  causes.size() == 0) {
            listOfItems = copyList;
        } else {
            listOfItems = copyList.stream().filter(item -> selectedItemsId.contains(item.id)).collect(Collectors.toList());
        }
        if (listOfItems.size() == 0) {
            no_records.setVisibility(View.VISIBLE);
        }
        System.out.println(selectedItemsId);
        System.out.println(listOfItems.size());
        itemAdapter.setItemList(listOfItems);
        itemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (flagSearch) {
            flagSearch = false;
            listOfItems = copyList;
            itemAdapter.setItemList(listOfItems);
            itemAdapter.notifyDataSetChanged();
            actv.setText("");
            actv.clearFocus();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.search:
                cross.setVisibility(View.VISIBLE);
                actv.setVisibility(View.VISIBLE);
                break;
        }
        return true;
    }

    public void setItemAdapter() {
        itemAdapter = new ItemAdapter(context, listOfItems);
        recyclerView.setAdapter(itemAdapter);
    }
}

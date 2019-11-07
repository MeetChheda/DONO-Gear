package com.example.donogear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
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

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener, onSavePressed {

    public static final String COLLECTIBLES = "Collectible";
    public static final String COLLECTIBLE_IMAGES = "CollectibeImages";
    public static final String TAGS = "Tags";

    public boolean searchFlag;
    public List<String> searchArray;
    public static List<String>[] tags;
    public List<String> tagsSelected;
    public Set<String> selectedItemsId;
    public List<ItemDetails> listOfItems, copyList;
    public Context context;
    public ItemAdapter itemAdapter;
    public AutoCompleteTextView actv;
    public Map<String, List<String>> tagsToItems;
    public BottomNavigationView navigationView;

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
        itemAdapter = new ItemAdapter(context, listOfItems);

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

    private void initializeLayout() {
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
        navigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
        navigationView.setSelectedItemId(R.id.navigation_search);
        navigationView.setItemIconSize(120);
        loadFragment(new SearchPageFragment());


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
        searchFlag = false;
    }

    /**
     * Implementing interface onSavePressed's method to pass data from Activity to fragment and back
     * This function receives data ffrom the FilterFragment after the filters have been set. They
     * are then sent to SearchFragment again to adjust items based on those filters only
     * @param topics - selected topic filters
     * @param causes - selected causes filters
     */
    @Override
    public void passData(List<String> topics, List<String> causes) {
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

        System.out.println(selectedItemsId);
        System.out.println(listOfItems.size());
        itemAdapter.setItemList(listOfItems);
        itemAdapter.notifyDataSetChanged();
        loadFragment(new SearchPageFragment());
    }

    /**
     * Facilitates switching of tabs (as fragments)
     * @param menuItem - a selectable menu item representing different tabs
     * @return - returns whether opening the fragment was successful or not
     */

    /**
     * Tries to load the fragment using FragmentManager
     * @param currentFragment - fragment to be loaded based on menuItem click
     * @return - returns success status of opening a new fragment
     */
    private boolean loadFragment(Fragment currentFragment) {
        if (currentFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, currentFragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(!(fragment instanceof  myOnBackPressed)|| !((myOnBackPressed)fragment).onBackPressed()) {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment currentFragment = null;
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                currentFragment = new HomePageFragment();
                break;

            case R.id.navigation_search:
                currentFragment = new SearchPageFragment();
                break;

            case R.id.navigation_profile:
                currentFragment = new UserProfileFragment();
                break;
        }
        return loadFragment(currentFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ParseQuery.clearAllCachedResults();
    }
}

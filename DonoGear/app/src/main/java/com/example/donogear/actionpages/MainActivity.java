package com.example.donogear.actionpages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.donogear.R;
import com.example.donogear.interfaces.myOnBackPressed;
import com.example.donogear.interfaces.onSavePressed;
import com.example.donogear.models.ItemDetails;
import com.example.donogear.utils.ItemAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.tabs.TabLayout;
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

import static com.example.donogear.utils.Constants.AUCTION_IDENTIFIER;
import static com.example.donogear.utils.Constants.COLLECTIBLES;
import static com.example.donogear.utils.Constants.COLLECTIBLE_IMAGES;
import static com.example.donogear.utils.Constants.DROP_IDENTIFIER;
import static com.example.donogear.utils.Constants.RAFFLE_IDENTIFIER;
import static com.example.donogear.utils.Constants.TAGS;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener, onSavePressed {

    public boolean searchFlag;
    public List<String> searchArray;
    public static List<String>[] tags;
    public List<String> tagsSelected;
    public Set<String> selectedItemsId;
    public List<ItemDetails> listOfItems, copyList, superCopyList;
    public Context context;
    public ItemAdapter itemAdapter;
    public Map<String, List<String>> tagsToItems;
    public BottomNavigationView mainNavigation;
    public TabLayout innerTabs;

    public boolean hasAllData, hasAllImages;

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
        manageInnerTabs();
        initializeLayout();
        readData();
        getFilters();
        itemAdapter = new ItemAdapter(context, listOfItems);
    }

    /**
     * Retrieve all filters from the database
     */
    private void getFilters() {
        ParseQuery<ParseObject> tagsQuery = ParseQuery.getQuery(TAGS);
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
                    tags[tagType].add(name);
                }
            } else {
                // Something is wrong
                Toast.makeText(MainActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                Log.e("Error", e.toString());
            }
        });
    }

    /**
     * Queries all the collectible-data from the database
     */
    private void readData() {
        ParseQuery<ParseObject> collectibleQuery = ParseQuery.getQuery(COLLECTIBLES);
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
                    final String itemDescription = item.getString("description");
                    final String highestBidder = item.getString("highestBidder") != null ?
                            item.getString("highestBidder") : "Be the first one to bid!";
                    final String category = item.getString("category");
                    final Date endDate = item.getDate("auctionEndDate");
                    ItemDetails itemDetails = new ItemDetails(itemId, itemName, itemDescription, buyNowPrice,
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
            superCopyList.addAll(listOfItems);
            hasAllData = true;
        });
    }

    /**
     * Get's images for each collectible
     * @param itemId - ID of a single item
     * @return list of all images for the corresponding item
     */
    private List<File> getImagesForItems(String itemId) {
        ParseQuery<ParseObject> imagesQuery = ParseQuery.getQuery(COLLECTIBLE_IMAGES);
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
        hasAllImages = true;
        return allImages;
    }

    private void initializeLayout() {
        mainNavigation = findViewById(R.id.navigation);
        mainNavigation.setOnNavigationItemSelectedListener(this);
        mainNavigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
        mainNavigation.setSelectedItemId(R.id.navigation_search);
        mainNavigation.setItemIconSize(120);

        context = getBaseContext();
        listOfItems = new ArrayList<>();
        copyList = new ArrayList<>();
        superCopyList = new ArrayList<>();
        tags = new ArrayList[2];
        tags[0] = new ArrayList<>();
        tags[1] = new ArrayList<>();
        tagsSelected = new ArrayList<>();
        searchArray = new ArrayList<>();
        selectedItemsId = new HashSet<>();
        tagsToItems = new HashMap<>();
        searchFlag = false;
        hasAllData = false;
        hasAllImages = true;

        loadFragment(new SearchPageFragment(), AUCTION_IDENTIFIER);
    }

    /**
     * Facilitates inner tab-switching (i.e. Raffles, Auction, Drops)
     */
    private void manageInnerTabs() {
        innerTabs = findViewById(R.id.innertabs);
        innerTabs.getTabAt(1).select();
        innerTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                listOfItems = new ArrayList<>(superCopyList);
                copyList = new ArrayList<>(superCopyList);
                switch (tab.getPosition()) {
                    case 0:
                        loadFragment(new SearchPageFragment(), RAFFLE_IDENTIFIER);
                        break;
                    case 1:
                        loadFragment(new SearchPageFragment(), AUCTION_IDENTIFIER);
                        break;
                    case 2:
                        loadFragment(new SearchPageFragment(), DROP_IDENTIFIER);
                        break;
                }
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    /**
     * Implementing interface onSavePressed's method to pass data from Activity to fragment and back
     * This function receives data from the FilterFragment after the filters have been set. They
     * are then sent to SearchFragment again to adjust items based on those filters only
     * @param topics - selected topic filters
     * @param causes - selected causes filters
     */
    @Override
    public void passData(List<String> topics, List<String> causes, String category) {
        selectedItemsId = new HashSet<>();
        tagsSelected = new ArrayList<>(topics);
        tagsSelected.addAll(causes);
        System.out.println(tagsSelected);
        for (String str: tagsSelected) {
            if (tagsToItems.containsKey(str)) {
                selectedItemsId.addAll(tagsToItems.get(str));
            }
        }
        filterItemsByCategory(category);
        if (topics.size() == 0 &&  causes.size() == 0) {
            listOfItems = copyList;
        } else {
            listOfItems = copyList.stream()
                    .filter(item -> selectedItemsId.contains(item.id))
                    .collect(Collectors.toList());
        }

        System.out.println(selectedItemsId);
        System.out.println(listOfItems.size());
        itemAdapter.setItemList(listOfItems);
        itemAdapter.notifyDataSetChanged();
        loadFragment(new SearchPageFragment(), category);
    }

    /**
     * Filters to show items only for the selected category. This method ensures that the filters
     * are also applied only on the items pertaining to the current chosen category
     * @param category - category selected
     */
    private void filterItemsByCategory(String category) {
        System.out.println("Filtering by " + category);
        listOfItems = superCopyList.stream()
                .filter(item -> item.category.equals(category))
                .collect(Collectors.toList());
        copyList = superCopyList.stream()
                .filter(item -> item.category.equals(category))
                .collect(Collectors.toList());
    }

    /**
     * Tries to load the fragment using FragmentManager
     * @param currentFragment - fragment to be loaded based on menuItem click
     * @return - returns success status of opening a new fragment
     */
    private boolean loadFragment(Fragment currentFragment, String type) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        if (currentFragment != null) {
            currentFragment.setArguments(bundle);
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
        if(!(fragment instanceof myOnBackPressed)|| !((myOnBackPressed)fragment).onBackPressed()) {
            super.onBackPressed();
            finish();
        }
    }

    /**
     * Facilitates bottom-nav-bar tab switching
     * @param menuItem - clickable menuItem (tab)
     * @return success or not
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment currentFragment = null;
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                innerTabs.setVisibility(View.GONE);
                currentFragment = new HomePageFragment();
                break;

            case R.id.navigation_search:
                innerTabs.setVisibility(View.VISIBLE);
                currentFragment = new SearchPageFragment();
                break;

            case R.id.navigation_profile:
                innerTabs.setVisibility(View.GONE);
                currentFragment = new UserProfileFragment();
                break;
        }
        return loadFragment(currentFragment, AUCTION_IDENTIFIER);
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
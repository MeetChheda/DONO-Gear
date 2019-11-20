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
import com.example.donogear.models.AnnouncementDetails;
import com.example.donogear.models.CausesDetails;
import com.example.donogear.models.DonorDetails;
import com.example.donogear.models.ItemDetails;
import com.example.donogear.utils.AnnouncementAdapter;
import com.example.donogear.utils.CausesAdapter;
import com.example.donogear.utils.DonorAdapter;
import com.example.donogear.utils.ItemAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.tabs.TabLayout;
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

import static com.example.donogear.utils.Constants.ANNOUNCEMENTS;
import static com.example.donogear.utils.Constants.AUCTION_IDENTIFIER;
import static com.example.donogear.utils.Constants.CAUSES_IDENTIFIER;
import static com.example.donogear.utils.Constants.COLLECTIBLES;
import static com.example.donogear.utils.Constants.COLLECTIBLE_IMAGES;
import static com.example.donogear.utils.Constants.DONOR;
import static com.example.donogear.utils.Constants.DONOR_IDENTIFIER;
import static com.example.donogear.utils.Constants.DROP_IDENTIFIER;
import static com.example.donogear.utils.Constants.HOME_IDENTIFIER;
import static com.example.donogear.utils.Constants.PROCEEDS;
import static com.example.donogear.utils.Constants.RAFFLE_IDENTIFIER;
import static com.example.donogear.utils.Constants.TAGS;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener, onSavePressed {

    public boolean searchFlag;
    public List<String> searchArray;
    public static List<String>[] tags;
    public List<String> tagsSelected;
    public Set<String> selectedItemsId;
    public List<ItemDetails> listOfItems;
    public List<ItemDetails> copyList;
    public List<ItemDetails> superCopyList;
    public List<DonorDetails> donorDetailsList;
    public List<CausesDetails> causesDetailsList;
    public List<AnnouncementDetails> announcementDetailsList;
    public Context context;
    public ItemAdapter itemAdapter;
    public CausesAdapter causesAdapter;
    public DonorAdapter donorAdapter;
    public AnnouncementAdapter announcementAdapter;
    public Map<String, List<String>> tagsToItems;
    public BottomNavigationView mainNavigation;
    public TabLayout innerTabs;
    public TabLayout innerBrowseTabs;
    public TabLayout innerHomeTabs;
    public boolean hasAllData, hasAllImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParseQuery.clearAllCachedResults();
        // Initialize basic layout specifics and Adapter
        manageInnerTabs();
        manageInnerBrowseTabs();
        manageInnerHomeTabs();
        initializeLayout();
        readData();
        getFilters();
        itemAdapter = new ItemAdapter(context, listOfItems);
        donorAdapter = new DonorAdapter(context, donorDetailsList);
        causesAdapter = new CausesAdapter(context, causesDetailsList);
        announcementAdapter = new AnnouncementAdapter(context, announcementDetailsList);
    }

    /**
     * Retrieve all filters from the database
     */
    private void getFilters() {
        ParseQuery<ParseObject> tagsQuery = ParseQuery.getQuery(TAGS);
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
     * Queries all the collectible-data, donors-data and causes/proceeds data from the database
     */
    private void readData() {

        /**
         * Querying collectible-item data
         */
        ParseQuery<ParseObject> collectibleQuery = ParseQuery.getQuery(COLLECTIBLES);
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
                    final boolean trending = item.getBoolean("trending");
                    ItemDetails itemDetails = new ItemDetails(itemId, itemName, itemDescription, buyNowPrice,
                            currentBid, highestBidder, category, endDate, itemImages, trending);
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


        /**
         * Querying donor data
         */
        ParseQuery<ParseObject> donorQuery = ParseQuery.getQuery(DONOR);
        donorQuery.findInBackground((donors, e) -> {
            if (e == null) {
                for (ParseObject donor: donors) {
                    String donorId = donor.getObjectId();
                    final List<File> donorImageList = getImageForDonor(donorId);
                    final String donorName = donor.getString("name");
                    final String category = donor.getString("category");
                    DonorDetails donorObject = new DonorDetails(donorId, donorName, category, donorImageList);
                    donorDetailsList.add(donorObject);
                    donorAdapter.notifyDataSetChanged();
                }
            } else {
                // Something is wrong
                Toast.makeText(MainActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                Log.e("Error", e.toString());
            }
        });


        /**
         * Querying causes data
         */
        ParseQuery<ParseObject> causesQuery = ParseQuery.getQuery(PROCEEDS);
        causesQuery.findInBackground((causes, e) -> {
            if (e == null) {
                for (ParseObject cause: causes) {
                    String causeId = cause.getObjectId();
                    final List<File> causeImageList = getImageForCause(causeId);
                    final String causeTitle = cause.getString("proceedTitle");
                    final String category = cause.getString("category");
                    final String websiteUrl = cause.getString("websiteUrl");
                    CausesDetails causeObject = new CausesDetails(causeId, causeTitle, category, causeImageList, websiteUrl);
                    causesDetailsList.add(causeObject);
                    causesAdapter.notifyDataSetChanged();
                }
            } else {
                // Something is wrong
                Toast.makeText(MainActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                Log.e("Error", e.toString());
            }
        });

        ParseQuery<ParseObject> announcementQuery = ParseQuery.getQuery(ANNOUNCEMENTS);
        announcementQuery.findInBackground((announcements, e) -> {

            if (e == null) {
                for (ParseObject announcement: announcements) {
                    String announcementId = announcement.getObjectId();
                    final List<File> announcementImageList = getImageForAnnouncement(announcementId);
                    System.out.println(announcementImageList.size());
                    final String announcementTitle = announcement.getString("title");
                    System.out.println(announcementTitle);
                    final String announcementDescription = announcement.getString("description");
                    AnnouncementDetails announcementObject = new AnnouncementDetails(announcementId,
                            announcementTitle, announcementDescription, announcementImageList);
                    announcementDetailsList.add(announcementObject);
                    announcementAdapter.notifyDataSetChanged();
                }
            } else {
                // Something is wrong
                Toast.makeText(MainActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                Log.e("Error", e.toString());
            }
        });
    }


    private List<File> getImageForAnnouncement(String announcementId) {
        ParseQuery<ParseObject> announcementImageQuery = ParseQuery.getQuery(ANNOUNCEMENTS);
        List<File> imageFileList = new ArrayList<>();
        announcementImageQuery.whereEqualTo("objectId", announcementId);
        announcementImageQuery.getFirstInBackground((object, e) -> {
            if (e == null) {
                if (object.getParseFile("image") != null) {
                    try {
                        if (object.getParseFile("image").getFile() != null) {
                            File imageFile = object.getParseFile("image").getFile();
                            imageFileList.add(imageFile);
                            announcementAdapter.notifyDataSetChanged();
                        }
                    } catch (ParseException ex) {
                        Log.e("Error", e.toString());
                    }
                }
            }
        });
        return imageFileList;
    }

    /**
     * Gets images for each cause
     * @param causeId - ID of single cause
     * @return list of all images present in the Proceeds table
     */
    private List<File> getImageForCause(String causeId) {
        ParseQuery<ParseObject> causeImageQuery = ParseQuery.getQuery(PROCEEDS);
        List<File> imageFileList = new ArrayList<>();
        causeImageQuery.whereEqualTo("objectId", causeId);
        causeImageQuery.getFirstInBackground((object, e) -> {
            if (e == null) {
                if (object.getParseFile("proceedImage1") != null) {
                    try {
                        if (object.getParseFile("proceedImage1").getFile() != null) {
                            File imageFile = object.getParseFile("proceedImage1").getFile();
                            imageFileList.add(imageFile);
                            causesAdapter.notifyDataSetChanged();
                        }
                    } catch (ParseException ex) {
                        Log.e("Error", e.toString());
                    }
                }
            }
        });
        return imageFileList;
    }

    /**
     * Gets images for each donor
     * @param donorId - ID of single donor
     * @return list of all images present in the Donor table
     */
    private List<File> getImageForDonor(String donorId) {
        ParseQuery<ParseObject> donorImageQuery = ParseQuery.getQuery(DONOR);
        List<File> imageFileList = new ArrayList<>();
        donorImageQuery.whereEqualTo("objectId", donorId);
        donorImageQuery.getFirstInBackground((object, e) -> {
            if (e == null) {
                if (object.getParseFile("image") != null) {
                    try {
                        if (object.getParseFile("image").getFile() != null) {
                            File imageFile = object.getParseFile("image").getFile();
                            imageFileList.add(imageFile);
                            donorAdapter.notifyDataSetChanged();
                        }
                    } catch (ParseException ex) {
                        Log.e("Error", e.toString());
                    }
                }
            }
        });
        return imageFileList;
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
                            Log.e("Error", e.toString());
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
        donorDetailsList = new ArrayList<>();
        causesDetailsList = new ArrayList<>();
        announcementDetailsList = new ArrayList<>();
        superCopyList = new ArrayList<>();

        // We have two types/categories of tags i.e. Topics and Causes
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
     * Facilitates inner tab-switching in Search fragment (i.e. Raffles, Auction, Drops)
     */
    private void manageInnerTabs() {
        innerTabs = findViewById(R.id.innerSearchtabs);

        // Load the tab at index 1 which is Auction
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
     * Facilitates inner tab-switching in Browse fragment(i.e. Donor, Causes)
     */
    private void manageInnerBrowseTabs() {
        innerBrowseTabs = findViewById(R.id.innerBrowsetabs);

        // Load the tab at index 0 which is Donor
        innerBrowseTabs.getTabAt(0).select();
        innerBrowseTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        loadFragment(new BrowsePageFragment(), DONOR_IDENTIFIER);
                        break;
                    case 1:
                        loadFragment(new BrowsePageFragment(), CAUSES_IDENTIFIER);
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void manageInnerHomeTabs() {
        innerHomeTabs = findViewById(R.id.innerHometabs);

        // Load the tab at index 0 which is Donor
        innerHomeTabs.getTabAt(0).select();
        innerBrowseTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        loadFragment(new BrowsePageFragment(), HOME_IDENTIFIER);
                        break;
                }
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
     * @param category - selected category amongst the three types (raffles, auctions, drops)
     *                 It also tells us about the active tab (fragment) open
     */
    @Override
    public void passData(List<String> topics, List<String> causes, String category) {
        selectedItemsId = new HashSet<>();
        tagsSelected = new ArrayList<>(topics);
        tagsSelected.addAll(causes);
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
        String tab = new String();
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                innerTabs.setVisibility(View.GONE);
                innerBrowseTabs.setVisibility(View.GONE);
                innerHomeTabs.setVisibility(View.VISIBLE);
                currentFragment = new HomePageFragment();
                tab = HOME_IDENTIFIER;
                break;
            case R.id.navigation_browse:
                innerTabs.setVisibility(View.GONE);
                innerBrowseTabs.setVisibility(View.VISIBLE);
                innerHomeTabs.setVisibility(View.GONE);
                tab = DONOR_IDENTIFIER;
                currentFragment = new BrowsePageFragment();
                break;
            case R.id.navigation_search:
                innerTabs.setVisibility(View.VISIBLE);
                innerBrowseTabs.setVisibility(View.GONE);
                innerHomeTabs.setVisibility(View.GONE);
                currentFragment = new SearchPageFragment();
                tab = AUCTION_IDENTIFIER;
                break;

            case R.id.navigation_profile:
                innerTabs.setVisibility(View.GONE);
                innerBrowseTabs.setVisibility(View.GONE);
                innerHomeTabs.setVisibility(View.GONE);
                currentFragment = new UserProfileFragment();
                tab = "Profile";
                break;
        }

        return loadFragment(currentFragment, tab);
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

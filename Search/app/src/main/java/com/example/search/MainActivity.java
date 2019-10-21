package com.example.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.os.Handler;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MainActivity extends FragmentActivity implements FilterFragment.onSavePressed {

    public static final String COLLECTIBLES = "Collectible";
    public static final String COLLECTIBLE_IMAGES = "CollectibeImages";
    public static final String TAGS = "Tags";

    List<String> searchArray;
    public static List<String>[] tags;
    List<String> tagsSelected;
    Set<String> selectedItemsId;
    private RecyclerView recyclerView;
    private List<ItemDetails> listOfItems, copyList;
    Context context;
    ItemAdapter itemAdapter;
    FloatingActionButton filterButton;
    private Map<String, List<String>> tagsToItems;
    public static BottomSheetDialogFragment fragment;

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
        System.out.println("--------> In main runnable ----> " + listOfItems.size());
        itemAdapter.notifyDataSetChanged();
        searchBox();

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("tagsSelected", new ArrayList<>(tagsSelected));
                fragment = new FilterFragment();
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), fragment.getTag());
            }
        });
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
                    if (name != null || objects != null && objects.size() > 0) {
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
        recyclerView = findViewById(R.id.card_view_recycler_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        filterButton = findViewById(R.id.filter_button);

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

        itemAdapter = new ItemAdapter(context, listOfItems);
        recyclerView.setAdapter(itemAdapter);
    }

    private void searchBox() {
        // Search auto - complete
        ArrayAdapter<String> adapter = new ArrayAdapter<> (this, android.R.layout.select_dialog_item, searchArray);
        //Getting the instance of AutoCompleteTextView
        AutoCompleteTextView actv = findViewById(R.id.autoCompleteTextView);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actv.setTextColor(Color.BLACK);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ParseQuery.clearAllCachedResults();
    }

    @Override
    public void passData(List<String> topics, List<String> causes) {
        selectedItemsId = new HashSet<>();
        tagsSelected = new ArrayList<>(topics);
        tagsSelected.addAll(causes);
        System.out.println(tagsSelected);
        System.out.println(tagsToItems);
        for (String str: tagsSelected) {
            if (tagsToItems.containsKey(str) && tagsToItems.get(str) != null) {
                selectedItemsId.addAll(tagsToItems.get(str));
            }
        }
        if ((topics == null || topics.size() == 0) && (causes == null || causes.size() == 0)) {
            listOfItems = copyList;
        } else {
            listOfItems = copyList.stream().filter(item -> selectedItemsId.contains(item.id)).collect(Collectors.toList());
        }
        System.out.println(selectedItemsId);
        System.out.println(listOfItems.size());
        itemAdapter.setItemList(listOfItems);
        itemAdapter.notifyDataSetChanged();
    }
}

package com.example.donogear.actionpages;

import com.example.donogear.R;
import com.example.donogear.models.ItemDetails;

import org.junit.*;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.donogear.utils.Constants.AUCTION_IDENTIFIER;
import static com.example.donogear.utils.Constants.DROP_IDENTIFIER;
import static com.example.donogear.utils.Constants.RAFFLE_IDENTIFIER;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)

/**
 * Tests to check if the list of items are filtered correctly based on the current category
 */
public class MainActivityTest {

    private List<ItemDetails> itemDetailsList;
    private MainActivity mainActivity;
    private Map<String, List<String>> tagsMap;

    @Before
    public void setUp() {
        mainActivity = Robolectric.buildActivity(MainActivity.class)
                .create()
                .get();
    }
    /**
     * Initializing data
     */
    @Before
    public void initData() {
        InitializeItems initializeItems = new InitializeItems();
        initializeItems.init();
        itemDetailsList = initializeItems.getItemDetailsList();
        tagsMap = new HashMap<>();
        mainActivity = new MainActivity();
        fillTags();
    }

    /**
     * Test to check when the item list is empty
     */
    @Test
    public void filterButEmptyListTest() {
        initData();
        List<ItemDetails> myList = mainActivity.filterItemsByCategory(new ArrayList<>(),
                AUCTION_IDENTIFIER);
        Assert.assertEquals(0, myList.size());
    }

    /**
     * Test to filter the list only for auction items (returns 3 items correctly, of 5)
     */
    @Test
    public void filterForAuctionTest() {
        initData();
        List<ItemDetails> myList = mainActivity.filterItemsByCategory(itemDetailsList, AUCTION_IDENTIFIER);
        Assert.assertEquals(3, myList.size());
        List<String> possibleNames = Arrays.asList(
                "item1", "item2", "item5"
        );
        Assert.assertTrue(possibleNames.contains(myList.get(0).itemName));
        Assert.assertTrue(possibleNames.contains(myList.get(1).itemName));
        Assert.assertTrue(possibleNames.contains(myList.get(2).itemName));
    }

    /**
     * Test to filter the list only for raffle items (returns 1 item correctly, of 5)
     */
    @Test
    public void filterForRafflesTest() {
        initData();
        List<ItemDetails> myList = mainActivity.filterItemsByCategory(itemDetailsList, RAFFLE_IDENTIFIER);
        Assert.assertEquals(1, myList.size());
        Assert.assertEquals("item3", myList.get(0).itemName);
    }

    /**
     * Test to filter the list only for drops-items (returns 1 item correctly, of 5)
     */
    @Test
    public void filterForDropsTest() {
        initData();
        List<ItemDetails> myList = mainActivity.filterItemsByCategory(itemDetailsList, DROP_IDENTIFIER);
        Assert.assertEquals(1, myList.size());
        Assert.assertEquals("item4", myList.get(0).itemName);
    }


    /**
     * Test to check whether all items are displayed when no filters are selected (or reset filters)
     */
    @Test
    public void checkForNoFilterTest() {
        initData();
        List<String> selectedTags = new ArrayList<>();;
        List<String> selectedCauses = new ArrayList<>();
        System.out.println(itemDetailsList.size());
        List<ItemDetails> filteredList = mainActivity.filterItemsBySelectedTags(selectedTags,
                selectedCauses, itemDetailsList, tagsMap);
        Assert.assertEquals(5, filteredList.size());
    }

    /**
     * Test to filter items based on three tags (returned list is an OR query, i.w. returns items
     * which belong to ANY ONE or more tags)
     */
    @Test
    public void checkForThreeTagsTest() {
        initData();
        List<String> selectedTags = Arrays.asList("sports", "art");
        List<String> selectedCauses = Collections.singletonList("education");
        List<ItemDetails> filteredList = mainActivity.filterItemsBySelectedTags(selectedTags,
                selectedCauses, itemDetailsList, tagsMap);
        Assert.assertEquals(2, filteredList.size());
    }

    /**
     * Test to filter items based on only one single tag
     */
    @Test
    public void checkForOneTagTest() {
        initData();
        List<String> selectedTags = Collections.singletonList("music");
        List<String> selectedCauses = new ArrayList<>();
        List<ItemDetails> filteredList = mainActivity.filterItemsBySelectedTags(selectedTags,
                selectedCauses, itemDetailsList, tagsMap);
        Assert.assertEquals(1, filteredList.size());
    }

    /**
     * Test to filter items based on tags which have no items currently in the database. An
     * appropriate message is displayed and the no items are found
     */
    @Test
    public void checkForNoEligibleFilterTest() {
        initData();
        List<String> selectedTags = Collections.singletonList("art");
        List<String> selectedCauses = Collections.singletonList("education");
        List<ItemDetails> filteredList = mainActivity.filterItemsBySelectedTags(selectedTags,
                selectedCauses, itemDetailsList, tagsMap);
        Assert.assertEquals(0, filteredList.size());
    }

    @Test
    public void shouldNotBeNullAndShouldHaveTabs() {
        Assert.assertNotNull(mainActivity);
        Assert.assertNotNull(mainActivity.findViewById(R.id.navigation));
        Assert.assertNotNull(mainActivity.findViewById(R.id.innerSearchtabs));
        Assert.assertNotNull(mainActivity.findViewById(R.id.innerBrowsetabs));
    }

    /**
     * Initializing tags to items mapping
     */
    private void fillTags() {
        List<String> sportItems = Arrays.asList("01", "03");
        tagsMap.put("sports", sportItems);
        List<String> fashionItems = Arrays.asList("01", "05");
        tagsMap.put("fashion", fashionItems);
        List<String> musicItems = Collections.singletonList("02");
        tagsMap.put("music", musicItems);
    }

}

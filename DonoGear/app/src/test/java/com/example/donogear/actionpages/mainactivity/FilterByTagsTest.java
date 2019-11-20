package com.example.donogear.actionpages.mainactivity;

import com.example.donogear.actionpages.InitializeItems;
import com.example.donogear.actionpages.MainActivity;
import com.example.donogear.models.ItemDetails;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests to check if the list of items are filtered correctly based on the tags used for filtering
 */
public class FilterByTagsTest {

    private List<ItemDetails> itemDetailsList;
    private MainActivity mainActivity;
    private Map<String, List<String>> tagsMap;

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
     * Test to check whether all items are displayed when no filters are selected (or reset filters)
     */
    @Test
    public void checkForNoFilterTest() {
        List<String> selectedTags = new ArrayList<>();;
        List<String> selectedCauses = new ArrayList<>();
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
        List<String> selectedTags = Collections.singletonList("art");
        List<String> selectedCauses = Collections.singletonList("education");
        List<ItemDetails> filteredList = mainActivity.filterItemsBySelectedTags(selectedTags,
                selectedCauses, itemDetailsList, tagsMap);
        Assert.assertEquals(0, filteredList.size());
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

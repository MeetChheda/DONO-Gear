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


public class FilterByTagsTest {

    private List<ItemDetails> itemDetailsList;
    private MainActivity mainActivity;
    private Map<String, List<String>> tagsMap;

    @Before
    public void initData() {
        InitializeItems initializeItems = new InitializeItems();
        initializeItems.init();
        itemDetailsList = initializeItems.getItemDetailsList();
        tagsMap = new HashMap<>();
        mainActivity = new MainActivity();
        fillTags();
    }

    @Test
    public void checkForNoFilterTest() {
        List<String> selectedTags = new ArrayList<>();;
        List<String> selectedCauses = new ArrayList<>();
        List<ItemDetails> filteredList = mainActivity.filterItemsBySelectedTags(selectedTags,
                selectedCauses, itemDetailsList, tagsMap);
        Assert.assertEquals(5, filteredList.size());
    }

    @Test
    public void checkForTwoTagsTest() {
        List<String> selectedTags = Arrays.asList("sports", "art");
        List<String> selectedCauses = Collections.singletonList("education");
        List<ItemDetails> filteredList = mainActivity.filterItemsBySelectedTags(selectedTags,
                selectedCauses, itemDetailsList, tagsMap);
        Assert.assertEquals(2, filteredList.size());
    }

    @Test
    public void checkForOneTagTest() {
        List<String> selectedTags = Collections.singletonList("music");
        List<String> selectedCauses = new ArrayList<>();
        List<ItemDetails> filteredList = mainActivity.filterItemsBySelectedTags(selectedTags,
                selectedCauses, itemDetailsList, tagsMap);
        Assert.assertEquals(1, filteredList.size());
    }

    @Test
    public void checkForNoEligibleFilterTest() {
        List<String> selectedTags = Collections.singletonList("art");
        List<String> selectedCauses = Collections.singletonList("education");
        List<ItemDetails> filteredList = mainActivity.filterItemsBySelectedTags(selectedTags,
                selectedCauses, itemDetailsList, tagsMap);
        Assert.assertEquals(0, filteredList.size());
    }

    private void fillTags() {
        List<String> sportItems = Arrays.asList("01", "03");
        tagsMap.put("sports", sportItems);
        List<String> fashionItems = Arrays.asList("01", "05");
        tagsMap.put("fashion", fashionItems);
        List<String> musicItems = Collections.singletonList("02");
        tagsMap.put("music", musicItems);
    }

}

package com.example.donogear.actionpages;

import com.example.donogear.models.CausesDetails;
import com.example.donogear.models.DonorDetails;
import com.example.donogear.models.ItemDetails;
import com.example.donogear.utils.DonorAdapter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class BrowsePageFragmentTest {

    private List<DonorDetails> donorDetailsList;
    private List<CausesDetails> causesDetailsList;
    private BrowsePageFragment browsePageFragment;
    private DonorAdapter donorAdapter;
    /**
     * Initializing data
     */
    @Before
    public void initData() {
        InitializeItems initializeItems = new InitializeItems();
        initializeItems.initDonors();
        initializeItems.initCauses();
        donorDetailsList = initializeItems.getDonorDetailsList();
        causesDetailsList = initializeItems.getCausesDetailsList();
        browsePageFragment = new BrowsePageFragment();
        donorAdapter = new DonorAdapter();
    }

    // TC-01-02
    @Test
    public void getAllDonorListFromFragmentTest() {
        Assert.assertEquals(3, browsePageFragment.displayDonor(donorDetailsList)    );
    }

    // TC-01-03
    @Test
    public void getAllCausesListFromFragmentTest() {
        Assert.assertEquals(2, browsePageFragment.displayCauses(causesDetailsList));
    }
}

package com.example.donogear.actionpages.mainactivity;

import com.example.donogear.R;
import com.example.donogear.actionpages.MainActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
/**
 * Test to check if an activity exists and has some valuable components
 */
public class HasFragmentsTest {

    MainActivity mainActivity;

    /**
     * Create a mock of the main activity
     */
    @Before
    public void setUp() {
        mainActivity = Robolectric.buildActivity(MainActivity.class)
                .create()
                .get();
    }

    /**
     * Test to check that the activity is present (notNull) and checks for the core components, i.e.
     * bottom navigation, inner tabs for categories (auctions, raffles, drops) and for browse tabs
     */
    @Test
    public void shouldNotBeNullAndShouldHaveTabs() {
        Assert.assertNotNull(mainActivity);
        Assert.assertNotNull(mainActivity.findViewById(R.id.navigation));
        Assert.assertNotNull(mainActivity.findViewById(R.id.innerSearchtabs));
        Assert.assertNotNull(mainActivity.findViewById(R.id.innerBrowsetabs));
    }
}

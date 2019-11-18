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
public class HasFragmentsTest {

    MainActivity mainActivity;

    @Before
    public void setUp() throws Exception {
        mainActivity = Robolectric.buildActivity(MainActivity.class)
                .create()
                .get();
    }

    @Test
    public void shouldNotBeNullAndShouldHaveTabs() {
        Assert.assertNotNull(mainActivity);
        Assert.assertNotNull(mainActivity.findViewById(R.id.navigation));
        Assert.assertNotNull(mainActivity.findViewById(R.id.innerSearchtabs));
        Assert.assertNotNull(mainActivity.findViewById(R.id.innerBrowsetabs));
    }
}

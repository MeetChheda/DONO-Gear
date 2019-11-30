package com.example.donogear.interfaces;

import com.example.donogear.interfaces.TickTime;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests to check whether interface TickTime returns the time correctly parsed in the required
 * format (DD : HH : MM : SS)
 */
public class TickTimeTest {
    private long timeInMilliSec;

    /**
     * Test to parse time for one day
     */
    @Test
    public void displayTimeOneDay() {
        timeInMilliSec = 24 * 60 * 60 * 1000;
        String time = TickTime.displayTime(timeInMilliSec);
        String expectedOutcome = "01 : 00 : 00 : 00";
        Assert.assertEquals(expectedOutcome, time);
    }

    /**
     * Test to parse time for one hour
     */
    @Test
    public void displayTimeOneHour() {
        timeInMilliSec = 60 * 60 * 1000;
        String time = TickTime.displayTime(timeInMilliSec);
        String expectedOutcome = "00 : 01 : 00 : 00";
        Assert.assertEquals(expectedOutcome, time);
    }

    /**
     * Test to parse time for one minute
     */
    @Test
    public void displayTimeOneMinute() {
        timeInMilliSec = 60 * 1000;
        String time = TickTime.displayTime(timeInMilliSec);
        String expectedOutcome = "00 : 00 : 01 : 00";
        Assert.assertEquals(expectedOutcome, time);
    }
}

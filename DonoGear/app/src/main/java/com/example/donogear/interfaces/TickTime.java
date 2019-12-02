package com.example.donogear.interfaces;

import java.util.Arrays;
import java.util.List;

/**
 * Ticks time by returning the time remaining till the end date parsed as (Day : Hours : Min : Sec)
 */
public interface TickTime {
    static String displayTime(long time) {
        final List<String> timeList = getTimeList(time);
        return String.join(" : ", timeList).trim();
    }

    static List<String> getTimeList(long difference) {
        long millisInDay = 1000L * 60 * 60 * 24;
        long millisInHour = 1000L * 60 * 60;
        long millisInMinute = 1000L * 60;
        long millisInSecond = 1000;

        long days = difference / millisInDay;
        long daysDivisionResidueMillis = difference - days * millisInDay;
        String day = days < 10 ? "0" + days : "" + days;

        long hours = daysDivisionResidueMillis / millisInHour;
        long hoursDivisionResidueMillis = daysDivisionResidueMillis - hours * millisInHour;
        String hour = hours < 10 ? "0" + hours : "" + hours;

        long minutes = hoursDivisionResidueMillis / millisInMinute;
        long minutesDivisionResidueMillis = hoursDivisionResidueMillis - minutes * millisInMinute;
        String min = minutes < 10 ? "0" + minutes : "" + minutes;

        long seconds = minutesDivisionResidueMillis / millisInSecond;
        String sec = seconds < 10 ? "0" + seconds : "" + seconds;
        return Arrays.asList(day, hour, min, sec);
    }
}

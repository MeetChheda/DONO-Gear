package com.example.donogear.interfaces;

/**
 * Interface to override the default onBackPressed to modify behavior for some cases. Pressing the back
 * button for fragments and activities have different behavior at times.
 */
public interface myOnBackPressed {
    boolean onBackPressed();
}

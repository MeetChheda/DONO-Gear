package com.example.donogear.interfaces;

import android.os.Bundle;
/**
 * Interface used to pass data from fragment to activity and back. Can be overridden in fragments
 * to communicate with its activity
 */
public interface onSavePressed {
    /**
     * This method is a generic method to pass data between fragment and its activity. The parameters
     * can be tweaked accordingly to ensure what us
     * @param bundle - all data required to be passed from the fragment back to the activity
     */
    void passData(Bundle bundle);
}

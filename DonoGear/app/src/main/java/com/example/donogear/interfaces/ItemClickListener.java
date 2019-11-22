package com.example.donogear.interfaces;

import android.view.View;

/**
 * Interface to manage behavior when an item is clicked on the main page
 */
public interface ItemClickListener {
    /**
     * The method definition of the click interface
     * @param view - the view which is clicked
     * @param position - adapter position (i.e. item position in the list)
     * @param type - type of item
     */
    void onItemClick(View view, int position, String type);

}

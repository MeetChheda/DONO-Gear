package com.example.donogear.interfaces;

import java.util.List;

/**
 * Interface used to pass data from fragment to activity and back. Can be overridden in fragments
 * to communicate with its activity
 */
public interface onSavePressed {
    void passData(List<String> topics, List<String> causes, String category);
}

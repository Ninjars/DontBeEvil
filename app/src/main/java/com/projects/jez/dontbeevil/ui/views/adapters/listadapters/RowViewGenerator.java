package com.projects.jez.dontbeevil.ui.views.adapters.listadapters;

import android.view.View;

/**
 * An interface that ensures a data object T can
 * be converted into a View.
 * This is designed to be used by RowAdapter, for a List.
 * You should create a class, specific to your data item
 * that implements this interface, and pass it to RowAdapter.
 */
public interface RowViewGenerator<T> {
    View createView();
    void updateView(View v, T item);
}

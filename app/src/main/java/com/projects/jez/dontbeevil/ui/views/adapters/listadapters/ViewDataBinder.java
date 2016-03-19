package com.projects.jez.dontbeevil.ui.views.adapters.listadapters;

import android.view.View;

public interface ViewDataBinder <T> {
    void bind(View view, T data);
}

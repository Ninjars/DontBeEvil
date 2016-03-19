package com.projects.jez.dontbeevil.ui.views.adapters.listadapters;

import android.content.Context;

import java.util.Collections;

public class LayoutSingleRowAdapter extends LayoutRowAdapter<Void> {

    public LayoutSingleRowAdapter(Context context, int rowLayoutId, ViewDataBinder<Void> viewBinder) {
        super(context, Collections.singletonList((Void)null), rowLayoutId, viewBinder);
    }
}
package com.projects.jez.dontbeevil.ui.views.adapters.listadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.projects.jez.utils.observable.Observable;
import com.projects.jez.utils.observable.ObservableList.ObservableList;

import java.util.List;

public class LayoutRowAdapter <T> extends RowAdapter<T> implements RowViewGenerator<T>{
    int mRowLayoutId;
    protected ViewDataBinder<T> mViewDataBinder;

    public LayoutRowAdapter(Context context, List<T> data, int rowLayoutId, ViewDataBinder<T> viewBinder) {
        super(context, data);
        mRowLayoutId = rowLayoutId;
        mViewDataBinder = viewBinder;
        super.setRowViewGenerator(this);
    }

    public LayoutRowAdapter(Context context, Observable<List<T>> data, int rowLayoutId, ViewDataBinder<T> viewBinder) {
        super(context, data);
        mRowLayoutId = rowLayoutId;
        mViewDataBinder = viewBinder;
        super.setRowViewGenerator(this);
    }

    public LayoutRowAdapter(Context context, ObservableList<T> data, int rowLayoutId, ViewDataBinder<T> viewBinder) {
        super(context, data);
        mRowLayoutId = rowLayoutId;
        mViewDataBinder = viewBinder;
        super.setRowViewGenerator(this);
    }

    @Override
    public void setRowViewGenerator(RowViewGenerator<T> viewGenerator) {
        throw new RuntimeException("You should not be calling this. Use RowAdapter directly if you want to use this method");
    }

    @Override
    public View createView() {
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(mRowLayoutId, null);
    }

    @Override
    public void updateView(View v, T item) {
        mViewDataBinder.bind(v, item);
    }
}
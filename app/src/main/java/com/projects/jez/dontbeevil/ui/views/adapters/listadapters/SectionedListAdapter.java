package com.projects.jez.dontbeevil.ui.views.adapters.listadapters;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.projects.jez.utils.observable.Observable;
import com.projects.jez.utils.observable.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * A BaseAdapter that houses a List of other ListAdapters.
 * Use this class if your ListView is to contain rows of different types.
 * For example, a header row, followed by many mission rows.
 * You would create individual adapters for each data type (a section)
 * and add them to this "uber" adapter which will treat them as consecutive rows.
 */
public class SectionedListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

    private List<ListAdapter> mSectionAdapters;
    private int mCount;

    public SectionedListAdapter(List<ListAdapter> sectionListAdapters){
        construct(sectionListAdapters);
    }

    public SectionedListAdapter(Observable<List<ListAdapter>> adapters) {
        adapters.addObserverImmediate(new Observer<List<ListAdapter>>() {
            @Override
            public void observe(List<ListAdapter> arg) {
                construct(arg);
            }
        });
    }

    private void construct(List<ListAdapter> sectionListAdapters) {
        mSectionAdapters = new ArrayList<>(sectionListAdapters);
        for(ListAdapter listAdapter : mSectionAdapters){
            listAdapter.registerDataSetObserver(new DataSetObserver() {
            	@Override
            	public void onChanged() {
					onContentChange();
					notifyDataSetChanged();
            	}
            	
            	@Override
            	public void onInvalidated() {
            		notifyDataSetInvalidated();
            	}
			});
        }
        onContentChange();
		notifyDataSetChanged();
    }

    private ItemPosition getItemPosition(int position){
        for(ListAdapter listAdapter : mSectionAdapters){
            if(position < listAdapter.getCount()){
                return new ItemPosition(listAdapter, position);
            } else {
                position -= listAdapter.getCount();
            }
        }
        //position is invalid.
        return null;
    }
    
    private void onContentChange() {
    	mCount = 0;
        for(ListAdapter listAdapter : mSectionAdapters){
            mCount += listAdapter.getCount();
        }
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Object getItem(int position) {
        ItemPosition itemPosition = getItemPosition(position);
        return itemPosition.mListAdapter.getItem(itemPosition.mPosition);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ItemPosition itemPosition = getItemPosition(position);
        return itemPosition.mListAdapter.getView(itemPosition.mPosition, view, viewGroup);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ItemPosition itemPosition = getItemPosition(position);
        if(itemPosition.mListAdapter instanceof AdapterView.OnItemClickListener){
            AdapterView.OnItemClickListener itemClickListener = (AdapterView.OnItemClickListener) itemPosition.mListAdapter;
            itemClickListener.onItemClick(adapterView, view, itemPosition.mPosition, l);
        }
    }

    private class ItemPosition {
        ListAdapter mListAdapter;
        int mPosition;//within list adapter

        private ItemPosition(ListAdapter adapter, int position){
            mListAdapter = adapter;
            mPosition = position;
        }
    }
}
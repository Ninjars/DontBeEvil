package com.projects.jez.dontbeevil.ui.views.adapters.listadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.projects.jez.dontbeevil.R;
import com.projects.jez.utils.observable.Disposable;
import com.projects.jez.utils.observable.Observable;
import com.projects.jez.utils.observable.ObservableList.ObservableList;
import com.projects.jez.utils.observable.ObservableListChange;
import com.projects.jez.utils.observable.Observer;

import java.util.List;

/**
 * RowAdapter is a generic ArrayAdapter to
 * back listviews. Rather than subclassing a listview,
 * we can create one of these, give it a customised
 * RowViewGenerator implementation.
 */
public class RowAdapter<T> extends ArrayAdapter<T>
    implements AdapterView.OnItemClickListener {
    private static final String TAG = RowAdapter.class.getSimpleName();
    private static final boolean DLOG = false;
    private Disposable disposable;

    protected RowViewGenerator<T> mRowViewGenerator;
    protected ItemSelectionHandler<T> mItemSelectionListener;

    public enum RowDividersMode{
        NONE,
        ALL_ROWS,
        ALL_BUT_LAST_ROW
    }
    private RowDividersMode mRowDividersMode = RowDividersMode.NONE;

    /**
     * Constructor for a single item
     */
    public RowAdapter(Context context, T item){
        super(context, 0);
        add(item);
    }

    /**
     * Constructor for multi item.
     */
    public RowAdapter(Context context, List<T> data){
        super(context, 0);
        addAll(data);
    }

    /**
     * Constructor for observable list.
     */
    public RowAdapter(Context context, Observable<List<T>> data) {
        super(context, 0);
        disposable = data.addObserverImmediate(new Observer<List<T>>() {
            @Override
            public void observe(List<T> arg) {
                clear();
                addAll(arg);
                notifyDataSetChanged();
            }
        });
    }

    /**
     * Constructor for ObservableList.
     */
    public RowAdapter(Context context, ObservableList<T> data) {
        super(context, 0);
        disposable = data.stream(new Observer<ObservableListChange<T>>() {
            @Override
            public void observe(ObservableListChange<T> arg) {
                switch (arg.getOperation()) {
                    case ADD:
                        insert(arg.getElement(), arg.getIndex());
                        break;
                    case REMOVE:
                        remove(arg.getElement());
                }
                notifyDataSetChanged();
            }
        });
    }

    public void showRowDividers(RowDividersMode rowDividerMode){
        mRowDividersMode = rowDividerMode;
    }

    public void setRowViewGenerator(RowViewGenerator<T> viewGenerator){
        mRowViewGenerator = viewGenerator;
    }

    public void setSelectionListener(ItemSelectionHandler<T> listener){
        mItemSelectionListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Content view
        View v = mRowViewGenerator.createView();

        //Mo:  "This prevents highlights on views without an Item Selection Listener (ISL).
        //      This doesn't cover edge cases (like adding an ISL after creating the view).
        //      but we don't have a use case for that (yet? ever?) so deal with it later :)"
        v.setFocusable(mItemSelectionListener == null);

        mRowViewGenerator.updateView(v, getItem(position));
        //
        boolean showRowDivider = (mRowDividersMode == RowDividersMode.ALL_ROWS) || (mRowDividersMode == RowDividersMode.ALL_BUT_LAST_ROW && position < getCount() - 1);
        if(showRowDivider) {
            //Add a divider to our layout
            LinearLayout viewContainer = new LinearLayout(getContext());
            viewContainer.setOrientation(LinearLayout.VERTICAL);
            viewContainer.addView(v);

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewContainer.addView(inflater.inflate(R.layout.default_row_divider, viewContainer, false));
            return viewContainer;
        } else {
            return v;
        }
    }

    /**
     * AdapterView.OnItemClickListener
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(mItemSelectionListener != null) {
            mItemSelectionListener.onSelected(getItem(position));
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (disposable != null) disposable.dispose();
        super.finalize();
    }
}

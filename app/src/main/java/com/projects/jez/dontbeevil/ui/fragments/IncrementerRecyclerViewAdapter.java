package com.projects.jez.dontbeevil.ui.fragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projects.jez.dontbeevil.R;
import com.projects.jez.dontbeevil.data.Incrementer;
import com.projects.jez.dontbeevil.engine.LoopTaskManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Jeremy.Stevens on 03/11/2016.
 */

public class IncrementerRecyclerViewAdapter extends RecyclerView.Adapter<IncrementerViewHolder> {

    @NonNull
    private final List<Incrementer> items;
    private final Comparator<Incrementer> comparator;
    private final LoopTaskManager taskManager;

    public IncrementerRecyclerViewAdapter(
            @NonNull final LoopTaskManager taskManager,
            @NonNull Collection<Incrementer> items,
            @NonNull Comparator<Incrementer> comparator) {
        this.items = new ArrayList<>(items);
        this.comparator = comparator;
        Collections.sort(this.items, comparator);
        this.taskManager = taskManager;
    }

    public void add(Incrementer incrementer) {
        items.add(incrementer);
        Collections.sort(this.items, comparator);
        int index = items.indexOf(incrementer);
        notifyItemInserted(index);
    }

    public void remove(Incrementer incrementer) {
        int index = items.indexOf(incrementer);
        items.remove(incrementer);
        notifyItemRemoved(index);
    }

    @Override
    public IncrementerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.value_readout, parent, false);
        return new IncrementerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(IncrementerViewHolder holder, int position) {
        // unbind old
        int oldPosition = holder.getOldPosition();
        if (oldPosition >= 0 && oldPosition < items.size()) {
            Incrementer oldIncrementer = items.get(oldPosition);
            oldIncrementer.attachListener(null);
        }
        holder.bind(taskManager, items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

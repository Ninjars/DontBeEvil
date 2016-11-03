package com.projects.jez.dontbeevil.ui.fragments;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.projects.jez.dontbeevil.R;
import com.projects.jez.dontbeevil.data.IIncrementerListener;
import com.projects.jez.dontbeevil.data.IIncrementerUpdater;
import com.projects.jez.dontbeevil.data.Incrementer;
import com.projects.jez.dontbeevil.engine.LoopTaskManager;
import com.projects.jez.dontbeevil.engine.Range;
import com.projects.jez.utils.Logger;

import java.lang.ref.WeakReference;

/**
 * Created by Jeremy.Stevens on 03/11/2016.
 */

public class IncrementerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final TextView readoutTitle;
    private final TextView readoutValue;
    private final TextView readoutCost;
    private final ProgressBar progressBar;
    private final IIncrementerListener incrementerListener;
    private @Nullable Incrementer boundIncrementer;

    public IncrementerViewHolder(View view) {
        super(view);
        view.setOnClickListener(this);
        readoutTitle = (TextView) view.findViewById(R.id.readout_title);
        readoutValue = (TextView) view.findViewById(R.id.readout_value);
        readoutCost = (TextView) view.findViewById(R.id.readout_cost);
        progressBar = (ProgressBar) view.findViewById(R.id.readout_progress);

        incrementerListener = new IIncrementerListener() {
            @Override
            public void onUpdate(IIncrementerUpdater updater) {
                if (readoutValue != null) {
                    readoutValue.setText(String.valueOf((int) updater.getValue()));
                }

                if (readoutCost != null) {
                    Long value = updater.getCostValue();
                    if (value == null) {
                        readoutCost.setVisibility(View.GONE);
                    } else {
                        readoutCost.setVisibility(View.VISIBLE);
                        readoutCost.setText(updater.getCostName() + ": "
                                + String.valueOf(-updater.getCostValue()));
                    }
                }
            }
        };
    }

    public void bind(final LoopTaskManager taskManager, final Incrementer incrementer) {
        this.boundIncrementer = incrementer;
        incrementer.attachListener(incrementerListener);
        readoutTitle.setText(incrementer.getTitle());

        Range range = incrementer.getRange();
        if (range == null) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            final WeakReference<ProgressBar> weakProgressBar = new WeakReference<>(progressBar);
            taskManager.startOrReplaceLoopingTask(getUpdateTaskKey(incrementer.getId()), 15, new Runnable() {
                @Override
                public void run() {
                    long currentTime = System.currentTimeMillis();
                    ProgressBar strongProgressBar = weakProgressBar.get();
                    if (strongProgressBar == null) {
                        return;
                    }
                    Range range = incrementer.getRange();
                    if (range == null) {
                        strongProgressBar.setProgress(0);
                    } else {
                        double progression = range.getCappedProgression(currentTime);
                        int progressValue = (int) Math.floor(progression * strongProgressBar.getMax());
                        strongProgressBar.setProgress(progressValue);
                    }
                }
            });
        }
    }

    private static String getUpdateTaskKey(String id) {
        return id + "_updater";
    }

    @Override
    public void onClick(View view) {
        if (boundIncrementer == null) {
            Logger.e(this, "onSelected() item <NULL>");
        } else {
            Logger.d(this, "onSelected() item " + boundIncrementer.getId());
            boundIncrementer.preformPurchaseActions();
        }
    }
}

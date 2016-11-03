package com.projects.jez.dontbeevil.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.projects.jez.dontbeevil.R;
import com.projects.jez.dontbeevil.data.IIncrementerListener;
import com.projects.jez.dontbeevil.data.IIncrementerUpdater;
import com.projects.jez.dontbeevil.data.Incrementer;
import com.projects.jez.dontbeevil.engine.LoopTaskManager;
import com.projects.jez.dontbeevil.engine.Range;
import com.projects.jez.dontbeevil.managers.Environment;
import com.projects.jez.dontbeevil.managers.GameManager;
import com.projects.jez.dontbeevil.state.GameState;
import com.projects.jez.dontbeevil.ui.IncrementerComparator;
import com.projects.jez.dontbeevil.ui.views.adapters.listadapters.ItemSelectionHandler;
import com.projects.jez.dontbeevil.ui.views.adapters.listadapters.LayoutRowAdapter;
import com.projects.jez.dontbeevil.ui.views.adapters.listadapters.ViewDataBinder;
import com.projects.jez.utils.Logger;
import com.projects.jez.utils.observable.ObservableList.ObservableList;

import java.lang.ref.WeakReference;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private Handler mHandler;
    private Environment mEnvironment;

    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mEnvironment = Environment.getInstance(getContext().getApplicationContext());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        View view = getView();
        View playButton = view.findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameManager gameManager = mEnvironment.getGameManager();
                boolean appliedChange = gameManager.getPlaysIncrementer().modifyValue(1);
                // TODO: perform action if change didn't apply
            }
        });
        GameManager gameManager = mEnvironment.getGameManager();
        mHandler = new Handler(Looper.getMainLooper());
        bindReadouts(view, gameManager, mEnvironment.getTaskManager());
    }

    @Override
    public void onPause() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onPause();
    }

    private void bindReadouts(View view, GameManager gameManager, final LoopTaskManager taskManager) {
        GameState gameState = gameManager.getGameState();
        ObservableList<Incrementer> readouts = gameState.getReadouts().sort(new IncrementerComparator());

        LayoutRowAdapter<Incrementer> readoutAdapter = new LayoutRowAdapter<>(view.getContext(), readouts, R.layout.value_readout, new ViewDataBinder<Incrementer>() {
            @Override
            public void bind(View view, final Incrementer data) {
                TextView readoutTitle = (TextView) view.findViewById(R.id.readout_title);
                readoutTitle.setText(data.getTitle());
                final TextView readoutValue = (TextView) view.findViewById(R.id.readout_value);
                final TextView readoutCost = (TextView) view.findViewById(R.id.readout_cost);
                final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.readout_progress);

                final WeakReference<TextView> weakValueView = new WeakReference<>(readoutValue);
                final WeakReference<TextView> weakCostView = new WeakReference<>(readoutCost);
                final WeakReference<ProgressBar> weakProgressBar = new WeakReference<>(progressBar);

                data.addListener(new IIncrementerListener() {

                    @Override
                    public void onUpdate(IIncrementerUpdater updater) {
                        TextView strongValueView = weakValueView.get();
                        if (strongValueView != null) {
                            strongValueView.setText(String.valueOf((int) updater.getValue()));
                        }

                        TextView strongCostView = weakCostView.get();
                        if (strongCostView != null) {
                            Long value = updater.getCostValue();
                            if (value == null) {
                                strongCostView.setVisibility(View.GONE);
                            } else {
                                strongCostView.setVisibility(View.VISIBLE);
                                strongCostView.setText(updater.getCostName() + ": "
                                        + String.valueOf(-updater.getCostValue()));
                            }
                        }
                    }
                });

                Range range = data.getRange();
                if (range == null) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    taskManager.startOrReplaceLoopingTask(getUpdateTaskKey(data.getId()), 15, new Runnable() {
                        @Override
                        public void run() {
                            long currentTime = System.currentTimeMillis();
                            ProgressBar strongProgressBar = weakProgressBar.get();
                            if (strongProgressBar == null) {
                                return;
                            }
                            Range range = data.getRange();
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
        });
        readoutAdapter.setSelectionListener(new ItemSelectionHandler<Incrementer>() {
            @Override
            public void onSelected(Incrementer item) {
                Logger.d(MainActivityFragment.this, "onSelected() item " + item.getId());
                item.preformPurchaseActions();
            }
        });
        ListView listView = (ListView) view.findViewById(R.id.readout_list);
        listView.setAdapter(readoutAdapter);
        listView.setOnItemClickListener(readoutAdapter);
    }

    private static String getUpdateTaskKey(String id) {
        return id + "_updater";
    }
}

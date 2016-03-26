package com.projects.jez.dontbeevil.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.projects.jez.dontbeevil.R;
import com.projects.jez.dontbeevil.data.Incrementer;
import com.projects.jez.dontbeevil.engine.Range;
import com.projects.jez.dontbeevil.managers.Environment;
import com.projects.jez.dontbeevil.managers.GameManager;
import com.projects.jez.dontbeevil.state.GameState;
import com.projects.jez.dontbeevil.ui.IncrementerComparator;
import com.projects.jez.dontbeevil.ui.views.adapters.listadapters.ItemSelectionHandler;
import com.projects.jez.dontbeevil.ui.views.adapters.listadapters.LayoutRowAdapter;
import com.projects.jez.dontbeevil.ui.views.adapters.listadapters.ViewDataBinder;
import com.projects.jez.utils.Box;
import com.projects.jez.utils.RefreshSignalProvider;
import com.projects.jez.utils.observable.Mapper;
import com.projects.jez.utils.observable.Observable;
import com.projects.jez.utils.observable.ObservableList.ObservableList;
import com.projects.jez.utils.react.ProgressbarProperties;
import com.projects.jez.utils.react.TextViewProperties;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final String TAG = MainActivityFragment.class.getSimpleName();
    private static final boolean DLOG = true;
    private GameManager mGameManager;

    public MainActivityFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();
        mGameManager = new GameManager(Environment.getInstance(getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        View view = getView();
        View playButton = view.findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameManager.getPlaysIncrementer().addValue(1);
            }
        });
        bindReadouts(view, mGameManager);
    }

    private static void bindReadouts(View view, GameManager gameManager) {
        GameState gameState = gameManager.getGameState();
        ObservableList<Incrementer> readouts = gameState.getReadouts().sort(new IncrementerComparator());
        final Observable<Boolean> refreshObs = RefreshSignalProvider.getInstance().getSignal();

        LayoutRowAdapter<Incrementer> readoutAdapter = new LayoutRowAdapter<>(view.getContext(), readouts, R.layout.value_readout, new ViewDataBinder<Incrementer>() {
            @Override
            public void bind(View view, final Incrementer data) {
                TextView readoutTitle = (TextView) view.findViewById(R.id.readout_title);
                readoutTitle.setText(data.getTitle());
                TextView readoutValue = (TextView) view.findViewById(R.id.readout_value);
                Observable<String> readoutValueText = data.getValue().map(new Mapper<Double, String>() {
                    @Override
                    public String map(Double arg) {
                        return String.valueOf(arg.intValue());
                    }
                });
                TextViewProperties.bindTextProperty(readoutValue, readoutValueText);

                final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.readout_progress);
                Observable<Box<Range>> rangeObs = data.getRangeObservable();
                if (rangeObs == null) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    Observable<Integer> progressObs = rangeObs.join(refreshObs).map(new Mapper<Pair<Box<Range>, Boolean>, Integer>() {
                        @Override
                        public Integer map(Pair<Box<Range>, Boolean> arg) {
                            Range range = arg.first.getValue();
                            if (range == null) return 0;
                            double progression = range.getCappedProgression(System.currentTimeMillis());
                            return (int) Math.floor(progression * progressBar.getMax());
                        }
                    });
                    ProgressbarProperties.bindProgressProperty(progressBar, progressObs);
                }
            }
        });
        readoutAdapter.setSelectionListener(new ItemSelectionHandler<Incrementer>() {
            @Override
            public void onSelected(Incrementer item) {
                if (DLOG) Log.d(TAG, "onSelected() item " + item.getId());
                item.preformPurchaseActions();
            }
        });
        ListView listView = (ListView) view.findViewById(R.id.readout_list);
        listView.setAdapter(readoutAdapter);
        listView.setOnItemClickListener(readoutAdapter);
    }
}

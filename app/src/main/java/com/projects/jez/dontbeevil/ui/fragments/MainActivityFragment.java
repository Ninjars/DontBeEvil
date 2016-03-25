package com.projects.jez.dontbeevil.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.projects.jez.dontbeevil.R;
import com.projects.jez.dontbeevil.data.Incrementer;
import com.projects.jez.dontbeevil.managers.Environment;
import com.projects.jez.dontbeevil.managers.GameManager;
import com.projects.jez.dontbeevil.state.GameState;
import com.projects.jez.dontbeevil.ui.IncrementerComparator;
import com.projects.jez.dontbeevil.ui.views.adapters.listadapters.ItemSelectionHandler;
import com.projects.jez.dontbeevil.ui.views.adapters.listadapters.LayoutRowAdapter;
import com.projects.jez.dontbeevil.ui.views.adapters.listadapters.ViewDataBinder;
import com.projects.jez.utils.observable.Mapper;
import com.projects.jez.utils.observable.Observable;
import com.projects.jez.utils.observable.ObservableList.ObservableList;
import com.projects.jez.utils.react.TextViewProperties;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final String TAG = MainActivityFragment.class.getSimpleName();
    private static final boolean DLOG = true;

    public MainActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        final GameManager gameManager = Environment.getInstance(getContext()).getGameManager();
        View view = getView();
        View playButton = view.findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameManager.getPlaysIncrementer().increment();
            }
        });
        bindReadouts(view);
    }

    private static void bindReadouts(View view) {
        final GameManager gameManager = Environment.getInstance(view.getContext()).getGameManager();
        GameState gameState = gameManager.getGameState();
        ObservableList<Incrementer> readouts = gameState.getReadouts().sort(new IncrementerComparator());

        LayoutRowAdapter<Incrementer> readoutAdapter = new LayoutRowAdapter<>(view.getContext(), readouts, R.layout.value_readout, new ViewDataBinder<Incrementer>() {
            @Override
            public void bind(View view, Incrementer data) {
                TextView readoutTitle = (TextView) view.findViewById(R.id.readout_title);
                readoutTitle.setText(data.getTitle());
                TextView readoutValue = (TextView) view.findViewById(R.id.readout_value);
                Observable<String> readoutValueText = data.getValue().map(new Mapper<Long, String>() {
                    @Override
                    public String map(Long arg) {
                        return arg.toString();
                    }
                });
                TextViewProperties.bindTextProperty(readoutValue, readoutValueText);
            }
        });
        readoutAdapter.setSelectionListener(new ItemSelectionHandler<Incrementer>() {
            @Override
            public void onSelected(Incrementer item) {
                if (DLOG) Log.d(TAG, "onSelected() item " + item.getId());
                item.increment();
            }
        });
        ListView listView = (ListView) view.findViewById(R.id.readout_list);
        listView.setAdapter(readoutAdapter);
        listView.setOnItemClickListener(readoutAdapter);
    }
}

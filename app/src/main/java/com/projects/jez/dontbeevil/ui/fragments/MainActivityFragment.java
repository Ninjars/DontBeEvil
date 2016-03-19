package com.projects.jez.dontbeevil.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.projects.jez.dontbeevil.R;
import com.projects.jez.dontbeevil.managers.Environment;
import com.projects.jez.dontbeevil.managers.GameManager;
import com.projects.jez.dontbeevil.state.GameState;
import com.projects.jez.dontbeevil.state.IncrementerReadout;
import com.projects.jez.dontbeevil.ui.IncrementerReadoutComparator;
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

        final GameManager gameManager = Environment.getInstance().getGameManager();
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
        final GameManager gameManager = Environment.getInstance().getGameManager();
        GameState gameState = gameManager.getGameState();
        ObservableList<IncrementerReadout> readouts = gameState.getReadouts().sort(new IncrementerReadoutComparator());

        ListAdapter readoutAdapter = new LayoutRowAdapter<>(view.getContext(), readouts, R.layout.value_readout, new ViewDataBinder<IncrementerReadout>() {
            @Override
            public void bind(View view, IncrementerReadout data) {
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
        ListView listView = (ListView) view.findViewById(R.id.readout_list);
        listView.setAdapter(readoutAdapter);
    }
}

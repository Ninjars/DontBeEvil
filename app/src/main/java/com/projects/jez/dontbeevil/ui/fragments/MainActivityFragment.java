package com.projects.jez.dontbeevil.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projects.jez.dontbeevil.R;
import com.projects.jez.dontbeevil.Environment;
import com.projects.jez.dontbeevil.data.IncrementableValue;
import com.projects.jez.dontbeevil.data.Incrementer;
import com.projects.jez.dontbeevil.managers.IncrementerManager;
import com.projects.jez.utils.observable.Mapper;
import com.projects.jez.utils.observable.Observable;
import com.projects.jez.utils.react.TextViewProperties;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final String cPlayIncrementerId = "plays";

    public MainActivityFragment() {
        Environment environment = Environment.getInstance();
        IncrementerManager manager = environment.getIncrementerManager();
        if (manager.getIncrementer(cPlayIncrementerId) == null) {
            manager.addIncrementer(new Incrementer(cPlayIncrementerId, new IncrementableValue()));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        Environment environment = Environment.getInstance();
        IncrementerManager manager = environment.getIncrementerManager();
        final Incrementer playIncrementer = manager.getIncrementer(cPlayIncrementerId);

        View view = getView();
        View playButton = view.findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playIncrementer.getValue().increment();
            }
        });

        TextView readoutTitle = (TextView) view.findViewById(R.id.readout_title);
        readoutTitle.setText(playIncrementer.getId());
        TextView readoutValue = (TextView) view.findViewById(R.id.readout_value);
        Observable<String> readoutValueText = playIncrementer.getValue().getCount().map(new Mapper<Long, String>() {
            @Override
            public String map(Long arg) {
                return arg.toString();
            }
        });
        TextViewProperties.bindTextProperty(readoutValue, readoutValueText);
        readoutTitle.setText(playIncrementer.getId());
    }
}

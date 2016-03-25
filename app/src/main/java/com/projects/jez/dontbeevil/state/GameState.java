package com.projects.jez.dontbeevil.state;

import com.projects.jez.dontbeevil.data.Incrementer;
import com.projects.jez.dontbeevil.managers.IncrementerManager;
import com.projects.jez.utils.observable.ObservableList.ObservableArrayList;
import com.projects.jez.utils.observable.ObservableList.ObservableList;

import java.util.ArrayList;

/**
 * Created by Jez on 18/03/2016.
 */
public class GameState {
    private static final String TAG = GameState.class.getSimpleName();
    private static final boolean DLOG = true;

    private final ObservableArrayList<Incrementer> readouts;

    public GameState(IncrementerManager incManager) {
        readouts = new ObservableArrayList<>(new ArrayList<>(incManager.getAllIncrementers()));
    }

    public ObservableList<Incrementer> getReadouts() {
        return readouts;
    }
}

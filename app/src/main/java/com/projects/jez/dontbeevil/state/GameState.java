package com.projects.jez.dontbeevil.state;

import android.util.Log;

import com.projects.jez.dontbeevil.data.Incrementer;
import com.projects.jez.dontbeevil.managers.IncrementerManager;
import com.projects.jez.utils.observable.ObservableList.ObservableArrayList;
import com.projects.jez.utils.observable.ObservableList.ObservableList;

/**
 * Created by Jez on 18/03/2016.
 */
public class GameState {
    private static final String TAG = GameState.class.getSimpleName();
    private static final boolean DLOG = true;

    private final ObservableArrayList<IncrementerReadout> readouts = new ObservableArrayList<>();

    public GameState(IncrementerManager incManager) {
        for (Incrementer readout : incManager.getAllIncrementers()) {
            if (DLOG) Log.d(TAG, "adding readout for " + readout.getId());
            readouts.add(readout);
        }
    }

    public ObservableList<IncrementerReadout> getReadouts() {
        return readouts;
    }
}

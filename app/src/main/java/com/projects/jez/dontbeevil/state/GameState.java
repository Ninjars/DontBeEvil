package com.projects.jez.dontbeevil.state;

import com.projects.jez.dontbeevil.Constants;
import com.projects.jez.dontbeevil.managers.IncrementerManager;
import com.projects.jez.utils.observable.ObservableList.ObservableArrayList;
import com.projects.jez.utils.observable.ObservableList.ObservableList;

/**
 * Created by Jez on 18/03/2016.
 */
public class GameState {
    private final ObservableArrayList<IncrementerReadout> readouts = new ObservableArrayList<>();

    public GameState(IncrementerManager incManager) {
        readouts.add(incManager.getIncrementer(Constants.cPlayIncrementerId));
    }

    public ObservableList<IncrementerReadout> getReadouts() {
        return readouts;
    }
}

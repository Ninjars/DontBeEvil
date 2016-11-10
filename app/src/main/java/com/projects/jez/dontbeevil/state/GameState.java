package com.projects.jez.dontbeevil.state;

import com.projects.jez.dontbeevil.data.Incrementer;
import com.projects.jez.dontbeevil.managers.IncrementerManager;

import java.util.Collection;

/**
 * Created by Jez on 18/03/2016.
 */
public class GameState {
    private final IncrementerManager incrementerManager;

    public GameState(IncrementerManager incManager) {
        incrementerManager = incManager;
    }

    public Collection<Incrementer> getReadouts() {
        return incrementerManager.getAllIncrementers();
    }
}

package com.projects.jez.dontbeevil.managers;

import com.projects.jez.dontbeevil.Constants;
import com.projects.jez.dontbeevil.data.Incrementer;
import com.projects.jez.dontbeevil.state.GameState;

/**
 * Created by Jez on 18/03/2016.
 */
public class GameManager {
    private static final String TAG = GameManager.class.getSimpleName();
    private static final boolean DLOG = true;

    private GameState mGameState;
    private final IncrementerManager mIncrementerManager;

    public GameManager(final IncrementerManager incrementerManager) {
        mIncrementerManager = incrementerManager;
        mGameState = new GameState(incrementerManager);
    }

    public GameState getGameState() {
        return mGameState;
    }

    public Incrementer getPlaysIncrementer() {
        return mIncrementerManager.getIncrementer(Constants.cPlayIncrementerId);
    }
}

package com.projects.jez.dontbeevil.managers;

import com.projects.jez.dontbeevil.Constants;
import com.projects.jez.dontbeevil.data.Incrementer;
import com.projects.jez.dontbeevil.state.GameState;

/**
 * Created by Jez on 18/03/2016.
 */
public class GameManager {

    private final GameState mGameState;
    private final Incrementer mPlaysIncrementer;

    protected GameManager(IncrementerManager incManager) {
        mGameState = new GameState(incManager);
        mPlaysIncrementer = incManager.getIncrementer(Constants.cPlayIncrementerId);
    }

    public GameState getGameState() {
        return mGameState;
    }

    public Incrementer getPlaysIncrementer() {
        return mPlaysIncrementer;
    }
}

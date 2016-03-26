package com.projects.jez.dontbeevil.managers;

import android.util.Log;

import com.projects.jez.dontbeevil.Constants;
import com.projects.jez.dontbeevil.content.IncrementerScript;
import com.projects.jez.dontbeevil.data.Incrementer;
import com.projects.jez.dontbeevil.state.GameState;
import com.projects.jez.utils.MapperUtils;
import com.projects.jez.utils.observable.Mapper;

import java.util.List;

/**
 * Created by Jez on 18/03/2016.
 */
public class GameManager {
    private static final String TAG = GameManager.class.getSimpleName();
    private static final boolean DLOG = true;

    private GameState mGameState;
    private Environment mEnvironment;

    public GameManager(final Environment environment) {
        mEnvironment = environment;
        List<Incrementer> incrementers = MapperUtils.optionalMapOptionalList(environment.getContentLoader().getIncrementers(), new Mapper<IncrementerScript, Incrementer>() {
            @Override
            public Incrementer map(IncrementerScript arg) {
                if (DLOG) Log.d(TAG, "creating incrementer with id " + arg.getId());
                return new Incrementer(arg, environment.getIncrementerManager(), environment.getTaskManager());
            }
        });
        environment.getIncrementerManager().addAll(incrementers);
        mGameState = new GameState(environment.getIncrementerManager());
    }

    public GameState getGameState() {
        return mGameState;
    }

    public Incrementer getPlaysIncrementer() {
        return mEnvironment.getIncrementerManager().getIncrementer(Constants.cPlayIncrementerId);
    }
}

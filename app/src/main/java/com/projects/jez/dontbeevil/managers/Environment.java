package com.projects.jez.dontbeevil.managers;

import android.content.Context;
import android.util.Log;

import com.projects.jez.dontbeevil.content.ContentLoader;
import com.projects.jez.dontbeevil.content.IncrementerScript;
import com.projects.jez.dontbeevil.data.Incrementer;
import com.projects.jez.dontbeevil.engine.LoopTaskManager;
import com.projects.jez.utils.MapperUtils;
import com.projects.jez.utils.observable.Mapper;

import java.util.List;

/**
 * Created by Jez on 18/03/2016.
 */
public final class Environment {
    private static final String TAG = Environment.class.getSimpleName();
    private static final boolean DLOG = true;
    private static Environment instance = null;

    private final IncrementerManager incrementerManager;
    private final LoopTaskManager taskManager;
    private final ContentLoader contentLoader;
    private final GameManager gameManager;

    public static synchronized Environment getInstance(Context context) {
        if (instance == null) {
            Log.d(TAG, "### instantiated ###");
            instance = new Environment(context);
        }
        return instance;
    }

    private Environment(Context context) {
        contentLoader = new ContentLoader(context);
        taskManager = new LoopTaskManager();

        incrementerManager = new IncrementerManager();

        List<Incrementer> incrementers = MapperUtils.optionalMapOptionalList(contentLoader.getIncrementers(), new Mapper<IncrementerScript, Incrementer>() {
            @Override
            public Incrementer map(IncrementerScript arg) {
                if (DLOG) Log.d(TAG, "creating incrementer with id " + arg.getId());
                return new Incrementer(arg, incrementerManager, taskManager);
            }
        });
        incrementerManager.addAll(incrementers);
        gameManager = new GameManager(incrementerManager);
    }

    public IncrementerManager getIncrementerManager() {
        return incrementerManager;
    }

    public LoopTaskManager getTaskManager() {
        return taskManager;
    }

    public ContentLoader getContentLoader() {
        return contentLoader;
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}

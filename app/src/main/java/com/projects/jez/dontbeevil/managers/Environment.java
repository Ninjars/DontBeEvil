package com.projects.jez.dontbeevil.managers;

import android.content.Context;

import com.projects.jez.dontbeevil.content.ContentLoader;
import com.projects.jez.dontbeevil.content.IncrementerScript;
import com.projects.jez.dontbeevil.data.Incrementer;
import com.projects.jez.dontbeevil.engine.LoopTaskManager;
import com.projects.jez.utils.Logger;
import com.projects.jez.utils.MapperUtils;
import com.projects.jez.utils.observable.Mapper;

import java.util.List;

/**
 * Created by Jez on 18/03/2016.
 */
public final class Environment {
    private static Environment instance = null;

    private final IncrementerManager incrementerManager;
    private final LoopTaskManager taskManager;
    private final ContentLoader contentLoader;
    private final GameManager gameManager;

    public static synchronized Environment getInstance(Context context) {
        if (instance == null) {
            instance = new Environment(context);
            Logger.d(instance, "### instantiated ###");
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
                Logger.d(Environment.this, "creating incrementer with id " + arg.getId());
                return Incrementer.create(arg, incrementerManager, taskManager);
            }
        });
        incrementerManager.addAll(incrementers);

        List<Incrementer> upgrades = MapperUtils.optionalMapOptionalList(contentLoader.getUpgrades(), new Mapper<IncrementerScript, Incrementer>() {
            @Override
            public Incrementer map(IncrementerScript arg) {
                return Incrementer.create(arg, incrementerManager, taskManager);
            }
        });
        incrementerManager.addAll(upgrades);

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

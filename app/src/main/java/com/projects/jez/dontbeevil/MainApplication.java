package com.projects.jez.dontbeevil;

import android.app.Application;

/**
 * Created by Jez on 05/10/2016.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DebugConfig.init();
    }
}

package com.projects.jez.dontbeevil.data;

import android.support.annotation.Nullable;

/**
 * Created by Jez on 05/10/2016.
 */

public interface IIncrementerUpdater {
    double getValue();
    String getCostName();
    @Nullable
    Long getCostValue();
}

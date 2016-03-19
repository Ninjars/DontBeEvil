package com.projects.jez.dontbeevil.state;

import com.projects.jez.utils.observable.Observable;

/**
 * Created by Jez on 18/03/2016.
 */
public interface IncrementerReadout {
    Observable<Long> getValue();
    String getTitle();
    String getCaption();
    Integer getSortOrder();
}

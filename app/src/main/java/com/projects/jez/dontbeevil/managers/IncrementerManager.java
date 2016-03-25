package com.projects.jez.dontbeevil.managers;

import android.support.annotation.Nullable;

import com.projects.jez.dontbeevil.data.Incrementer;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jez on 18/03/2016.
 */
public class IncrementerManager {
    private HashMap<String, Incrementer> mIncrementers = new HashMap<>();

    protected IncrementerManager() {
        // protected constructor
    }

    public void addIncrementer(Incrementer incrementer) {
        mIncrementers.put(incrementer.getId(), incrementer);
    }

    @Nullable
    public Incrementer getIncrementer(String id) {
        return mIncrementers.get(id);
    }

    public Collection<Incrementer> getAllIncrementers() {
        return mIncrementers.values();
    }

    public void addAll(List<Incrementer> incrementers) {
        for (Incrementer incrementer : incrementers) {
            mIncrementers.put(incrementer.getId(), incrementer);
        }
    }
}

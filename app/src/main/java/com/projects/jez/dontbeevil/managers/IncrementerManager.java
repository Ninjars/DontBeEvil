package com.projects.jez.dontbeevil.managers;

import com.projects.jez.dontbeevil.data.Incrementer;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Jez on 18/03/2016.
 */
public class IncrementerManager {
    private HashMap<String, Incrementer> mIncrementers = new HashMap<>();

    public void addIncrementer(Incrementer incrementer) {
        mIncrementers.put(incrementer.getId(), incrementer);
    }

    public Incrementer getIncrementer(String id) {
        return mIncrementers.get(id);
    }

    public Collection<Incrementer> getAllIncrementers() {
        return mIncrementers.values();
    }
}

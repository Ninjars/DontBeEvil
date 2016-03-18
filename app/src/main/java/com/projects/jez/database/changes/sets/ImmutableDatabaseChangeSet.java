package com.projects.jez.database.changes.sets;

import com.projects.jez.database.changes.DatabaseChangeSet;
import com.projects.jez.database.changes.DatabaseClassChangeSet;
import com.projects.jez.database.changes.Filter;
import com.projects.jez.database.changes.FilterMap;

import java.util.HashMap;
import java.util.Map.Entry;

public class ImmutableDatabaseChangeSet implements DatabaseChangeSet {
	
	private HashMap<Class<?>, DatabaseClassChangeSet<?>> mChanges = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public <T> ImmutableDatabaseChangeSet(HashMap<Class<?>, DatabaseClassChangeSet<?>> changeSets, FilterMap filters) {
		if (filters == null) {
			filters = new FilterMap();
			filters.setIncludesUnfilteredClasses(true);
		}
		for (Entry<Class<?>, DatabaseClassChangeSet<?>> entry : changeSets.entrySet()) {
			Class<T> modelClass = (Class<T>) entry.getKey();
			DatabaseClassChangeSet<T> set = (DatabaseClassChangeSet<T>) entry.getValue();
			Filter<? super T> filter = filters.get(modelClass);
			if (filter != null) {
				set = new ImmutableDatabaseClassChangeSet<>(set, filter);
			}
			else if (!filters.includesUnfilteredClasses()) {
				continue;
			}
			
			if (!set.isEmpty()) {
				mChanges.put(modelClass, set);
			}
		}
	}

	public ImmutableDatabaseChangeSet(HashMap<Class<?>, DatabaseClassChangeSet<?>> changeSets) {
		this(changeSets, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> DatabaseClassChangeSet<T> getChangeSetForClass(
			Class<T> modelClass) {
		return (DatabaseClassChangeSet<T>) mChanges.get(modelClass);
	}
	
	@Override
	public boolean isEmpty() {
		return mChanges.isEmpty(); // we've filtered out empty objects, so if we have anything, they're not empty
	}

}

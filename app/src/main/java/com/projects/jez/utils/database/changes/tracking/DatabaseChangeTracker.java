package com.projects.jez.utils.database.changes.tracking;

import com.projects.jez.utils.database.Database.TRANSACTION_TYPE;
import com.projects.jez.utils.database.changes.DatabaseChangeObserver;
import com.projects.jez.utils.database.changes.DatabaseChangeSet;
import com.projects.jez.utils.database.changes.DatabaseClassChangeSet;
import com.projects.jez.utils.database.changes.FilterMap;
import com.projects.jez.utils.database.changes.sets.ImmutableDatabaseChangeSet;

import java.util.HashMap;
import java.util.Map.Entry;

public class DatabaseChangeTracker {
	
	private HashMap<Class<?>, DatabaseClassChangeTracker<?>> mPerClassChangeTrackers = new HashMap<>();
	
	private HashMap<DatabaseChangeObserver, FilterMap> mObservers = new HashMap<>();
	
	private boolean mBatching;
	private boolean mDidMakeBatchedChanges;

	private <T> DatabaseClassChangeTracker<T> getChangeTracker(Class<T> modelClass) {
		@SuppressWarnings("unchecked")
		DatabaseClassChangeTracker<T> changeTracker = (DatabaseClassChangeTracker<T>) mPerClassChangeTrackers.get(modelClass);
		if (changeTracker == null) {
			changeTracker = new DatabaseClassChangeTracker<>();
			mPerClassChangeTrackers.put(modelClass, changeTracker);
		}
		return changeTracker;
	}
	
	private <T> DatabaseClassChangeTracker<T> getChangeTracker(T object) {
		@SuppressWarnings("unchecked")
		Class<T> classType = (Class<T>) object.getClass();
		return getChangeTracker(classType);
	}
		
	public <T> void willChange(T object, TRANSACTION_TYPE type) {
		DatabaseClassChangeTracker<T> classChangeTracker = getChangeTracker(object);
		classChangeTracker.addChange(object, type);
	}
	
	public <T> void didChange(T object, TRANSACTION_TYPE type) {
		if (mBatching) {
			mDidMakeBatchedChanges = true;
		}
		else {
			sendChangeNotifications();
		}
	}
	
	public void addChangeObserver(FilterMap filter, DatabaseChangeObserver observer) {
		if (filter == null) {
			filter = new FilterMap();
			filter.setIncludesUnfilteredClasses(true);
		}
		mObservers.put(observer, filter);
	}

	public void removeChangeObserver(DatabaseChangeObserver observer) {
		mObservers.remove(observer);
	}
	
	private void sendChangeNotifications() {
		HashMap<Class<?>, DatabaseClassChangeSet<?>> classChangeSets = new HashMap<>();
		for (Entry<Class<?>, DatabaseClassChangeTracker<?>> entry : mPerClassChangeTrackers.entrySet()) {
			DatabaseClassChangeTracker<?> classChangeTracker = entry.getValue();
			DatabaseClassChangeSet<?> classChanges = classChangeTracker.getChangeSet();
			if (classChanges != null) {
				classChangeSets.put(entry.getKey(), classChanges);
			}
			classChangeTracker.clear();
		}
		for (Entry<DatabaseChangeObserver, FilterMap> entry : mObservers.entrySet()) {
			DatabaseChangeObserver observer = entry.getKey();
			DatabaseChangeSet changeSet = new ImmutableDatabaseChangeSet(classChangeSets, entry.getValue());
			observer.onChange(changeSet);
		}
	}
	
	public void performBatchChanges(Runnable runnable) {
		
		mBatching = true;
		mDidMakeBatchedChanges = false;
		
		runnable.run();
		
		if (mDidMakeBatchedChanges) {
			sendChangeNotifications();
		}
		
		mBatching = false;
	}
	
}

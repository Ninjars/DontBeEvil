package com.projects.jez.database.changes.sets;

import com.projects.jez.database.Database.TRANSACTION_TYPE;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class MutableDatabaseClassChangeSet<T> extends
		AbstractDatabaseClassChangeSet<T> {
		
	private class ChangesHashMap extends HashMap<TRANSACTION_TYPE, HashSet<T>> {
		
		private static final long serialVersionUID = -5435728593850117434L;

		private HashSet<T> getOrCreate(TRANSACTION_TYPE type) {
			HashSet<T> changedObjects = get(type);
			if(changedObjects == null){
				changedObjects = new HashSet<>();
				put(type, changedObjects);
			}
			return changedObjects;
		}
		
	}
	
	private ChangesHashMap mChanges = new ChangesHashMap();
	
	@Override
	public boolean isEmpty() {
		return mChanges.isEmpty();
	}
	
	@Override
	public Collection<T> getChangedObjects(TRANSACTION_TYPE type) {
		return mChanges.get(type);
	}
	
	public void addChange(T object, TRANSACTION_TYPE type) {
		mChanges.getOrCreate(type).add(object);
	}
	
	public void clear() {
		mChanges.clear();
	}
}

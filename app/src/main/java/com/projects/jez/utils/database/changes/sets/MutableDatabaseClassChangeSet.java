package com.projects.jez.utils.database.changes.sets;

import com.projects.jez.utils.database.Database;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class MutableDatabaseClassChangeSet<T> extends
		AbstractDatabaseClassChangeSet<T> {
		
	private class ChangesHashMap extends HashMap<Database.TRANSACTION_TYPE, HashSet<T>> {
		
		private static final long serialVersionUID = -5435728593850117434L;

		private HashSet<T> getOrCreate(Database.TRANSACTION_TYPE type) {
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
	public Collection<T> getChangedObjects(Database.TRANSACTION_TYPE type) {
		return mChanges.get(type);
	}
	
	public void addChange(T object, Database.TRANSACTION_TYPE type) {
		mChanges.getOrCreate(type).add(object);
	}
	
	public void clear() {
		mChanges.clear();
	}
}

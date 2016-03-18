package com.projects.jez.database.changes.sets;

import com.projects.jez.database.Database.TRANSACTION_TYPE;
import com.projects.jez.database.changes.DatabaseClassChangeSet;
import com.projects.jez.database.changes.Filter;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class ImmutableDatabaseClassChangeSet<T> extends
		AbstractDatabaseClassChangeSet<T> {
	
	private HashMap<TRANSACTION_TYPE, HashSet<T>> mChanges = new HashMap<>();
	
	public ImmutableDatabaseClassChangeSet(DatabaseClassChangeSet<T> set, Filter<? super T> filter) {
		mChanges = new HashMap<>();
		if (set == null) return;
		
		for (TRANSACTION_TYPE type : TRANSACTION_TYPE.values()) {
			Collection<T> objects = set.getChangedObjects(type);
			if (objects == null) continue;
			
			if (filter != null) {
				HashSet<T> filteredObjects = new HashSet<>();
				for (T object : objects) {
					if (filter.include(object, type)) {
						filteredObjects.add(object);
					}
				}
				objects = filteredObjects;
			}
			
			if (!objects.isEmpty()) {
				mChanges.put(type, new HashSet<>(objects));
			}
		}
	}
	
	public ImmutableDatabaseClassChangeSet(DatabaseClassChangeSet<T> set) {
		this(set, null);
	}
	
	@Override
	public boolean isEmpty() {
		return mChanges.isEmpty(); // we've filtered out empty objects, so if we have anything, they're not empty
	}
	
	@Override
	public Collection<T> getChangedObjects(TRANSACTION_TYPE type) {
		return mChanges.get(type);
	}
}

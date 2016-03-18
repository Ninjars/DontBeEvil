package com.projects.jez.utils.database.changes.sets;

import com.projects.jez.utils.database.changes.DatabaseClassChangeSet;
import com.projects.jez.utils.database.changes.Filter;
import com.projects.jez.utils.database.Database;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class ImmutableDatabaseClassChangeSet<T> extends
		AbstractDatabaseClassChangeSet<T> {
	
	private HashMap<Database.TRANSACTION_TYPE, HashSet<T>> mChanges = new HashMap<>();
	
	public ImmutableDatabaseClassChangeSet(DatabaseClassChangeSet<T> set, Filter<? super T> filter) {
		mChanges = new HashMap<>();
		if (set == null) return;
		
		for (Database.TRANSACTION_TYPE type : Database.TRANSACTION_TYPE.values()) {
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
	public Collection<T> getChangedObjects(Database.TRANSACTION_TYPE type) {
		return mChanges.get(type);
	}
}

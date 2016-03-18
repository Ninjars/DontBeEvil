package com.projects.jez.utils.database.changes.tracking;

import com.projects.jez.utils.database.Database.TRANSACTION_TYPE;
import com.projects.jez.utils.database.changes.DatabaseClassChangeSet;
import com.projects.jez.utils.database.changes.sets.ImmutableDatabaseClassChangeSet;
import com.projects.jez.utils.database.changes.sets.MutableDatabaseClassChangeSet;

public class DatabaseClassChangeTracker<T> {
	
	private MutableDatabaseClassChangeSet<T> mChangeSet = new MutableDatabaseClassChangeSet<>();
	
	public void addChange(T object, TRANSACTION_TYPE type) {
		if (mChangeSet == null) {
			mChangeSet = new MutableDatabaseClassChangeSet<>();
		}
		mChangeSet.addChange(object, type);
	}
	
	public DatabaseClassChangeSet<T> getChangeSet() {
		return new ImmutableDatabaseClassChangeSet<>(mChangeSet);
	}
	
	public void clear() {
		mChangeSet.clear();
	}
}

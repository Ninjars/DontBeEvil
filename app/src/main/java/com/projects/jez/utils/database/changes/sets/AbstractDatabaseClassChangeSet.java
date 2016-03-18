package com.projects.jez.utils.database.changes.sets;

import com.projects.jez.utils.database.Database.TRANSACTION_TYPE;
import com.projects.jez.utils.database.changes.DatabaseClassChangeSet;

import java.util.Collection;

public abstract class AbstractDatabaseClassChangeSet<T> implements DatabaseClassChangeSet<T> {

	@Override
	public abstract Collection<T> getChangedObjects(TRANSACTION_TYPE transactionType);
	
	public abstract boolean isEmpty();

	@Override
	public Collection<T> getInsertedObjects() {
		return getChangedObjects(TRANSACTION_TYPE.INSERT);
	}

	@Override
	public Collection<T> getUpdatedObjects() {
		return getChangedObjects(TRANSACTION_TYPE.UPDATE);
	}

	@Override
	public Collection<T> getDeletedObjects() {
		return getChangedObjects(TRANSACTION_TYPE.DELETE);
	}
}

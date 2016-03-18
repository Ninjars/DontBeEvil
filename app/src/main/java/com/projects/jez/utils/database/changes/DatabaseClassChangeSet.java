package com.projects.jez.utils.database.changes;

import com.projects.jez.utils.database.Database.TRANSACTION_TYPE;

import java.util.Collection;


public interface DatabaseClassChangeSet<T> {
	
	/*
	 * (non-Javadoc)
	 * @see com.projects.jez.database.DatabaseClassChangeSet#getChangedObjects(Database.TRANSACTION_TYPE)
	 * Implementations are encouraged to not return empty objects and instead return null when there's no change.
	 * This way you mostly wouldn't need to check for emptiness, for example when trying to optimize a section of your code.
	 * However, as this is in no way enforced in code, any such assumption should *not* change the correctness of your code. 
	 */
	Collection<T> getChangedObjects(TRANSACTION_TYPE transactionType);
	
	boolean isEmpty();
	
	// Convenience methods
	
	Collection<T> getInsertedObjects();
	
	Collection<T> getUpdatedObjects();
	
	Collection<T> getDeletedObjects();
	
}

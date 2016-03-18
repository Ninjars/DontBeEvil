package com.projects.jez.database.changes;


public interface DatabaseChangeSet {
	
	/*
	 * (non-Javadoc)
	 * @see com.projects.jez.database.DatabaseClassChangeSet#getChangedObjects(com.projects.jez.database.Database.TRANSACTION_TYPE)
	 * Implementations are encouraged to not return empty objects and instead return null when there's no change.
	 * This way you mostly wouldn't need to check for emptiness, for example when trying to optimize a section of your code.
	 * However, as this is in no way enforced in code, any such assumption should *not* change the correctness of your code. 
	 */
	<T> DatabaseClassChangeSet<T> getChangeSetForClass(Class<T> modelClass);
	
	boolean isEmpty();
}

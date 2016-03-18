package com.projects.jez.database;


import com.projects.jez.database.changes.DatabaseChangeObserver;
import com.projects.jez.database.changes.FilterMap;

public interface Database {
	
	enum TRANSACTION_TYPE {
		INSERT,
		UPDATE,
		DELETE
	}
	
	/* Fetch */
	
	<T extends BaseModel> T fetchObject(Class<T> objectClass, Long id);
	
	/* Update */
	
	<T extends BaseModel> void insert(T object);
	
	<T extends BaseModel> void update(T object);
	
	<T extends BaseModel> void delete(T object);

    <T extends BaseModel> void refresh(T object);

    <T extends BaseModel> T create(Class<T> objectClass);

    <T extends BaseModel> T create(Class<T> objectClass, DatabaseObjectInitialiser<T> initialiser);
	
	/* Batching Changes */
	
	void performBatchChanges(Runnable runnable);
	
	/* Observe */
	
	void addFilteredChangeObserver(FilterMap filter, DatabaseChangeObserver observer);

	void addChangeObserver(DatabaseChangeObserver observer);

	void removeChangeObserver(DatabaseChangeObserver observer);
	
}

package com.projects.jez.database.changes;

public interface DatabaseChangeObserver {
	void onChange(DatabaseChangeSet changes);
}

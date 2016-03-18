package com.projects.jez.utils.database.changes;

public interface DatabaseChangeObserver {
	void onChange(DatabaseChangeSet changes);
}

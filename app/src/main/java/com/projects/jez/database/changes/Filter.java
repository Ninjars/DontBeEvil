package com.projects.jez.database.changes;

import com.projects.jez.database.Database.TRANSACTION_TYPE;

public interface Filter<T> {
	boolean include(T object, TRANSACTION_TYPE type);
}

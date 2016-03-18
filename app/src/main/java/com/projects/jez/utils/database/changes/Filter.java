package com.projects.jez.utils.database.changes;

import com.projects.jez.utils.database.Database.TRANSACTION_TYPE;

public interface Filter<T> {
	boolean include(T object, TRANSACTION_TYPE type);
}

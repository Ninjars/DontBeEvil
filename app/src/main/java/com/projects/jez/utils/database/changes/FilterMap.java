package com.projects.jez.utils.database.changes;

import java.util.HashMap;

public class FilterMap {
	
	private boolean includesUnfilteredClasses;
	private HashMap<Class<?>, Filter<?>> mFilters = new HashMap<>();
	
	public <T> void put(Class<T> modelClass, Filter<? super T> filter) {
		mFilters.put(modelClass, filter);
	}

	@SuppressWarnings("unchecked")
	public <T> Filter<? super T> get(Class<T> modelClass) {
		return  (Filter<? super T>)mFilters.get(modelClass);
	}

	public boolean includesUnfilteredClasses() {
		return includesUnfilteredClasses;
	}

	public void setIncludesUnfilteredClasses(boolean includesUnfilteredClasses) {
		this.includesUnfilteredClasses = includesUnfilteredClasses;
	}
	
}

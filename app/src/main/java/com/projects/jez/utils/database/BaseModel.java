package com.projects.jez.utils.database;

import com.j256.ormlite.field.DatabaseField;

public abstract class BaseModel {
	@DatabaseField(generatedId=true)
	private Long id;
	
	protected BaseModel() {/* for ORMLite use */}

	public Long getId() {
		return id;
	}
}

package com.projects.jez.utils.database.orm;

public class OrmDatabaseInfo {
    private final VersionInfo[] mVersionInfos;
    private final String mName;

    public OrmDatabaseInfo(String name, VersionInfo[] versionInfos) {
        mName = name;
        mVersionInfos = versionInfos;
    }

    public String getName() {
		return mName;
	}

	public int getVersion() {
		return mVersionInfos.length;
	}

    public VersionInfo[] getVersionInfos() {
        return mVersionInfos;
    }
}

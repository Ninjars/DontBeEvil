package com.projects.jez.utils.database.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class OrmDatabaseHelper extends OrmLiteSqliteOpenHelper {
	private static final String TAG = "DatabaseHelper";
	private OrmDatabaseInfo mInfo;

	public OrmDatabaseHelper(Context context, OrmDatabaseInfo info) {
		super(context, info.getName(), null, info.getVersion());
		mInfo = info;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        updateDb(db, connectionSource, 0, mInfo.getVersion());
    }

    @Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        updateDb(db, connectionSource, oldVersion, newVersion);
	}

    public void updateDb(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        int index = oldVersion;
        VersionInfo[] upgradeCommands = mInfo.getVersionInfos();
        while (index < newVersion) {
            VersionInfo info = upgradeCommands[index];
            createTables(info, connectionSource);
            // don't want to execute table changes if it's the first run, as the tables will be correct
            boolean isUpdatingExistingDb = oldVersion != 0;
            if (isUpdatingExistingDb) {
                executeCommands(info, db);
            }
            index++;
        }
    }

    private static void createTables(VersionInfo info, ConnectionSource connectionSource) {
        for (Class<?> modelClass : info.classes) {
            try {
                TableUtils.createTable(connectionSource, modelClass);
            } catch (SQLException e) {
                Log.i(TAG, "Exception while creating table", e);
            }
        }
    }

    private static void executeCommands(VersionInfo info, SQLiteDatabase db) {
        for (String string : info.commands) {
            db.execSQL(string);
        }
    }
}

package com.projects.jez.database.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.projects.jez.database.BaseModel;
import com.projects.jez.database.Database;
import com.projects.jez.database.DatabaseObjectInitialiser;
import com.projects.jez.database.changes.DatabaseChangeObserver;
import com.projects.jez.database.changes.FilterMap;
import com.projects.jez.database.changes.tracking.DatabaseChangeTracker;

import java.lang.reflect.Constructor;

public abstract class OrmDatabase implements Database {
	
	private DatabaseChangeTracker mChangeTracker = new DatabaseChangeTracker();
	
	private OrmDatabaseHelper mDatabaseHelper;
	private SQLiteDatabase mSqliteDatabase;
	
	protected abstract OrmDatabaseInfo getDatabaseInfo(); 
	
	public String getName() {
		return getDatabaseInfo().getName();
	}
	
	public OrmDatabase(Context context) {
		OrmDatabaseInfo info = getDatabaseInfo();
		mDatabaseHelper = new OrmDatabaseHelper(context, info);
		mSqliteDatabase = mDatabaseHelper.getWritableDatabase();
		OpenHelperManager.setOpenHelperClass(mDatabaseHelper.getClass());
		OpenHelperManager.setHelper(mDatabaseHelper);
	}	
	
	protected SQLiteDatabase getSqliteDatabase() {
		return mSqliteDatabase;
	}
	
	/* Fetch */
	
	public <T extends BaseModel> T fetchObject(Class<T> objectClass, Long id) {
		RuntimeExceptionDao<T, Long> dao = getRuntimeExceptionDaoForModelClass(objectClass);
		return dao.queryForId(id);
	}
	
	/* Update */
	
	public <T extends BaseModel> void insert(T object) {
		RuntimeExceptionDao<T, Long> dao = getRuntimeExceptionDaoForObject(object);
		mChangeTracker.willChange(object, TRANSACTION_TYPE.INSERT);
		dao.create(object);
		dao.refresh(object);
		mChangeTracker.didChange(object, TRANSACTION_TYPE.INSERT);
	}

    public <T extends BaseModel> T create(Class<T> objectClass) {
        return create(objectClass, null);
    }

    @Nullable
    public <T extends BaseModel> T create(Class<T> objectClass, DatabaseObjectInitialiser<T> initialiser){
        try {
            Constructor<T> constructor = objectClass.getDeclaredConstructor(new Class[]{});
            T object = constructor.newInstance();
            if (initialiser != null) {
                initialiser.init(object);
            }
            insert(object);
            return object;
        } catch (IllegalStateException es) {
            es.printStackTrace();
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not create new " + objectClass.getSimpleName() + ": " + e.getMessage());
        }
    }
	
	public <T extends BaseModel> void update(T object) {
		RuntimeExceptionDao<T, Long> dao = getRuntimeExceptionDaoForObject(object);
		mChangeTracker.willChange(object, TRANSACTION_TYPE.UPDATE);
		dao.update(object);
		mChangeTracker.didChange(object, TRANSACTION_TYPE.UPDATE);
	}
	
	public <T extends BaseModel> void delete(T object) {
		RuntimeExceptionDao<T, Long> dao = getRuntimeExceptionDaoForObject(object);
		mChangeTracker.willChange(object, TRANSACTION_TYPE.DELETE);
		dao.delete(object);
		mChangeTracker.didChange(object, TRANSACTION_TYPE.DELETE);
	}

    public <T extends BaseModel> void refresh(T object) {
        RuntimeExceptionDao<T, Long> dao = getRuntimeExceptionDaoForObject(object);
        dao.refresh(object);
    }
	
	/* Batching Changes */
	
	public void performBatchChanges(Runnable runnable) {
		mChangeTracker.performBatchChanges(runnable);
	}
	
	/* Observing */
	
	@Override
	public void addChangeObserver(DatabaseChangeObserver observer) {
		addFilteredChangeObserver(null, observer);
	}
	
	@Override
	public void addFilteredChangeObserver(FilterMap filter, DatabaseChangeObserver observer) {
		mChangeTracker.addChangeObserver(filter, observer);
	}

	@Override
	public void removeChangeObserver(DatabaseChangeObserver observer) {
		mChangeTracker.removeChangeObserver(observer);
	}
	
	/* DAOs */
	
	// A wrapper for getRuntimeExceptionDao() that makes sure we use the right Long class.
	// use this instead of getRuntimeExceptionDao()
	private <T extends BaseModel> RuntimeExceptionDao<T, Long> getRuntimeExceptionDaoForModelClass(Class <T> c) {
		return mDatabaseHelper.getRuntimeExceptionDao(c);
	}
	
	@SuppressWarnings("unchecked")
	private <T extends BaseModel> RuntimeExceptionDao<T, Long> getRuntimeExceptionDaoForObject(T object) {
        return (RuntimeExceptionDao<T, Long>) getRuntimeExceptionDaoForModelClass(object.getClass());
	}
	
}

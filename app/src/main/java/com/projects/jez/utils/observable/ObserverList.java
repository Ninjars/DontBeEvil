package com.projects.jez.utils.observable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ObserverList<T> {
    private Lock mLock = new ReentrantLock();
	
	class ObserverListDisposable implements Disposable{
		@Override
		public void dispose() {
			mLock.lock();
            mObservers.remove(this);
            mLock.unlock();
		}
	}

    class ObserverListInitialDisposable implements Disposable{
        @Override
        public void dispose() {
            mLock.lock();
            mInitialObserver = null;
            mLock.unlock();
        }
    }
	
	private HashMap<Disposable, Observer<T>> mObservers;
    private Observer<T> mInitialObserver;
	
	Disposable addObserver(Observer<T> observer){
        mLock.lock();
        Disposable disposable;
        if (mInitialObserver == null && mObservers == null) {
            mInitialObserver = observer;
            disposable = new ObserverListInitialDisposable();
        } else {
            if (mObservers == null) mObservers = new HashMap<>();
            disposable = new ObserverListDisposable();
            mObservers.put(disposable, observer);
        }
        mLock.unlock();
		return disposable;
	}
	
	void notifyObservers(T item){
        mLock.lock();
        HashMap<Disposable, Observer<T>> copy = null;
        if (mObservers != null) {
            copy = new HashMap<>(mObservers);
        }
        mLock.unlock();

        if (mInitialObserver != null) {
            mInitialObserver.observe(item);
        }
        if (copy != null) {
            for (Map.Entry<Disposable, Observer<T>> entry : copy.entrySet()) {
                Observer<T> observer = entry.getValue();
                observer.observe(item);
            }
        }
	}
}

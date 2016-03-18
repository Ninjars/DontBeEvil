package com.projects.jez.observable;

/**
 * The only object that can supposedly write to the
 * Observable.
 * @author babu
 *
 * @param <T>
 */
public class Sink<T> implements Observer<T> {
	private Observable.Putter<T> mObservablePutter;
	
	Sink(Observable.Putter<T> observablePutter){
		mObservablePutter = observablePutter;
	}

	public void put(T item){
		mObservablePutter.put(item);
	}

	@Override
	public void observe(T arg) {
		put(arg);
	}
}

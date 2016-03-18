package com.projects.jez.observable;

public class Source <T> {
	private Sink<T> mSinkOf;
	private Observable<T> mObservable;
	
	public Source(T initialValue){
        this(null, initialValue);
	}

    public Source(Object capturedObject, T initialValue) {
        mObservable = new Observable<>(capturedObject, new Observable.Generator<T>() {
            @Override
            public void generate(Observable.Putter<T> putter) {
                mSinkOf = new Sink<>(putter);
            }
        });
        mSinkOf.put(initialValue);
    }

    public Sink<T> getSink(){
		return mSinkOf;
	}
	
	public Observable<T> getObservable(){
		return mObservable;
	}

    public void put(T arg) {
        mSinkOf.put(arg);
    }
}

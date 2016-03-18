package com.projects.jez.utils.observable;

import android.support.annotation.Nullable;
import android.util.Pair;

import com.projects.jez.utils.MapperUtils;
import com.projects.jez.utils.Reducer;

import java.lang.ref.WeakReference;
import java.util.List;

public class Observable<T> {
	private final String TAG = getClass().getSimpleName();
	private T current;
	private ObserverList<T> mObserverList = new ObserverList<>();
    private boolean mInitializing = true;
    private final Object mCapturedObject;

    public Observable(Generator<T> generator) {
        this(null, generator);
    }

    public Observable(Object capturedObj, Generator<T> generator) {
        mCapturedObject = capturedObj;
        Putter<T> putter = new PutterImpl();
        generator.generate(putter);
        mInitializing = false;
    }

    public Observable(T constantValue) {
        mCapturedObject = null;
        current = constantValue;
        mInitializing = false;
    }

    @Nullable
	public T getCurrent() {
		return current;
	}
	
	private void innerPut(T item) {
        if (item == null) {
            return;
        }
		current = item;
        if (!mInitializing) {
            mObserverList.notifyObservers(item);
        }
	}
	
	public Disposable addObserver(Observer<T> observer) {
        return mObserverList.addObserver(observer);
	}

    // A variation on observe which also calls the observer with current value if it's not null
    public Disposable addObserverImmediate(Observer<T> observer) {
        if(current != null){
            observer.observe(current);
        }
        return addObserver(observer);
    }

    /**
     * Allows an object to be used in an observer without the observer preventing the object's lifecycle being extended by it.
     * @param object to be weak referenced
     * @param observer returns the object and the value being observed as a pair; Pair.first will be the value of the weak reference.
     */
    public <S> Disposable addWeakObserver(S object, final Observer<Pair<S, T>> observer) {
        final WeakReference<S> reference = new WeakReference<>(object);
        return addObserver(new Observer<T>() {
            @Override
            public void observe(T arg) {
                S value = reference.get();
                if (value != null) {
                    observer.observe(new Pair<>(value, arg));
                }
            }
        });
    }

    /**
     * Allows an object to be used in an observer without the observer preventing the object's lifecycle being extended by it.
     * @param object to be weak referenced
     * @param observer returns the object and the value being observed as a pair; Pair.first will be the value of the weak reference.
     */
    public <S> Disposable addWeakObserverImmediate(S object, final Observer<Pair<S, T>> observer) {
        final WeakReference<S> reference = new WeakReference<>(object);
        return addObserverImmediate(new Observer<T>() {
            @Override
            public void observe(T arg) {
                S value = reference.get();
                if (value != null) {
                    observer.observe(new Pair<>(value, arg));
                }
            }
        });
    }

    public interface Putter<T> extends Observer<T> {
        void put(T item);
    }

    public class PutterImpl implements Putter<T> {
        public void put(T item) {
            innerPut(item);
        }

        @Override
        public void observe(T arg) {
            this.put(arg);
        }
    }

    public interface Generator<T> {
        void generate(Putter<T> putter);
    }

    // Transformations

    /**
     * When either component observable is updated, this observable returns that value.
     * If both observables have value initially, the value passed in the arg takes precidence.
     */
    public Observable<T> merge(Observable<T> that) {
        return ComposedObservable.merge(this, that);
    }

    public <U, S> Observable<S> combine(Observable<U> that, ComposedObservable.Combiner<T, U, S> combiner) {
        return ComposedObservable.combine(this, that, combiner);
    }

    public <S> Observable<S> reduce(final S initalValue, final Reducer<S, T> reducer) {
        return new Observable<>(this, new Generator<S>() {
            @Override
            public void generate(final Putter<S> putter) {
                putter.put(initalValue);
                addObserverImmediate(new ReducerObserver<>(initalValue, reducer, putter));
            }
        });
    }

    public <S> Observable<S> map(final Mapper<T, S> mapper) {
        return new Observable<>(new Generator<S>() {
            @Override
            public void generate(final Putter<S> putter) {
                addObserverImmediate(new Observer<T>() {
                    @Override
                    public void observe(T arg) {
                        putter.put(mapper.map(arg));
                    }
                });
            }
        });
    }

    public <S> Observable<S> flatMap(final Mapper<T, Observable<S>> mapper) {
        return Observable.flatten(this.map(mapper));
    }

    public <S> Observable<S> strongMap(final Mapper<T, S> mapper) {
        return new Observable<>(this, new Generator<S>() {
            @Override
            public void generate(final Putter<S> putter) {
                addObserverImmediate(new Observer<T>() {
                    @Override
                    public void observe(T arg) {
                        putter.put(mapper.map(arg));
                    }
                });
            }
        });
    }

    public <S> Observable<Pair<T, S>> join(Observable<S> that) {
        return combine(that, new ComposedObservable.Combiner<T, S, Pair<T, S>>() {
            @Override
            public Pair<T, S> combine(T t, S s) {
                return new Pair<>(t, s);
            }
        });
    }

    public static <T> Observable<T> flatten(final Observable<Observable<T>> arg) {
        return new Observable<>(new Generator<T>() {
            public Observable<Disposable> currentDisposable;

            @Override
            public void generate(final Putter<T> putter) {
                Observable<Disposable> disposable = arg.map(new Mapper<Observable<T>, Disposable>() {
                    @Override
                    public Disposable map(Observable<T> arg) {
                        return arg.addObserverImmediate(putter);
                    }
                });

                currentDisposable = disposable.reduce(null, new Reducer<Disposable, Disposable>() {
                    @Override
                    public Disposable reduce(Disposable lastDisposable, Disposable newDisposable) {
                        if (lastDisposable != null) {
                            lastDisposable.dispose();
                        }
                        return newDisposable;
                    }
                });
            }
        });
    }

    private static class ReducerObserver<S, T> implements Observer<T>{
        Putter<S> mPutter;//Source
        S mAccumulatedValue; //count
        Reducer<S, T> mReducer; //to calculate count

        public ReducerObserver(S initialValue, Reducer<S, T> reducer, Putter<S> putter){
            mPutter = putter;
            mReducer = reducer;
            mAccumulatedValue = initialValue;
        }

        @Override
        public void observe(T value) {
            S accumulatedValue = mReducer.reduce(mAccumulatedValue, value);
            if (accumulatedValue != null) {
                mAccumulatedValue = accumulatedValue;
                mPutter.put(mAccumulatedValue);
            }
        }
    }

    public Observable<T> getWeakCopy() {
        Source<T> source = new Source<>(null);
        this.addWeakObserverImmediate(source, new Observer<Pair<Source<T>, T>>() {
            @Override
            public void observe(Pair<Source<T>, T> arg) {
                arg.first.put(arg.second);
            }
        });
        return source.getObservable();
    }

    public static <T> Observable<List<T>> collect(final List<Observable<T>> inList) {
        final Source<T> updateSignal = new Source<>(null);
        return new Observable<>(new Generator<List<T>>() {
            @Override
            public void generate(final Putter<List<T>> putter) {
                for (Observable<T> obs : inList) {
                    obs.addObserverImmediate(updateSignal.getSink());
                }

                updateSignal.getObservable().addObserverImmediate(new Observer<T>() {
                    @Override
                    public void observe(T arg) {
                        List<T> initialList = MapperUtils.optionalMap(inList, new Mapper<Observable<T>, T>() {
                            @Override
                            public T map(Observable<T> arg) {
                                return arg.getCurrent();
                            }
                        });
                        putter.put(initialList);
                    }
                });
            }
        });
    }

    @Override
    public String toString() {
        return "<" + TAG + " " + current + ">";
    }
}

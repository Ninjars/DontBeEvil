package com.projects.jez.utils.observable.ObservableList;

import android.util.Log;

import com.projects.jez.utils.observable.Disposable;
import com.projects.jez.utils.observable.Filter;
import com.projects.jez.utils.observable.Mapper;
import com.projects.jez.utils.observable.Observable;
import com.projects.jez.utils.observable.ObservableListChange;
import com.projects.jez.utils.observable.Observer;
import com.projects.jez.utils.observable.Source;
import com.projects.jez.utils.Box;
import com.projects.jez.utils.MapperUtils;
import com.projects.jez.utils.Reducer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class ObservableList<Element> {

    private UpdateSource updateSource;

    private class UpdateSource {
        private final Source<Integer> updateObservable = new Source<>(0);

        UpdateSource() {
            stream(new Observer<ObservableListChange<Element>>() {
                @Override
                public void observe(ObservableListChange<Element> arg) {
                    switch (arg.getOperation()) {
                        case ADD:
                            updateObservable.put(updateObservable.getObservable().getCurrent() + 1);
                            break;
                        case REMOVE:
                            updateObservable.put(updateObservable.getObservable().getCurrent() - 1);
                            break;
                    }
                }
            });
        }

        public Observable<Boolean> getUpdateSignal() {
            return updateObservable.getObservable().map(new Mapper<Integer, Boolean>() {
                @Override
                public Boolean map(Integer arg) {
                    return true;
                }
            });
        }

        public Observable<Integer> getListSizeSignal() {
            return updateObservable.getObservable();
        }
    }

    private UpdateSource getUpdateSource() {
        if (updateSource == null) {
            updateSource = new UpdateSource();
        }
        return updateSource;
    }

    /**
     * Simple signal that fires when the stream updates.
     */
    public Observable<Boolean> getUpdateSignal() {
        return getUpdateSource().getUpdateSignal();
    }

    /**
     * Signal that reports the list size
     */
    public Observable<Integer> getListSizeSignal() {
        return getUpdateSource().getListSizeSignal();
    }

    /**
     * Signal that returns the current contents of the observableList.
     * May not fire correctly if the observable expires.
     */
    public abstract Observable<List<Element>> getValuesObservable();

    public abstract Disposable stream(Observer<ObservableListChange<Element>> observer);

    public ObservableList<Element> sort(Comparator<Element> comparator) {
        return new SortedObservableList<>(this, comparator);
    }

    public ObservableList<Element> filter(final Filter<Element> filter) {
        return optionalMap(new Mapper<Element, Element>() {
            @Override
            public Element map(Element arg) {
                return filter.passes(arg) ? arg : null;
            }
        });
    }

    public <T> ObservableList<T> map(Mapper<Element, T> mapper) {
        return new MappedObservableList<>(this, mapper);
    }

    public <T> ObservableList<T> optionalMap(Mapper<Element, T> mapper) {
        return new OptionalMappedObservableList<>(this, mapper);
    }

    public <T> ObservableList<Box<T>> flatMap(Mapper<Element, Observable<T>> mapper) {
        return new FlattenedObservableList<>(this, mapper);
    }

    public <T> ObservableList<T> optionalFlatMap(Mapper<Element, Observable<T>> mapper) {
        return flatMap(mapper).optionalMap(new Mapper<Box<T>, T>() {
            @Override
            public T map(Box<T> arg) {
                return arg.getValue();
            }
        });
    }

    public <T> Observable<T> reduce(T initial, final Reducer<T, ObservableListChange<Element>> reducer) {
        final Source<T> source = new Source<>(this, initial);
        stream(new Observer<ObservableListChange<Element>>() {
            @Override
            public void observe(ObservableListChange<Element> arg) {
                source.put(reducer.reduce(source.getObservable().getCurrent(), arg));
            }
        });
        return source.getObservable();
    }

    public Observable<Box<Element>> fold(final Reducer<Element, Element> reducer) {
        final ArrayList<Element> snapshot = new ArrayList<>();
        return reduce(new Box<Element>(null), new Reducer<Box<Element>, ObservableListChange<Element>>() {
            @Override
            public Box<Element> reduce(Box<Element> accumulatedValue, ObservableListChange<Element> deltaValue) {
                switch (deltaValue.getOperation()) {
                    case ADD:
                        snapshot.add(deltaValue.getIndex(), deltaValue.getElement());
                        break;
                    case REMOVE:
                        snapshot.remove(deltaValue.getIndex());
                        break;
                }
                return new Box<>(MapperUtils.fold(snapshot, reducer));
            }
        });
    }

    public ObservableList<Element> flatFilter(final Mapper<Element, Observable<Boolean>> shouldInclude) {
        ObservableList<Box<Box<Element>>> signal = flatMap(new Mapper<Element, Observable<Box<Element>>>() {
            @Override
            public Observable<Box<Element>> map(final Element element) {
                return shouldInclude.map(element).map(new Mapper<Boolean, Box<Element>>() {
                    @Override
                    public Box<Element> map(Boolean shouldInclude) {
                        return shouldInclude ? new Box<>(element) : new Box<Element>(null);
                    }
                });
            }
        });
        return signal.optionalMap(new Mapper<Box<Box<Element>>, Element>() {
            @Override
            public Element map(Box<Box<Element>> arg) {
                Box<Element> boxed = arg.getValue();
                if (boxed == null) return null;
                return boxed.getValue();
            }
        });
    }

    public <Group> ObservableList<GroupedElements<Group, Element>> group(Mapper<Element, Group> grouper) {
        return new GroupedObservableList<>(this, grouper);
    }

    public static <T> ObservableList<Box<T>> flatten(ObservableList<Observable<T>> source) {
        return source.flatMap(new Mapper<Observable<T>, Observable<T>>() {
            @Override
            public Observable<T> map(Observable<T> arg) {
                return arg;
            }
        });
    }

    public static <T> ObservableList<T> wrap(Observable<List<T>> source) {
        return new WrappedObservableList<>(source);
    }

    public static <T> ObservableList<Box<T>> wrap(List<Observable<T>> data) {
        return new FlattenedObservableList<>(new ObservableArrayList<>(data), new Mapper<Observable<T>, Observable<T>>() {
            @Override
            public Observable<T> map(Observable<T> arg) {
                return arg;
            }
        });
    }

    public List<Element> getSnapshot() {
        final ArrayList<Element> returnList = new ArrayList<>();
        Disposable dis = stream(new Observer<ObservableListChange<Element>>() {
            @Override
            public void observe(ObservableListChange<Element> arg) {
                switch (arg.getOperation()) {
                    case ADD:
                        returnList.add(arg.getIndex(), arg.getElement());
                        break;
                    case REMOVE:
                        returnList.remove(arg.getIndex());
                        break;
                }
            }
        });
        dis.dispose();
        return returnList;
    }

    public void startLogging(String tag) {
        startLogging(tag, false);
    }

    public void startLogging(String tag, boolean logSnapshot) {
        startLogging(tag, new Mapper<Element, Element>() {
            @Override
            public Element map(Element arg) {
                return arg;
            }
        }, logSnapshot);
    }

    public <T> void startLogging(final String tag, final Mapper<Element, T> transform, final boolean logSnapshot) {
        Log.i(tag, ">> starting");
        final WeakReference<ObservableList<Element>> selfReference = new WeakReference<>(this);
        stream(new Observer<ObservableListChange<Element>>() {
            @Override
            public void observe(ObservableListChange<Element> arg) {
                if (selfReference.get() != null) {
                    Log.d(tag, "perform " + arg.getOperation() + " at " + arg.getIndex() + " with " + transform.map(arg.getElement()));
                    if (logSnapshot) {
                        Log.v(tag, "snapshot: " + ObservableList.this.map(transform));
                    }
                }
            }
        });
    }
}

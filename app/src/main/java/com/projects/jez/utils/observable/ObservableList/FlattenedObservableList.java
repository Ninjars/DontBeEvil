package com.projects.jez.utils.observable.ObservableList;

import android.util.Pair;

import com.projects.jez.utils.observable.Disposable;
import com.projects.jez.utils.observable.Mapper;
import com.projects.jez.utils.observable.Observable;
import com.projects.jez.utils.observable.ObservableListChange;
import com.projects.jez.utils.observable.Observer;
import com.projects.jez.utils.Box;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

final class FlattenedObservableList<Element, OldElement> extends ObservableList<Box<Element>> {

    private final Disposable streamDisposable;
    private final ArrayList<SignalController<Element>> elementSignals = new ArrayList<>();
    private final ObservableArrayList<Box<Element>> obsList = new ObservableArrayList<>();
    private final Object dataReference; // reference to data, to prevent it being released

    public FlattenedObservableList(ObservableList<OldElement> data, final Mapper<OldElement, Observable<Element>> mapper) {
        dataReference = data;
        final WeakReference<FlattenedObservableList<Element, OldElement>> listRef = new WeakReference<>(this);
        streamDisposable = data.stream(new Observer<ObservableListChange<OldElement>>() {
            @Override
            public void observe(ObservableListChange<OldElement> arg) {
                FlattenedObservableList<Element, OldElement> value = listRef.get();
                if (value != null) {
                    value.process(arg, mapper);
                }
            }
        });
    }

    private void process(ObservableListChange<OldElement> arg, Mapper<OldElement, Observable<Element>> mapper) {
        int index = arg.getIndex();
        final SignalController<Element> controller;
        switch (arg.getOperation()) {
            case ADD:
                controller = new SignalController<>(index, mapper.map(arg.getElement()), obsList);
                elementSignals.add(index, controller);
                break;
            case REMOVE:
                controller = elementSignals.get(index);
                controller.unlink();
                elementSignals.remove(index);
                obsList.remove(controller.index);
                break;
            default:
                throw new RuntimeException("unexpected operation " + arg.getOperation());
        }
        rebuildIndexes(index);
    }

    private void rebuildIndexes(int startIndex) {
        for (int i = startIndex; i < elementSignals.size(); i++) {
            elementSignals.get(i).setIndex(i);
        }
    }

    @Override
    public Disposable stream(Observer<ObservableListChange<Box<Element>>> observer) {
        return obsList.stream(observer);
    }

    private class SignalController<T> {
        private int index;
        private final Disposable disposable;

        public SignalController(int index, Observable<T> observable, final ObservableArrayList<Box<T>> destination) {
            this.index = index;
            destination.add(index, new Box<T>(null));

            if (observable != null) {
                disposable = observable.addWeakObserverImmediate(this, new Observer<Pair<SignalController<T>, T>>() {
                    @Override
                    public void observe(Pair<SignalController<T>, T> arg) {
                        SignalController<T> self = arg.first;
                        destination.remove(self.index);
                        destination.add(self.index, new Box<>(arg.second));
                    }
                });
            } else {
                disposable = null;
            }
        }

        @Override
        protected void finalize() throws Throwable {
            if (disposable != null) {
                disposable.dispose();
            }
            super.finalize();
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void unlink() {
            if (disposable != null) {
                disposable.dispose();
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (streamDisposable != null) {
            streamDisposable.dispose();
        }
        super.finalize();
    }

    @Override
    public Observable<List<Box<Element>>> getValuesObservable() {
        return obsList.getUpdateSignal().map(new Mapper<Boolean, List<Box<Element>>>() {
            @Override
            public List<Box<Element>> map(Boolean arg) {
                return null;
            }
        });
    }
}

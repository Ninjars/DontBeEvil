package com.projects.jez.observable.ObservableList;

import com.projects.jez.observable.Disposable;
import com.projects.jez.observable.Mapper;
import com.projects.jez.observable.Observable;
import com.projects.jez.observable.ObservableListChange;
import com.projects.jez.observable.Observer;

import java.util.ArrayList;
import java.util.List;

final class WrappedObservableList<Element> extends ObservableList<Element> {

    private final ObservableArrayList<Element> obsList = new ObservableArrayList<>();
    private final Object dataReference; // reference to data, to prevent it being released
    private final Disposable disposable;

    public WrappedObservableList(Observable<List<Element>> data) {
        dataReference = data;
        disposable = data.addObserverImmediate(new Observer<List<Element>>() {
            @Override
            public void observe(List<Element> arg) {
                obsList.clear();
                for (Element e : arg) {
                    obsList.add(e);
                }
            }
        });
    }

    @Override
    public Disposable stream(Observer<ObservableListChange<Element>> observer) {
        return obsList.stream(observer);
    }

    @Override
    protected void finalize() throws Throwable {
        if (disposable != null) {
            disposable.dispose();
        }
        super.finalize();
    }

    @Override
    public Observable<List<Element>> getValuesObservable() {
        return obsList.getUpdateSignal().map(new Mapper<Boolean, List<Element>>() {
            @Override
            public List<Element> map(Boolean arg) {
                return new ArrayList<>(obsList.getElements());
            }
        });
    }
}

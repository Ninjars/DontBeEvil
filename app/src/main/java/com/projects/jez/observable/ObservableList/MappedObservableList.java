package com.projects.jez.observable.ObservableList;

import com.projects.jez.observable.Disposable;
import com.projects.jez.observable.Mapper;
import com.projects.jez.observable.Observable;
import com.projects.jez.observable.ObservableListChange;
import com.projects.jez.observable.Observer;

import java.util.ArrayList;
import java.util.List;

final class MappedObservableList<Element> extends ObservableList<Element> {

    private final ObservableArrayList<Element> obsList = new ObservableArrayList<>();
    private final Object dataReference; // reference to data, to prevent it being released
    private final Disposable streamDisposable;

    public <SourceType> MappedObservableList(ObservableList<SourceType> data, final Mapper<SourceType, Element> mapper) {
        dataReference = data;
        streamDisposable = data.stream(new Observer<ObservableListChange<SourceType>>() {
            @Override
            public void observe(ObservableListChange<SourceType> arg) {
                obsList.apply(new ObservableListChange<>(arg.getOperation(), mapper.map(arg.getElement()), arg.getIndex()));
            }
        });
    }

    @Override
    public Disposable stream(Observer<ObservableListChange<Element>> observer) {
        return obsList.stream(observer);
    }

    @Override
    protected void finalize() throws Throwable {
        if (streamDisposable != null) {
            streamDisposable.dispose();
        }
        super.finalize();
    }

    @Override
    public String toString() {
        return obsList.toString();
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

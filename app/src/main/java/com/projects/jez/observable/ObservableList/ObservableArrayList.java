package com.projects.jez.observable.ObservableList;

import com.projects.jez.observable.Disposable;
import com.projects.jez.observable.Mapper;
import com.projects.jez.observable.Observable;
import com.projects.jez.observable.ObservableListChange;
import com.projects.jez.observable.Observer;
import com.projects.jez.observable.Sink;
import com.projects.jez.observable.Source;

import java.util.ArrayList;
import java.util.List;

public final class ObservableArrayList<Element> extends ObservableList<Element> {

    private final Source<ObservableListChange<Element>> changeSource = new Source<>(null);
    private final List<Element> data = new ArrayList<>();

    protected Sink<ObservableListChange<Element>> getChangeSink() {
        return changeSource.getSink();
    }

    public ObservableArrayList(){}

    public ObservableArrayList(List<Element> initial) {
        data.addAll(initial);
    }

    /**
     * @return the current data contents. Be very careful not to change this list!
     */
    List<Element> getElements() {
        return data;
    }

    public void add(Element element) {
        add(data.size(), element);
    }

    public void add(int index, Element element) {
        data.add(index, element);
        getChangeSink().put(new ObservableListChange<>(ObservableListChange.OPERATION.ADD, element, index));
    }

    public void remove(Element element) {
        if (element == null) return;
        for (int index = 0; index < data.size(); index++) {
            if (data.get(index).equals(element)) {
                remove(index);
                break;
            }
        }
    }

    public void remove(int index) {
        if (index < 0 || index >= data.size()) return; // ignore invalid indexes
        Element removedObj = data.get(index);
        data.remove(index);
        getChangeSink().put(new ObservableListChange<>(ObservableListChange.OPERATION.REMOVE, removedObj, index));
    }

    void clear() {
        while (!data.isEmpty()) {
            remove(0);
        }
    }

    @Override
    public Disposable stream(Observer<ObservableListChange<Element>> observer) {
        for (int i = 0; i < data.size(); i++) {
            observer.observe(new ObservableListChange<>(ObservableListChange.OPERATION.ADD, data.get(i), i));
        }
        return changeSource.getObservable().addObserver(observer);
    }

    public void apply(ObservableListChange<Element> change) {
        switch (change.getOperation()) {
            case ADD:
                add(change.getIndex(), change.getElement());
                break;
            case REMOVE:
                remove(change.getIndex());
                break;
            default:
                throw new RuntimeException("unexpected change operation " + change.getOperation());
        }
    }

    @Override
    public String toString() {
        return data.toString();
    }

    @Override
    public Observable<List<Element>> getValuesObservable() {
        return getUpdateSignal().map(new Mapper<Boolean, List<Element>>() {
            @Override
            public List<Element> map(Boolean arg) {
                return new ArrayList<>(getElements());
            }
        });
    }
}

package com.projects.jez.utils.observable.ObservableList;

import com.projects.jez.utils.observable.Disposable;
import com.projects.jez.utils.observable.Mapper;
import com.projects.jez.utils.observable.Observable;
import com.projects.jez.utils.observable.ObservableListChange;
import com.projects.jez.utils.observable.Observer;

import java.util.ArrayList;
import java.util.List;

final class OptionalMappedObservableList<Element, OldElement> extends ObservableList<Element> {

    private final List<Integer> destinationIndexes = new ArrayList<>();
    private final ObservableArrayList<Element> obsList = new ObservableArrayList<>();
    private final Object dataReference; // reference to data, to prevent it being released
    private final Disposable streamDisposable;

    public OptionalMappedObservableList(ObservableList<OldElement> data, final Mapper<OldElement, Element> mapper) {
        dataReference = data;
        streamDisposable = data.stream(new Observer<ObservableListChange<OldElement>>() {
            @Override
            public void observe(ObservableListChange<OldElement> arg) {
                switch (arg.getOperation()) {
                    case ADD:
                        Element val = mapper.map(arg.getElement());
                        if (val != null) {
                            destinationIndexes.add(arg.getIndex(), 0);
                            rebuildIndexes();
                            obsList.add(destinationIndexes.get(arg.getIndex()), val);
                        } else {
                            destinationIndexes.add(arg.getIndex(), null);
                        }
                        break;
                    case REMOVE:
                        Integer removeIndex = destinationIndexes.get(arg.getIndex());
                        destinationIndexes.remove(arg.getIndex());
                        if (removeIndex != null) {
                            obsList.remove(removeIndex);
                            rebuildIndexes();
                        }
                        break;
                }
            }
        });
    }

    private void rebuildIndexes() {
        int counter = 0;
        for (int i = 0; i < destinationIndexes.size(); i++) {
            if (destinationIndexes.get(i) != null) {
                destinationIndexes.set(i, counter);
                counter++;
            }
        }
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
}

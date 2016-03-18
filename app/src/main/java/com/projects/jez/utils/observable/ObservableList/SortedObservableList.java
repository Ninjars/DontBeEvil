package com.projects.jez.utils.observable.ObservableList;

import com.projects.jez.utils.observable.Disposable;
import com.projects.jez.utils.observable.Mapper;
import com.projects.jez.utils.observable.Observable;
import com.projects.jez.utils.observable.ObservableListChange;
import com.projects.jez.utils.observable.Observer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class SortedObservableList<Element> extends ObservableList<Element> {

    private final Comparator<Element> mComparator;
    private final List<Integer> destinationIndexes = new ArrayList<>();
    private final Object dataReference; // reference to data, to prevent it being released
    private final ObservableArrayList<Element> obsList = new ObservableArrayList<>();
    private final Disposable streamDisposable;

    public SortedObservableList(ObservableList<Element> data, final Comparator<Element> comparator) {
        dataReference = data;
        mComparator = comparator;
        streamDisposable = data.stream(new Observer<ObservableListChange<Element>>() {
            @Override
            public void observe(ObservableListChange<Element> arg) {
                int changeIndex = arg.getIndex();
                switch (arg.getOperation()) {
                    case ADD:
                        List<Element> exisitingList = obsList.getSnapshot();
                        boolean added = false;
                        for (int i = 0; i < exisitingList.size(); i++) {
                            Element e = exisitingList.get(i);
                            int comparison = mComparator.compare(arg.getElement(), e);
                            if (comparison > 0) {
                                shiftIndexesFrom(i, +1);
                                destinationIndexes.add(changeIndex, i);
                                obsList.add(i, arg.getElement());
                                added = true;
                                break;
                            }
                        }
                        // may not have found match; if not, append element
                        if (!added) {
                            destinationIndexes.add(changeIndex);
                            obsList.add(arg.getElement());
                        }
                        break;

                    case REMOVE:
                        if (arg.getElement() == null) return;
                        int removeIndex = destinationIndexes.get(changeIndex);
                        obsList.remove(removeIndex);
                        destinationIndexes.remove(changeIndex);
                        shiftIndexesFrom(removeIndex+1, -1);
                        break;
                }
            }
        });
    }

    private void shiftIndexesFrom(int location, int offset) {
        for (int i = 0; i < destinationIndexes.size(); i++) {
            int destination = destinationIndexes.get(i);
            if (destination >= location) {
                destinationIndexes.set(i, destination + offset);
            }
        }
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
    public Observable<List<Element>> getValuesObservable() {
        return obsList.getUpdateSignal().map(new Mapper<Boolean, List<Element>>() {
            @Override
            public List<Element> map(Boolean arg) {
                return new ArrayList<>(obsList.getElements());
            }
        });
    }
}

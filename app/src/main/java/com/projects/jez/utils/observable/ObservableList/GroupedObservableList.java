package com.projects.jez.utils.observable.ObservableList;

import com.projects.jez.utils.observable.Disposable;
import com.projects.jez.utils.observable.Mapper;
import com.projects.jez.utils.observable.Observable;
import com.projects.jez.utils.observable.ObservableListChange;
import com.projects.jez.utils.observable.Observer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

final class GroupedObservableList<Element, Group> extends ObservableList<GroupedElements<Group, Element>> {

    private final ObservableArrayList<GroupedElements<Group, Element>> obsList = new ObservableArrayList<>();
    private final Object dataReference; // reference to data, to prevent it being released
    private final Disposable streamDisposable;

    public GroupedObservableList(ObservableList<Element> data, final Mapper<Element, Group> grouper) {
        dataReference = data;
        final WeakReference<GroupedObservableList<Element, Group>> weakSelfRef = new WeakReference<>(this);
        streamDisposable = data.stream(new Observer<ObservableListChange<Element>>() {
            @Override
            public void observe(ObservableListChange<Element> arg) {
                if (weakSelfRef.get() != null) {
                    weakSelfRef.get().process(arg, grouper);
                }
            }
        });
    }

    private void process(ObservableListChange<Element> change, Mapper<Element, Group> grouper) {
        final Element element = change.getElement();
        final Group targetGroup = grouper.map(element);
        final List<GroupedElements<Group, Element>> elementsList = obsList.getElements();
        GroupedElements<Group, Element> groupedElement = null;
        for (int i = 0; i < elementsList.size(); i++) {
            GroupedElements<Group, Element> grouped = elementsList.get(i);
            if (grouped.getGroup().equals(targetGroup)) {
                groupedElement = grouped;
                break;
            }
        }
        switch (change.getOperation()) {
            case ADD:
                if (groupedElement != null) {
                    groupedElement.getElements().add(0, element);
                } else {
                    List<Element> initial = new ArrayList<>();
                    initial.add(element);
                    obsList.add(new GroupedElements<>(targetGroup, new ObservableArrayList<>(initial)));
                }
                break;
            case REMOVE:
                if (groupedElement == null) return; // unable to remove if the group doesn't exist!
                groupedElement.getElements().remove(element);
                break;
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
    public String toString() {
        return obsList.toString();
    }

    @Override
    public Disposable stream(Observer<ObservableListChange<GroupedElements<Group, Element>>> observer) {
        return obsList.stream(observer);
    }

    @Override
    public Observable<List<GroupedElements<Group, Element>>> getValuesObservable() {
        return obsList.getUpdateSignal().map(new Mapper<Boolean, List<GroupedElements<Group, Element>>>() {
            @Override
            public List<GroupedElements<Group, Element>> map(Boolean arg) {
                return new ArrayList<>(obsList.getElements());
            }
        });
    }
}

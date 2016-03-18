package com.projects.jez.utils.observable.ObservableList;

public class GroupedElements<Group, Element> {
    private Group group;
    private ObservableArrayList<Element> elements;

    public GroupedElements(Group group, ObservableArrayList<Element> elements) {
        this.group = group;
        this.elements = elements;
    }

    public ObservableArrayList<Element> getElements() {
        return elements;
    }

    public Group getGroup() {
        return group;
    }
}

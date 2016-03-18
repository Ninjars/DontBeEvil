package com.projects.jez.observable;

/**
 * Created by jez on 09/12/2015.
 */
public class ObservableListChange<Element> {
    public enum OPERATION {
        ADD,
        REMOVE
    }
    private final OPERATION operation;
    private final Element element;
    private final int index;

    public ObservableListChange(OPERATION operation, Element element, int index) {
        this.operation = operation;
        this.element = element;
        this.index = index;
    }

    public OPERATION getOperation() {
        return operation;
    }

    public Element getElement() {
        return element;
    }

    public int getIndex() {
        return index;
    }

    public <NewElement> ObservableListChange<NewElement> map(Mapper<Element, NewElement> mapper) {
        return new ObservableListChange<>(operation, mapper.map(element), index);
    }

    @Override
    public String toString() {
        return "<" + getClass().getSimpleName() + ": " + operation + " @ " + index + " on " + element + ">";
    }
}

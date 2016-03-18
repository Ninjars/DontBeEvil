package com.projects.jez.utils.observable;

/**
 * Created by jez on 12/02/2016.
 */
public class TaggedSignal<Tag, Element> {
    private final Tag tag;
    private final Observable<Element> signal;

    public TaggedSignal(Tag tag, Observable<Element> signal) {
        this.tag = tag;
        this.signal = signal;
    }

    public <NewElement> TaggedSignal<Tag, NewElement> flatMap(Mapper<Element, Observable<NewElement>> mapper) {
        return new TaggedSignal<>(tag, signal.flatMap(mapper));
    }

    public <NewElement> TaggedSignal<Tag, NewElement> map(Mapper<Element, NewElement> mapper) {
        return new TaggedSignal<>(tag, signal.map(mapper));
    }

    public <NewTag> TaggedSignal<NewTag, Element> group(Mapper<Tag, NewTag> grouper) {
        return new TaggedSignal<>(grouper.map(tag), signal);
    }

    public Tag getTag() {
        return tag;
    }

    public Observable<Element> getSignal() {
        return signal;
    }
}

package com.projects.jez.observable;


import com.projects.jez.observable.ObservableList.GroupedElements;
import com.projects.jez.observable.ObservableList.ObservableArrayList;
import com.projects.jez.observable.ObservableList.ObservableList;
import com.projects.jez.utils.Box;
import com.projects.jez.utils.Reducer;

public class TaggedSignalCollection<Tag, Element> {
    private final ObservableList<TaggedSignal<Tag, Box<Element>>> list;

    public static <Tag, Element> TaggedSignalCollection<Tag, Element> create(ObservableArrayList<TaggedSignal<Tag, Element>> list) {
        ObservableList<TaggedSignal<Tag, Box<Element>>> optionalList = list.map(new Mapper<TaggedSignal<Tag, Element>, TaggedSignal<Tag, Box<Element>>>() {
            @Override
            public TaggedSignal<Tag, Box<Element>> map(TaggedSignal<Tag, Element> arg) {
                return arg.map(new Box.BoxMapper<Element>());
            }
        });
        return new TaggedSignalCollection<>(optionalList);
    }

    private TaggedSignalCollection(ObservableList<TaggedSignal<Tag, Box<Element>>> list) {
        this.list = list;
    }

    private <NewElement> TaggedSignalCollection<Tag, NewElement> boxedMap(final Mapper<Box<Element>, Box<NewElement>> mapper) {
        return new TaggedSignalCollection<>(list.map(new Mapper<TaggedSignal<Tag, Box<Element>>, TaggedSignal<Tag, Box<NewElement>>>() {
            @Override
            public TaggedSignal<Tag, Box<NewElement>> map(TaggedSignal<Tag, Box<Element>> arg) {
                return arg.map(mapper);
            }
        }));
    }

    public <NewElement> TaggedSignalCollection<Tag, NewElement> map(final Mapper<Element, NewElement> mapper) {
        return boxedMap(new Mapper<Box<Element>, Box<NewElement>>() {
            @Override
            public Box<NewElement> map(Box<Element> arg) {
                return arg.map(mapper);
            }
        });
    }

    public <NewElement> TaggedSignalCollection<Tag, NewElement> flatMap(final Mapper<Element, Box<NewElement>> mapper) {
        return boxedMap(new Mapper<Box<Element>, Box<NewElement>>() {
            @Override
            public Box<NewElement> map(Box<Element> arg) {
                return arg.flatMap(mapper);
            }
        });
    }

    public <NewTag> TaggedSignalCollection<NewTag, Element> fold(final Reducer<Element, Element> reducer, final Mapper<Tag, NewTag> grouper) {
        ObservableList<GroupedElements<NewTag, TaggedSignal<Tag, Box<Element>>>> grouped = list.group(new Mapper<TaggedSignal<Tag, Box<Element>>, NewTag>() {
            @Override
            public NewTag map(TaggedSignal<Tag, Box<Element>> arg) {
                return grouper.map(arg.getTag());
            }
        });
        ObservableList<TaggedSignal<NewTag, Box<Element>>> folded = grouped.map(new Mapper<GroupedElements<NewTag, TaggedSignal<Tag, Box<Element>>>, TaggedSignal<NewTag, Box<Element>>>() {
            @Override
            public TaggedSignal<NewTag, Box<Element>> map(GroupedElements<NewTag, TaggedSignal<Tag, Box<Element>>> arg) {
                ObservableList<Box<Box<Element>>> signal = arg.getElements().flatMap(new Mapper<TaggedSignal<Tag, Box<Element>>, Observable<Box<Element>>>() {
                    @Override
                    public Observable<Box<Element>> map(TaggedSignal<Tag, Box<Element>> arg) {
                        return arg.getSignal();
                    }
                });
                ObservableList<Element> el = signal.optionalMap(new Mapper<Box<Box<Element>>, Element>() {
                    @Override
                    public Element map(Box<Box<Element>> arg) {
                        Box<Element> boxed = arg.getValue();
                        if (boxed != null) {
                            return boxed.getValue();
                        }
                        return null;
                    }
                });
                return new TaggedSignal<>(arg.getGroup(), el.fold(reducer));
            }
        });
        return new TaggedSignalCollection<>(folded);
    }
}

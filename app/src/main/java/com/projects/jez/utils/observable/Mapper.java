package com.projects.jez.utils.observable;

public interface Mapper<T, S> {
    S map(T arg);
}

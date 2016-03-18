package com.projects.jez.observable;

public interface Mapper<T, S> {
    S map(T arg);
}

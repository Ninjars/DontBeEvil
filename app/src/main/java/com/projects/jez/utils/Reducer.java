package com.projects.jez.utils;

public interface Reducer<T, U> {
    T reduce(T accumulatedValue, U deltaValue);
}

package com.projects.jez.utils;

import android.util.Pair;

import com.projects.jez.utils.observable.Mapper;

public class Box<T> {
    private final T mValue;

    public Box(T value) {
        mValue = value;
    }

    public T getValue() {
        return mValue;
    }

    public boolean valueNotNull() {
        return mValue != null;
    }

    public static class BoxMapper<T> implements Mapper<T, Box<T>> {
        @Override
        public Box<T> map(T arg) {
            return new Box<>(arg);
        }
    }

    public static class UnboxPair<T, S> implements Mapper<Pair<Box<T>, Box<S>>, Pair<T, S>> {
        @Override
        public Pair<T, S> map(Pair<Box<T>, Box<S>> arg) {
            T firstValue = arg.first == null ? null : arg.first.getValue();
            S secondValue = arg.second == null ? null : arg.second.getValue();
            return new Pair<>(firstValue, secondValue);
        }
    }

    public <S> Box<S> map(Mapper<T, S> mapper) {
        if (mValue == null) {
            return new Box<>(null);
        } else {
            return new Box<>(mapper.map(mValue));
        }
    }

    public <S> Box<S> flatMap(Mapper<T, Box<S>> mapper) {
        if (mValue == null) {
            return new Box<>(null);
        } else {
            return mapper.map(mValue);
        }
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() + " " + (mValue == null ? "null" : mValue.toString()) + ">";
    }
}

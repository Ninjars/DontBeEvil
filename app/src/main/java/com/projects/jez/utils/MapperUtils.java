package com.projects.jez.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.projects.jez.observable.Filter;
import com.projects.jez.observable.Mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapperUtils {
    /**
     * Given a List and an associated Mapper
     * this method will return all values for keys
     * found in List.
     */
    @NonNull
    public static <T, S> List<S> map(@NonNull List<T> list, @NonNull Mapper<T, S> mapper){
        List<S> mapValues = new ArrayList<>();
        for(T listEntry : list){
            mapValues.add(mapper.map(listEntry));
        }
        return mapValues;
    }
    /**
     * Given a List and an associated Mapper
     * this method will return all values for keys
     * found in List, ignoring null values.
     */
    @NonNull
    public static <T, S> List<S> optionalMap(@NonNull List<T> list, @NonNull Mapper<T, S> mapper){
        List<S> mapValues = new ArrayList<>();
        for(T listEntry : list){
            if (listEntry != null) {
                S mappedVal = mapper.map(listEntry);
                if (mappedVal != null) {
                    mapValues.add(mappedVal);
                }
            }
        }
        return mapValues;
    }
    /**
     * Given a List and an associated Mapper
     * this method will return all values for keys
     * found in List. If passed list is null, will return an empty list.
     */
    @NonNull
    public static <T, S> List<S> optionalMapOptionalList(@Nullable List<T> list, @NonNull Mapper<T, S> mapper){
        if (list == null) {
            return new ArrayList<>();
        } else {
            return map(list, mapper);
        }
    }

    /**
     * Given a list, this method returns a list that pairs the index of an entry with the entry.
     * This list can then be used in a mapper that also needs an index
     */
    @NonNull
    public static <T> List<Pair<Integer, T>> enumerateListEntries(@NonNull List<T> list){
        List<Pair<Integer, T>> mapValues = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            mapValues.add(new Pair<>(i, list.get(i)));
        }
        return mapValues;
    }

    /**
     * flattens list of lists to just a single list
     */
    @NonNull
    public static <T> List<T> flatten(@NonNull List<List<T>> nestedList) {
        List<T> list = new ArrayList<>();
        for (List<T> sublist : nestedList) {
            list.addAll(sublist);
        }
        return list;
    }

    @NonNull
    public static <T, S> List<S> filterAndMap(@NonNull List<T> list, @NonNull Filter<T> filter, @NonNull Mapper<T, S> mapper){
        List<S> mapValues = new ArrayList<>();
        for(T listEntry : list){
            if (filter.passes(listEntry)) {
                mapValues.add(mapper.map(listEntry));
            }
        }
        return mapValues;
    }

    @NonNull
    public static <T, S> List<S> mapPairs(@NonNull List<T> list, @NonNull Mapper<Pair<T, T>, S> mapper) {
        List<S> mapValues = new ArrayList<>();
        T previous = null;
        for (T val : list) {
            if (previous != null) {
                mapValues.add(mapper.map(new Pair<>(previous, val)));
            }
            previous = val;
        }
        return mapValues;
    }

    @NonNull
    public static <T, S> T reduce(@NonNull List<S> list, @NonNull T initialValue, @NonNull Reducer<T, S> reducer) {
        T lastVal = initialValue;
        for (S value : list) {
            lastVal = reducer.reduce(lastVal, value);
        }
        return lastVal;
    }
    /**
     * Given a List and an associated Mapper
     * this method will return unique values for keys
     * found in List as a Set.
     */
    @NonNull
    public static <T, S> Set<S> mapUniqueValues(@NonNull List<T> list, @NonNull Mapper<T, S> mapper){
        Set<S> mapValues = new HashSet<>();
        for(T listEntry : list){
            mapValues.add(mapper.map(listEntry));
        }
        return mapValues;
    }

    @Nullable
    public static <T> T fold(List<T> list, Reducer<T, T> reducer) {
        T lastVal = null;
        for (T value : list) {
            if (lastVal == null) {
                lastVal = value;
            } else {
                lastVal = reducer.reduce(lastVal, value);
            }
        }
        return lastVal;
    }
}

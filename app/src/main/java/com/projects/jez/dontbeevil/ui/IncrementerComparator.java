package com.projects.jez.dontbeevil.ui;

import com.projects.jez.dontbeevil.data.Incrementer;

import java.util.Comparator;

public class IncrementerComparator implements Comparator<Incrementer> {
    @Override
    public int compare(Incrementer lhs, Incrementer rhs) {
        return lhs.getSortOrder().compareTo(rhs.getSortOrder());
    }
}

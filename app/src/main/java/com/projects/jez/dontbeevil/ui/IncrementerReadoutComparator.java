package com.projects.jez.dontbeevil.ui;

import com.projects.jez.dontbeevil.state.IncrementerReadout;

import java.util.Comparator;

public class IncrementerReadoutComparator implements Comparator<IncrementerReadout> {
    @Override
    public int compare(IncrementerReadout lhs, IncrementerReadout rhs) {
        return lhs.getSortOrder().compareTo(rhs.getSortOrder());
    }
}

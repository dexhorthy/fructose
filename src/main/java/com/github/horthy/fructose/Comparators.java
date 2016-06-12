package com.github.horthy.fructose;

import java.util.Comparator;

/**
 * Created by on 6/12/16.
 */
public final class Comparators {

    private Comparators() {}

    public static <T> Comparator<Comparator<T>> comparing(T o1, T o2) {
        return Comparator.<Comparator<T>, Integer>comparing(c -> c.compare(o1, o2));
    }
}

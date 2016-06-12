package com.github.horthy.fructose;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

/**
 * Created by on 6/12/16.
 */
public class ComparatorsTest {

    /**
     * handy for debugging
     */
    private static class NamedComparator<T> implements Comparator<T> {
        private final Comparator<T> delegate;
        private final String name;

        private NamedComparator(Comparator<T> delegate, String name) {
            this.delegate = delegate;
            this.name = name;
        }

        @Override
        public int compare(T o1, T o2) {
            return delegate.compare(o1, o2);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final Comparator<String> compareByLength = new NamedComparator<>(
            Comparator.comparing(String::length),
            "compareBylength"
    );

    private final Comparator<String> compareByLastChar = new NamedComparator<>(
           Comparator.<String, Character>comparing(s -> s.charAt(s.length() - 1)) ,
            "compareByLastChar"
    );

    private final Comparator<String> naturalOrder = new NamedComparator<>(
            Comparator.<String>naturalOrder(),
            "naturalOrder"
    );

    @Test
    public void testComparing() throws Exception {
        assertEquals(
                1,
                Comparators.comparing("abcd", "xyz").compare(
                        compareByLength,
                        compareByLastChar
                )
        );
    }

    @Test
    public void testReversed() throws Exception {
        assertEquals(
                -1,
                Comparators.comparing("abcd", "xyz").compare(
                        compareByLastChar,
                        compareByLength
                )
        );
    }

    @Test
    public void testSameValue() throws Exception {
        assertEquals(
                0,
                Comparators.comparing("abc", "abc").compare(
                        compareByLastChar,
                        compareByLength
                )
        );
    }

    @Test
    public void testSameComparator() throws Exception {
        assertEquals(
                0,
                Comparators.comparing("abcd", "xyz").compare(
                        compareByLastChar,
                        compareByLastChar
                )
        );
    }

    @Test
    public void testStream() throws Exception {
        List<Comparator<String>> sorted = Stream.of(
                compareByLength,                     // bc < abc
                naturalOrder                         // abc < bc
        ).sorted(Comparators.comparing("abc", "bc")) // naturalOrder < compareByLength
                .collect(toList());


        List<Comparator<String>> expected = ImmutableList.of(
                naturalOrder,
                compareByLength
        );


        assertEquals(expected, sorted);
    }

}

package com.github.horthy.fructose;

import org.junit.Test;

import static com.google.common.collect.Maps.immutableEntry;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * created by dex on 6/11/16.
 */
public class PredicatesTest {

    @Test
    public void testTesting() throws Exception {
        assertTrue(Predicates.testing("foo").test(x -> x.startsWith("f")));
        assertFalse(Predicates.testing("foo").test(x -> x.startsWith("b")));
    }

    @Test
    public void testKeys() throws Exception {
        boolean passes =
                Predicates.keys((String k) -> k.length() < 3)
                        .test(immutableEntry("ab", "cd"));

        boolean fails =
                Predicates.keys((String k) -> k.length() < 3)
                        .test(immutableEntry("abc", "cd"));

        assertTrue(passes);
        assertFalse(fails);

    }

    @Test
    public void testValues() throws Exception {
        boolean passes =
                Predicates.<String>values(v -> v.length() < 3)
                        .test(immutableEntry("ab", "cd"));

        boolean fails =
                Predicates.<String>values(v -> v.length() < 3)
                        .test(immutableEntry("ab", "cde"));

        assertTrue(passes);
        assertFalse(fails);

    }

    @Test
    public void testNegate() throws Exception {
        assertFalse(Predicates.negate(x -> true).test(null));
    }
}

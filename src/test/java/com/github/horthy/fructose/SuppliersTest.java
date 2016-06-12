package com.github.horthy.fructose;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * created by dex on 6/11/16.
 */
public class SuppliersTest {

    @Test
    public void testSupplying() throws Exception {
        assertEquals("foo", Suppliers.supplying("foo").get());
    }

    @Test
    public void testMapToFunc() throws Exception {

        int result = Suppliers.mapping(String::length).apply(() -> "fooBarBaz").get();

        assertEquals(9, result);
    }

    @Test
    public void testMapping() throws Exception {

        int result = Suppliers.mapping(() -> "fooBarBaz", String::length).get();

        assertEquals(9, result);
    }

}

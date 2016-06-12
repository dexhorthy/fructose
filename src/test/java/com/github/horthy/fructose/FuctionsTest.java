package com.github.horthy.fructose;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * created by dex on 6/11/16.
 */
public class FuctionsTest {

    @Test
    public void testApplying() throws Exception {
        assertEquals(6, Functions.applying(5).apply(x -> x + 1));
    }

    @Test
    public void testBiFunctionApplying() throws Exception {
        assertEquals(3, Functions.applying(1,2).apply((x,y) -> x + y));
    }

    @Test
    public void testBinding() throws Exception {
        assertEquals("barbaz", Functions.binding("bar").apply((x,y) -> x + y).apply("baz"));
    }

    @Test
    public void testBindingLeft() throws Exception {
        assertEquals("barbaz", Functions.bindLeft((x, y) -> x + y, "bar").apply("baz"));
    }

    @Test
    public void testBindingRight() throws Exception {
        assertEquals("bazbar", Functions.bindRight((x, y) -> x + y, "bar").apply("baz"));
    }

    @Test
    public void testBindingLeftFunction() throws Exception {
        assertEquals("barbaz", Functions.bindLeft("bar").apply((x, y) -> x + y).apply("baz"));
    }

    @Test
    public void testBindingRightFunction() throws Exception {
        assertEquals("bazbar", Functions.bindRight("bar").apply((x, y) -> x + y).apply("baz"));
    }


}

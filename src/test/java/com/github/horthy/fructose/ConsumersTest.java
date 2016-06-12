package com.github.horthy.fructose;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * created by dex on 6/11/16.
 */
public class ConsumersTest {

    @Test
    public void testConsuming() throws Exception {
        final AtomicBoolean target = new AtomicBoolean(false);
        Consumers.binding(target::set, true).run();

        assertTrue(target.get());
    }

    @Test
    public void testConsumerOf() throws Exception {
        final AtomicBoolean target = new AtomicBoolean(false);
        Consumers.binding(true).apply(target::set).run();
        assertTrue(target.get());
    }

    @Test
    public void testMapping() throws Exception {
        final AtomicInteger target = new AtomicInteger(0);

        Consumers.mapping(target::set, String::length)
                .accept("foobarbaz");

        assertEquals(9, target.get());

    }

    @Test
    public void testMappingFunction() throws Exception {
        final AtomicInteger target = new AtomicInteger(0);

        Consumers.mapping(String::length)
                .apply(target::set)
                .accept("foobarbaz");

        assertEquals(9, target.get());
    }

    @Test
    public void testConsumingConsumer() throws Exception {
        final AtomicInteger target = new AtomicInteger(0);

        Consumers.accepting(1).accept(target::set);

        assertEquals(1, target.get());

    }
}

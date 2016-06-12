package com.github.horthy.fructose;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * created by dex on 6/11/16.
 */
public class MapsTest {
    @Test
    public void testMapGetting() throws Exception {
        assertEquals("bar", Maps.getting("foo").apply(ImmutableMap.of("foo", "bar")));
        assertEquals(null, Maps.getting("foo").apply(ImmutableMap.of("spam", "eggs")));
    }

    @Test
    public void testCollectEntries() throws Exception {
        ImmutableMap<String, String> map = ImmutableMap.of(
                "foo", "bar",
                "spam", "eggs"
        );

        Map<String, String> collected =
                map.entrySet()
                        .stream()
                        .collect(Maps.collectEntries());

        assertEquals(map, collected);

    }
}

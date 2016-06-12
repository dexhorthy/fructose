package com.github.horthy.fructose;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * created by dex on 6/11/16.
 */
public final class Maps {

    private Maps() {}

    /**
     * K -> (Map[K,V] -> V)
     */
    public static <K, V> Function<Map<K, V>, V> getting(K key) {
        return new GettingKey<>(key);
    }

    public static <K,V> Collector<Map.Entry<K, V>, ?, Map<K,V>> collectEntries() {
        return Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
        );
    }

    private static class GettingKey<K, V> implements Function<Map<K, V>, V> {
        private final K key;

        public GettingKey(K key) {
            this.key = key;
        }

        @Override
        public V apply(Map<K, V> m) {
            return m.get(key);
        }
    }
}

package com.github.horthy.fructose;

import java.util.Map;
import java.util.function.Predicate;

/**
 * created by dex on 6/11/16.
 */
public final class Predicates {

    private Predicates(){}

    /**
     *  T -> ((T -> bool) -> bool)
     */
    public static <T> Predicate<Predicate<T>> testing(T predicand) {
        return new Testing<>(predicand);
    }

    /**
     *  (K -> bool) -> (Entry[K,?] -> bool)
     */
    public static <K> Predicate<Map.Entry<K,?>> keys(Predicate<K> p) {
        return new TestingKey<>(p);
    }

    /**
     *  (V -> bool) -> (Entry[?,V] -> bool)
     */
    public static <V> Predicate<Map.Entry<?,V>> values(Predicate<V> p) {
        return new TestingValue<>(p);
    }

    public static <T> Predicate<T> negate(Predicate<T> p) {
        return new NegatedPredicate<>(p);
    }

    private static class Testing<T> implements Predicate<Predicate<T>> {
        private final T predicand;

        public Testing(T predicand) {
            this.predicand = predicand;
        }

        @Override
        public boolean test(Predicate<T> p) {
            return p.test(predicand);
        }
    }

    private static class TestingKey<K> implements Predicate<Map.Entry<K, ?>> {
        private final Predicate<K> p;

        public TestingKey(Predicate<K> p) {
            this.p = p;
        }

        @Override
        public boolean test(Map.Entry<K, ?> e) {
            return p.test(e.getKey());
        }
    }

    private static class TestingValue<V> implements Predicate<Map.Entry<?, V>> {
        private final Predicate<V> p;

        public TestingValue(Predicate<V> p) {
            this.p = p;
        }

        @Override
        public boolean test(Map.Entry<?, V> e) {
            return p.test(e.getValue());
        }
    }

    private static class NegatedPredicate<T> implements Predicate<T> {
        private final Predicate<T> p;

        public NegatedPredicate(Predicate<T> p) {
            this.p = p;
        }

        @Override
        public boolean test(T t) {
            return !p.test(t);
        }
    }
}

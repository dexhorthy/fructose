package com.github.horthy.fructose;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by on 6/11/16.
 */
public final class Consumers {

    private Consumers() {}

    /**
     * T (T -> ) -> ( -> )
     */
    public static <T> Runnable binding(Consumer<T> consumer, T t) {
        return new ConsumingRunnable<>(consumer, t);
    }


    /**
     * T -> ((T -> ) -> ( -> ))
     */
    public static <T> Function<Consumer<T>, Runnable> binding(T t) {
        return new ToConsumerOf<>(t);
    }

    /**
     * T -> ((T -> ) -> )
     */
    public static <T> Consumer<Consumer<T>> accepting(T t) {
        return c -> c.accept(t);
    }

    /**
     * ( R -> ) (T -> R) -> (T -> )
     */
    public static <T,R> Consumer<T> mapping(Consumer<R> consumer, Function<T,R> mapper) {
        return new MappingConsumer<>(consumer, mapper);
    }

    /**
     * (T -> R) -> ((R -> ) -> (T -> ))
     */
    public static <T,R> Function<Consumer<R>, Consumer<T>> mapping(Function<T,R> mapper) {
        return new ConsumerMapper<>(mapper);
    }

    private static class MappingConsumer<T,R> implements Consumer<T> {
        private final Consumer<R> consumer;
        private final Function<T, R> mapper;

        public MappingConsumer(Consumer<R> consumer, Function<T, R> mapper) {
            this.consumer = consumer;
            this.mapper = mapper;
        }

        @Override
        public void accept(T t) {
            consumer.accept(mapper.apply(t));
        }
    }

    private static class ConsumingRunnable<T> implements Runnable {
        private final Consumer<T> consumer;
        private final T t;

        public ConsumingRunnable(Consumer<T> consumer, T t) {
            this.consumer = consumer;
            this.t = t;
        }

        @Override
        public void run() {
            consumer.accept(t);
        }
    }

    private static class ConsumerMapper<R, T> implements Function<Consumer<R>, Consumer<T>> {
        private final Function<T, R> mapper;

        public ConsumerMapper(Function<T, R> mapper) {
            this.mapper = mapper;
        }

        @Override
        public Consumer<T> apply(Consumer<R> c) {
            return new MappingConsumer<>(c, mapper);
        }
    }

    private static class ToConsumerOf<T> implements Function<Consumer<T>, Runnable> {
        private final T t;

        public ToConsumerOf(T t) {
            this.t = t;
        }

        @Override
        public Runnable apply(Consumer<T> c) {
            return new ConsumingRunnable<>(c, t);
        }
    }
}

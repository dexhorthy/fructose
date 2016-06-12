package com.github.horthy.fructose;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * created by dex on 6/11/16.
 */
class DecoratedConsumer<T> implements Consumer<T> {

    private final Consumer<T> delegate;
    private final ConsumerDecoration<T> decoration;

    private DecoratedConsumer(Consumer<T> delegate, ConsumerDecoration<T> decoration) {
        this.delegate = delegate;
        this.decoration = decoration;
    }

    @Override
    public void accept(T t) {
        decoration.before(t);
        try {
            delegate.accept(t);
        } catch (Throwable th) {
            decoration.failure(t, th);
            throw th;
        }
        decoration.success(t);
    }


    public static class Builder<T> {
        private Consumer<T> before = t -> {};
        private Consumer<T> delegate;
        private Consumer<T> success = (t) -> {};
        private BiConsumer<T, Throwable> failure = (t, th) -> {};

        Builder(Consumer<T> function) {
            this.delegate = function;
        }

        public Builder<T> before(Consumer<T> before) {
            this.before = before;
            return this;
        }

        public Builder<T> success(Consumer<T> success) {
            this.success = success;
            return this;
        }

        public Builder<T> failure(BiConsumer<T, Throwable> failure) {
            this.failure = failure;
            return this;
        }

        public Consumer<T> build() {
            return new DecoratedConsumer<>(delegate, new ConsumerDecoration<>(before, success, failure));
        }
    }

    private static class ConsumerDecoration<T> {
        private final Consumer<T> before;
        private final Consumer<T> success;
        private final BiConsumer<T, Throwable> failure;

        ConsumerDecoration(Consumer<T> before, Consumer<T> success, BiConsumer<T, Throwable> failure) {
            this.before = before;
            this.success = success;
            this.failure = failure;
        }


        public void before(T t) {
            before.accept(t);
        }

        public void success(T t) {
            success.accept(t);
        }

        public void failure(T t, Throwable r) {
            failure.accept(t, r);
        }

    }
}

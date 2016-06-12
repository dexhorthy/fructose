package com.github.horthy.fructose;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * created by dex on 6/11/16.
 */
class DecoratedFunction<T, R> implements Function<T, R> {
    private final Function<T, R> delegate;
    private final FunctionDecoration<T, R> decoration;

    private DecoratedFunction(Function<T, R> delegate, FunctionDecoration<T, R> decoration) {
        this.delegate = delegate;
        this.decoration = decoration;
    }

    @Override
    public R apply(T t) {
        decoration.before(t);
        R r;
        try {
            r = delegate.apply(t);
        } catch (Throwable th) {
            decoration.failure(t, th);
            throw th;
        }
        decoration.success(t, r);
        return r;
    }

    public static class Builder<T, R> {
        private Consumer<T> before = t -> {};
        private Function<T, R> delegate;
        private BiConsumer<T, R> success = (t, r) -> {};
        private BiConsumer<T, Throwable> failure = (t, th) -> {};

        Builder(Function<T, R> function) {
            this.delegate = function;
        }

        public Builder<T, R> before(Consumer<T> before) {
            this.before = before;
            return this;
        }

        public Builder<T, R> success(BiConsumer<T, R> success) {
            this.success = success;
            return this;
        }

        public Builder<T, R> failure(BiConsumer<T, Throwable> failure) {
            this.failure = failure;
            return this;
        }

        public Function<T, R> build() {
            return new DecoratedFunction<>(delegate, new FunctionDecoration<>(before, success, failure));
        }
    }

    private static class FunctionDecoration<T, R> {
        private final Consumer<T> before;
        private final BiConsumer<T, R> success;
        private final BiConsumer<T, Throwable> failure;

        FunctionDecoration(Consumer<T> before, BiConsumer<T, R> success, BiConsumer<T, Throwable> failure) {
            this.before = before;
            this.success = success;
            this.failure = failure;
        }

        public void before(T t) {
            before.accept(t);
        }

        public void success(T t, R r) {
            success.accept(t, r);
        }

        public void failure(T t, Throwable r) {
            failure.accept(t, r);
        }

    }
}

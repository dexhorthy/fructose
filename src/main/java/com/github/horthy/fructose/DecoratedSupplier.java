package com.github.horthy.fructose;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * created by dex on 6/11/16.
 */
class DecoratedSupplier<T> implements Supplier<T> {

    private final Supplier<T> delegate;
    private final SupplierDecoration<T> decoration;

    private DecoratedSupplier(Supplier<T> delegate, SupplierDecoration<T> decoration) {
        this.delegate = delegate;
        this.decoration = decoration;
    }

    @Override
    public T get() {
        decoration.before();
        T t;
        try {
            t = delegate.get();
        } catch (Throwable th) {
            decoration.failure(th);
            throw th;
        }
        decoration.success(t);
        return t;
    }

    public static class Builder<T> {
        private Runnable before = () -> {};
        private Supplier<T> delegate;
        private Consumer<T> success = (t) -> {};
        private Consumer<Throwable> failure = (th) -> {};

        Builder(Supplier<T> supplier) {
            this.delegate = supplier;
        }

        public Builder<T> before(Runnable before) {
            this.before = before;
            return this;
        }

        public Builder<T> success(Consumer<T> success) {
            this.success = success;
            return this;
        }

        public Builder<T> failure(Consumer<Throwable> failure) {
            this.failure = failure;
            return this;
        }

        public Supplier<T> build() {
            return new DecoratedSupplier<>(delegate, new SupplierDecoration<>(before, success, failure));
        }
    }

    private static class SupplierDecoration<T> {
        private final Runnable before;
        private final Consumer<T> success;
        private final Consumer<Throwable> failure;

        SupplierDecoration(Runnable before, Consumer<T> success, Consumer<Throwable> failure) {
            this.before = before;
            this.success = success;
            this.failure = failure;
        }


        public void before() {
            before.run();
        }

        public void success(T t) {
            success.accept(t);
        }

        public void failure(Throwable th) {
            failure.accept(th);
        }

    }
}

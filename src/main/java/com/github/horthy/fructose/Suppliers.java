package com.github.horthy.fructose;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * created by dex on 6/11/16.
 */
public final class Suppliers {

    private Suppliers() {}

    /**
     * T -> ( -> T)
     */
    public static <T> Supplier<T> supplying(T t) {
        return new Supplying<>(t);
    }


    /**
     * (T -> R) -> (( -> T) -> ( -> R))
     */
    public static <T,R> Function<Supplier<T>, Supplier<R>> mapping(Function<T,R> mapper) {
        return new SupplierMapper<>(mapper);
    }

    /**
     * ( -> T) (T -> R) -> ( -> R)
     */
    public static <T,R> Supplier<R> mapping(Supplier<T> supplier, Function<T,R> mapper) {
        return new MappedSupplier<>(mapper, supplier);
    }

    private static class Supplying<T> implements Supplier<T> {
        private final T t;

        public Supplying(T t) {
            this.t = t;
        }

        @Override
        public T get() {
            return t;
        }
    }

    private static class MappedSupplier<T, R> implements Supplier<R> {
        private final Function<T, R> mapper;
        private final Supplier<T> supplier;

        public MappedSupplier(Function<T, R> mapper, Supplier<T> supplier) {
            this.mapper = mapper;
            this.supplier = supplier;
        }

        @Override
        public R get() {
            return mapper.apply(supplier.get());
        }
    }

    private static class SupplierMapper<T, R> implements Function<Supplier<T>, Supplier<R>> {
        private final Function<T, R> mapper;

        public SupplierMapper(Function<T, R> mapper) {
            this.mapper = mapper;
        }

        @Override
        public Supplier<R> apply(Supplier<T> s) {
            return new MappedSupplier<>(mapper, s);
        }
    }
}

package com.github.horthy.fructose;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * created by dex on 6/11/16.
 */
public final class Decorators {

    private Decorators() {}

    /**
     * (T -> R) (T -> ) (T R -> ) (T ex -> ) -> (T -> R)
     */
    public static <T,R> DecoratedFunction.Builder<T,R> decorating(Function<T,R> f) {
        return new DecoratedFunction.Builder<>(f);
    }

    /**
     * (T -> ) (T -> ) (T -> ) (T ex -> ) -> (T -> )
     */
    public static <T> DecoratedConsumer.Builder<T> decorating(Consumer<T> f) {
        return new DecoratedConsumer.Builder<>(f);
    }

    /**
     * ( -> T) ( -> ) (T -> ) (ex -> ) -> ( -> T)
     */
    public static <T> DecoratedSupplier.Builder<T> decorating(Supplier<T> f) {
        return new DecoratedSupplier.Builder<>(f);
    }

}

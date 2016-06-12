package com.github.horthy.fructose;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * created by dex on 6/11/16.
 */
public final class Functions {

    private Functions() {}

    /**
     * T -> ((T -> R) -> R)
     */
    public static <T, R> Function<Function<T, R>, R> applying(T t) {
        return new AppliedFunction<>(t);
    }

    /**
     * T R -> ((T R -> U) -> U)
     */
    public static <T,R,U> Function<BiFunction<T, R, U>, U>  applying(T t, R r) {
        return new AppliedBiFunction<>(t, r);
    }

    /**
     * same as {@link #bindLeft}
     */
    public static <T,R,U> Function<BiFunction<T, R, U>, Function<R, U>>  binding(T t) {
        return bindLeft(t);
    }

    /**
     *  T -> ((T R -> U) -> (R -> U))
     */
    public static <T,R,U> Function<BiFunction<T, R, U>, Function<R, U>> bindLeft(T t) {
        return new LeftBinder<>(t);
    }

    /**
     *  R -> ((T R -> U) -> (T -> U))
     */
    public static <T,R,U> Function<BiFunction<T, R, U>, Function<T, U>> bindRight(R r) {
        return new RightBinder<>(r);
    }

    /**
     * (T R -> U) T -> (R -> U)
     */
    public static <T,R,U> Function<R, U> bindLeft(BiFunction<T,R,U> f, T t) {
        return new LeftBindingFunction<>(f, t);
    }

    /**
     * (T R -> U) R -> (T -> U)
     */
    public static <T,R,U> Function<T, U> bindRight(BiFunction<T,R,U> f, R r) {
        return new RightBindingFunction<>(f, r);
    }

    private static class LeftBindingFunction<T, R, U> implements Function<R, U> {
        private final BiFunction<T, R, U> f;
        private final T t;

        public LeftBindingFunction(BiFunction<T, R, U> f, T t) {
            this.f = f;
            this.t = t;
        }

        @Override
        public U apply(R r) {
            return f.apply(t, r);
        }
    }

    private static class RightBindingFunction<T, R, U> implements Function<T, U> {
        private final BiFunction<T, R, U> f;
        private final R r;

        public RightBindingFunction(BiFunction<T, R, U> f, R r) {
            this.f = f;
            this.r = r;
        }

        @Override
        public U apply(T t) {
            return f.apply(t, r);
        }
    }

    private static class AppliedFunction<T, R> implements Function<Function<T, R>, R> {
        private final T applicand;

        public AppliedFunction(T applicand) {
            this.applicand = applicand;
        }

        @Override
        public R apply(Function<T, R> f) {
            return f.apply(applicand);
        }
    }

    private static class AppliedBiFunction<T, R, U> implements Function<BiFunction<T, R, U>, U> {
        private final T t;
        private final R r;

        public AppliedBiFunction(T t, R r) {
            this.t = t;
            this.r = r;
        }

        @Override
        public U apply(BiFunction<T, R, U> b) {
            return b.apply(t, r);
        }
    }

    private static class LeftBinder<T, R, U> implements Function<BiFunction<T, R, U>, Function<R, U>> {
        private final T t;

        public LeftBinder(T t) {
            this.t = t;
        }

        @Override
        public Function<R, U> apply(BiFunction<T, R, U> f) {
            return new LeftBindingFunction<>(f, t);
        }
    }

    private static class RightBinder<T, R, U> implements Function<BiFunction<T, R, U>, Function<T, U>> {
        private final R r;

        public RightBinder(R r) {
            this.r = r;
        }

        @Override
        public Function<T, U> apply(BiFunction<T, R, U> f) {
            return new RightBindingFunction<>(f, r);
        }
    }

}

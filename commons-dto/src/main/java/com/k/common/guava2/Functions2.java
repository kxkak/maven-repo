package com.k.common.guava2;

import com.google.common.base.Function;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 提供一些常用的Function
 */
public final class Functions2 {
    private Functions2(){}

    /**
     * 函数(A, B) -> A
     */
    @SuppressWarnings("unchecked")
    public static <U> Function<Pair<U, ?>, U> newFirstOfPairFunction() {
        return FirstOfPairFunction.INSTANCE;
    }

    /**
     * 函数(A, B) -> B
     */
    @SuppressWarnings("unchecked")
    public static <U> Function<Pair<?, U>, U> newSecondOfPairFunction() {
        return SecondOfPairFunction.INSTANCE;
    }

    /**
     * 把一个A -> B的函数封装为
     * A -> (A, B)的函数
     */
    public static <F, T> Function<F, Pair<F, T>> newInAndOutFunction(Function<? super F, ? extends T> function) {
        return new InAndOutFunction<F, T>(function);
    }

    /**
     * 函数Identifiable -> Id
     */
    @SuppressWarnings("unchecked")
    public static <F extends Identifiable<T>, T> Function<F, T> newIdFunction() {
        return IdFunction.INSTANCE;
    }

    private static final class InAndOutFunction<F, T> implements Function<F, Pair<F, T>> {

        private final Function<? super F, ? extends T> function;

        private InAndOutFunction(Function<? super F, ? extends T> function) {
            this.function = function;
        }

        @Override
        public Pair<F, T> apply(F input) {
            // Explicit type parameter just because eclipse is not cool
            // noinspection RedundantTypeArguments
            return Pair.<F, T>of(input, function.apply(input));
        }

    }

    private static final class FirstOfPairFunction<U> extends SafeFunction<Pair<U, ?>, U> {

        public static final FirstOfPairFunction INSTANCE = new FirstOfPairFunction();

        @Override
        public U safeApply(Pair<U, ?> input) {
            return input.getFirst();
        }
    }

    private static final class SecondOfPairFunction<U> extends SafeFunction<Pair<?, U>, U> {

        public static final SecondOfPairFunction INSTANCE = new SecondOfPairFunction();

        @Override
        public U safeApply(Pair<?, U> input) {
            return input != null ? input.getSecond() : null;
        }
    }

    private static final class IdFunction<F extends Identifiable<T>, T> extends SafeFunction<F, T> {

        public static final IdFunction INSTANCE = new IdFunction();

        @Override
        public T safeApply(F input) {
            return input.getIdentify();
        }
    }

	public static final class StringToIntegerFunction extends SafeFunction<String, Integer> {

		public static final StringToIntegerFunction INSTANCE = new StringToIntegerFunction();

		@Override
		protected Integer safeApply(String input) {
			return NumberUtils.toInt(input);
		}
	}

    public static final class KeyValueIsSameFunction extends SafeFunction<Object, Object> {
        public static final KeyValueIsSameFunction INSTANCE = new KeyValueIsSameFunction();

        @Override
        protected Object safeApply(Object input) {
            return input;
        }
    }
}

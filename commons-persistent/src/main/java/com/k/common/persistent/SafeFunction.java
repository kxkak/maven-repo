package com.k.common.persistent;

import java.util.Objects;
import java.util.function.Function;

/**
 * 在输入null值时直接返回null的Function
 * java8函数式接口,简化Transformer的写法
 *
 * Usage:
 * 1. Lists2.transform(list, (SafeFunction< String, String >) String::trim);
 * 2. Lists2.transform(list, (SafeFunction< String, String >) (v) -> v + ":WTF");
 *
 * @Author: 黄志泉
 * @Datetime: 2016-04-24 11:12
 * @since 0.3.1
 */
@FunctionalInterface
public interface SafeFunction<F, O> extends Function<F ,O>, com.google.common.base.Function<F, O> {

    @Override
    default O apply(F input) {
        return input != null ? safeApply(input) : null;
    }

    O safeApply(F input);

    default <V> Function<V, O> compose(Function<? super V, ? extends F> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }

    default <V> SafeFunction<F, V> andThen(SafeFunction<? super O, ? extends V> after) {
        Objects.requireNonNull(after);
        return (F f) -> after.apply(apply(f));
    }
}

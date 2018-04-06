package com.k.common.guava2;

import com.google.common.base.Function;

/**
 * 在输入null值时直接返回null的Function
 */
public abstract class SafeFunction<F, O> implements Function<F, O> {

    public final O apply(F input) {
        return input != null ? safeApply(input) : null;
    }

    /**
     * @param input 不会为null
     * @return
     */
    protected abstract O safeApply(F input);

}

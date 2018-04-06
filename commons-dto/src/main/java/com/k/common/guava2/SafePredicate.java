package com.k.common.guava2;

import com.google.common.base.Predicate;

/**
 *
 */
public abstract class SafePredicate<F> implements Predicate<F> {

    @Override
    public final boolean apply(F input) {
        return input != null && safeApply(input);
    }

    /**
     * @param input 不会为null
     * @return
     */
    protected abstract boolean safeApply(F input);
}

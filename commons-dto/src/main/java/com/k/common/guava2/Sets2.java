package com.k.common.guava2;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 提供一些常用的Set相关的工具函数
 */
public final class Sets2 {

    private Sets2() {}

    public static <F, T> Set<T> transform(Collection<F> fromList, Function<? super F, T> function) {
        Collection<T> output = Collections2.transform(fromList, function);
        return Sets.newHashSet(output);
    }

    /**
     * 返回一个不可变的集合，这个集合的contains检验永远返回真除非入参为null
     */
    public static <T> Set<T> newAlwaysContainsSet() {
        //noinspection unchecked
        return AlwaysContainsSet.INSTANCE;
    }

    private static class AlwaysContainsSet extends HashSet implements Serializable {

        @SuppressWarnings("unchecked")
        public static final Set INSTANCE = Collections.unmodifiableSet(new AlwaysContainsSet());

        @Override
        public boolean contains(Object o) {
            return o != null;
        }

    }
}

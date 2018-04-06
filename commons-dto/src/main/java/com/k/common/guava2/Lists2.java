package com.k.common.guava2;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 提供一些常用的List相关的工具函数
 */
public final class Lists2 {
    private Lists2() {}

    /**
     * 把{Key -> Value}的Map根据Key[]的顺序转化为Value[]，不存在的元素填充null
     */
    public static <K, V>
    List<V> newListFromKeyListAndValueMap(Collection<? extends K> ids, Map<? extends K, ? extends V> map) {
        List<V> values = Lists.newArrayListWithCapacity(ids.size());
        for (K id : ids) {
            values.add(map.get(id));
        }
        return values;
    }

    public static <I, V extends Identifiable<I>>
    List<I> newIdList(Collection<? extends V> values) {
        return transform(values, Functions2.<V, I>newIdFunction());
    }

    /**
     * 同Lists.transform但把lazy transform的行为关闭
     */
    public static <F, T> List<T> transform(Collection<F> fromList, Function<? super F, ? extends T> function) {
        Collection<? extends T> output = Collections2.transform(fromList, function);
        return Lists.newArrayList(output);
    }

    /**
     * 把A[]转化为(A,B)[]
     */
    public static <F, T> List<Pair<F, T>> transformToInOutPair(List<? extends F> fromList, Function<? super F, ? extends T> function) {
        Function<F, Pair<F, T>> fn = Functions2.newInAndOutFunction(function);
        List<Pair<F, T>> output = Lists.transform(fromList, fn);
        return Lists.newArrayList(output);
    }

	/**
	 * 同{@link Collections2#filter} 但把lazy行为关闭
	 */
	public static <E> List<E> filter(List<E> unfiltered, Predicate<? super E> predicate) {
		Collection<E> output = Collections2.filter(unfiltered, predicate);
		return Lists.newArrayList(output);
	}

	/**
	 * 滤除List中的null元素
	 */
	public static <E> List<E> removeNull(List<E> unfiltered) {
		return filter(unfiltered, Predicates.notNull());
	}

	public static <T> List<T> page(List<T> list, int offset, int count) {
		int fromIndex = Math.min(offset, list.size());
		int toIndex = Math.min(offset + count, list.size());
		return Lists.newArrayList(list.subList(fromIndex, toIndex));
	}

    /**
     * 把A[], B[]合并为(A,B)[]
     * @throws IllegalArgumentException 如果A[], B[]长度不一致
     */
    public static <U, V> List<Pair<U, V>> zip(List<? extends U> list1, List<? extends V> list2){
        if (list1 == null || list2 == null || list1.size() != list2.size()) {
            throw new IllegalArgumentException("Two list must have the same size");
        }
        List<Pair<U, V>> results = Lists.newArrayListWithCapacity(list1.size());
        for (int i = 0; i < list1.size(); ++i) {
            // Explicit type parameter just because eclipse is not cool
            // noinspection RedundantTypeArguments
            Pair<U, V> pair = Pair.<U, V>of(list1.get(i), list2.get(i));
            results.add(pair);
        }
        return results;
    }

    /**
     * 把具有同样Id的A[], B[]合并为(A,B)[]
     * @return 和A[]的元素一一对应的(A,B)[]
     */
    public static <U extends Identifiable<I>, V extends Identifiable<I>, I>
    List<Pair<U, V>> zipWithSameIdList(List<? extends U> list1, List<? extends V> list2) {
        return zipWithMap(list1, Maps2.<V, I>newIdToValueMap(list2));
    }

    /**
     * 把能映射到同样的Key的A[], B[]合并为(A,B)[]
     * @return 和A[]的元素一一对应的(A,B)[]
     */
    public static <U extends Identifiable<K>, V, K>
    List<Pair<U, V>> zipWithSameKeyList(List<U> list1, List<V> list2, Function<V, K> keyFn2) {
        return zipWithMap(list1, Maps2.<K, V>newMapWithValue(list2, keyFn2));
    }

    /**
     * 把具有Id的A[]和(Id -> B)的Map合并为(A,B)[]
     * @return 和A[]元素一一对应的(A,B)[]
     */
    public static <U extends Identifiable<I>, V, I>
    List<Pair<U, V>> zipWithMap(List<? extends U> list, final Map<? extends I, ? extends V> map) {
        return Lists2.<U, V>transformToInOutPair(list, new SafeFunction<U, V>() {

            @Override
            public V safeApply(U input) {
                return map.get(input.getIdentify());
            }
        });
    }
}

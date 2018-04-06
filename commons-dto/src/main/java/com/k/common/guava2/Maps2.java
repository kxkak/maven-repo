package com.k.common.guava2;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 提供一些常用的Map相关的工具函数
 */
public final class Maps2 {
    private Maps2() {
    }

    /**
     * 把Identifiable[] 转化为 {Id -> Identifiable}的Map
     */
    public static <F extends Identifiable<I>, I>
    Map<I, F> newIdToValueMap(Collection<? extends F> coll) {
        return newMapWithValue(coll, Functions2.<F, I>newIdFunction());
    }

    /**
     * 根据函数 A->B 把 A[] 转化为 {B -> A}的Map
     */
    public static <K, V> Map<K, V> newMapWithValue(Collection<? extends V> coll,
                                                   Function<V, K> valueToKeyFunction) {
        if (CollectionUtils.isEmpty(coll)) {
            return Collections.emptyMap();
        }
        Map<K, V> results = Maps.newHashMapWithExpectedSize(coll.size());
        for (V value : coll) {
            K key = valueToKeyFunction.apply(value);
            results.put(key, value);
        }
        return results;
    }

    /**
     * 根据函数 A->B 把 A[] 转化为 {A -> B}的Map
     */
    public static <K, V> Map<K, V> newMapWithKey(Collection<? extends K> coll,
                                                 Function<K, V> keyToValueFunction) {
        if (CollectionUtils.isEmpty(coll)) {
            return Collections.emptyMap();
        }
        Map<K, V> results = Maps.newHashMapWithExpectedSize(coll.size());
        for (K key : coll) {
            V value = keyToValueFunction.apply(key);
            results.put(key, value);
        }
        return results;
    }

    public static Map mGet(Collection keys, Map map) {
        Map mGetMap = Maps.newHashMap();
        for (Object key : keys) {
            if (map.containsKey(key)) {
                mGetMap.put(key, map.get(key));
            }
        }
        return mGetMap;
    }

    public static Map<String, String> getStringMap(Map<String, Object> objectMap){
        if (MapUtils.isEmpty(objectMap)) {
            return Collections.emptyMap();
        }
        Map<String, String> stringMap = Maps.newHashMap();
        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            stringMap.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return stringMap;
    }
    public static Map<String, Object> getObjectMap(Map<String, String> stringMap){
        if (MapUtils.isEmpty(stringMap)) {
            return Collections.emptyMap();
        }
        Map<String, Object> objectMap = Maps.newHashMap();
        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            objectMap.put(entry.getKey(), entry.getValue());
        }
        return objectMap;
    }
    public static List<Pair<String, String>> transformMap2ListPair(Map<String, String> paramMap) {
        List<Pair<String, String>> params = Lists.newArrayList();
        for (String key : paramMap.keySet()) {
            params.add(Pair.of(key, paramMap.get(key)));
        }
        return params;
    }

}

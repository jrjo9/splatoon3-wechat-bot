package com.mayday9.splatoonbot.common.util.core;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class MapHelper {

    public static <K, V, T> Map<K, V> toMap(List<T> list, Function<T, K> keyFunction, Function<T, V> valueFunction) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Map<K, V> map = Maps.newHashMap();
        for (T t : list) {
            K key = keyFunction.apply(t);
            V value = valueFunction.apply(t);
            map.put(key, value);
        }
        return map;
    }

    public static <K, V> Map<K, V> toMap(List<V> list, MapKey<V, K> keyFunction) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Map<K, V> map = Maps.newHashMap();
        for (V t : list) {
            if (t == null) {
                continue;
            }
            K key = keyFunction.key(t);
            if (key == null) {
                continue;
            }
            map.put(key, t);
        }
        return map;
    }

    public static <K, V> Map<K, V> toMapSkipDuplicateKey(List<V> list, MapKey<V, K> keyFunction) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Map<K, V> map = Maps.newHashMap();
        for (V t : list) {
            if (t == null) {
                continue;
            }
            K key = keyFunction.key(t);
            if (key == null || map.containsKey(key)) {
                continue;
            }
            map.put(key, t);
        }
        return map;
    }

    public static <K, V> Map<K, List<V>> toListMap(List<V> list, MapKey<V, K> keyFunction) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Map<K, List<V>> map = Maps.newHashMap();
        for (V t : list) {
            if (t == null) {
                continue;
            }
            K key = keyFunction.key(t);
            if (key == null) {
                continue;
            }
            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<V>());
            }
            map.get(key).add(t);
        }
        return map;
    }

    public interface MapKey<S, K> {
        K key(S source);
    }

    public static <K, V, F> Map<K, V> toMap(List<K> keys, List<V> values) {
        if (CollectionUtils.isEmpty(keys) || CollectionUtils.isEmpty(values) || keys.size() != values.size()) {
            return Collections.emptyMap();
        }
        Map<K, V> map = Maps.newHashMap();
        for (int i = 0; i < keys.size(); i++) {
            K key = keys.get(i);
            V value = values.get(i);
            if (null == key || null == value) {
                continue;
            }
            map.put(key, value);
        }
        return map;
    }

    public static <K, V, F> Map<K, V> toMap(List<K> keys, List<F> values, Function<F, V> function) {
        if (CollectionUtils.isEmpty(keys) || CollectionUtils.isEmpty(values) || keys.size() != values.size()) {
            return Collections.emptyMap();
        }
        Map<K, V> map = Maps.newHashMap();
        for (int i = 0; i < keys.size(); i++) {
            K key = keys.get(i);
            V value = function.apply(values.get(i));
            if (null == key || null == value) {
                continue;
            }
            map.put(key, value);
        }
        return map;
    }
}

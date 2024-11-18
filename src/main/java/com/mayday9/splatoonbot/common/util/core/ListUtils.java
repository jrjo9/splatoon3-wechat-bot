package com.mayday9.splatoonbot.common.util.core;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ListUtils {
    public static <T, K> Map<K, T> toMap(List<T> list, FieldSelector<T, K> selector) {
        if (CollectionUtils.isEmpty(list)) return Collections.emptyMap();
        Map<K, T> map = new HashMap<>(list.size());
        for (T t : list) {
            K key = selector.select(t);
            if (key != null) map.put(key, t);
        }
        return map;
    }

    public static <T, K> Map<K, List<T>> groupBy(List<T> list, FieldSelector<T, K> selector) {
        if (CollectionUtils.isEmpty(list)) return Collections.emptyMap();
        Map<K, List<T>> map = new HashMap<>();
        for (T t : list) {
            K key = selector.select(t);
            if (key == null) continue;
            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<T>());
            }
            map.get(key).add(t);
        }
        return map;
    }

    public static <T, K> List<K> select(List<T> list, FieldSelector<T, K> selector) {
        if (CollectionUtils.isEmpty(list)) return Collections.emptyList();
        List<K> filedList = new ArrayList<>(list.size());
        for (T t : list) {
            K key = selector.select(t);
            if (key != null) filedList.add(key);
        }
        return filedList;
    }

    public static <T, K> List<K> distinctSelect(List<T> list, FieldSelector<T, K> selector) {
        if (CollectionUtils.isEmpty(list)) return Collections.emptyList();
        Set<K> filedSet = new HashSet<>();
        for (T t : list) {
            K key = selector.select(t);
            if (key != null) filedSet.add(key);
        }
        return new ArrayList<>(filedSet);
    }

    @SafeVarargs
    public static <T> List<T> unionWithoutDuplicate(List<T>... values) {
        if (null == values || values.length <= 0) return Collections.emptyList();
        Set<T> unionFiledSet = new HashSet<>();
        for (List<T> value : values) {
            if (!CollectionUtils.isEmpty(value)) {
                unionFiledSet.addAll(value);
            }
        }
        return new ArrayList<>(unionFiledSet);
    }

    public static <T, K> List<T> skipDuplicateKey(List<T> list, FieldSelector<T, K> selector) {
        if (CollectionUtils.isEmpty(list)) return Collections.emptyList();
        List<T> filedList = new ArrayList<>(list.size());
        Map<K, T> map = toMap(list, selector);
        for (K key : map.keySet()) {
            filedList.add(map.get(key));
        }
        return filedList;
    }
}

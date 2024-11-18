package com.mayday9.splatoonbot.common.util.core.processor;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
public final class DataExtractProcessor {

    public static <K, V> Map<K, V> toMap(List<V> values, String fieldName) {
        Map<K, V> map = new HashMap<>();
        for (V value : values) {
            try {
                Field field = ReflectionUtils.findField(value.getClass(), fieldName);
                field.setAccessible(true);
                K key = (K) field.get(value);
                if (key != null) {
                    map.put(key, value);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }

    public static <K, V> Map<K, List<V>> toListMap(List<V> values, String fieldName) {
        Map<K, List<V>> map = new HashMap<>();
        for (V value : values) {
            try {
                Field field = ReflectionUtils.findField(value.getClass(), fieldName);
                field.setAccessible(true);
                K key = (K) field.get(value);
                if (key != null) {
                    if (!map.containsKey(key)) {
                        map.put(key, new ArrayList<V>());
                    }
                    map.get(key).add(value);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }

    public static <K, V> Set<K> toSet(List<V> values, String fieldName) {
        Set<K> keys = new HashSet<>();
        for (V value : values) {
            try {
                Field field = ReflectionUtils.findField(value.getClass(), fieldName);
                field.setAccessible(true);
                K fieldValue = (K) field.get(value);
                if (fieldValue != null) {
                    keys.add(fieldValue);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return keys;
    }

    public static <K, V> List<K> toList(List<V> values, String fieldName) {
        Set<K> keys = new HashSet<>();
        for (V value : values) {
            try {
                Field field = ReflectionUtils.findField(value.getClass(), fieldName);
                field.setAccessible(true);
                K fieldValue = (K) field.get(value);
                if (fieldValue != null) {
                    keys.add(fieldValue);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return new ArrayList<>(keys);
    }
}

package com.mayday9.splatoonbot.common.util.core;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

public final class SplitUtils {

    public static final String COMMA = ",";
    public static final String POINT = ".";
    public static final String RAIL = "-";

    public static List<String> splitToStringList(String value, String pattern) {
        return splitByWholePreserveToken(value, pattern, false);
    }

    public static List<String> splitByWholePreserveToken(String value, String pattern, boolean preserveToken) {
        return splitToList(value, pattern, preserveToken);
    }

    public static List<Integer> splitToIntegerList(String value, String pattern) {
        return splitByWholePreserveToken(value, pattern, new ToIntegerFunction(), false);
    }

    public static <T> List<T> split(String value, String pattern, Function<String, T> function) {
        return splitByWholePreserveToken(value, pattern, function, false);
    }

    public static <T> List<T> splitByWholePreserveToken(String value, String pattern, Function<String, T> function, boolean preserveAllToken) {
        return Lists.transform(splitToList(value, pattern, preserveAllToken), function);
    }

    public static String integrateStringListToString(List<String> keywords, String tag) {
        if (CollectionUtils.isEmpty(keywords)) return "";
        StringBuilder builder = new StringBuilder(200);
        for (String keyword : keywords) {
            if (builder.length() > 0) {
                builder.append(tag);
            }
            builder.append(keyword);
        }
        return builder.toString();
    }

    public static <T, K> String integrateStringListToString(List<String> keywords, String tag, Function<String, K> function) {
        if (CollectionUtils.isEmpty(keywords)) return "";
        StringBuilder builder = new StringBuilder(200);
        for (String t : keywords) {
            if (builder.length() > 0) {
                builder.append(tag);
            }
            K key = function.apply(t);
            if (null != key) builder.append(key);
        }
        return builder.toString();
    }

    public static String integrateIntegerListToString(List<Integer> keywords, String tag) {
        if (CollectionUtils.isEmpty(keywords)) return "";
        StringBuilder builder = new StringBuilder(200);
        for (Integer keyword : keywords) {
            if (builder.length() > 0) {
                builder.append(tag);
            }
            builder.append(keyword);
        }
        return builder.toString();
    }

    public static <T, K> String integrateIntegerListToString(List<Integer> keywords, String tag, Function<Integer, K> function) {
        if (CollectionUtils.isEmpty(keywords)) return "";
        StringBuilder builder = new StringBuilder(200);
        for (Integer t : keywords) {
            if (builder.length() > 0) {
                builder.append(tag);
            }
            K key = function.apply(t);
            if (null != key) builder.append(key);
        }
        return builder.toString();
    }

    static class ToIntegerFunction implements Function<String, Integer> {
        @Override
        public Integer apply(String input) {
            return Integer.valueOf(input);
        }
    }

    private static List<String> splitToList(String value, String pattern, boolean preserveToken) {
        if (preserveToken)
            return Lists.newArrayList(StringUtils.splitPreserveAllTokens(value, pattern));
        return Lists.newArrayList(StringUtils.split(value, pattern));
    }
}

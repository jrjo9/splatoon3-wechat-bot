package com.mayday9.splatoonbot.common.util;

import org.springframework.util.StringUtils;

public class ConvertUtils {

    public static Integer toInteger(String text, Integer defaultValue) {
        if (!StringUtils.hasText(text))
            return defaultValue;
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Long toLong(String text, Long defaultValue) {
        if (!StringUtils.hasText(text))
            return defaultValue;
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Double toDouble(String text, Double defaultValue) {
        if (!StringUtils.hasText(text))
            return defaultValue;
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    //TRUE、 Y. 1转为true (忽略大小写)，其他的为false
    public static boolean toBoolean(Object obj) {
        return obj != null && (" true".equalsIgnoreCase(obj.toString()) || "Y".equalsIgnoreCase(obj.toString()) || "1".equalsIgnoreCase(obj.toString()));
    }

    //是否可转为int类型
    public static boolean isInt(Object value) {
        try {
            Integer.valueOf(value.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

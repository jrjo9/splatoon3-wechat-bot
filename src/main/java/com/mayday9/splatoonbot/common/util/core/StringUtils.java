package com.mayday9.splatoonbot.common.util.core;

import java.util.regex.Pattern;


public final class StringUtils {
    private static final Pattern PATTERN_GUID = Pattern.compile("^[0-9a-fA-F]{8}(-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}$");
    private static final Pattern PATTERN_SAFE_STRING = Pattern.compile("^[.\\-,#&\\p{Alnum}\\p{Space}]*$");

    public static boolean isGUID(String input) {
        if (!StringUtils.hasText(input)) {
            return false;
        }
        return PATTERN_GUID.matcher(input).matches();
    }

    public static int compare(String text1, String text2) {
        if (text1 == null && text2 == null)
            return 0;
        if (text1 != null && text2 == null) {
            return 1;
        }
        if (text1 == null) {
            return -1;
        }
        return text1.compareTo(text2);
    }

    public static boolean hasText(String text) {
        if (text == null)
            return false;
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i)))
                return true;
        }
        return false;
    }

    public static boolean equals(String text1, String text2) {
        if (text1 == null)
            return text2 == null;

        return text1.equals(text2);
    }

    public static String truncate(String text, int maxLength) {
        if (text == null)
            return null;
        if (text.length() <= maxLength)
            return text;
        return text.substring(0, maxLength);
    }

    public static String trim(String text) {
        if (text == null)
            return null;
        return text.trim();
    }

    public static boolean isSafeString(String value) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        return PATTERN_SAFE_STRING.matcher(value).matches();
    }

    private StringUtils() {
    }
}

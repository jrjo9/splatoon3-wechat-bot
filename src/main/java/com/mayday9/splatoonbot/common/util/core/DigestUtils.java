package com.mayday9.splatoonbot.common.util.core;

/**
 * provide wrapper of common codec for convenience
 */
public final class DigestUtils {
    public static String md5(String text) {
        return md5(text.getBytes(CharacterEncodings.CHARSET_UTF_8));
    }

    public static String md5(byte[] bytes) {
        return org.apache.commons.codec.digest.DigestUtils.md5Hex(bytes);
    }

    public static String sha512(String text) {
        return org.apache.commons.codec.digest.DigestUtils.sha512Hex(text);
    }

    private DigestUtils() {
    }
}

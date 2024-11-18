package com.mayday9.splatoonbot.common.util.core.crypto;


import com.mayday9.splatoonbot.common.util.core.CharacterEncodings;
import com.mayday9.splatoonbot.common.util.core.ClasspathResource;
import com.mayday9.splatoonbot.common.util.core.EncodingUtils;

/**
 * Util to decrypt sensitive data, such as db user password
 * Using RSA+Base64, and all string based,
 */
public final class EncryptionUtils {
    public static String encrypt(String plainText, ClasspathResource publicKey) {
        return encrypt(plainText, publicKey.getBytes());
    }

    public static String encrypt(String plainText, byte[] publicKey) {
        RSA rsa = new RSA();
        rsa.setPublicKey(publicKey);
        byte[] encryptedBytes = rsa.encrypt(plainText.getBytes(CharacterEncodings.CHARSET_UTF_8));
        return EncodingUtils.base64(encryptedBytes);
    }

    public static String decrypt(String encryptedText, ClasspathResource privateKey) {
        RSA rsa = new RSA();
        rsa.setPrivateKey(privateKey.getBytes());
        byte[] encryptedBytes = EncodingUtils.decodeBase64(encryptedText);
        byte[] plainText = rsa.decrypt(encryptedBytes);
        return new String(plainText, CharacterEncodings.CHARSET_UTF_8);
    }

    private EncryptionUtils() {
    }
}

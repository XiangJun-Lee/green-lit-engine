package com.keji.green.lit.engine.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * 加密工具类
 * @author xiangjun_lee
 */
public class EncryptionUtils {
    private static final String ALGORITHM = "AES";

    private static final String KEY = "Kj9#mP2$vL5nX8@q";
    

    private static final Pattern BASE64_PATTERN = Pattern.compile("^[A-Za-z0-9+/]*={0,2}$");

    /**
     * 判断字符串是否为Base64编码
     *
     * @param str 需要判断的字符串
     * @return 是否为Base64编码
     */
    private static boolean isBase64(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return BASE64_PATTERN.matcher(str).matches();
    }

    /**
     * 加密字符串
     *
     * @param value 需要加密的字符串
     * @return 加密后的字符串
     */
    public static String encrypt(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        try {
            SecretKeySpec key = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }
    }

    /**
     * 解密字符串
     *
     * @param encrypted 加密后的字符串
     * @return 解密后的字符串
     */
    public static String decrypt(String encrypted) {
        if (encrypted == null || encrypted.isEmpty()) {
            return encrypted;
        }
        
        // 如果字符串不是Base64编码，说明是未加密的数据，直接返回
        if (!isBase64(encrypted)) {
            return encrypted;
        }
        
        try {
            SecretKeySpec key = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(decryptedBytes);
        } catch (Exception e) {
            // 如果解密失败，说明可能是其他Base64编码的数据，直接返回原数据
            return encrypted;
        }
    }
} 
package com.videoapp.libcommon.utils;

import android.util.Base64;

/**
 * Base64工具
 *
 * @author mos
 * @date 2017.02.13
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class Base64Util {
    /**
     * BASE64解密
     *
     * @param key Base64码
     * @return 解密后的值
     */
    public static byte[] decryptBase64(String key) {
        byte[] result = null;

        try {
            result = Base64.decode(key.getBytes(), Base64.NO_WRAP);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * BASE64解密
     *
     * @param key Base64码
     * @return 解密后的值
     */
    public static String decryptBase64AsStr(String key) {

        return new String(decryptBase64(key));
    }

    /**
     * BASE64加密
     *
     * @param key 待加密的字节码
     * @return 加密后的字符串
     */
    public static String encryptBase64(byte[] key) {
        byte[] data = Base64.encode(key, Base64.NO_WRAP);

        if (data != null) {

            return new String(data);
        }

        return null;
    }

    /**
     * BASE64加密
     *
     * @param key 待加密的字符串
     * @return 加密后的字符串
     */
    public static String encryptBase64(String key) {

        return encryptBase64(key.getBytes());
    }
}

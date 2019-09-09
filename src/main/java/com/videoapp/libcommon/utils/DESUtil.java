package com.videoapp.libcommon.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES加解密工具
 *
 * @author mos
 * @date 2017.02.03
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class DESUtil {
    /** 算法名称 */
    public static final String KEY_ALGORITHM = "DES";

    /** 算法名称/加密模式/填充方式 */
    public static final String CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";

    /**
     * 生成密钥
     *
     * @param desKey 密钥
     * @return 密钥对象
     */
    private static SecretKey generateKey(String desKey) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {
        DESKeySpec dks = new DESKeySpec(desKey.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        SecretKey secretKey = keyFactory.generateSecret(dks);

        return secretKey;
    }

    /**
     * 加密数据
     *
     * @param data 待加密数据
     * @param desKey 密钥
     * @return 加密后的数据
     */
    public static byte[] encrypt(byte[] data, String desKey) {
        byte[] result = null;

        if (data == null || desKey == null) {

            return result;
        }

        try {
            // 实例化Cipher对象，它用于完成实际的加密操作
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

            // 初始化Cipher对象，设置为加密模式
            cipher.init(Cipher.ENCRYPT_MODE, generateKey(desKey));
            result = cipher.doFinal(data);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 解密数据
     *
     * @param data 待解密数据
     * @param desKey 密钥
     * @return 解密后的数据
     */
    public static byte[] decrypt(byte[] data, String desKey) {
        byte[] result = null;

        if (data == null || desKey == null) {

            return result;
        }

        try {
            // 实例化Cipher对象，它用于完成实际的加密操作
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

            // 初始化Cipher对象，设置为加密模式
            cipher.init(Cipher.DECRYPT_MODE, generateKey(desKey));
            result = cipher.doFinal(data);

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}

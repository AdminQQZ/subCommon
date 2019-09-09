package com.videoapp.libcommon.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5算法工具
 *
 * @author mos
 * @date 2017.02.04
 * @note 计算出来的MD5为32位，若需16位MD5，则md5.substring(8, 24)即可
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class MD5Util {
    /** 全局数组 */
    private final static String[] DIGITS = {"0", "1", "2", "3", "4",
            "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f"};

    /**
     * 将字节转换为字符串形式
     *
     * @param byteData 字节
     * @return
     */
    private static String byteToArrayString(byte byteData) {
        int iRet = byteData;
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;

        return DIGITS[iD1] + DIGITS[iD2];
    }

    /**
     * 将字节数组转换为字符串
     *
     * @param byteArray 字节数组
     * @return 字符串
     */
    private static String byteToString(byte[] byteArray) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            sBuffer.append(byteToArrayString(byteArray[i]));
        }

        return sBuffer.toString();
    }

    /**
     * 获取字符串的MD5
     *
     * @param strObj 字符串对象
     * @return MD5字符串
     */
    public static String getMD5(String strObj) {
        if (strObj == null) {

            return null;
        }

        return getMD5(strObj.getBytes());
    }

    /**
     * 获取字节数组的MD5
     *
     * @param byteArray 字节数组
     * @return MD5字符串
     */
    public static String getMD5(byte[] byteArray) {
        String resultString = null;

        if (byteArray != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                resultString = byteToString(md.digest(byteArray));
            } catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            }
        }

        return resultString;
    }

    /**
     * 获取文件的MD5
     *
     * @param path 文件路径
     * @return 文件的MD5字符串(若文件不存在，则返回"")
     */
    public static String getMD5FromFile(String path) {
        String ret = "";

        try {
            FileInputStream fis = new FileInputStream(path);
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[8192];
            int len = 0;
            while ((len = fis.read(buffer)) > 0) {
                md.update(buffer, 0, len);
            }

            // 转换为md5字符串
            ret = byteToString(md.digest());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }
}

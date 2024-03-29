package com.videoapp.libcommon.utils;


import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import com.videoapp.libcommon.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * <p> 转换相关工具类 </p><br>
 *
 * @author lwc
 * @date 2017/3/10 15:36
 * @note -
 * bytes2HexString, hexString2Bytes        : byteArr与hexString互转
 * chars2Bytes, bytes2Chars                : charArr与byteArr互转
 * memorySize2Byte, byte2MemorySize        : 以unit为单位的内存大小与字节数互转
 * byte2FitMemorySize                      : 字节数转合适内存大小
 * timeSpan2Millis, millis2TimeSpan        : 以unit为单位的时间长度与毫秒时间戳互转
 * millis2FitTimeSpan                      : 毫秒时间戳转合适时间长度
 * bytes2Bits, bits2Bytes                  : bytes与bits互转
 * input2OutputStream, output2InputStream  : inputStream与outputStream互转
 * inputStream2Bytes, bytes2InputStream    : inputStream与byteArr互转
 * outputStream2Bytes, bytes2OutputStream  : outputStream与byteArr互转
 * inputStream2String, string2InputStream  : inputStream与string按编码互转
 * outputStream2String, string2OutputStream: outputStream与string按编码互转
 * bitmap2Bytes, bytes2Bitmap              : bitmap与byteArr互转
 * drawable2Bitmap, bitmap2Drawable        : drawable与bitmap互转
 * drawable2Bytes, bytes2Drawable          : drawable与byteArr互转
 * view2Bitmap                             : view转Bitmap
 * dp2px, px2dp                            : dp与px互转
 * sp2px, px2sp                            : sp与px互转
 * -------------------------------------------------------------------------------------------------
 * @modified mos
 * @date 2017.05.19
 * @note 1. 加入十六进制字符串转int函数：hexStr2Int
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class ConvertUtils {

    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private ConvertUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * byteArr转hexString
     * <p>例如：</p>
     * bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
     *
     * @param bytes 字节数组
     * @return 16进制大写字符串
     */
    public static String bytes2HexString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        int len = bytes.length;
        if (len <= 0) {
            return null;
        }
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = HEX_DIGITS[bytes[i] >>> 4 & 0x0f];
            ret[j++] = HEX_DIGITS[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    /**
     * hexString转byteArr
     * <p>例如：</p>
     * hexString2Bytes("00A8") returns { 0, (byte) 0xA8 }
     *
     * @param hexString 十六进制字符串
     * @return 字节数组
     */
    public static byte[] hexString2Bytes(String hexString) {
        if (StringUtils.isSpace(hexString)) {
            return null;
        }
        int len = hexString.length();
        if (len % 2 != 0) {
            hexString = "0" + hexString;
            len = len + 1;
        }
        char[] hexBytes = hexString.toUpperCase().toCharArray();
        byte[] ret = new byte[len >> 1];
        for (int i = 0; i < len; i += 2) {
            ret[i >> 1] = (byte) (hex2Dec(hexBytes[i]) << 4 | hex2Dec(hexBytes[i + 1]));
        }
        return ret;
    }

    /**
     * hexChar转int
     *
     * @param hexChar hex单个字节
     * @return 0..15
     */
    private static int hex2Dec(char hexChar) {
        if (hexChar >= '0' && hexChar <= '9') {
            return hexChar - '0';
        } else if (hexChar >= 'A' && hexChar <= 'F') {
            return hexChar - 'A' + 10;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * charArr转byteArr
     *
     * @param chars 字符数组
     * @return 字节数组
     */
    public static byte[] chars2Bytes(char[] chars) {
        if (chars == null || chars.length <= 0) {
            return null;
        }
        int len = chars.length;
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) (chars[i]);
        }
        return bytes;
    }

    /**
     * byteArr转charArr
     *
     * @param bytes 字节数组
     * @return 字符数组
     */
    public static char[] bytes2Chars(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        int len = bytes.length;
        if (len <= 0) {
            return null;
        }
        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            chars[i] = (char) (bytes[i] & 0xff);
        }
        return chars;
    }

    /**
     * 以unit为单位的内存大小转字节数
     *
     * @param memorySize 大小
     * @param unit 单位类型
     * <ul>
     * <li>{@link ConstUtils.MemoryUnit#BYTE}: 字节</li>
     * <li>{@link ConstUtils.MemoryUnit#KB}  : 千字节</li>
     * <li>{@link ConstUtils.MemoryUnit#MB}  : 兆</li>
     * <li>{@link ConstUtils.MemoryUnit#GB}  : GB</li>
     * </ul>
     * @return 字节数
     */
    public static long memorySize2Byte(long memorySize, int unit) {
        if (memorySize < 0) {
            return -1;
        }
        switch (unit) {
            default:
            case ConstUtils.MemoryUnit.BYTE:
                return memorySize;
            case ConstUtils.MemoryUnit.KB:
                return memorySize * ConstUtils.KB;
            case ConstUtils.MemoryUnit.MB:
                return memorySize * ConstUtils.MB;
            case ConstUtils.MemoryUnit.GB:
                return memorySize * ConstUtils.GB;
        }
    }

    /**
     * 字节数转以unit为单位的内存大小
     *
     * @param byteNum 字节数
     * @param unit 单位类型
     * <ul>
     * <li>{@link ConstUtils.MemoryUnit#BYTE}: 字节</li>
     * <li>{@link ConstUtils.MemoryUnit#KB}  : 千字节</li>
     * <li>{@link ConstUtils.MemoryUnit#MB}  : 兆</li>
     * <li>{@link ConstUtils.MemoryUnit#GB}  : GB</li>
     * </ul>
     * @return 以unit为单位的size
     */
    public static double byte2MemorySize(long byteNum, int unit) {
        if (byteNum < 0) {
            return -1;
        }
        switch (unit) {
            default:
            case ConstUtils.MemoryUnit.BYTE:
                return (double) byteNum;
            case ConstUtils.MemoryUnit.KB:
                return (double) byteNum / ConstUtils.KB;
            case ConstUtils.MemoryUnit.MB:
                return (double) byteNum / ConstUtils.MB;
            case ConstUtils.MemoryUnit.GB:
                return (double) byteNum / ConstUtils.GB;
        }
    }

    /**
     * 字节数转合适内存大小
     * <p>保留3位小数</p>
     *
     * @param byteNum 字节数
     * @return 合适内存大小
     */
    @SuppressLint("DefaultLocale")
    public static String byte2FitMemorySize(long byteNum) {
        if (byteNum < 0) {
            return "shouldn't be less than zero!";
        } else if (byteNum < ConstUtils.KB) {
            return String.format("%.3fB", (double) byteNum + 0.0005);
        } else if (byteNum < ConstUtils.MB) {
            return String.format("%.3fKB", (double) byteNum / ConstUtils.KB + 0.0005);
        } else if (byteNum < ConstUtils.GB) {
            return String.format("%.3fMB", (double) byteNum / ConstUtils.MB + 0.0005);
        } else {
            return String.format("%.3fGB", (double) byteNum / ConstUtils.GB + 0.0005);
        }
    }

    /**
     * 以unit为单位的时间长度转毫秒时间戳
     *
     * @param timeSpan 毫秒时间戳
     * @param unit 单位类型
     * <ul>
     * <li>{@link ConstUtils.TimeUnit#MSEC}: 毫秒</li>
     * <li>{@link ConstUtils.TimeUnit#SEC }: 秒</li>
     * <li>{@link ConstUtils.TimeUnit#MIN }: 分</li>
     * <li>{@link ConstUtils.TimeUnit#HOUR}: 小时</li>
     * <li>{@link ConstUtils.TimeUnit#DAY }: 天</li>
     * </ul>
     * @return 毫秒时间戳
     */
    public static long timeSpan2Millis(long timeSpan, int unit) {
        switch (unit) {
            default:
            case ConstUtils.TimeUnit.MSEC:
                return timeSpan;
            case ConstUtils.TimeUnit.SEC:
                return timeSpan * ConstUtils.SEC;
            case ConstUtils.TimeUnit.MIN:
                return timeSpan * ConstUtils.MIN;
            case ConstUtils.TimeUnit.HOUR:
                return timeSpan * ConstUtils.HOUR;
            case ConstUtils.TimeUnit.DAY:
                return timeSpan * ConstUtils.DAY;
        }
    }

    /**
     * 毫秒时间戳转以unit为单位的时间长度
     *
     * @param millis 毫秒时间戳
     * @param unit 单位类型
     * <ul>
     * <li>{@link ConstUtils.TimeUnit#MSEC}: 毫秒</li>
     * <li>{@link ConstUtils.TimeUnit#SEC }: 秒</li>
     * <li>{@link ConstUtils.TimeUnit#MIN }: 分</li>
     * <li>{@link ConstUtils.TimeUnit#HOUR}: 小时</li>
     * <li>{@link ConstUtils.TimeUnit#DAY }: 天</li>
     * </ul>
     * @return 以unit为单位的时间长度
     */
    public static long millis2TimeSpan(long millis, int unit) {
        switch (unit) {
            default:
            case ConstUtils.TimeUnit.MSEC:
                return millis;
            case ConstUtils.TimeUnit.SEC:
                return millis / ConstUtils.SEC;
            case ConstUtils.TimeUnit.MIN:
                return millis / ConstUtils.MIN;
            case ConstUtils.TimeUnit.HOUR:
                return millis / ConstUtils.HOUR;
            case ConstUtils.TimeUnit.DAY:
                return millis / ConstUtils.DAY;
        }
    }

    /**
     * 毫秒时间戳转合适时间长度
     *
     * @param millis 毫秒时间戳
     * <p>小于等于0，返回null</p>
     * @param precision 精度
     * <ul>
     * <li>precision = 0，返回null</li>
     * <li>precision = 1，返回天</li>
     * <li>precision = 2，返回天和小时</li>
     * <li>precision = 3，返回天、小时和分钟</li>
     * <li>precision = 4，返回天、小时、分钟和秒</li>
     * <li>precision &gt;= 5，返回天、小时、分钟、秒和毫秒</li>
     * </ul>
     * @return 合适时间长度
     */
    @SuppressLint("DefaultLocale")
    public static String millis2FitTimeSpan(long millis, int precision) {
        if (millis <= 0 || precision <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String[] units = {"天", "小时", "分钟", "秒", "毫秒"};
        int[] unitLen = {86400000, 3600000, 60000, 1000, 1};
        precision = Math.min(precision, 5);
        for (int i = 0; i < precision; i++) {
            if (millis >= unitLen[i]) {
                long mode = millis / unitLen[i];
                millis -= mode * unitLen[i];
                sb.append(mode).append(units[i]);
            }
        }
        return sb.toString();
    }

    /**
     * bytes转bits
     *
     * @param bytes 字节数组
     * @return bits
     */
    public static String bytes2Bits(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            for (int j = 7; j >= 0; --j) {
                sb.append(((aByte >> j) & 0x01) == 0 ? '0' : '1');
            }
        }
        return sb.toString();
    }

    /**
     * bits转bytes
     *
     * @param bits 二进制
     * @return bytes
     */
    public static byte[] bits2Bytes(String bits) {
        int lenMod = bits.length() % 8;
        int byteLen = bits.length() / 8;
        // 不是8的倍数前面补0
        if (lenMod != 0) {
            for (int i = lenMod; i < 8; i++) {
                bits = "0" + bits;
            }
            byteLen++;
        }
        byte[] bytes = new byte[byteLen];
        for (int i = 0; i < byteLen; ++i) {
            for (int j = 0; j < 8; ++j) {
                bytes[i] <<= 1;
                bytes[i] |= bits.charAt(i * 8 + j) - '0';
            }
        }
        return bytes;
    }

    /**
     * inputStream转outputStream
     *
     * @param is 输入流
     * @return outputStream子类
     */
    public static ByteArrayOutputStream input2OutputStream(InputStream is) {
        if (is == null) {
            return null;
        }
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] b = new byte[ConstUtils.KB];
            int len;
            while ((len = is.read(b, 0, ConstUtils.KB)) != -1) {
                os.write(b, 0, len);
            }
            return os;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            CloseUtils.closeIO(is);
        }
    }

    /**
     * inputStream转byteArr
     *
     * @param is 输入流
     * @return 字节数组
     */
    public static byte[] inputStream2Bytes(InputStream is) {
        if (is == null) {
            return null;
        }
        return input2OutputStream(is).toByteArray();
    }

    /**
     * byteArr转inputStream
     *
     * @param bytes 字节数组
     * @return 输入流
     */
    public static InputStream bytes2InputStream(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        return new ByteArrayInputStream(bytes);
    }

    /**
     * outputStream转byteArr
     *
     * @param out 输出流
     * @return 字节数组
     */
    public static byte[] outputStream2Bytes(OutputStream out) {
        if (out == null) {
            return null;
        }
        return ((ByteArrayOutputStream) out).toByteArray();
    }

    /**
     * outputStream转byteArr
     *
     * @param bytes 字节数组
     * @return 字节数组
     */
    public static OutputStream bytes2OutputStream(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            os.write(bytes);
            return os;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            CloseUtils.closeIO(os);
        }
    }

    /**
     * inputStream转string按编码
     *
     * @param is 输入流
     * @param charsetName 编码格式
     * @return 字符串
     */
    public static String inputStream2String(InputStream is, String charsetName) {
        if (is == null || StringUtils.isSpace(charsetName)) {
            return null;
        }
        try {
            return new String(inputStream2Bytes(is), charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * string转inputStream按编码
     *
     * @param string 字符串
     * @param charsetName 编码格式
     * @return 输入流
     */
    public static InputStream string2InputStream(String string, String charsetName) {
        if (string == null || StringUtils.isSpace(charsetName)) {
            return null;
        }
        try {
            return new ByteArrayInputStream(string.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * outputStream转string按编码
     *
     * @param out 输出流
     * @param charsetName 编码格式
     * @return 字符串
     */
    public static String outputStream2String(OutputStream out, String charsetName) {
        if (out == null || StringUtils.isSpace(charsetName)) {
            return null;
        }
        try {
            return new String(outputStream2Bytes(out), charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * string转outputStream按编码
     *
     * @param string 字符串
     * @param charsetName 编码格式
     * @return 输入流
     */
    public static OutputStream string2OutputStream(String string, String charsetName) {
        if (string == null || StringUtils.isSpace(charsetName)) {
            return null;
        }
        try {
            return bytes2OutputStream(string.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * bitmap转byteArr
     *
     * @param bitmap bitmap对象
     * @param format 格式
     * @return 字节数组
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap, Bitmap.CompressFormat format) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        return baos.toByteArray();
    }

    /**
     * byteArr转bitmap
     *
     * @param bytes 字节数组
     * @return bitmap
     */
    public static Bitmap bytes2Bitmap(byte[] bytes) {
        return (bytes == null || bytes.length == 0) ? null : BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * drawable转bitmap
     *
     * @param drawable drawable对象
     * @return bitmap
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (null == drawable) {
            return null;
        }

        try {
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }
        } catch (Exception e) {
        }

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * bitmap转drawable
     *
     * @param res resources对象
     * @param bitmap bitmap对象
     * @return drawable
     */
    public static Drawable bitmap2Drawable(Resources res, Bitmap bitmap) {
        return bitmap == null ? null : new BitmapDrawable(res, bitmap);
    }

    /**
     * drawable转byteArr
     *
     * @param drawable drawable对象
     * @param format 格式
     * @return 字节数组
     */
    public static byte[] drawable2Bytes(Drawable drawable, Bitmap.CompressFormat format) {
        return drawable == null ? null : bitmap2Bytes(drawable2Bitmap(drawable), format);
    }

    /**
     * byteArr转drawable
     *
     * @param res resources对象
     * @param bytes 字节数组
     * @return drawable
     */
    public static Drawable bytes2Drawable(Resources res, byte[] bytes) {
        return res == null ? null : bitmap2Drawable(res, bytes2Bitmap(bytes));
    }

    /**
     * view转Bitmap
     *
     * @param view 视图
     * @return bitmap
     */
    public static Bitmap view2Bitmap(View view) {
        if (view == null) {
            return null;
        }
        Bitmap ret = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(ret);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return ret;
    }

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(float dpValue) {
        final float scale = Utils.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     *
     * @param pxValue px值
     * @return dp值
     */
    public static int px2dp(float pxValue) {
        final float scale = Utils.getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp转px
     *
     * @param spValue sp值
     * @return px值
     */
    public static int sp2px(float spValue) {
        final float fontScale = Utils.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * px转sp
     *
     * @param pxValue px值
     * @return sp值
     */
    public static int px2sp(float pxValue) {
        final float fontScale = Utils.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 十六进制字符串转整型
     *
     * @param data 字符串
     * @return 整型值
     * @note data最长为8个字符，否则将截取前面8个有效字符(0x除外)。
     */
    public static int hexStr2Int(String data) {
        // 去除0x 前缀
        if (data.toLowerCase(Locale.getDefault()).startsWith("0x")) {
            data = data.substring(2);
        }
        if (data.length() > 8) {
            data = data.substring(0, 8);
        }

        // Integer.parseInt不能处理符号位，因此采用Long型来处理
        long value = Long.parseLong(data, 16);

        return (int) (value & 0xffffffff);
    }

    /**
     * list<E> 转换 list<T>，转换类型
     *
     * @param old 旧的list<E>
     * @return 新的list<T>
     */
    public static <T, E> List<T> list2List(List<E> old, IOnConvert<T, E> onConvert) {
        if (old == null || old.size() < 0) {
            return null;
        }
        List<T> list = new ArrayList<>();
        if (old.size() == 0) {
            return list;
        }
        for (E e : old) {
            list.add(onConvert.convertListener(e));
        }
        return list;
    }

    /**
     * list 移除重复的数据
     *
     * @param list list
     * @return 去重后的list
     */
    public static <E> List<E> removeDuplicateWithOrder(List<E> list) {
        List<E> newList = new ArrayList<>();
        for (E o : list) {
            if (!newList.contains(o)) {
                newList.add(o);
            }
        }
        return newList;
    }

    /**
     * String 转换 byte
     *
     * @param origin 来源
     * @return 结果
     * @note 如果有异常返回0
     */
    public static byte string2Byte(String origin) {
        byte result = 0;
        try {
            result = Byte.valueOf(origin);
        } catch (Exception e) {
            // 不做处理
        }
        return result;
    }

    /**
     * String 转换 short
     *
     * @param origin 来源
     * @return 结果
     * @note 如果有异常返回0
     */
    public static short string2Short(String origin) {
        short result = 0;
        try {
            result = Short.valueOf(origin);
        } catch (Exception e) {
            // 不做处理
        }
        return result;
    }

    /**
     * String 转换 int
     *
     * @param origin 来源
     * @return 结果
     * @note 如果有异常返回0
     */
    public static int string2Int(String origin) {
        int result = 0;
        try {
            result = Integer.valueOf(origin);
        } catch (Exception e) {
            // 不做处理
        }
        return result;
    }

    /**
     * String 转换 long
     *
     * @param origin 来源
     * @return 结果
     * @note 如果有异常返回0L
     */
    public static long string2Long(String origin) {
        long result = 0L;
        try {
            result = Long.valueOf(origin);
        } catch (Exception e) {
            // 不做处理
        }
        return result;
    }

    /**
     * String 转换 float
     *
     * @param origin 来源
     * @return 结果
     * @note 如果有异常返回0.0f
     */
    public static float string2Float(String origin) {
        float result = 0.0f;
        try {
            result = Float.valueOf(origin);
        } catch (Exception e) {
            // 不做处理
        }
        return result;
    }

    /**
     * String 转换 double
     *
     * @param origin 来源
     * @return 结果
     * @note 如果有异常返回0.0d
     */
    public static double string2Double(String origin) {
        double result = 0.0d;
        try {
            result = Double.valueOf(origin);
        } catch (Exception e) {
            // 不做处理
        }
        return result;
    }

    /**
     * String 转换 boolean
     *
     * @param origin 来源
     * @return 结果
     * @note 如果有异常返回false
     */
    public static boolean string2Boolean(String origin) {
        boolean result = false;
        try {
            result = Boolean.valueOf(origin);
        } catch (Exception e) {
            // 不做处理
        }
        return result;
    }

    /**
     * color 转换 int
     *
     * @param origin 来源
     * @return 结果
     * @note 如果有异常返回黑色
     */
    public static int color2Int(String origin) {
        int result = ResUtils.getColor(R.color.black);

        if (TextUtils.isEmpty(origin)) {
            return result;
        }

        if (!origin.startsWith("#")) {
            origin = "#" + origin;
        }

        try {
            return Color.parseColor(origin);
        } catch (Exception e) {
            return result;
        }
    }

    /**
     * outputStream转inputStream
     *
     * @param out 输出流
     * @return inputStream子类
     */
    public ByteArrayInputStream output2InputStream(OutputStream out) {
        if (out == null) {
            return null;
        }
        return new ByteArrayInputStream(((ByteArrayOutputStream) out).toByteArray());
    }

    /**
     * 转换接口
     *
     * @param <T> 旧的转换类型
     * @param <E> 新的转换类型
     */
    public interface IOnConvert<T, E> {
        /**
         * 转化类型
         *
         * @param e 旧的类型
         * @return 新的类型
         */
        T convertListener(E e);
    }
}

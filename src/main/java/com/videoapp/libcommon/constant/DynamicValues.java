package com.videoapp.libcommon.constant;

import com.videoapp.libcommon.utils.LogUtils;

/**
 * @author - qqz
 * @date - 2019/5/28
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class DynamicValues {
    private static int sWebViewBalance = 0;
    /**
     * 设置balance值
     *
     * @param increment 是否增加
     */
    public static void setWebViewBalance(boolean increment) {
        if (increment) {
            sWebViewBalance++;
        } else {
            sWebViewBalance--;
        }

        LogUtils.d("webview balance: " + sWebViewBalance);
    }
}

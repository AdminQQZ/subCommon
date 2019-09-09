package com.videoapp.libcommon.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * BroadcastReceiver基类
 *
 * @author qqz
 * @date 2019.05.23
 * @note 1. 项目中所有子类必须继承自此基类
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public abstract class BaseBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        onReceive(context, intent, 0);
    }

    /**
     * onReceive回调
     *
     * @param context 参考回调文档说明
     * @param intent 参考回调文档说明
     * @param flag 标志(暂未使用)
     */
    public abstract void onReceive(Context context, Intent intent, int flag);
}

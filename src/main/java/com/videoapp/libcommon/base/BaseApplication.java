package com.videoapp.libcommon.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import com.videoapp.libcommon.R;
import com.videoapp.libcommon.utils.Utils;
import com.videoapp.libcommon.utils.loader.LoaderFactory;

import java.lang.ref.WeakReference;

/**
 * Application基类
 *
 * @author qqz
 * @date 2017.01.23
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class BaseApplication extends Application implements Application.ActivityLifecycleCallbacks {
    /** 调试TAG前缀 */
    private static final String TAG_PREFIX = "videos";
    /** 单例 */
    private static BaseApplication sInstance = null;
    /** 获取目前正在展示的Activity */
    private static WeakReference<Activity> sRefActivity;

    /**
     * 单例模式
     *
     * @return BaseApplication
     */
    public static BaseApplication getInstance() {
        return sInstance;
    }

    @Nullable
    public static Activity getRefActivity() {
        return sRefActivity.get();
    }

    @CallSuper
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        // 内存泄露检测(Release版本时为空操作)


        // 工具初始化
        Utils.init(this);

        // 工具类初始化

        // 崩溃记录

        // Retrofit初始化

        //注册生命周期的回调
        registerActivityLifecycleCallbacks(this);
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        sRefActivity = new WeakReference<>(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

}

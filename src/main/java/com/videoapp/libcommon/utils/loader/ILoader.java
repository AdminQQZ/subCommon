package com.videoapp.libcommon.utils.loader;

import android.content.Context;
import android.widget.ImageView;

/**
 * @author - qqz
 * @date - 2019/5/6
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public interface ILoader {

    /**
     * 初始化
     *
     * @param context 上下文
     */
    void init(Context context, Options options);

    /**
     * 网络下载图片
     *
     * @param target ImageView控件
     * @param url 地址
     * @param options 参数设置
     */
    void loadNet(ImageView target, String url, Options options);
    /**
     * 网络下载图片
     *
     * @param target ImageView控件
     * @param url 地址
     */
    void loadNet(ImageView target, String url);

    /**
     * 资源文件加载图片
     *
     * @param target ImageView控件
     * @param resId 地址
     * @param options 参数设置
     */
    void loadResource(ImageView target, int resId, Options options);

    /**
     * 资源文件加载图片
     *
     * @param target ImageView控件
     * @param resId 地址
     */
    void loadResource(ImageView target, int resId);

    /**
     * 网络请求监听
     *
     * @param context 上下文
     * @param url 请求链接
     * @param onImageLoadListener 网络请求的监听器
     */
    void loadListener(Context context, Object url, IOnImageLoadListener onImageLoadListener);
}

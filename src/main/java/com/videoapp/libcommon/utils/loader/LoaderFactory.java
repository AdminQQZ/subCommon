package com.videoapp.libcommon.utils.loader;

/**
 * 加载工厂
 *
 * @author - qqz
 * @date - 2019/5/6
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class LoaderFactory {
    /** 加载接口 */
    private static ILoader mILoader;

    /**
     * 获取加载器
     *
     * @return loader
     */
    public static ILoader getILoader() {
        if (mILoader == null) {
            synchronized (LoaderFactory.class) {
                mILoader = new GlideLoader();
            }
        }
        return mILoader;
    }
}

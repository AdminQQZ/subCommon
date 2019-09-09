package com.videoapp.libcommon.bean;


import com.videoapp.libcommon.utils.JsonUtil;
import com.videoapp.libcommon.utils.LogUtils;
import com.videoapp.opsrc.utils.jsbridge.CallBackFunction;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Js使用的内部组件的调用结果(即将发生)
 *
 * @author mos
 * @date 2017.12.04
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class JsPendingActionResult extends BaseActionResult {
    /** 回调 */
    private CallBackFunction mCallback;

    /**
     * 设置回调
     *
     * @param callback 回调
     * @note 此函数仅供框架调用
     */
    public void setCallback(CallBackFunction callback) {
        mCallback = callback;
    }

    /**
     * 通知结果
     *
     * @param result 结果
     */
    public void notifyResult(ActionResult result) {
        if (mCallback != null) {
            Observable.just(result)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ActionResult>() {
                        @Override
                        public void call(ActionResult result) {
                            mCallback.onCallBack(JsonUtil.objectToJsonString(result));
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            LogUtils.d(throwable);
                        }
                    });
        }
    }
}

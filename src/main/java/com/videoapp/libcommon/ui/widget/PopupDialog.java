package com.videoapp.libcommon.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.videoapp.libcommon.R;


/**
 * 对话框
 *
 * @author - qqz
 * @date - 2019/4/19
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class PopupDialog extends Dialog {
    /** 环境 */
    protected Context mContext;
    /** 当前窗体 */
    public Window mWindow;
    /** 根布局 */
    protected View mRootView;
    // 返回监听
    protected IBackListener mBackListener;

    @Deprecated
    public PopupDialog(@NonNull Context context, int style) {
        super(context, style);
        mContext = context;
    }
    /**
     * 设置窗口矩形区
     *
     * @param rect 矩形结构
     */
    public void setWindowRect(Rect rect) {
        WindowManager.LayoutParams lp = mWindow.getAttributes();

        // 设置坐标及大小
        lp.x = rect.left;
        lp.y = rect.top;
        lp.width = rect.width();
        lp.height = rect.height();

        mWindow.setAttributes(lp);
        setGravity(Gravity.LEFT | Gravity.TOP);
    }

    /**
     * 返回监听
     */
    public static interface IBackListener {
        /**
         * 取消键被按下
         */
        public void onBack();
    }
    /**
     * 返回监听
     *
     * @param listener 监听器
     */
    public void setBackListener(IBackListener listener) {
        mBackListener = listener;
    }
    /**
     * 构造函数
     *
     * @param context 上下文
     * @param layoutId 布局id
     * @param style 样式,如果填写 0 那么使用系统样式
     */
    public PopupDialog(@NonNull Context context, int layoutId, int style) {
        super(context, style);
        mContext = context;
        initView(layoutId);
    }
    /**
     * 设置透明度
     *
     * @param alpha 透明度(0.0f完全透明，1.0f不透明)
     */
    public void setAlpha(float alpha) {
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.alpha = alpha;
        mWindow.setAttributes(lp);
    }
    /**
     * 构造函数
     *
     * @param context 上下文
     * @param layoutId 布局id
     * @param dim d
     */
    public PopupDialog(@NonNull Context context, int layoutId, boolean dim) {
        super(context, (dim ? R.style.FullScreenDialog : R.style.FullScreenDialogTrans));
        mContext = context;
        initView(layoutId);
    }


    /**
     * 初始化控件
     *
     * @param layoutId c
     */
    private void initView(int layoutId) {
        if (layoutId != 0) {

            setCancelable(true);

            setCanceledOnTouchOutside(true);

            mWindow = getWindow();

            mRootView = LayoutInflater.from(mContext).inflate(layoutId, (ViewGroup) mWindow.getDecorView());

        }
    }

    /**
     * 设置文字
     *
     * @param resId 控件id
     * @param text txt
     */
    public void setText(int resId, String text) {
        View viewById = mRootView.findViewById(resId);
        if (viewById instanceof TextView) {
            ((TextView) viewById).setText(text);
        }
    }

    /**
     * 设置窗口动画
     *
     * @param resId 动画资源id
     */
    public void setWindowAnimations(int resId) {
        mWindow.setWindowAnimations(resId);
    }

    /**
     * 设置文字大小
     *
     * @param resId r
     * @param size s
     */
    public void setTextSize(int resId, int size) {
        View viewById = mRootView.findViewById(resId);
        if (viewById instanceof TextView) {
            ((TextView) viewById).setTextSize(size);
        }
    }

    /**
     * 设置重心
     *
     * @param gravity 重心
     */
    public void setGravity(int gravity) {
        mWindow.setGravity(gravity);
    }

    /**
     * 设置动画
     *
     * @param resId 资源id
     */
    public void setAnimations(int resId) {
        mWindow.setWindowAnimations(resId);
    }

    /**
     * 设置监听
     *
     * @param resId id
     * @param onClickListener 监听
     */
    public void setClickListener(int resId, View.OnClickListener onClickListener) {
        mRootView.findViewById(resId).setOnClickListener(onClickListener);
    }

    /**
     * 设置变暗的不透明度
     *
     * @param amount 不透明度(0.0 ~ 完全透明，1.0 ~ 全黑)
     */
    public void setDimAmount(float amount) {
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams layoutParams = mWindow.getAttributes();
        layoutParams.dimAmount = amount;
        mWindow.setAttributes(layoutParams);
    }

    /**
     * 设置偏移
     *
     * @param x 横向偏移
     * @param y 纵向偏移
     */
    public void setAttributes(int x, int y) {
        // 设置具体参数
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        // 设置偏移
        lp.x = x;
        lp.y = y;
        mWindow.setAttributes(lp);
        setGravity(Gravity.LEFT | Gravity.TOP);
    }

    /**
     * 设置填充宽度
     */
    public void setMatchWidth() {
        WindowManager.LayoutParams attributes = mWindow.getAttributes();
        attributes.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mWindow.setAttributes(attributes);
    }

    /**
     * 设置高度
     */
    public void setMatchHeight() {
        WindowManager.LayoutParams attributes = mWindow.getAttributes();
        attributes.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mWindow.setAttributes(attributes);
    }


}

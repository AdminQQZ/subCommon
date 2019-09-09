package com.videoapp.opsrc.utils.jsbridge;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.videoapp.libcommon.BuildConfig;
import com.videoapp.libcommon.R;
import com.videoapp.libcommon.base.BaseActivity;
import com.videoapp.libcommon.base.BaseApplication;
import com.videoapp.libcommon.constant.DynamicValues;
import com.videoapp.libcommon.utils.JsonUtil;
import com.videoapp.libcommon.utils.LogUtils;
import com.videoapp.libcommon.utils.NetworkUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * jsBridge封装
 *
 * @author - mos
 * @date - 2017.04.13
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class JsBridgeWrapper {
    private WeakReference<BaseActivity> mActivityWeakReference;
    private BridgeWebView mBridgeWebView;
    private boolean mIsRegisterJs = false;
    private boolean mIsError = false;
    private WebViewOption mOption;
    private IPageAction mPageAction;
    private IPageListener mPageListener;
    private IOverrideDialog mIOverrideDialog;
    private Map<String, String> mJavascriptInjectionMap = new HashMap<>();
    private Map<String, IOverrideUrlLoading> mExternalOverrideUrlLoading = new HashMap<>();
    private ViewGroup mVgErrorLayout;
    private MaterialProgressBar mMpbLoadingProgress;
    private Set<String> mPageStartSet = new HashSet<>();
    private RelativeLayout mWebViewContainer;
    private TextView mTvTitleView;

    /** 构造函数私有化 */
    private JsBridgeWrapper() {
    }



    /**
     * 创建 实例
     *
     * @param baseActivity
     * @param containerView
     * @return
     */
    public static JsBridgeWrapper createInstance(BaseActivity baseActivity, ViewGroup containerView,
                                                 WebViewOption option,
                                                 IPageAction action, IPageListener listener) {
        JsBridgeWrapper wrapper = new JsBridgeWrapper();
        wrapper.mActivityWeakReference = new WeakReference<>(baseActivity);
        wrapper.mPageAction = action;
        wrapper.mPageListener = listener;
        wrapper.mOption = option;

        if (wrapper.mOption == null) {
            wrapper.mOption = new WebViewOption();
            wrapper.mOption.url = "";
            wrapper.mOption.title = "";
            wrapper.mOption.cookie = "";
            wrapper.mOption.showTitleBar = false;
        }

        wrapper.initWebView(wrapper.mActivityWeakReference.get(), containerView);

        wrapper.initData();
        return wrapper;
    }

    /**
     * 初始化
     *
     * @param baseActivity
     * @param containerView
     */
    private void initWebView(BaseActivity baseActivity, ViewGroup containerView) {
        LayoutInflater inflater;
        if (baseActivity != null) {
            inflater = baseActivity.getLayoutInflater();
        } else {
            inflater = LayoutInflater.from(BaseApplication.getInstance());
        }

        View mRootView = inflater.inflate(R.layout.layout_js_bridge_web_view, containerView);
        mTvTitleView = mRootView.findViewById(R.id.tv_simple_title_bar_title);

        mWebViewContainer = mRootView.findViewById(R.id.rellay_web_view_container);
        mBridgeWebView = WebViewManager.getInstance().createWebView(baseActivity, mWebViewContainer);

        if (mOption == null || mOption.hardwareAccelerated) {
            mBridgeWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mBridgeWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        WebSettings settings = mBridgeWebView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setGeolocationEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }

        if (!TextUtils.isEmpty(mOption.userAgentSuffix)) {
            String ua = settings.getUserAgentString();
            settings.setUserAgentString(ua + " " + mOption.userAgentSuffix);
        }

        if (mOption.loadCache) {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        } else {
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        mBridgeWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (BaseApplication.getInstance().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
        mBridgeWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                BaseApplication.getInstance().startActivity(intent);
            }
        });

        mBridgeWebView.setWebViewClient(new BridgeWebViewClient(mBridgeWebView) {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

                showErrorPage(view, description);

                if (mPageListener != null) {
                    mPageListener.onError(errorCode, failingUrl);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                mPageStartSet.add(url);

                if (mOption.blockNetworkImage) {
                    view.getSettings().setBlockNetworkImage(true);
                }

                hideErrorPage();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (!mPageStartSet.contains(url)) {

                    return;
                }
                mPageStartSet.remove(url);

                DynamicValues.setWebViewBalance(false);

                injectJavascript(view, url);

                if (mOption.blockNetworkImage) {
                    view.getSettings().setBlockNetworkImage(false);
                }

                if (view.canGoBack()) {
//                    mVgIconClose.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Debug.isDebuggerConnected()) {
                    LogUtils.d("shouldOverrideUrlLoading: " + url);
                }

                final int process = WebViewManager.getInstance().processShouldOverrideUrlLoading(view, url);
                if (process != WebViewManager.PROCESS_NOTHING) {

                    return true;
                }

                boolean disposed = false;
                for (Map.Entry<String, IOverrideUrlLoading> entry : mExternalOverrideUrlLoading.entrySet()) {
                    String startUrl = entry.getKey();
                    if (WebViewManager.matchStartUrl(url, startUrl)) {
                        IOverrideUrlLoading overrideUrlLoading = entry.getValue();
                        disposed = overrideUrlLoading.onOverrideUrlLoading(view, url, mPageAction);

                        break;
                    }
                }
                if (disposed) {

                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        mBridgeWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                setTitle(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (mOption != null && !mOption.indeterminate) {
                    if (newProgress == 100) {
                        mMpbLoadingProgress.setVisibility(View.GONE);
                    } else {
                        if (mMpbLoadingProgress.getVisibility() == View.GONE) {
                            mMpbLoadingProgress.setVisibility(View.VISIBLE);
                        }
                        if (newProgress == 0) {
                            newProgress = 5;
                        }
                        mMpbLoadingProgress.setProgress(newProgress);
                    }
                } else {
                    if (mPageAction != null) {
                        if (newProgress == 100) {
                            mPageAction.onHideIndeterminateLoading();
                        } else {
                            mPageAction.onShowIndeterminateLoading();
                        }
                    }
                }
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                // Android 5.0+
                if (mPageAction != null) {
                    mPageAction.onOpenFileChooserAboveL(filePathCallback, fileChooserParams);
                }
                return true;
            }

            /**
             * 打开文件
             *
             * @param uploadMsg
             * @note For Android < 3.0
             */
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            /**
             * 打开文件
             *
             * @param uploadMsg
             * @note For Android 3.0+
             */
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooser(uploadMsg, acceptType, "");
            }

            /**
             * 打开文件
             *
             * @param uploadMsg
             * @param acceptType
             * @param capture
             * @note For Android 4.1+
             */
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                if (mPageAction != null) {
                    mPageAction.onOpenFileChooser(uploadMsg, acceptType, capture);
                }
            }

            /**
             * 定位提示的回调
             *
             * @param origin 请求地址
             * @param callback 定位权限的回调
             */
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                if (null != mIOverrideDialog) {
                    return mIOverrideDialog.onOverrideAlert(view, url, message, result);
                }
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                if (null != mIOverrideDialog) {
                    return mIOverrideDialog.onOverrideConfirm(view, url, message, result);
                }
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                if (null != mIOverrideDialog) {
                    return mIOverrideDialog.onOverridePrompt(view, url, message, defaultValue, result);
                }
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });

        mMpbLoadingProgress = containerView.findViewById(R.id.mpb_web_view_progress);
        mMpbLoadingProgress.bringToFront();

        mVgErrorLayout = (ViewGroup) containerView.findViewById(R.id.vg_web_view_error_page);
        mVgErrorLayout.bringToFront();
        mVgErrorLayout.findViewById(R.id.tv_web_view_error_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLastFailingUrl();
            }
        });
    }

    /**
     * 加载上次失败的Url
     */
    private void loadLastFailingUrl() {
        if (mBridgeWebView == null) {

            return;
        }

        if (mOption != null && !mOption.indeterminate) {
            mMpbLoadingProgress.setProgress(5);
        } else {
            if (mPageAction != null) {
                mPageAction.onShowIndeterminateLoading();
            }
        }

        mBridgeWebView.reload();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (mOption != null && mBridgeWebView != null) {
            if (mOption.url == null) {
                mOption.url = "";
            }

            if (mOption.url.startsWith("http") || mOption.url.startsWith("https")) {
                if (NetworkUtils.isConnected()) {
                    WebViewSession.setCookie(mOption.url, mOption.cookie);

                    final Map<String, String> headers = new HashMap<>();
                    JsonUtil.parseFlatJson(mOption.header, new JsonUtil.IParseCallback() {
                        @Override
                        public void onKeyValue(String key, String value) {
                            headers.put(key, value);
                        }
                    });

                    if (mPageListener != null) {
                        mPageListener.onBegin(mOption.url, mOption.title);
                    }

                    mBridgeWebView.loadUrl(mOption.url, headers);

                    DynamicValues.setWebViewBalance(true);
                } else {
                    showErrorPage(mBridgeWebView, "network not available");

                    if (mPageListener != null) {
                        mPageListener.onError(-1, mOption.url);
                    }
                }
            } else {
                mBridgeWebView.loadUrl(mOption.url);
            }
        }
    }

    /**
     *
     * @param webview webview
     * @param url url
     */
    private void injectJavascript(WebView webview, String url) {
        List<String> injectedRecord = new ArrayList<>();

        for (Map.Entry<String, String> entry : mJavascriptInjectionMap.entrySet()) {
            String startUrl = entry.getKey();
            String javascriptPath = entry.getValue();
            if ("*".equals(startUrl) || WebViewManager.matchStartUrl(url, startUrl)) {
                if (!injectedRecord.contains(javascriptPath)) {
                    BridgeUtil.webViewLoadLocalJs(webview, javascriptPath);
                    webview.loadUrl("javascript:try{onInjectJS();}catch(e){}");
                    injectedRecord.add(javascriptPath);
                }

                continue;
            }
        }
    }

    /**
     *
     * @param view webview
     * @param reason 错误原因
     */
    private void showErrorPage(WebView view, String reason) {
        mIsError = true;
        TextView tvReason = mVgErrorLayout.findViewById(R.id.tv_web_view_error_reason);
        if (!TextUtils.isEmpty(reason)) {
            tvReason.setText("(" + reason + ")");
        } else {
            tvReason.setText("reason");
        }
        mVgErrorLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏错误页面
     */
    private void hideErrorPage() {
        if (mBridgeWebView == null) {

            return;
        }

        if (mIsError) {
            mBridgeWebView.clearView();
            String title = mBridgeWebView.getTitle();
            setTitle(title);
        }
        mIsError = false;
        mVgErrorLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    private void setTitle(String title) {
        if (title != null && mOption != null && mOption.overrideTitle && !mIsError) {
            String substring = null;
            if (title.length() > 6) {
                substring = title.substring(0, 6);
            } else {
                substring = title;
            }
            mTvTitleView.setText(substring);
        }
    }

    /**
     * 注册Js回调
     *
     * @note 必须在createInstance之后执行。
     */
    public void registerJsCallback() {
        initWebViewJsCallback();

        if (!mIsRegisterJs) {
            mIsRegisterJs = true;
            WebViewManager.getInstance().registerJsCallback(mBridgeWebView);
        }
    }

    /**
     * 恢复
     */
    public void onResume() {
        if (mBridgeWebView != null) {
            mBridgeWebView.onResume();
            mBridgeWebView.resumeTimers();
        }
    }

    /**
     * 暂停
     */
    public void onPause() {
        if (mBridgeWebView != null) {
            mBridgeWebView.onPause();
            mBridgeWebView.pauseTimers();
        }
    }

    /**
     * 释放
     *
     * @note 页面结束时，一定要调用此函数，避免内存泄露
     */
    public void onDestroy() {
        if (mPageAction != null) {
            mPageAction.onHideIndeterminateLoading();
        }

        WebViewSession.clearCookie();

        if (mBridgeWebView != null) {
            mBridgeWebView.setOnKeyListener(null);
            mBridgeWebView.setWebViewClient(null);
            mBridgeWebView.setDownloadListener(null);

            mWebViewContainer.removeAllViews();
            mBridgeWebView.clearHistory();
            mBridgeWebView.clearCache(true);
            mBridgeWebView.loadUrl("about:blank");
            mBridgeWebView.onPause();
            mBridgeWebView.removeAllViews();
            mBridgeWebView.destroyDrawingCache();
            mBridgeWebView.pauseTimers();
            mBridgeWebView.destroy();
            mBridgeWebView = null;
        }
    }

    /**
     * 初始化WebView相关的Js回调
     */
    private void initWebViewJsCallback() {
    }

    /**
     * 回退
     */
    public void goBack() {
        if (mBridgeWebView == null) {

            return;
        }

        if (mPageListener != null) {
            WebBackForwardList webBackForwardList = mBridgeWebView.copyBackForwardList();
            int currentIndex = webBackForwardList.getCurrentIndex();
            String previousUrl = "";
            if (currentIndex > 0) {
                previousUrl = webBackForwardList.getItemAtIndex(currentIndex - 1).getUrl();
            }
            mPageListener.onBack(previousUrl);
        }

        if (mPageAction != null) {
            mPageAction.onHideIndeterminateLoading();
        }
        if (mBridgeWebView.canGoBack()) {
            mBridgeWebView.goBack();
        } else {
            if (mPageAction != null) {
                mPageAction.onFinish();
            }
        }
    }


    public void onWebShow() {
        if (mBridgeWebView != null) {
            mBridgeWebView.loadUrl("javascript:try{onWebShow();}catch(e){}");
        }
    }

    public void addJavascriptInjection(String startUrl, String javascriptPath) {
        if (TextUtils.isEmpty(startUrl) || TextUtils.isEmpty(javascriptPath)) {

            return;
        }

        mJavascriptInjectionMap.put(startUrl, javascriptPath);
    }


    /**
     * 页面状态监听
     */
    public interface IPageListener {
        /**
         * 开始加载
         *
         * @param url 地址
         */
        void onBegin(String url, String title);

        /**
         * 返回被点击
         *
         * @param previousUrl 上一个地址
         */
        void onBack(String previousUrl);

        /**
         * 结束被点击
         */
        void onFinish();

        /**
         * 错误回调
         *
         * @param code http错误码
         * @param failingUrl 错误的url
         */
        void onError(int code, String failingUrl);
    }

    /**
     * 重写Url加载回调
     */
    public interface IOverrideUrlLoading {
        /**
         * 重写Url加载
         *
         * @param webview webview
         * @param url url
         * @param pageAction 页面动作控制
         * @return true -- 已成功重写  false --  未成功重写
         */
        boolean onOverrideUrlLoading(WebView webview, String url, IPageAction pageAction);
    }

    /**
     * 重写Alert Confirm Prompt的样式
     */
    public interface IOverrideDialog {
        /**
         * 重写Alert样式
         *
         * @param view webview
         * @param url url
         * @param message message
         * @param result 页面动作控制
         * @return true -- 已成功重写  false --  未成功重写
         */
        boolean onOverrideAlert(WebView view, String url, String message, JsResult result);

        /**
         * 重写Confirm样式
         *
         * @param view webview
         * @param url url
         * @param message message
         * @param result 页面动作控制
         * @return true -- 已成功重写  false --  未成功重写
         */
        boolean onOverrideConfirm(WebView view, String url, String message, JsResult result);

        /**
         * 重写Prompt样式
         *
         * @param view webview
         * @param url url
         * @param message message
         * @param defaultValue 默认值
         * @param result 页面动作控制
         * @return true -- 已成功重写  false --  未成功重写
         */
        boolean onOverridePrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result);
    }

    /**
     * 页面动作
     */
    public interface IPageAction {
        /**
         * 显示转圈Loading
         */
        void onShowIndeterminateLoading();

        /**
         * 隐藏转圈Loading
         */
        void onHideIndeterminateLoading();

        /**
         * 显示toast
         *
         * @param msg 消息
         */
        void onShowToast(String msg);

        /**
         * 结束
         */
        void onFinish();

        /**
         * 打开文件选择器
         *
         * @param filePathCallback 文件选择器打开后的回调
         * @param acceptType acceptType
         * @param capture capture
         * @note Android 5.0版本以下
         */
        void onOpenFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, String capture);

        /**
         * 打开文件选择器
         *
         * @param filePathCallback 文件选择器打开后的回调
         * @param fileChooserParams 参数
         * @note Android 5.0版本以上
         */
        void onOpenFileChooserAboveL(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams);
    }
}

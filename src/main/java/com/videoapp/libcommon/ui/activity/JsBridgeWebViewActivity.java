package com.videoapp.libcommon.ui.activity;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

import com.videoapp.libcommon.R;
import com.videoapp.libcommon.base.BaseActivity;
import com.videoapp.libcommon.utils.ResUtils;
import com.videoapp.opsrc.utils.jsbridge.JsBridgeWrapper;
import com.videoapp.opsrc.utils.jsbridge.WebViewOption;

/**
 * jsBridgeWebView
 * @author - qqz
 * @date - 2019/5/28
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class JsBridgeWebViewActivity extends BaseActivity {
    /** 选项(参数，WebViewOption) */
    public static final String EXTRA_OPTIONS = "extra_options";
    /** 打开文件请求码 */
    private static final int REQUEST_CODE_OPEN_FILE = 15888;
    /** WebView包装器 */
    protected JsBridgeWrapper mWebViewWrapper;
    /** WebView参数 */
    public WebViewOption mOption;
    /** 文件路径回调 */
    private ValueCallback<Uri> mFilePathCallback;
    /** 文件路径回调 */
    private ValueCallback<Uri[]> mFilePathCallbackAboveL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_js_bridge_web_view);
        if (savedInstanceState != null) {
            mOption = savedInstanceState.getParcelable(EXTRA_OPTIONS);
        } else {
            mOption = getIntent().getParcelableExtra(EXTRA_OPTIONS);
        }

        ViewGroup containerView = (ViewGroup) findViewById(R.id.fralay_web_view_container);
        mWebViewWrapper = JsBridgeWrapper.createInstance(this, containerView, mOption, new JsBridgeWrapper.IPageAction() {
            @Override
            public void onShowIndeterminateLoading() {
            }

            @Override
            public void onHideIndeterminateLoading() {
                hideLoading();
            }

            @Override
            public void onShowToast(String msg) {
                showToast(msg);
            }

            @Override
            public void onFinish() {
                JsBridgeWebViewActivity.this.finish();
            }

            @Override
            public void onOpenFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, String capture) {
                if (mFilePathCallback != null) {

                    return;
                }
                mFilePathCallback = filePathCallback;
                openFileChooser(acceptType);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onOpenFileChooserAboveL(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                if (mFilePathCallbackAboveL != null) {

                    return;
                }
                mFilePathCallbackAboveL = filePathCallback;
                String[] acceptTypes = fileChooserParams.getAcceptTypes();
                String acceptType = acceptTypes == null ? "" : acceptTypes[0];
                openFileChooser(acceptType);
            }
        }, new JsBridgeWrapper.IPageListener() {
            @Override
            public void onBegin(String url, String title) {
                JsBridgeWebViewActivity.this.onBegin(url, title);
            }

            @Override
            public void onBack(String previousUrl) {
                JsBridgeWebViewActivity.this.onBack(previousUrl);
            }

            @Override
            public void onFinish() {
                JsBridgeWebViewActivity.this.onFinish();
            }

            @Override
            public void onError(int code, String failingUrl) {
                JsBridgeWebViewActivity.this.onError(code, failingUrl);
            }
        });
    }

    /**
     * 打开文件
     *
     * @param acceptType 类型
     */
    private void openFileChooser(String acceptType) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        if (TextUtils.isEmpty(acceptType)) {
            i.setType("*/*");
        } else {
            i.setType(acceptType);
        }
        startActivityForResult(Intent.createChooser(i, ResUtils.getString(R.string.web_view_open_file)), REQUEST_CODE_OPEN_FILE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_OPEN_FILE) {
            if (mFilePathCallback != null) {
                handleOpenFileResult(resultCode, data);
            } else if (mFilePathCallbackAboveL != null) {
                handleOpenFileResultAboveL(resultCode, data);
            }
            mFilePathCallback = null;
            mFilePathCallbackAboveL = null;
        }
    }

    /**
     * 处理打开文件结果
     *
     * @param resultCode 结果
     * @param data 数据
     * @note Android 5.0以下
     */
    private void handleOpenFileResult(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            mFilePathCallback.onReceiveValue(null);

            return;
        }

        Uri result = data.getData();
        mFilePathCallback.onReceiveValue(result);
    }

    /**
     * 开始加载
     *
     * @param url url地址
     * @param title 标题
     */
    public void onBegin(String url, String title) {
        // 不处理
    }

    /**
     * 返回被点击
     *
     * @param previousUrl 上一个链接
     */
    public void onBack(String previousUrl) {
        // 不处理
    }

    /**
     * 结束被点击
     */
    public void onFinish() {
        // 不处理
    }

    /**
     * 错误回调
     *
     * @param code http错误码
     * @param failingUrl 错误的url
     */
    public void onError(int code, String failingUrl) {
        // 不处理
    }

    /**
     * 处理打开文件结果
     *
     * @param data 数据
     * @note Android 5.0+
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void handleOpenFileResultAboveL(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            mFilePathCallbackAboveL.onReceiveValue(null);

            return;
        }

        Uri[] results = null;

        String dataString = data.getDataString();
        ClipData clipData = data.getClipData();
        if (clipData != null) {
            results = new Uri[clipData.getItemCount()];
            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                results[i] = item.getUri();
            }
        }
        if (dataString != null) {
            results = new Uri[]{Uri.parse(dataString)};
        }

        mFilePathCallbackAboveL.onReceiveValue(results);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            mWebViewWrapper.goBack();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        mWebViewWrapper.registerJsCallback();
        mWebViewWrapper.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mWebViewWrapper.onWebShow();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebViewWrapper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebViewWrapper. onDestroy();
    }
}

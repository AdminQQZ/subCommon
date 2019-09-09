package com.videoapp.libcommon.utils.loader;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.videoapp.libcommon.utils.ConvertUtils;

/**
 * @author - qqz
 * @date - 2019/5/6
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class GlideLoader implements ILoader {
    /** 选项 */
    private Options mOptions;

    @Override
    public void init(Context context, Options options) {
        mOptions = options;
    }

    @Override
    public void loadNet(ImageView view, String url, Options options) {
        load(requestManager(view.getContext()).load(url), view, options);
    }

    @Override
    public void loadNet(ImageView target, String url) {
        load(requestManager(target.getContext()).load(url), target, mOptions);
    }

    @Override
    public void loadResource(ImageView view, int resId, Options options) {
        load(requestManager(view.getContext()).load(resId), view, options);
    }

    @Override
    public void loadResource(ImageView target, int resId) {
        load(requestManager(target.getContext()).load(resId), target, mOptions);
    }

    @Override
    public void loadListener(Context context, Object url, final IOnImageLoadListener onImageLoadListener) {
        requestManager(context).asBitmap().load(url).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                if (null != e) {
                    onImageLoadListener.onFail(e.fillInStackTrace());
                } else {
                    onImageLoadListener.onFail(new NullPointerException().fillInStackTrace());
                }
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                onImageLoadListener.onSuccess(model, resource);
                return false;
            }
        }).submit();
    }


    /**
     * 请求加载器
     *
     * @param context c
     * @return 加载器
     */
    public RequestManager requestManager(Context context) {
        return Glide.with(context);
    }

    /**
     * 加载
     *
     * @param view v
     * @param imageView u
     * @param options o
     */
    private void load(RequestBuilder<?> view, ImageView imageView, Options options) {
        RequestOptions requestOptions = new RequestOptions();
        if (options == null) {
            options = Options.defaultOptions();
        }

        if (options.loadingResId != Options.RES_NONE) {
            requestOptions.placeholder(options.loadingResId);
        }

        if (options.loadErrorResId != Options.RES_NONE) {
            requestOptions.error(options.loadErrorResId);
        }
        if (options.thumbnail != Options.RES_NONE) {
            view.thumbnail(options.thumbnail);
        }

        if (options.type != Options.RES_NONE) {
            switch (options.type) {
                default:
                    break;
                case Options.TYPE_CIRCLE:
                    requestOptions.circleCrop();
                    break;
                case Options.ROUNDED_CORNERS:
                    requestOptions.transform(new com.videoapp.libcommon.ui.widget.RoundedCornersTransformation(imageView.getContext(), ConvertUtils.dp2px(5), 0));
                    break;
            }
        }
        view.apply(requestOptions).into(imageView);
    }
}

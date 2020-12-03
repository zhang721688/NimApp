
package com.zxn.netease.nimsdk.common.ui.ptr2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.ViewGroup;
import android.widget.FrameLayout;

@SuppressLint("ViewConstructor")
public abstract class LoadingLayout extends FrameLayout implements ILoadingLayout {


    public LoadingLayout(Context context) {
        super(context);
    }

    protected abstract void hideAllViews();

    protected abstract void onPull(float scaleOfLayout);

    protected abstract void pullToRefresh();

    protected abstract void refreshing();

    protected abstract void releaseToRefresh();

    protected abstract void reset();

    protected abstract void showInvisibleViews();

    protected abstract int getContentSize();

    public final void setWidth(int width) {
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.width = width;
        requestLayout();
    }


    public final void setHeight(int height) {
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.height = height;
        requestLayout();
    }

    public abstract void setTextColor(ColorStateList color);
}

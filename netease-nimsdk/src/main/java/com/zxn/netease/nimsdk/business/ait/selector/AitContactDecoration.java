package com.zxn.netease.nimsdk.business.ait.selector;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import com.zxn.netease.nimsdk.common.ui.recyclerview.decoration.DividerItemDecoration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by hzchenkang on 2017/6/22.
 */

public class AitContactDecoration extends DividerItemDecoration {

    // 不需要分割线
    private Set<Integer> ignoreDecorations;

    public AitContactDecoration(Context context, int orientation, List<Integer> ignoreDecorations) {
        super(context, orientation);
        if (ignoreDecorations != null) {
            this.ignoreDecorations = new HashSet<>(ignoreDecorations);
        }
    }

    @Override
    protected boolean needDrawDecoration(RecyclerView parent, int position) {
        if (ignoreDecorations != null) {
            int viewType = parent.getAdapter().getItemViewType(position);
            return !ignoreDecorations.contains(viewType);
        }
        return true;
    }
}

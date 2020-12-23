package com.zxn.netease.nimsdk.common.activity;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.zxn.netease.nimsdk.R;
import com.zxn.netease.nimsdk.api.wrapper.NimToolBarOptions;
import com.zxn.netease.nimsdk.common.ui.recyclerview.adapter.BaseQuickAdapter;
import com.zxn.netease.nimsdk.common.ui.recyclerview.decoration.DividerItemDecoration;
import com.zxn.netease.nimsdk.common.ui.recyclerview.holder.BaseViewHolder;
import com.zxn.netease.nimsdk.common.ui.recyclerview.listener.OnItemClickListener;

import java.util.List;

/**
 * 列表Activity抽象类
 * <p>
 * Created by huangjun on 2017/6/21.
 */
public abstract class ListActivityBase<T> extends UI {


    protected abstract String getTitleString();

    protected abstract List<T> onLoadData();

    protected abstract int onItemResId();

    protected abstract void convertItem(BaseViewHolder helper, T item);

    protected abstract void onItemClick(T item);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nim_list_activity_layout);

        // toolbar
        ToolBarOptions options = new NimToolBarOptions();
        options.titleString = getTitleString();
        setToolBar(R.id.toolbar, options);

        // RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.data_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.addOnItemTouchListener(new OnItemClickListener<Adapter>() {
            @Override
            public void onItemClick(Adapter adapter, View view, int position) {
                ListActivityBase.this.onItemClick(adapter.getItem(position));
            }
        });

        // ios style
//        OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        // adapter
        final List<T> data = onLoadData();
        final BaseQuickAdapter<T, BaseViewHolder> adapter = new Adapter(recyclerView, onItemResId(), data) {
            @Override
            protected void convert(BaseViewHolder helper, T item, int position, boolean isScrolling) {
                convertItem(helper, item);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    abstract class Adapter extends BaseQuickAdapter<T, BaseViewHolder> {

        Adapter(RecyclerView recyclerView, final int layoutId, List<T> data) {
            super(recyclerView, layoutId, data);
        }
    }
}
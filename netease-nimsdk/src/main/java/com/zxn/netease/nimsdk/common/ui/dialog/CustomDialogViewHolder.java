package com.zxn.netease.nimsdk.common.ui.dialog;

import android.util.Pair;
import android.widget.TextView;

import com.zxn.netease.nimsdk.R;
import com.zxn.netease.nimsdk.common.adapter.TViewHolder;

public class CustomDialogViewHolder extends TViewHolder {

    private TextView itemView;

    @Override
    protected int getResId() {
        return R.layout.nim_custom_dialog_list_item;
    }

    @Override
    protected void inflate() {
        itemView = view.findViewById(R.id.custom_dialog_text_view);
    }

    @Override
    protected void refresh(Object item) {
        if (item instanceof Pair<?, ?>) {
            Pair<String, Integer> pair = (Pair<String, Integer>) item;
            itemView.setText(pair.first);
            itemView.setTextColor(context.getResources().getColor(pair.second));
        }
    }

}

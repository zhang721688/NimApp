package com.netease.nim.demo.session.viewholder;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.netease.nim.demo.R;
import com.netease.nim.demo.session.extension.StickerAttachment;
import com.zxn.netease.nimsdk.business.session.emoji.StickerManager;
import com.zxn.netease.nimsdk.business.session.viewholder.MsgViewHolderBase;
import com.zxn.netease.nimsdk.business.session.viewholder.MsgViewHolderThumbBase;
import com.zxn.netease.nimsdk.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;

/**
 * Created by zhoujianghua on 2015/8/7.
 */
public class MsgViewHolderSticker extends MsgViewHolderBase {

    private ImageView baseView;

    public MsgViewHolderSticker(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_sticker;
    }

    @Override
    public void inflateContentView() {
        baseView = findViewById(R.id.message_item_sticker_image);
        baseView.setMaxWidth(MsgViewHolderThumbBase.getImageMaxEdge());
    }

    @Override
    public void bindContentView() {
        StickerAttachment attachment = (StickerAttachment) message.getAttachment();
        if (attachment == null) {
            return;
        }

        Glide.with(context)
                .load(StickerManager.getInstance().getStickerUri(attachment.getCatalog(), attachment.getChartlet()))
                .apply(new RequestOptions()
                        .error(R.drawable.nim_default_img_failed)
                        .diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(baseView);
    }

    @Override
    protected int leftBackground() {
        return R.drawable.nim_message_left_white_bg;
    }

    @Override
    protected int rightBackground() {
        return R.drawable.nim_message_right_blue_bg;
    }
}

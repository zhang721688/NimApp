package com.netease.nim.demo.session.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.netease.nim.demo.R;
import com.zxn.netease.nimsdk.business.session.module.Container;
import com.zxn.netease.nimsdk.business.session.module.ModuleProxy;
import com.zxn.netease.nimsdk.business.session.module.list.MessageListPanelEx;
import com.zxn.netease.nimsdk.common.activity.ToolBarOptions;
import com.zxn.netease.nimsdk.common.activity.UI;
import com.zxn.netease.nimsdk.api.wrapper.NimToolBarOptions;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * 消息历史查询界面
 * <p/>
 * Created by huangjun on 2015/4/17.
 */
public class MessageHistoryActivity extends UI implements ModuleProxy {

    private static final String EXTRA_DATA_ACCOUNT = "EXTRA_DATA_ACCOUNT";
    private static final String EXTRA_DATA_SESSION_TYPE = "EXTRA_DATA_SESSION_TYPE";
    private static final String EXTRA_PERSIST_CLEAR = "EXTRA_PERSIST_CLEAR";

    // context
    private SessionTypeEnum sessionType;
    private String account; // 对方帐号
    private boolean persistClear; // 是否入库被清除的消息

    private MessageListPanelEx messageListPanel;

    public static void start(Context context, String account, SessionTypeEnum sessionType, boolean persistClear) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATA_ACCOUNT, account);
        intent.putExtra(EXTRA_DATA_SESSION_TYPE, sessionType);
        intent.putExtra(EXTRA_PERSIST_CLEAR, persistClear);
        intent.setClass(context, MessageHistoryActivity.class);
        context.startActivity(intent);
    }

    /**
     * ***************************** life cycle *******************************
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = LayoutInflater.from(this).inflate(R.layout.message_history_activity, null);
        setContentView(rootView);

        ToolBarOptions options = new NimToolBarOptions();
        options.titleId = R.string.message_history_query;
        setToolBar(R.id.toolbar, options);

        onParseIntent();

        Container container = new Container(this, account, sessionType, this);
        messageListPanel = new MessageListPanelEx(container, rootView, null, true, true, persistClear);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        messageListPanel.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        messageListPanel.onDestroy();
    }

    protected void onParseIntent() {
        account = getIntent().getStringExtra(EXTRA_DATA_ACCOUNT);
        sessionType = (SessionTypeEnum) getIntent().getSerializableExtra(EXTRA_DATA_SESSION_TYPE);
        persistClear = getIntent().getBooleanExtra(EXTRA_PERSIST_CLEAR, true);
    }

    @Override
    public boolean sendMessage(IMMessage msg) {
        return false;
    }

    @Override
    public void onInputPanelExpand() {

    }

    @Override
    public void shouldCollapseInputPanel() {

    }

    @Override
    public void onItemFooterClick(IMMessage message) {

    }

    @Override
    public void onReplyMessage(IMMessage replyMsg) {

    }

    @Override
    public boolean isLongClickEnabled() {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (messageListPanel != null) {
            messageListPanel.onActivityResult(requestCode, resultCode, data);
        }
    }
}

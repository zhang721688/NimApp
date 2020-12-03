package com.zxn.netease.nimsdk.business.contact.core.viewholder;

import android.text.TextUtils;
import android.view.View;

import com.zxn.netease.nimsdk.business.contact.core.item.ContactItem;
import com.zxn.netease.nimsdk.business.contact.core.model.ContactDataAdapter;
import com.zxn.netease.nimsdk.business.contact.core.model.IContact;
import com.zxn.netease.nimsdk.impl.NimUIKitImpl;

public class OnlineStateContactHolder extends ContactHolder {

    @Override
    public void refresh(ContactDataAdapter adapter, int position, ContactItem item) {
        super.refresh(adapter, position, item);
        IContact contact = item.getContact();
        // 在线状态
        if (contact.getContactType() != IContact.Type.Friend || !NimUIKitImpl.enableOnlineState()) {
            desc.setVisibility(View.GONE);
        } else {
            String onlineStateContent = NimUIKitImpl.getOnlineStateContentProvider().getSimpleDisplay(contact.getContactId());
            if (TextUtils.isEmpty(onlineStateContent)) {
                desc.setVisibility(View.GONE);
            } else {
                desc.setVisibility(View.VISIBLE);
                desc.setText(onlineStateContent);
            }
        }
    }
}

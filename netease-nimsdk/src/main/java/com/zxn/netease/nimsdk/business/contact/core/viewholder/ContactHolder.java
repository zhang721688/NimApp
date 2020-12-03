package com.zxn.netease.nimsdk.business.contact.core.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zxn.netease.nimsdk.R;
import com.zxn.netease.nimsdk.api.NimUIKit;
import com.zxn.netease.nimsdk.business.contact.core.item.ContactItem;
import com.zxn.netease.nimsdk.business.contact.core.model.ContactDataAdapter;
import com.zxn.netease.nimsdk.business.contact.core.model.IContact;
import com.zxn.netease.nimsdk.common.ui.imageview.HeadImageView;
import com.zxn.netease.nimsdk.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.team.model.Team;

public class ContactHolder extends AbsContactViewHolder<ContactItem> {

    protected HeadImageView head;

    protected TextView name;

    protected TextView desc;

    protected RelativeLayout headLayout;

    @Override
    public void refresh(ContactDataAdapter adapter, int position, final ContactItem item) {
        // contact info
        final IContact contact = item.getContact();
        if (contact.getContactType() == IContact.Type.Friend) {
            head.loadBuddyAvatar(contact.getContactId());
        }
        name.setText(contact.getDisplayName());
        headLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (contact.getContactType() == IContact.Type.Friend) {
                    if (NimUIKitImpl.getContactEventListener() != null) {
                        NimUIKitImpl.getContactEventListener().onAvatarClick(context, item.getContact().getContactId());
                    }
                }
            }
        });
        // query result
        desc.setVisibility(View.GONE);
        /*
        TextQuery query = adapter.getQuery();
        HitInfo hitInfo = query != null ? ContactSearch.hitInfo(contact, query) : null;
        if (hitInfo != null && !hitInfo.text.equals(contact.getDisplayName())) {
            desc.setVisibility(View.VISIBLE);
        } else {
            desc.setVisibility(View.GONE);
        }
        */
    }

    @Override
    public View inflate(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.nim_contacts_item, null);
        headLayout = view.findViewById(R.id.head_layout);
        head = view.findViewById(R.id.contacts_item_head);
        name = view.findViewById(R.id.contacts_item_name);
        desc = view.findViewById(R.id.contacts_item_desc);
        return view;
    }
}

package com.samego.alic.monitor.wechat.wechatrecord.model.listener;

import com.samego.alic.monitor.wechat.wechatrecord.bean.Contact;

import java.util.List;

public interface OnGetContactListener {
    void successful(List<Contact> contacts);

    void fail();
}

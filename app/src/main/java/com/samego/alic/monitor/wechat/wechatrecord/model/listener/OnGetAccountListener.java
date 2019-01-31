package com.samego.alic.monitor.wechat.wechatrecord.model.listener;

import com.samego.alic.monitor.wechat.wechatrecord.bean.Account;

public interface OnGetAccountListener {
    void successful(Account account);

    void fail();
}

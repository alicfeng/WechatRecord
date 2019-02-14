package com.samego.alic.monitor.wechat.wechatrecord.utils;

import com.samego.alic.monitor.wechat.wechatrecord.configure.ApplicationCfg;

public class DevLog {
    public static void i(String message) {
        if (ApplicationCfg.DEBUG) {
            System.out.println("alicfeng Log -> " + message);
        }
    }
}

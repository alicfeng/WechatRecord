package com.samego.alic.monitor.wechat.wechatrecord.utils;

import com.samego.alic.monitor.wechat.wechatrecord.configure.AppConfig;

public class DevLog {
    public static void i(String message) {
        if (AppConfig.DEBUG) {
            System.out.println("alicfeng Log -> " + message);
        }
    }
}

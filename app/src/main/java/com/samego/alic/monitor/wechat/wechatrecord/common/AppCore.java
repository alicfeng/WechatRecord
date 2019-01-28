package com.samego.alic.monitor.wechat.wechatrecord.common;

import android.content.Context;

import com.samego.alic.monitor.wechat.wechatrecord.configure.WechatPackage;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SharedPreferencesUtil;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SystemUtil;

public class AppCore {
    /**
     * 初始化核心配置
     *
     * @param context 上下文
     * @return true-ok false 处理失败
     */
    public static boolean initCoreConfigure(Context context) {
        String imei = SystemUtil.imei(context);
        String uid = WechatPackage.uid(context);
        String dbPath = WechatPackage.getDBFilePath(context,uid);
        if (null == imei || null == uid) {
            return false;
        }
        String password = WechatPackage.password(imei, uid);
        SharedPreferencesUtil.set(context, "wx_psd", password);
        SharedPreferencesUtil.set(context, "wx_uid", uid);
        SharedPreferencesUtil.set(context, "wx_db", dbPath);
        SharedPreferencesUtil.set(context, "imei", imei);
        return true;
    }
}

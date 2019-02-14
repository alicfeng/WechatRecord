package com.samego.alic.monitor.wechat.wechatrecord.common;

import android.content.Context;

import com.samego.alic.monitor.wechat.wechatrecord.libs.WechatPackage;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SharedPreferencesUtil;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SystemUtil;

public class AppCore {
    // 同步频率(ms)
    public static final int SYNC_FREQUENCY = 1000 * 60;//1min

    // 时间段数据(ms) 获取多少微妙的聊天内容数据信息
    public static final long DATA_TIME = 0;//1000 * 60 * 60 * 12;

    /**
     * 初始化核心配置
     *
     * @param context 上下文
     * @return true-ok false 处理失败
     */
    public static boolean initCoreConfigure(Context context) {
        String imei = SystemUtil.imei(context);
        String uid = WechatPackage.uid();
        String dbPath = WechatPackage.getDBFilePath(context, uid);
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

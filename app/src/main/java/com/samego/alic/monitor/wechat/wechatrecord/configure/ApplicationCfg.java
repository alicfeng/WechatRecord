package com.samego.alic.monitor.wechat.wechatrecord.configure;

public class ApplicationCfg {
    // 调试模式
    public static final boolean DEBUG = true;

    // 同步频率(ms)
    public static final int SYNC_FREQUENCY = 1000 * 60;// 1min

    // 时间段数据(ms) 获取多少微妙的聊天内容数据信息
    public static final long DATA_TIME = 0;// 1000 * 60 * 60 * 12;
}

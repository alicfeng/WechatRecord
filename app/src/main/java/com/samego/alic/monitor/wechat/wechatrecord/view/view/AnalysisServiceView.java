package com.samego.alic.monitor.wechat.wechatrecord.view.view;

public interface AnalysisServiceView {
    /**
     * 服务正在开启
     */
    void starting();

    /**
     * 网络不可用
     */
    void networkUnavailability();

    /**
     * 初始化相关
     */
    void init();

    /**
     * 获取数据异常
     */
    void getDataFail();

    /**
     * 获取电源锁
     */
    void acquireWakeLock();


    /**
     * 释放设备电源锁
     */
    void releaseWakeLock();
}

package com.samego.alic.monitor.wechat.wechatrecord.view.view;

public interface MainActivityView {

    /**
     * 初始化
     */
    void init();

    /**
     * 检查核心服务
     */
    void checkCoreService();

    /**
     * 启动核心服务
     */
    void startCoreService();
}

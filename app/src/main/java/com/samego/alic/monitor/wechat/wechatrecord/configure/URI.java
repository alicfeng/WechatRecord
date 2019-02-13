package com.samego.alic.monitor.wechat.wechatrecord.configure;

public class URI {
    // 基础域
    private static final String BASE_DOMAIN = "http://192.168.2.119:8081";

    // 账号数据同步URI
    public static final String URI_ACCOUNT_SYNC = BASE_DOMAIN + "/platform/chatRecord/sync";

    public static final String URI_FILE_UPLOAD = BASE_DOMAIN + "/platform/file/upload";

    // 接口标识
    public static final int INTERFACE_SIGN_ACCOUNT = 1;// 账号同步sign
    public static final int INTERFACE_SIGN_CONTACT = 2;// 联系人同步sign
    public static final int INTERFACE_SIGN_CHATREACORD = 3;// 聊天记录同步sign
}

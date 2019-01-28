package com.samego.alic.monitor.wechat.wechatrecord.configure;

import android.content.Context;

import com.samego.alic.monitor.wechat.wechatrecord.utils.MD5Util;
import com.samego.alic.monitor.wechat.wechatrecord.utils.ShellUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class WechatPackage {
    private static final String PACKAGE_PATH = "/data/data/com.tencent.mm/";
    // auth_info_key_prefs.xml文件路径
    private static final String AUTH_INFO_PATH = "/data/data/com.tencent.mm/shared_prefs/auth_info_key_prefs.xml";
    private static final String DB_NAME = "EnMicroMsg.db";


    /**
     * 获取聊天数据库路径
     *
     * @param context 上下文
     * @param uid     uid
     * @return String
     */
    public static String getDBFilePath(Context context, String uid) {
        return PACKAGE_PATH + "MicroMsg/" + MD5Util.md5("mm" + uid) + "/" + DB_NAME;
    }

    /**
     * 获取文件的uid
     * 返回空说明微信没有登录Ω
     *
     * @param context 上下文
     * @return 微信的uid
     */
    public static String uid(Context context) {
        ShellUtil.command("chmod -R 777 " + AUTH_INFO_PATH);
        Document document = null;
        String uid = null;
        try {
            document = Jsoup.parse(new File(AUTH_INFO_PATH), "UTF-8");
            Elements elements = document.getElementsByTag("int");
            for (Element element : elements) {
                if ("_auth_uin".equals(element.attr("name"))) {
                    uid = element.attr("value");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uid;
    }

    /**
     * 获取微信的本地数据库密码
     *
     * @param imei 移动设备的唯一编码
     * @param uid  微信的uid
     * @return 本地数据路的密码
     */
    public static String password(String imei, String uid) {
        return (Objects.requireNonNull(MD5Util.md5(imei + uid))).substring(0, 7).toLowerCase();
    }
}

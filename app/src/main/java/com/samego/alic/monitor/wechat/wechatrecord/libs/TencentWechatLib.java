package com.samego.alic.monitor.wechat.wechatrecord.libs;

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

public class TencentWechatLib {
    private static final String PACKAGE_PATH = "/data/data/com.tencent.mm/";
    // auth_info_key_prefs.xml文件路径
    private static final String AUTH_INFO_PATH = "/data/data/com.tencent.mm/shared_prefs/auth_info_key_prefs.xml";
    private static final String DB_NAME = "EnMicroMsg.db";
    private static final String WX_FILE_PATH = "/storage/emulated/0/Tencent/Micromsg/";// 微信保存聊天时语音、图片、视频文件的地址


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
     * @return 微信的uid
     */
    public static String uid() {
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

    /**
     * 获取图片路径
     *
     * @param name 图片名称
     * @return 图片路径
     */
    public static String imagePath(String name) {
        try {
            return WX_FILE_PATH + MD5Util.md5("mm" + uid()) + "/image2/" + name.substring(0, 2) + "/" + name.substring(2, 4) + "/" + name;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取音频路径
     *
     * @param name 名称
     * @return 音频路径
     */
    public static String voicePath(String name) {
        String nameEnc = MD5Util.md5(name);
        if (nameEnc == null || name == null) {
            return null;
        }
        try {
            return WX_FILE_PATH + MD5Util.md5("mm" + uid()) + "/voice2/" + nameEnc.substring(0, 2) + "/" + nameEnc.substring(2, 4) + "/" + "msg_" + name + ".amr";
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取视频路径
     *
     * @param name 名称
     * @return 视频路径
     */
    public static String videoPath(String name) {
        String nameEnc = MD5Util.md5(name);
        if (nameEnc == null || name == null) {
            return null;
        }
        try {
            return WX_FILE_PATH + MD5Util.md5("mm" + uid()) + "/video/"+ name + ".mp4";
        } catch (Exception e) {
            return null;
        }
    }
}

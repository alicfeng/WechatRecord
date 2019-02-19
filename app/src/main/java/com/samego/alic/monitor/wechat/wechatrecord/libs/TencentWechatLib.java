package com.samego.alic.monitor.wechat.wechatrecord.libs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.blankj.utilcode.util.FileUtils;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.samego.alic.monitor.wechat.wechatrecord.utils.DevLog;
import com.samego.alic.monitor.wechat.wechatrecord.utils.MD5Util;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SharedPreferencesUtil;
import com.samego.alic.monitor.wechat.wechatrecord.utils.ShellUtil;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SystemUtil;
import com.samego.alic.monitor.wechat.wechatrecord.view.activity.MainActivity;

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
     * 初始化核心配置
     *
     * @param context 上下文
     * @return true-ok false 处理失败
     */
    public static boolean initWechatConfigure(Activity activity, Context context) {
        String imei = SystemUtil.imei(context);
        DevLog.i("imei" + imei);
        String uid = TencentWechatLib.uid(activity);
        String dbPath = TencentWechatLib.getDBFilePath(context, uid);
        if (null == imei || null == uid) {
            return false;
        }
        String password = TencentWechatLib.password(imei, uid);
        SharedPreferencesUtil.set(context, "wx_psd", password);
        SharedPreferencesUtil.set(context, "wx_uid", uid);
        SharedPreferencesUtil.set(context, "wx_db", dbPath);
        SharedPreferencesUtil.set(context, "imei", imei);
        return true;
    }

    /**
     * 获取聊天数据库路径
     *
     * @param context 上下文
     * @param uid     uid
     * @return String
     */
    private static String getDBFilePath(Context context, String uid) {
        return PACKAGE_PATH + "MicroMsg/" + MD5Util.md5("mm" + uid) + "/" + DB_NAME;
    }

    /**
     * 获取文件的uid
     * 返回空说明微信没有登录Ω
     *
     * @return 微信的uid
     */
    private static String uid(Activity activity) {
        String[] permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
        };
        PermissionsUtil.requestPermission(activity, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                DevLog.i("permissionGranted");
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
                DevLog.i("permissionDenied");
            }
        }, permissions);
        ShellUtil.command("chmod -R 777 " + AUTH_INFO_PATH);
        Document document = null;
        FileUtils.copyFile(AUTH_INFO_PATH, "/storage/emulated/0/auth_info_key_prefs.xml");
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
    private static String password(String imei, String uid) {
        return (Objects.requireNonNull(MD5Util.md5(imei + uid))).substring(0, 7).toLowerCase();
    }

    /**
     * 获取图片路径
     *
     * @param name 图片名称
     * @return 图片路径
     */
    public static String imagePath(Context context, String name) {
        String uid = SharedPreferencesUtil.get(context, "wx_uid", null);
        if ( uid == null) {
            return null;
        }
        try {
            return WX_FILE_PATH + MD5Util.md5("mm" + uid) + "/image2/" + name.substring(0, 2) + "/" + name.substring(2, 4) + "/" + name;
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
    public static String voicePath(Context context, String name) {
        String nameEnc = MD5Util.md5(name);
        String uid = SharedPreferencesUtil.get(context, "wx_uid", null);
        if (nameEnc == null || name == null || uid == null) {
            return null;
        }
        try {
            return WX_FILE_PATH + MD5Util.md5("mm" + uid) + "/voice2/" + nameEnc.substring(0, 2) + "/" + nameEnc.substring(2, 4) + "/" + "msg_" + name + ".amr";
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
    public static String videoPath(Context context,String name) {
        String nameEnc = MD5Util.md5(name);
        String uid = SharedPreferencesUtil.get(context, "wx_uid", null);
        if (nameEnc == null || name == null || uid == null) {
            return null;
        }
        try {
            return WX_FILE_PATH + MD5Util.md5("mm" + uid) + "/video/" + name + ".mp4";
        } catch (Exception e) {
            return null;
        }
    }
}

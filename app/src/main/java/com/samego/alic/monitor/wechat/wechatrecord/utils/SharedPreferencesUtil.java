package com.samego.alic.monitor.wechat.wechatrecord.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.samego.alic.monitor.wechat.wechatrecord.service.CoreService;

public class SharedPreferencesUtil {
    private CoreService analysisService = null;

    /**
     * 储存
     *
     * @param context 上下文
     * @param key     键名
     * @param value   键值
     */
    public static void set(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 获取
     *
     * @param context 上下文
     * @param key     键名
     * @param value   默认值
     * @return
     */
    public static String get(Context context, String key, @Nullable String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, value);
    }
}

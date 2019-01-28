package com.samego.alic.monitor.wechat.wechatrecord.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.samego.alic.monitor.wechat.wechatrecord.common.Constant;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SharedPreferencesUtil;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import java.util.ArrayList;

public class AnalysisService extends Service {
    //@androidx.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.backup();

        SQLiteDatabase.loadLibs(this);

        SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
            @Override
            public void preKey(SQLiteDatabase database) {

            }

            @Override
            public void postKey(SQLiteDatabase database) {
                database.rawExecSQL("PRAGMA cipher_migrate;"); // 兼容2.0的数据库
            }
        };

        String file = this.getFilesDir().getPath() + "/analysis.db";
        String password = SharedPreferencesUtil.get(this, "wx_psd", null);
       try {
            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(file, password, null, hook);
            Log.i("alicfeng", "打开数据库成功");
           Cursor cursor = database.rawQuery("select value from userinfo",null);
          // Cursor cursor = database.query("userinfo", new String[]{"value"}, null, null, null, null, null);
           //利用游标遍历所有数据对象
           while(cursor.moveToNext()) {
               String value = cursor.getString(cursor.getColumnIndex("value"));
               Log.i("alicfeng", value);
           }
            cursor.close();
        } catch (Exception e) {
            Log.e("alicfeng", e.getMessage());
            Log.e("alicfeng", "打开数据库失败");
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 复制数据库
     */
    public void backup() {
        String sourcePath = SharedPreferencesUtil.get(this, Constant.SP_WECHAT_DB_NAME, null);
        String targetPath = this.getFilesDir().getPath() + "/analysis.db";
        FileUtils.copyFile(sourcePath, targetPath);
    }
}

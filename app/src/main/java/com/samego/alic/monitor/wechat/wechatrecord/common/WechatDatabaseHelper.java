package com.samego.alic.monitor.wechat.wechatrecord.common;

import android.content.Context;
import android.util.Log;

import com.samego.alic.monitor.wechat.wechatrecord.utils.SharedPreferencesUtil;

import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

public class WechatDatabaseHelper {
    public static SQLiteDatabase openOrCreateDatabase(Context context) throws SQLException {
        SQLiteDatabase.loadLibs(context);

        SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
            @Override
            public void preKey(SQLiteDatabase database) {

            }

            @Override
            public void postKey(SQLiteDatabase database) {
                database.rawExecSQL("PRAGMA cipher_migrate;"); // 兼容2.0的数据库
            }
        };

        String file = context.getFilesDir().getPath() + "/analysis.db";
        Log.i("alicfeng",file);
        String password = SharedPreferencesUtil.get(context, "wx_psd", null);
        Log.i("password",password);
        return SQLiteDatabase.openOrCreateDatabase(file, password, null, hook);
    }
}

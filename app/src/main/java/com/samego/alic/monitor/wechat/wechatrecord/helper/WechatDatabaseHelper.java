package com.samego.alic.monitor.wechat.wechatrecord.helper;

import android.content.Context;

import com.samego.alic.monitor.wechat.wechatrecord.utils.SharedPreferencesUtil;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

public class WechatDatabaseHelper {
    public static SQLiteDatabase connect(Context context) throws SQLException {
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
        String password = SharedPreferencesUtil.get(context, "wx_psd", null);
        return SQLiteDatabase.openOrCreateDatabase(file, password, null, hook);
    }

    /**
     * 关闭数据库
     *
     * @param database 数据库句柄
     */
    public static void close(SQLiteDatabase database) {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    /**
     * 关闭数据库
     *
     * @param database 数据库句柄
     * @param cursor   cursor句柄
     */
    public static void close(SQLiteDatabase database, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}

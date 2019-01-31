package com.samego.alic.monitor.wechat.wechatrecord.model;

import android.content.Context;
import android.util.Log;

import com.samego.alic.monitor.wechat.wechatrecord.bean.Account;
import com.samego.alic.monitor.wechat.wechatrecord.bean.Contact;
import com.samego.alic.monitor.wechat.wechatrecord.model.listener.OnGetAccountListener;
import com.samego.alic.monitor.wechat.wechatrecord.model.listener.OnGetContactListener;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SharedPreferencesUtil;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import java.util.ArrayList;
import java.util.List;

public class AccountModelImpl implements AccountModel {
    @Override
    public void getAccount(Context context, OnGetAccountListener listener) {
        try {
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
            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(file, password, null, hook);
            Account account = new Account();
            String[] binds = new String[]{"2", "4"};
            Cursor cursor = database.rawQuery("select value from userinfo where id = ? or id = ?;", new String[]{"2", "33", "4", "0"});
            while (cursor.moveToNext()) {
                Log.i("alicfeng",cursor.getString(cursor.getColumnIndex("value")));
            }
            cursor.close();
            database.close();
        } catch (SQLException e) {
            Log.e("alicfeng", e.getMessage());
            listener.fail();
        }
    }

    @Override
    public void syncContactMessage(Context context, Account account) {

    }
}

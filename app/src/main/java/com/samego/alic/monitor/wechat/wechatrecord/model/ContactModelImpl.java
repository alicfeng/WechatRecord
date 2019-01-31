package com.samego.alic.monitor.wechat.wechatrecord.model;

import android.content.Context;
import android.util.Log;

import com.samego.alic.monitor.wechat.wechatrecord.bean.Contact;
import com.samego.alic.monitor.wechat.wechatrecord.model.listener.OnGetContactListener;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SharedPreferencesUtil;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import java.util.ArrayList;
import java.util.List;

public class ContactModelImpl implements ContactModel {
    @Override
    public void getContactList(Context context, OnGetContactListener listener) {
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
            List<Contact> contactList = new ArrayList<>();
            // verifyFlag!=0：公众号等类型 type=33：微信功能 type=2：未知 type=4：非好友
            // 一般公众号原始ID开头都是gh_
            // 群ID的结尾是@chatroom
            Cursor cursor = database.rawQuery("select * from rcontact where " +
                    "type != ? and " +
                    "type != ? and " +
                    "type != ? and " +
                    "verifyFlag = ? and " +
                    "username not like 'gh_%' and " +
                    "username not like '%@chatroom';", new String[]{"2", "33", "4", "0"});
            while (cursor.moveToNext()) {
                Contact contact = new Contact();
                contact.setUsername(cursor.getString(cursor.getColumnIndex("username")));
                contact.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
                contact.setType(cursor.getString(cursor.getColumnIndex("type")));
                contactList.add(contact);
            }
            cursor.close();
            database.close();
            listener.successful(contactList);
        } catch (SQLException e) {
            Log.e("alicfeng", e.getMessage());
            listener.fail();
        }
    }

    @Override
    public void syncContactMessage(Context context, List<Contact> contactList) {

    }
}

package com.samego.alic.monitor.wechat.wechatrecord.model;

import android.content.Context;
import android.util.Log;

import com.samego.alic.monitor.wechat.wechatrecord.bean.Account;
import com.samego.alic.monitor.wechat.wechatrecord.bean.ChatRecord;
import com.samego.alic.monitor.wechat.wechatrecord.model.listener.OnGetChatRecordListener;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SharedPreferencesUtil;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import java.util.ArrayList;
import java.util.List;

public class ChatRecordModelImpl implements ChatRecordModel {
    @Override
    public void getChatRecord(Context context, OnGetChatRecordListener listener) {
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
            List<ChatRecord> chatRecordList = new ArrayList<>();
            ChatRecord chatRecord = new ChatRecord();
            String[] binds = new String[]{"2", "4"};
            Cursor cursor = database.rawQuery("select * from message where talker not like 'gh_%';", null);
            while (cursor.moveToNext()) {
                chatRecord.setType(cursor.getString(cursor.getColumnIndex("type")));
                chatRecord.setContent(cursor.getString(cursor.getColumnIndex("content")));

                Log.i("alicfeng - chatRecord", String.valueOf(cursor.getInt(cursor.getColumnIndex("msgId"))));
                Log.i("alicfeng - chatRecord", String.valueOf(cursor.getInt(cursor.getColumnIndex("msgSvrId"))));
                Log.i("alicfeng - chatRecord", cursor.getString(cursor.getColumnIndex("type")));
                Log.i("alicfeng - chatRecord", cursor.getString(cursor.getColumnIndex("content")));
                Log.i("alicfeng - chatRecord", cursor.getString(cursor.getColumnIndex("isSend")));
                Log.i("alicfeng - chatRecord", cursor.getString(cursor.getColumnIndex("createTime")));
                Log.i("alicfeng - chatRecord", cursor.getString(cursor.getColumnIndex("talker")));
                if(chatRecord.getType().equals("34")){
                    Log.w("alicfeng - chatRecord", cursor.getString(cursor.getColumnIndex("imgPath")));
                }
                Log.i("alicfeng - chatRecord", "--------------------------");
                //Log.i("alicfeng - chatRecord", cursor.getString(cursor.getColumnIndex("imgPath")));


                chatRecordList.add(chatRecord);
            }
            cursor.close();
            database.close();
            listener.successful(chatRecordList);
        } catch (SQLException e) {
            Log.e("alicfeng", e.getMessage());
            listener.fail();
        }
    }

    @Override
    public void syncChatRecordMessage(final Context context, final List<ChatRecord> chatRecordList) {

    }
}

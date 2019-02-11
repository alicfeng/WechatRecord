package com.samego.alic.monitor.wechat.wechatrecord.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.samego.alic.monitor.wechat.wechatrecord.common.Constant;
import com.samego.alic.monitor.wechat.wechatrecord.presenter.AccountPresenter;
import com.samego.alic.monitor.wechat.wechatrecord.presenter.ChatRecordPresenter;
import com.samego.alic.monitor.wechat.wechatrecord.presenter.ContactPresenter;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SharedPreferencesUtil;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import java.util.Timer;
import java.util.TimerTask;


public class AnalysisService extends Service {
    private ContactPresenter contactPresenter;
    private AccountPresenter accountPresenter;
    private ChatRecordPresenter chatRecordPresenter;

    //@androidx.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.contactPresenter = new ContactPresenter(this);
        this.accountPresenter = new AccountPresenter(this);
        this.chatRecordPresenter = new ChatRecordPresenter(this);
        this.backup();
        Log.i("alicfeng", "打开数据库成功");
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
        Log.i("alicfeng", password + "----" + file);
        try {
            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(file, password, null, hook);
            Cursor cursor = database.rawQuery("PRAGMA table_info(message);", null);
            Log.i("alicfeng", "execute this " + cursor.getCount());
            while (cursor.moveToNext()) {
                Log.i("alicfeng", cursor.getString(cursor.getColumnIndex("name")));
                Log.i("alicfeng", cursor.getString(cursor.getColumnIndex("type")));
            }
            Log.i("alicfeng", "execute this two");
            cursor.close();
            database.close();
        } catch (Exception e) {
            Log.e("alicfeng", e.getMessage());
            e.printStackTrace();
            Log.e("alicfeng", "打开数据库失败");
        }

        syncTimer();
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


    /**
     * 计划定时器
     * 服务于同步助手
     */
    private void syncTimer(){
        Timer timer = new Timer(true);
        TimerTask task = new TimerTask() {
            public void run() {
                backup();
                accountPresenter.syncAccount();
                contactPresenter.syncContactList();
                chatRecordPresenter.syncChatRecord();
            }
        };
        timer.schedule(task, 0, 1000*10);
    }
}

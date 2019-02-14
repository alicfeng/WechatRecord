package com.samego.alic.monitor.wechat.wechatrecord.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.samego.alic.monitor.wechat.wechatrecord.configure.TN;
import com.samego.alic.monitor.wechat.wechatrecord.utils.DevLog;


public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TN.MESSAGE_UPLOAD_RECORD + "(" +
                "id INTEGER NOT NULL PRIMARY KEY," +
                "type INTEGER," +
                "msg_id VARCHAR(32)," +
                "resource VARCHAR(256)" +
                ")"
        );
        DevLog.i("资源上传记录表新建成功");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DevLog.i("db upgrade");
    }

    /**
     * 关闭数据库1
     *
     * @param writeDatabase writeDatabase
     * @param readDatabase  readDatabase
     * @param cursor        cursor
     */
    public static void closeDatabase(SQLiteDatabase writeDatabase, SQLiteDatabase readDatabase, Cursor cursor) {
        if (cursor != null)
            cursor.close();
        if (writeDatabase != null)
            writeDatabase.close();
        if (readDatabase != null)
            readDatabase.close();
    }

    /**
     * 关闭数据库2
     *
     * @param writeOrReadDatabase writeOrReadDatabase
     * @param cursor              cursor
     */
    public static void closeDatabase(SQLiteDatabase writeOrReadDatabase, Cursor cursor) {
        if (cursor != null && !cursor.isClosed())
            cursor.close();
        if (writeOrReadDatabase != null && writeOrReadDatabase.isOpen())
            writeOrReadDatabase.close();
    }

    /**
     * 关闭数据库2
     *
     * @param writeOrReadDatabase writeOrReadDatabase
     */
    public static void closeDatabase(SQLiteDatabase writeOrReadDatabase) {
        if (writeOrReadDatabase != null && writeOrReadDatabase.isOpen())
            writeOrReadDatabase.close();
    }
}


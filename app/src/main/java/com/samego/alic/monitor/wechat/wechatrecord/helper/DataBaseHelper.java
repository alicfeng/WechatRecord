package com.samego.alic.monitor.wechat.wechatrecord.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.samego.alic.monitor.wechat.wechatrecord.utils.DevLog;

/**
 * 数据库SQLite Helper
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    // 数据库名称 samegoChat
    public static final String DB_NAME = "samegoChat";

    // 聊天记录上传记录表
    public static final String TABLE_MESSAGE_RESOURCE_RECORD = "message_resource_record";

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DataBaseHelper.TABLE_MESSAGE_RESOURCE_RECORD + "(" +
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
        if (cursor != null) {
            cursor.close();
        }
        if (writeDatabase != null) {
            writeDatabase.close();
        }
        if (readDatabase != null) {
            readDatabase.close();
        }
    }

    /**
     * 关闭数据库2
     *
     * @param writeOrReadDatabase writeOrReadDatabase
     * @param cursor              cursor
     */
    public static void closeDatabase(SQLiteDatabase writeOrReadDatabase, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (writeOrReadDatabase != null && writeOrReadDatabase.isOpen()) {
            writeOrReadDatabase.close();
        }
    }

    /**
     * 关闭数据库2
     *
     * @param writeOrReadDatabase writeOrReadDatabase
     */
    public static void closeDatabase(SQLiteDatabase writeOrReadDatabase) {
        if (writeOrReadDatabase != null && writeOrReadDatabase.isOpen()) {
            writeOrReadDatabase.close();
        }
    }
}


package com.samego.alic.monitor.wechat.wechatrecord.utils;


import android.database.sqlite.SQLiteDatabase;

import com.samego.alic.monitor.wechat.wechatrecord.helper.DataBaseHelper;

import java.util.concurrent.atomic.AtomicInteger;


public class DatabaseManager {
    private AtomicInteger mOpenCounter = new AtomicInteger();
    private static DatabaseManager instance;
    private static DataBaseHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;


    public static synchronized void initializeInstance(DataBaseHelper helper) {
        if (instance == null) {
            instance = new DatabaseManager();
            mDatabaseHelper = helper;
        }
    }

    public static synchronized DatabaseManager getInstance(DataBaseHelper helper) {
        if (instance == null) {
            initializeInstance(helper);
        }
        return instance;
    }

    public synchronized SQLiteDatabase getWritableDatabase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized SQLiteDatabase getReadableDatabase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mDatabase = mDatabaseHelper.getReadableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {

        if (mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            mDatabase.close();
        }
    }
}


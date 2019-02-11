package com.samego.alic.monitor.wechat.wechatrecord.model;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.samego.alic.monitor.wechat.wechatrecord.bean.Account;
import com.samego.alic.monitor.wechat.wechatrecord.bean.RequestStructure;
import com.samego.alic.monitor.wechat.wechatrecord.bean.ResponseStructure;
import com.samego.alic.monitor.wechat.wechatrecord.common.ResponseCode;
import com.samego.alic.monitor.wechat.wechatrecord.common.URI;
import com.samego.alic.monitor.wechat.wechatrecord.model.listener.OnGetAccountListener;
import com.samego.alic.monitor.wechat.wechatrecord.utils.OkHttpManager;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SharedPreferencesUtil;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

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
            String[] accountValue = new String[2];
            Cursor cursor = database.rawQuery("select value from userinfo where id = ? or id = ?;", binds);
            while (cursor.moveToNext()) {
                Log.i("alicfeng", cursor.getString(cursor.getColumnIndex("value")));
                accountValue[cursor.getPosition()] = cursor.getString(cursor.getColumnIndex("value"));
            }
            account.setUsername(accountValue[0]);
            account.setNickname(accountValue[1]);
            cursor.close();
            database.close();
            listener.successful(account);
        } catch (SQLException e) {
            Log.e("alicfeng", e.getMessage());
            listener.fail();
        }
    }

    @Override
    public void syncAccountMessage(final Context context, final Account account) {
        // 头部header
        Map<String, Object> header = new HashMap<>();
        header.put("companyId", "tsb");

        // 主体body
        Map<String, Object> body = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        message.put("nickname", account.getNickname());
        body.put("type", 1);
        body.put("username", account.getUsername());
        body.put("message", message);

        RequestStructure structure = new RequestStructure();
        structure.setHeader(header);
        structure.setBody(body);
        final Gson gson = new Gson();
        String json = gson.toJson(structure);
        //post异步处理 结果是我的装备
        RequestBody requestBody = FormBody.create(OkHttpManager.MEDIA_TYPE_JSON, json);
        Log.i("alicfeng", json);
        OkHttpManager.postEnqueueAsync(URI.URI_ACCOUNT_SYNC, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("alicfeng - onFailure", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("alicfeng", result);
                ResponseStructure responseStructure = gson.fromJson(result, new TypeToken<ResponseStructure>() {
                }.getType());
                if (ResponseCode.SUCCESS.equals(responseStructure.getResultCode())) {
                    SharedPreferencesUtil.set(context, "username", account.getUsername());
                }
            }
        });
    }

    public static void main(String[] args) {
        System.out.println("f");
        Account account = new Account();
        account.setUsername("ff");
        account.setNickname("gg");
        // 头部header
        Map<String, Object> header = new HashMap<>();
        header.put("companyId", "tsb");

        // 主体body
        Map<String, Object> body = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        message.put("nickname", account.getNickname());
        body.put("type", 1);
        body.put("username", account.getUsername());
        body.put("message", message);

        RequestStructure structure = new RequestStructure();
        structure.setHeader(header);
        structure.setBody(body);
        Gson gson = new Gson();
        String json = gson.toJson(structure);
        System.out.println(json);


    }
}

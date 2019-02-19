package com.samego.alic.monitor.wechat.wechatrecord.model;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.samego.alic.monitor.wechat.wechatrecord.bean.Contact;
import com.samego.alic.monitor.wechat.wechatrecord.bean.RequestStructure;
import com.samego.alic.monitor.wechat.wechatrecord.bean.ResponseStructure;
import com.samego.alic.monitor.wechat.wechatrecord.common.ResponseCode;
import com.samego.alic.monitor.wechat.wechatrecord.configure.URI;
import com.samego.alic.monitor.wechat.wechatrecord.helper.OkHttpManager;
import com.samego.alic.monitor.wechat.wechatrecord.helper.WechatDatabaseHelper;
import com.samego.alic.monitor.wechat.wechatrecord.model.listener.OnGetContactListener;
import com.samego.alic.monitor.wechat.wechatrecord.utils.DevLog;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SharedPreferencesUtil;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ContactModelImpl implements ContactModel {
    @Override
    public void getContactList(Context context, OnGetContactListener listener) {
        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            database = WechatDatabaseHelper.connect(context);
            List<Contact> contactList = new ArrayList<>();
            // verifyFlag!=0：公众号等类型 type=33：微信功能 type=2：未知 type=4：非好友
            // 一般公众号原始ID开头都是gh_
            // 群ID的结尾是@chatroom
            cursor = database.rawQuery("select * from rcontact where " +
                    "type != ? and " +
                    "type != ? and " +
                    "type != ? and " +
                    "type != ? and " +
                    "verifyFlag = ? and " +
                    "username not like 'gh_%' and " +
                    "username not like '%@chatroom';", new String[]{"2", "33", "4", "0", "0"});
            while (cursor.moveToNext()) {
                Contact contact = new Contact();
                contact.setUsername(cursor.getString(cursor.getColumnIndex("username")));
                contact.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
                contact.setType(cursor.getString(cursor.getColumnIndex("type")));
                contactList.add(contact);
            }
            listener.successful(contactList);
        } catch (SQLException e) {
            Log.e("alicfeng", e.getMessage());
            listener.fail();
        } finally {
            WechatDatabaseHelper.close(database, cursor);
        }
    }

    @Override
    public void syncContactMessage(Context context, List<Contact> contactList) {
        String username = SharedPreferencesUtil.get(context, "username", null);

        if (null == username) {
            return;
        }
        // 头部header
        Map<String, Object> header = new HashMap<>();
        header.put("companyId", "tsb");

        // 主体body
        Map<String, Object> body = new HashMap<>();
        List message = new ArrayList();

        for (Contact contact : contactList) {
            if ("".equals(contact.getUsername()) || null == contact.getUsername() || "".equals(contact.getNickname()) || null == contact.getNickname()) {
                continue;
            }
            Map<String, String> item = new HashMap<>();
            item.put("nickname", contact.getNickname());
            item.put("username", contact.getUsername());
            item.put("type", contact.getType());
            message.add(item);
        }

        body.put("type", URI.INTERFACE_SIGN_CONTACT);
        body.put("username", username);
        body.put("message", message);

        RequestStructure structure = new RequestStructure();
        structure.setHeader(header);
        structure.setBody(body);
        final Gson gson = new Gson();
        final String json = gson.toJson(structure);
        //post异步处理 结果是我的装备
        RequestBody requestBody = FormBody.create(OkHttpManager.MEDIA_TYPE_JSON, json);
        Log.i("alicfeng", json);

        OkHttpManager.postEnqueueAsync(URI.URI_MESSAGE_SYNC, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("alicfeng - onFailure", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                ResponseStructure responseStructure = gson.fromJson(result, new TypeToken<ResponseStructure>() {
                }.getType());
                if (ResponseCode.SUCCESS.equals(responseStructure.getResultCode())) {
                    Log.i("alicfeng", "联系人同步成功");
                }
            }
        });
    }
}

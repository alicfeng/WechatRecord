package com.samego.alic.monitor.wechat.wechatrecord.model;

import android.content.ContentValues;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.samego.alic.monitor.wechat.wechatrecord.bean.ChatRecord;
import com.samego.alic.monitor.wechat.wechatrecord.bean.RequestStructure;
import com.samego.alic.monitor.wechat.wechatrecord.bean.ResponseStructure;
import com.samego.alic.monitor.wechat.wechatrecord.common.Application;
import com.samego.alic.monitor.wechat.wechatrecord.common.ResponseCode;
import com.samego.alic.monitor.wechat.wechatrecord.configure.ApplicationCfg;
import com.samego.alic.monitor.wechat.wechatrecord.configure.URI;
import com.samego.alic.monitor.wechat.wechatrecord.helper.DataBaseHelper;
import com.samego.alic.monitor.wechat.wechatrecord.helper.OkHttpManager;
import com.samego.alic.monitor.wechat.wechatrecord.helper.WechatDatabaseHelper;
import com.samego.alic.monitor.wechat.wechatrecord.libs.TencentWechatLib;
import com.samego.alic.monitor.wechat.wechatrecord.model.listener.OnGetChatRecordListener;
import com.samego.alic.monitor.wechat.wechatrecord.utils.DatabaseManager;
import com.samego.alic.monitor.wechat.wechatrecord.utils.DevLog;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SharedPreferencesUtil;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatRecordModelImpl implements ChatRecordModel {
    private UploadModel uploadModel;

    public ChatRecordModelImpl() {
        this.uploadModel = new UploadModel();
    }

    /**
     * "1" -> content  "3" -> "[图片]" "34" -> "[语音]"
     * "47" -> "[表情]"  "50" -> "[语音/视频通话]" "43" -> "[小视频]"
     * "49" -> "[分享]"
     * "48" -> content          // 位置信息
     * "10000" -> content       // 系统提示信息
     * else -> content          // 其他信息，包含红包、转账等
     * <p>
     * 注意: 图片、语音、小视频为资源文件的绝对路径，其他为字符内容
     *
     * @param context  上下文
     * @param listener 监听
     */
    @Override
    public void getChatRecord(Context context, OnGetChatRecordListener listener) {
        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            database = WechatDatabaseHelper.connect(context);
            List<ChatRecord> chatRecordList = new ArrayList<>();
            String createTime = String.valueOf(System.currentTimeMillis() - ApplicationCfg.DATA_TIME);
            String[] binds = new String[]{"1", "3", "34", "47", "50", "43", "49", createTime};
            cursor = database.rawQuery(
                    "select * from message where talker not like 'gh_%' and type in (?,?,?,?,?,?,?) and createTime>?;",
                    binds);

            // 特殊处理
            String[] rules = new String[]{"3", "34", "47", "50", "43", "49"};

            while (cursor.moveToNext()) {
                ChatRecord chatRecord = new ChatRecord();

                chatRecord.setMsgSvrId(String.valueOf(cursor.getLong(cursor.getColumnIndex("msgSvrId"))));
                chatRecord.setType(cursor.getString(cursor.getColumnIndex("type")));
                chatRecord.setContent(cursor.getString(cursor.getColumnIndex("content")));
                chatRecord.setIsSend(cursor.getString(cursor.getColumnIndex("isSend")));
                chatRecord.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
                chatRecord.setTalker(cursor.getString(cursor.getColumnIndex("talker")));

                if ((Arrays.asList(rules).contains(chatRecord.getType()))) {
                    String filePath, link;
                    String resource = cursor.getString(cursor.getColumnIndex("imgPath"));
                    switch (chatRecord.getType()) {
                        // 图片
                        case "3":
                            filePath = imagePath(database, chatRecord.getMsgSvrId());
                            link = this.path2link(context, filePath, chatRecord.getMsgSvrId(), chatRecord.getType());
                            // 上传
                            if (null == link) {
                                chatRecord = null;
                                break;
                            }
                            chatRecord.setContent(link);
                            break;

                        // 语音
                        case "34":
                            filePath = TencentWechatLib.voicePath(resource);
                            link = this.path2link(context, filePath, chatRecord.getMsgSvrId(), chatRecord.getType());
                            // 上传
                            if (null == link) {
                                chatRecord = null;
                                break;
                            }
                            chatRecord.setContent(link);
                            break;

                        // 表情
                        case "47":
                            // [微笑][微笑]
                            // 不做处理
                            break;

                        // 语音/视频通话
                        case "50":
                            chatRecord.setContent("进行了语音/视频通话");
                            break;

                        // 小视频mp4
                        case "43":
                            filePath = TencentWechatLib.videoPath(resource);
                            link = this.path2link(context, filePath, chatRecord.getMsgSvrId(), chatRecord.getType());
                            // 上传
                            if (null == link) {
                                chatRecord = null;
                                break;
                            }
                            chatRecord.setContent(link);
                            break;

                        // 分享
                        case "49":
                            // xml内容
                            Document document;
                            document = Jsoup.parse(chatRecord.getContent(), "UTF-8");
                            Element element = document.getElementsByTag("type").get(0);
                            String shareType = element.text();
                            switch (shareType) {
                                case "5":
                                    chatRecord.setContent("【分享-链接】<a href='" +
                                            document.getElementsByTag("url").get(0).text() +
                                            "'>" + document.getElementsByTag("title").get(0).text() +
                                            "</a>"
                                    );
                                    break;
                                case "6":
                                    chatRecord.setContent(
                                            "【分享-文件】" +
                                                    document.getElementsByTag("title").get(0).text()

                                    );
                                    break;
                                default:
                                    chatRecord.setContent("【分享】未分析到具体内容");
                                    break;
                            }
                            break;

                        default:
                            break;
                    }
                }
                if (null != chatRecord) {
                    chatRecordList.add(chatRecord);
                }
            }
            listener.successful(chatRecordList);
        } catch (SQLException e) {
            Log.e("alicfeng", e.getMessage());
            listener.fail();
        } finally {
            WechatDatabaseHelper.close(database, cursor);
        }
    }

    private String path2link(Context context, String path, String msgSvrId, String type) {
        String link = this.readResourceLink(context, msgSvrId, type);
        // 上传
        if (null == link) {
            if (null != path) {
                DevLog.i("开始上传" + msgSvrId);
                link = this.uploadModel.uploadFile(path);
                if (link != null) {
                    // 更新本地
                    this.saveResourceLink(context, msgSvrId, link, type);
                }
            }
        } else {
            DevLog.i("资源文件已上传" + msgSvrId);
        }
        return link;
    }

    @Override
    public void syncChatRecordMessage(final Context context, final List<ChatRecord> chatRecordList) {
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

        for (ChatRecord chatRecord : chatRecordList) {
            Map<String, String> item = new HashMap<>();
            item.put("msg_svr_id", chatRecord.getMsgSvrId());
            item.put("is_send", chatRecord.getIsSend());
            item.put("content", chatRecord.getContent());
            item.put("type", chatRecord.getType());
            item.put("talker", chatRecord.getTalker());
            item.put("create_time", chatRecord.getCreateTime());
            message.add(item);

            Log.i("alicfeng", "MsgSvrId - " + chatRecord.getMsgSvrId());
            Log.i("alicfeng", "IsSend - " + chatRecord.getIsSend());
            Log.i("alicfeng", "Content - " + chatRecord.getContent());
            Log.i("alicfeng", "Type - " + chatRecord.getType());
            Log.i("alicfeng", "Talker - " + chatRecord.getTalker());
            Log.i("alicfeng", "CreateTime - " + chatRecord.getCreateTime());
        }

        body.put("type", URI.INTERFACE_SIGN_CHATRECORD);
        body.put("username", username);
        body.put("message", message);

        RequestStructure structure = new RequestStructure();
        structure.setHeader(header);
        structure.setBody(body);

        final Gson gson = new Gson();
        String json = gson.toJson(structure);
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
                Log.i("alicfeng", result);
                ResponseStructure responseStructure = gson.fromJson(result, new TypeToken<ResponseStructure>() {
                }.getType());
                if (ResponseCode.SUCCESS.equals(responseStructure.getResultCode())) {
                    Log.i("alicfeng", "聊天信息同步成功");
                }
            }
        });
    }

    @Override
    public String imagePath(SQLiteDatabase database, String msgSvrId) {
        String bigImgPath = "";
        Cursor imgInfoCu = database.rawQuery(
                "select bigImgPath,msgSvrId from ImgInfo2 where msgSvrId=?;",
                new String[]{msgSvrId});
        if (imgInfoCu.getCount() > 0) {
            while (imgInfoCu.moveToNext()) {
                bigImgPath = imgInfoCu.getString(imgInfoCu.getColumnIndex("bigImgPath"));
            }
        }
        bigImgPath = TencentWechatLib.imagePath(bigImgPath);
        if (!imgInfoCu.isClosed()) {
            imgInfoCu.close();
        }
        return bigImgPath;
    }

    @Override
    public String readResourceLink(Context context, String msgSvrId, String type) {
        String link = null;
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context, DataBaseHelper.DB_NAME, null, Application.getVersionCode(context));
        DatabaseManager databaseManager = DatabaseManager.getInstance(dataBaseHelper);
        android.database.sqlite.SQLiteDatabase readableDatabase = dataBaseHelper.getReadableDatabase();

        //生成ContentValues对象，key:列名  value:想插入的值
        android.database.Cursor cursor = null;
        try {
            cursor = readableDatabase.query(DataBaseHelper.TABLE_MESSAGE_RESOURCE_RECORD, null, "msg_id=? and type=?", new String[]{msgSvrId, type}, null, null, null, null);
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    link = cursor.getString(cursor.getColumnIndex("resource"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            databaseManager.closeDatabase();
        }
        return link;
    }

    @Override
    public boolean saveResourceLink(Context context, String msgSvrId, String link, String type) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context, DataBaseHelper.DB_NAME, null, Application.getVersionCode(context));
        android.database.sqlite.SQLiteDatabase writableDatabase = dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("msg_id", msgSvrId);
        values.put("resource", link);
        values.put("type", type);
        writableDatabase.insert(DataBaseHelper.TABLE_MESSAGE_RESOURCE_RECORD, null, values);
        DataBaseHelper.closeDatabase(writableDatabase);
        return true;
    }

    public static void main(String[] args) {
        System.out.println(String.valueOf(System.currentTimeMillis() - ApplicationCfg.DATA_TIME));
    }
}

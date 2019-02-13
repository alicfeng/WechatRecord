package com.samego.alic.monitor.wechat.wechatrecord.model;

import android.content.Context;
import android.util.Log;

import com.samego.alic.monitor.wechat.wechatrecord.bean.ChatRecord;
import com.samego.alic.monitor.wechat.wechatrecord.common.AppCommon;
import com.samego.alic.monitor.wechat.wechatrecord.common.AppCore;
import com.samego.alic.monitor.wechat.wechatrecord.configure.TN;
import com.samego.alic.monitor.wechat.wechatrecord.helper.DataBaseHelper;
import com.samego.alic.monitor.wechat.wechatrecord.helper.WechatDatabaseHelper;
import com.samego.alic.monitor.wechat.wechatrecord.libs.WechatPackage;
import com.samego.alic.monitor.wechat.wechatrecord.model.listener.OnGetChatRecordListener;
import com.samego.alic.monitor.wechat.wechatrecord.utils.DatabaseManager;
import com.samego.alic.monitor.wechat.wechatrecord.utils.DevLog;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            String[] binds = new String[]{"1", "3", "34", "47", "50", "43", "49", String.valueOf(AppCore.DATA_TIME)};
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
                    String resource = cursor.getString(cursor.getColumnIndex("imgPath"));
                    switch (chatRecord.getType()) {
                        // 图片
                        case "3":
                            String filePath = imagePath(database, chatRecord.getMsgSvrId());
                            String link = this.resourceLink(context, chatRecord.getMsgSvrId());
                            // 上传
                            if (null == link) {
                                DevLog.i("资源文件为空" + chatRecord.getMsgSvrId());
                                if (null != filePath) {
                                    DevLog.i("开始上传" + chatRecord.getMsgSvrId());
                                    this.uploadModel.uploadFile(filePath);
                                }
                            }
                            chatRecord.setContent(link);
                            break;

                        // 语音
                        case "34":
                            chatRecord.setContent(WechatPackage.voicePath(resource));
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
                            chatRecord.setContent(WechatPackage.videoPath(resource));
                            break;

                        // 分享
                        case "49":
                            // xml内容
                            // 不作处理 - 处理的话服务端处理
                            break;

                        default:
                            break;
                    }
                }
                chatRecordList.add(chatRecord);
            }
            listener.successful(chatRecordList);
        } catch (SQLException e) {
            Log.e("alicfeng", e.getMessage());
            listener.fail();
        } finally {
            WechatDatabaseHelper.close(database, cursor);
        }
    }

    @Override
    public void syncChatRecordMessage(final Context context, final List<ChatRecord> chatRecordList) {
        for (ChatRecord chatRecord : chatRecordList) {
            Log.i("alicfeng", "MsgSvrId - " + chatRecord.getMsgSvrId());
            Log.i("alicfeng", "IsSend - " + chatRecord.getIsSend());
            Log.i("alicfeng", "Content - " + chatRecord.getContent());
            Log.i("alicfeng", "Type - " + chatRecord.getType());
            Log.i("alicfeng", "Talker - " + chatRecord.getTalker());
            Log.i("alicfeng", "CreateTime - " + chatRecord.getCreateTime());
        }
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
        bigImgPath = WechatPackage.imagePath(bigImgPath);
        return bigImgPath;
    }

    @Override
    public String resourceLink(Context context, String msgSvrId) {
        String link = null;
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context, TN.DATABASE_NAME, null, AppCommon.getVersionCode(context));
        DatabaseManager databaseManager = DatabaseManager.getInstance(dataBaseHelper);
        android.database.sqlite.SQLiteDatabase readableDatabase = dataBaseHelper.getReadableDatabase();

        //生成ContentValues对象，key:列名  value:想插入的值
        android.database.Cursor cursor = null;
        try {
            cursor = readableDatabase.query(TN.MESSAGE_UPLOAD_RECORD, null, "msg_id=?", new String[]{msgSvrId}, null, null, null, null);
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
}

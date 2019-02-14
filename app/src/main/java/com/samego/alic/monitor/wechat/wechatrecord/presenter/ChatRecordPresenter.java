package com.samego.alic.monitor.wechat.wechatrecord.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.samego.alic.monitor.wechat.wechatrecord.bean.ChatRecord;
import com.samego.alic.monitor.wechat.wechatrecord.model.ChatRecordModel;
import com.samego.alic.monitor.wechat.wechatrecord.model.ChatRecordModelImpl;
import com.samego.alic.monitor.wechat.wechatrecord.model.listener.OnGetChatRecordListener;
import com.samego.alic.monitor.wechat.wechatrecord.view.view.AnalysisServiceView;

import java.util.List;

/**
 * 聊天记录控制器ChatRecordPresenter
 */
public class ChatRecordPresenter {
    private Context context;
    private ChatRecordModel chatRecordModel;
    private Handler handler;
    private AnalysisServiceView analysisServiceView;

    public ChatRecordPresenter(Context context, AnalysisServiceView analysisServiceView) {
        this.context = context;
        this.chatRecordModel = new ChatRecordModelImpl();
        this.handler = new Handler(Looper.getMainLooper());
        this.analysisServiceView = analysisServiceView;
    }

    /**
     * 同步上传聊天记录
     */
    public void syncChatRecord() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                chatRecordModel.getChatRecord(context, new OnGetChatRecordListener() {
                    @Override
                    public void successful(final List<ChatRecord> chatRecordList) {
                        chatRecordModel.syncChatRecordMessage(context, chatRecordList);

                    }

                    @Override
                    public void fail() {
                        //analysisServiceView.getDataFail();
                    }
                });
            }
        }).start();
        handler.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}

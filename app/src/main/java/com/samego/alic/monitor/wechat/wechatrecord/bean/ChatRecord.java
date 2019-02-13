package com.samego.alic.monitor.wechat.wechatrecord.bean;

import java.io.Serializable;

/**
 * 聊天记录bean
 */
public class ChatRecord implements Serializable {
    // 信息ID
    private String msgSvrId;
    // 信息的类型
    private String type;
    // 是否发送的信息 1发送 0接收
    private String isSend;
    // 信息的创建时间戳 ms
    private String createTime;
    // 对话者
    private String talker;
    // 聊天内容  图片、语音、小视频为资源文件的绝对路径，其他为字符内容
    private String content;

    public ChatRecord() {

    }

    public String getMsgSvrId() {
        return msgSvrId;
    }

    public void setMsgSvrId(String msgSvrId) {
        this.msgSvrId = msgSvrId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsSend() {
        return isSend;
    }

    public void setIsSend(String isSend) {
        this.isSend = isSend;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTalker() {
        return talker;
    }

    public void setTalker(String talker) {
        this.talker = talker;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

package com.samego.alic.monitor.wechat.wechatrecord.bean;

import java.io.Serializable;

public class ResponseStructure implements Serializable {
    private String resultCode;
    private String message;
    private String data;

    ResponseStructure(){

    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

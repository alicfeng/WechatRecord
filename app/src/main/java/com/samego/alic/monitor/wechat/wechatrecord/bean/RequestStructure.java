package com.samego.alic.monitor.wechat.wechatrecord.bean;

import java.util.Map;

public class RequestStructure {
    private Map header;
    private Map body;

    public Map getHeader() {
        return header;
    }

    public void setHeader(Map header) {
        this.header = header;
    }

    public Map getBody() {
        return body;
    }

    public void setBody(Map body) {
        this.body = body;
    }
}

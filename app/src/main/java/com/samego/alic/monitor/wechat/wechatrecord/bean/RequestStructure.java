package com.samego.alic.monitor.wechat.wechatrecord.bean;

import java.io.Serializable;
import java.util.Map;

/**
 * 报文请求主体bean
 */
public class RequestStructure  implements Serializable {
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

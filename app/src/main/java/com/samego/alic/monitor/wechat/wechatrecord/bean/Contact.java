package com.samego.alic.monitor.wechat.wechatrecord.bean;

import java.io.Serializable;

public class Contact implements Serializable {
    private String username;
    private String nickname;
    private String type;

    public Contact() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

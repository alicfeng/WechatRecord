package com.samego.alic.monitor.wechat.wechatrecord.bean;

import java.io.Serializable;

/**
 * 联系人bean
 */
public class Contact implements Serializable {
    // 账号
    private String username;
    // 昵称
    private String nickname;
    // 类型 公众号等类型 type=33：微信功能 type=2：未知 type=4：非好友
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

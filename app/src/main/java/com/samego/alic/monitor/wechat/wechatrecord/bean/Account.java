package com.samego.alic.monitor.wechat.wechatrecord.bean;

/**
 * 微信账号bean
 */
public class Account {
    // 微信id
    private String username;
    // 昵称
    private String nickname;
    // 头像
    private String headimg;

    public Account() {

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

    public String getHeadimg() {
        return headimg;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }
}

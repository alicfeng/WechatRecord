package com.samego.alic.monitor.wechat.wechatrecord.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.samego.alic.monitor.wechat.wechatrecord.service.CoreService;
import com.samego.alic.monitor.wechat.wechatrecord.utils.DevLog;

public class BootBroadcastReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.samego.alic.monitor.wechat.wechatrecord.service")) {
            Intent service = new Intent(context, CoreService.class);
            service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            DevLog.i("CoreService restart");
            context.startService(service);
        }
    }
}
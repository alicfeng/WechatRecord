package com.samego.alic.monitor.wechat.wechatrecord.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.samego.alic.monitor.wechat.wechatrecord.service.AnalysisService;

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, AnalysisService.class);
        context.startService(service);
    }
}

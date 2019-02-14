package com.samego.alic.monitor.wechat.wechatrecord.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.samego.alic.monitor.wechat.wechatrecord.R;
import com.samego.alic.monitor.wechat.wechatrecord.common.AppCore;
import com.samego.alic.monitor.wechat.wechatrecord.service.AnalysisService;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SharedPreferencesUtil;
import com.samego.alic.monitor.wechat.wechatrecord.utils.ShellUtil;
import com.samego.alic.monitor.wechat.wechatrecord.utils.StatusBarCompat;
import com.sdsmdg.tastytoast.TastyToast;

public class MainActivity extends AppCompatActivity {
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.init();
        // 加载微信配置
        if (AppCore.initCoreConfigure(this)) {
            String password = SharedPreferencesUtil.get(this, "wx_psd", null);
            Toast.makeText(this, password, Toast.LENGTH_LONG).show();
            startService(intent);
        } else {
            TastyToast.makeText(this, "请登录微信", Toast.LENGTH_LONG ,TastyToast.WARNING).show();
        }
    }

    public void init(){
        StatusBarCompat.displayStatusBar(this);
        this.intent = new Intent(this,AnalysisService.class);
        if (!ShellUtil.isRoot()){
            TastyToast.makeText(this, "Please Root", Toast.LENGTH_LONG ,TastyToast.WARNING).show();
        }
    }
}

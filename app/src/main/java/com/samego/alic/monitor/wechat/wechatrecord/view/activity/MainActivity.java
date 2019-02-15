package com.samego.alic.monitor.wechat.wechatrecord.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.samego.alic.monitor.wechat.wechatrecord.R;
import com.samego.alic.monitor.wechat.wechatrecord.libs.TencentWechatLib;
import com.samego.alic.monitor.wechat.wechatrecord.service.CoreService;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SharedPreferencesUtil;
import com.samego.alic.monitor.wechat.wechatrecord.utils.ShellUtil;
import com.samego.alic.monitor.wechat.wechatrecord.utils.StatusBarCompat;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SystemUtil;
import com.samego.alic.monitor.wechat.wechatrecord.view.view.MainActivityView;
import com.sdsmdg.tastytoast.TastyToast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MainActivityView {
    private Intent intent;
    private TextView serviceStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.init();
        // 加载微信配置
        if (TencentWechatLib.initWechatConfigure(this)) {
            String password = SharedPreferencesUtil.get(this, "wx_psd", null);
            Toast.makeText(this, password, Toast.LENGTH_LONG).show();
            this.startCoreService();
        } else {
            TastyToast.makeText(this, "Please Login wechat", Toast.LENGTH_LONG, TastyToast.WARNING).show();
        }
    }

    /**
     * 初始化
     */
    public void init() {
        serviceStatus = findViewById(R.id.serviceStatus);
        serviceStatus.setOnClickListener(this);
        this.intent = new Intent(this, CoreService.class);
        this.checkCoreService();
        if (!ShellUtil.isRoot()) {
            TastyToast.makeText(this, "Please Root", Toast.LENGTH_LONG, TastyToast.WARNING).show();
        }
    }

    @Override
    public void checkCoreService() {
        if (SystemUtil.isServiceRunning(this, CoreService.class.getName())) {
            serviceStatus.setText(R.string.service_running);
        } else {
            serviceStatus.setText(R.string.service_stop);
        }
    }

    @Override
    public void startCoreService() {
        if (!SystemUtil.isServiceRunning(this, CoreService.class.getName())) {
            startService(intent);
            serviceStatus.setText(R.string.service_running);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.serviceStatus:
                if (serviceStatus.getText().equals(getResources().getText(R.string.service_stop))) {
                    this.startCoreService();
                }
                break;
        }
    }
}

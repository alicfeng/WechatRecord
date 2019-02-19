package com.samego.alic.monitor.wechat.wechatrecord.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.samego.alic.monitor.wechat.wechatrecord.R;
import com.samego.alic.monitor.wechat.wechatrecord.libs.TencentWechatLib;
import com.samego.alic.monitor.wechat.wechatrecord.service.CoreService;
import com.samego.alic.monitor.wechat.wechatrecord.utils.DevLog;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SharedPreferencesUtil;
import com.samego.alic.monitor.wechat.wechatrecord.utils.ShellUtil;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SystemUtil;
import com.samego.alic.monitor.wechat.wechatrecord.view.view.MainActivityView;
import com.sdsmdg.tastytoast.TastyToast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MainActivityView {
    private Intent intent;
    private TextView serviceStatus;

    private final static int REQUEST_PHONE_STATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
        };

        PermissionsUtil.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permission) {
                DevLog.i("permissionGranted");
            }

            @Override
            public void permissionDenied(@NonNull String[] permission) {
                DevLog.i("permissionDenied");
            }
        },permissions);

//        for (String str:permissions){
//            if(checkSelfPermission(str)!=PackageManager.PERMISSION_GRANTED){
//                DevLog.i("申请中" + str);
//                requestPermissions(new String[]{str}, 1);
//            }else {
//                DevLog.i("已经申请了" + str);
//            }
//        }


        this.init();
        // 加载微信配置
        if (TencentWechatLib.initWechatConfigure(this,this)) {
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
        serviceStatus = (TextView) findViewById(R.id.serviceStatus);
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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_PHONE_STATE && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this,SystemUtil.imei(this),Toast.LENGTH_LONG).show();
//        }
//    }
}

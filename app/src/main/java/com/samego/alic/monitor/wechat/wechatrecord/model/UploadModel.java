package com.samego.alic.monitor.wechat.wechatrecord.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.samego.alic.monitor.wechat.wechatrecord.bean.ResponseStructure;
import com.samego.alic.monitor.wechat.wechatrecord.common.ResponseCode;
import com.samego.alic.monitor.wechat.wechatrecord.configure.URI;
import com.samego.alic.monitor.wechat.wechatrecord.helper.OkHttpManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

public class UploadModel {
    public String uploadFile(String filePath) {
        String link = "";
        try {
            //表单文件
            File file = new File(filePath);
            Map<String, File> files = new HashMap<>();
            files.put("file", file);
            Response response = OkHttpManager.uploadFile(URI.URI_FILE_UPLOAD, files);
            Gson gson = new Gson();
            String result;
            if (null == response) {
                return null;
            }
            result = response.body().string();
            ResponseStructure responseStructure = gson.fromJson(result, new TypeToken<ResponseStructure>() {
            }.getType());
            if (ResponseCode.SUCCESS.equals(responseStructure.getResultCode())) {
                link = responseStructure.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return link;
    }
}

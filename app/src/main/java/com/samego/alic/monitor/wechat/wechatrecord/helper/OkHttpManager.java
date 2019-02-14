package com.samego.alic.monitor.wechat.wechatrecord.helper;

import android.os.Environment;

import com.blankj.utilcode.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * oKHttp的容器管理
 */
public class OkHttpManager {
    /**
     * 静态实例
     * 复用连接池
     */
    private static OkHttpManager okHttpManager;

    /**
     * OkHttpClient实例
     */
    private OkHttpClient client;

    /**
     * 单例模式
     * 对于但是模式网上有很对写法 实际得看需求
     *
     * @return OkHttpManager
     */
    private static OkHttpManager getInstance() {
        if (okHttpManager == null)
            okHttpManager = new OkHttpManager();
        return okHttpManager;
    }

    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    //post请求header-image
    public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
    // post请求json
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    // 表单文件上传
    public static final MediaType MUTILPART_FORM_DATA = MediaType.parse("multipart/form-data; charset=utf-8");

    /**
     * 构造方法
     */
    public OkHttpManager() {
        //实例化OkHttpClient
        client = new OkHttpClient();
        //配置okHttpClient的参数
        client.newBuilder().connectTimeout(10, TimeUnit.SECONDS);
        client.newBuilder().readTimeout(10, TimeUnit.SECONDS);
        client.newBuilder().writeTimeout(10, TimeUnit.SECONDS);
        //设置缓存信息的处理：创建缓存对象，构造方法用于控制缓存位置及最大缓存大小【单位是Byte】
        Cache cache = new Cache(new File(Environment.getExternalStorageDirectory().getPath()), 10 * 1024 * 1024);
        client.newBuilder().cache(cache);
    }

    //-----------------原生start----------------------
    //原生的同步请求

    /**
     * 原生的同步请求 支持header 对外方法
     *
     * @param request request
     * @return Response
     */
    public static Response execute(Request request) {
        return OkHttpManager.getInstance().doExecute(request);
    }

    /**
     * 原生的同步请求 支持header 对内方法
     *
     * @param request request
     * @return Response
     */
    public Response doExecute(Request request) {
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //原生的异步请求 处理回调

    /**
     * 原生的异步请求 支持header 对外方法
     *
     * @param request  request
     * @param callback callback
     */
    public static void enqueue(Request request, Callback callback) {
        OkHttpManager.getInstance().doEnqueue(request, callback);
    }

    /**
     * 原生的异步请求 支持header 对内方法
     *
     * @param request  request
     * @param callback callback
     */
    private void doEnqueue(Request request, Callback callback) {
        client.newCall(request).enqueue(callback);
    }

    //原生的异步请求 不处理回调

    /**
     * 原生的异步请求 支持header 对外方法
     *
     * @param request request
     */
    public static void enqueue(Request request) {
        OkHttpManager.getInstance().doEnqueue(request);
    }

    /**
     * 原生的异步请求 支持header 对内方法
     *
     * @param request request
     */
    private void doEnqueue(Request request) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //todo 这里虽然存在 但是不可以做羞羞事嗒
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //todo 这里虽然存在 但是不可以做羞羞事嗒

            }
        });
    }


    //-----------------原生end----------------------

    //GET同步请求 返回Response

    /**
     * get请求 不开启异步线程 公开方法
     *
     * @param url 请求参数url
     * @return response 返回响应
     */
    public static Response executeSync(String url) {
        return OkHttpManager.getInstance().doExecuteSync(url);
    }

    /**
     * get请求 不开启异步线程 内部方法
     *
     * @param url 请求参数url
     * @return response 返回响应
     */
    private Response doExecuteSync(String url) {
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful())
                return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //GET同步请求并获取数据

    /**
     * get请求 同步线程 公开方法
     *
     * @param url url
     * @return String
     */
    public static String executeSyncString(String url) {
        return OkHttpManager.getInstance().doExecuteSyncString(url);
    }

    /**
     * get请求 同步线程 内部方法
     *
     * @param url url
     * @return String
     */
    private String doExecuteSyncString(String url) {
        try {
            Response response = doExecuteSync(url);
            if (response != null)
                return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //GET 异步请求 内部方法

    /**
     * get 异步请求 公开方法
     * 对结果处理
     *
     * @param url url
     */
    public static void enqueueAsync(String url, Callback callback) {
        OkHttpManager.getInstance().doEnqueueAsync(url, callback);
    }

    /**
     * 自定义对结果处理 内部方法
     *
     * @param callback 回调方法
     */
    private void doEnqueueAsync(String url, Callback callback) {
        client.newCall(new Request.Builder().url(url).build()).enqueue(callback);
    }

    /**
     * get 异步请求 公开方法
     * 不对结果处理
     *
     * @param url url
     */
    public static void enqueueAsync(String url) {
        OkHttpManager.getInstance().doEnqueueAsync(url);
    }

    /**
     * 开启异步线程访问 内部方法
     * 不对结果处理
     */
    private void doEnqueueAsync(String url) {
        client.newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //todo 这里虽然存在 但是不可以做羞羞事嗒
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //todo 这里虽然存在 但是不可以做羞羞事嗒

            }
        });
    }

    //POST 异步访问 处理结果

    /**
     * post 异步访问 公开方法
     *
     * @param url      url
     * @param body     提交参数
     * @param callback 回调函数
     */
    public static void postEnqueueAsync(String url, RequestBody body, Callback callback) {
        OkHttpManager.getInstance().doPostEnqueueAsync(url, body, callback);
    }

    /**
     * post 异步访问 内部方法
     *
     * @param url      url
     * @param body     提交参数
     * @param callback 回调函数
     */
    private void doPostEnqueueAsync(String url, RequestBody body, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        //计划好了 那就干吧
        client.newCall(request).enqueue(callback);
    }

    //POST 异步访问 不处理结果

    /**
     * POST 异步访问 不处理结果 公开方法
     *
     * @param url  url
     * @param body body
     */
    public static void postEnqueueAsync(String url, RequestBody body) {
        OkHttpManager.getInstance().doPostEnqueueAsync(url, body);
    }

    /**
     * POST 异步访问 不处理结果 内部方法
     *
     * @param url  url
     * @param body body
     */
    private void doPostEnqueueAsync(String url, RequestBody body) {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        //计划好了 那就干吧
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //todo 这里虽然存在 但是不可以做羞羞事嗒
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //todo 这里虽然存在 但是不可以做羞羞事嗒

            }
        });
    }

    //POST  仅上传一个文件 对结果处理

    /**
     * 上传一个文件 公开方法
     *
     * @param url      url
     * @param file     file
     * @param callback callback
     */
    public static void uploadFileAsync(String url, File file, Callback callback) {
        OkHttpManager.getInstance().doUploadFileAsync(url, file, callback);
    }

    /**
     * 上传一个文件 内部方法
     *
     * @param url      url
     * @param file     file
     * @param callback callback
     */
    private void doUploadFileAsync(String url, File file, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                .build();

        client.newCall(request).enqueue(callback);
    }

    //POST  仅上传一个文件 对结果处理

    /**
     * 上传一个文件 公开方法
     *
     * @param url   url
     * @param files files
     */
    public static Response uploadFile(String url, Map<String, File> files) {
        return OkHttpManager.getInstance().doUploadFile(url, files);
    }

    /**
     * 上传一个文件 内部方法
     *
     * @param url   url
     * @param files files
     */
    private Response doUploadFile(String url, Map<String, File> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MUTILPART_FORM_DATA);

        //处理提交的文件
        if (files != null) {
            //次句暂且解决params为null
            builder.addFormDataPart("hidden", "debug");
            for (String key : files.keySet()) {
                //过滤 判断key是否为空
                if (!key.equals("")) {
                    builder.addFormDataPart(key, FileUtils.getFileName(files.get(key)), RequestBody.create(MEDIA_TYPE_PNG, files.get(key)));
                }
            }
        }
        MultipartBody body = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //表单提交(带文件) 动态参数 动态文件

    /**
     * post 文件上传 公开方法
     *
     * @param url    url
     * @param params params
     * @param files  file
     */
    public static void postFormAsync(String url, Map<String, String> params, Map<String, File> files, Callback callback) {
        OkHttpManager.getInstance().doPostFormAsync(url, params, files, callback);
    }

    /**
     * 文件上传 内部方法
     *
     * @param url    url
     * @param params params
     * @param files  files
     */
    private void doPostFormAsync(String url, Map<String, String> params, Map<String, File> files, Callback callback) {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        //处理提交的参数
        if (params != null) {
            //map 遍历
            for (String key : params.keySet()) {
                //过滤 判断key是否为空
                if (!key.equals("")) {
                    builder.addFormDataPart(key, params.get(key));
                }
            }
        }
        //处理提交的文件
        if (files != null) {
            //次句暂且解决params为null
            builder.addFormDataPart("hidden", "debug");
            for (String key : files.keySet()) {

                //过滤 判断key是否为空
                if (!key.equals("")) {
                    builder.addFormDataPart(key, key, RequestBody.create(MEDIA_TYPE_PNG, files.get(key)));
                }
            }
        }
        MultipartBody body = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
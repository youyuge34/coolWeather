package com.example.yousheng.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by yousheng on 17/3/30.
 */

//由于okhttp的出色封装，发起http请求只要调用这个静态方法就行了
public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        //注册一个未来会发生的回调，来响应服务器请求
        client.newCall(request).enqueue(callback);
    }
}

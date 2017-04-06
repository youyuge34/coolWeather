package com.example.yousheng.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.yousheng.coolweather.gson.Weather;
import com.example.yousheng.coolweather.util.HttpUtil;
import com.example.yousheng.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //设置每8小时就唤醒服务更新
        long hours = 8 * 60 * 60 * 1000;
        //系统开机至今的时间+8小时
        long triggerTime = SystemClock.elapsedRealtime() + hours;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent intent1 = PendingIntent.getService(this, 0, i, 0);
        //先取消旧的定时
        manager.cancel(intent1);
        //设置定时唤醒
        manager.set(AlarmManager.ELAPSED_REALTIME, triggerTime, intent1);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新bing每日一图
     */
    private void updateBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPicUrl = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic", bingPicUrl);
                editor.apply();
            }
        });
    }

    /**
     * 更新天气信息
     */
    private void updateWeather() {
        //从缓存中获取json数据，并得到其中的weatherId
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String jsonResponse = pref.getString("weather", null);
        if (jsonResponse != null) {
            Weather weather = Utility.handleWeatherResponse(jsonResponse);
            String weatherId = weather.basic.weatherId;

            //重新请求服务器，获取最新的气温信息写入缓存
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" +
                    weatherId + "&key=b5001d38517f4fd3b758dc769f78bd47";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    //解析json数据成为weather实体类
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    //如果status状态为ok，则将返回的json数据存入sharedpreferences中
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                    }
                }
            });
        }
    }
}

package com.example.yousheng.coolweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yousheng.coolweather.gson.Forecast;
import com.example.yousheng.coolweather.gson.Weather;
import com.example.yousheng.coolweather.util.HttpUtil;
import com.example.yousheng.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //初始化各种控件
        weatherLayout= (ScrollView) findViewById(R.id.weather_layout);
        titleCity= (TextView) findViewById(R.id.title_city);
        titleUpdateTime= (TextView) findViewById(R.id.title_update_time);
        degreeText= (TextView) findViewById(R.id.degree_text);
        weatherInfoText= (TextView) findViewById(R.id.weather_info_text);
        forecastLayout= (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText= (TextView) findViewById(R.id.aqi_text);
        pm25Text= (TextView) findViewById(R.id.pm25_text);
        comfortText= (TextView) findViewById(R.id.comfort_text);
        carWashText= (TextView) findViewById(R.id.car_wash_text);
        sportText= (TextView) findViewById(R.id.sport_text);
        bingPic= (ImageView) findViewById(R.id.bing_ic_img);



        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        //若有缓存则从缓存中加载图片，若无缓存则调用方法加载必应图片并存入缓存
        String bingPicUrl=prefs.getString("bing_pic",null);
        if(bingPicUrl!=null){
            Glide.with(this).load(bingPicUrl).into(bingPic);
        }else{
            loadBingPic();
        }

        String weatherString= prefs.getString("weather",null);
        if(weatherString!=null){
            //有SharedPreferences缓存时直接解析存储的json天气数据为weather类
            Weather weather= Utility.handleWeatherResponse(weatherString);
            //将天气数据写入视图层view中
            showWeatherInfo(weather);
        }else{
            //获取由城市选择页面传入的城市天气编号
            String weatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            //无缓存时去服务器查询天气，将返回的json存入sharedPreferences中并显示信息在view里
            requestWeather(weatherId);
        }
    }



    /**
     * 根据天气id请求城市天气信息
     * @param weatherId 由城市选择页面传入的城市天气编号
     */
    private void requestWeather(String weatherId) {
        //最终生成的城市天气请求链接
        String weatherUrl="http://guolin.tech/api/weather?cityid="+
                weatherId+"&key=b5001d38517f4fd3b758dc769f78bd47";
        //发送请求并且处理返回的response
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                 e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                //解析json数据成为weather实体类
                final Weather weather=Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //如果status状态为ok，则将返回的json数据存入sharedpreferences中
                        if(weather!=null&&"ok".equals(weather.status)){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            //把weather中的信息显示到view中
                            showWeatherInfo(weather);
                        }
                    }
                });
            }
        });
        //这样在每次请求天气的时候也会刷新背景图片
        loadBingPic();
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        String requestUrl="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingUrl=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingUrl);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingUrl).into(bingPic);
                    }
                });
            }
        });
    }

    /**
     * 处理并展示weather类中的数据，把数据层写入视图层中
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        titleCity.setText(weather.basic.cityName);
        titleUpdateTime.setText(weather.basic.update.updateTime.split(" ")[1]);
        degreeText.setText(weather.now.temperature+"°C");
        weatherInfoText.setText(weather.now.more.info);
        //未来天气的layout清空，在这里进行填充子项view
        forecastLayout.removeAllViews();
        //遍历每个实体数据类
        for(Forecast forecast:weather.forecastList){
            //初始化一个子项view,并写入数据
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            ((TextView)view.findViewById(R.id.date_text)).setText(forecast.date);
            ((TextView)view.findViewById(R.id.info_text)).setText(forecast.more.info);
            ((TextView)view.findViewById(R.id.max_text)).setText(forecast.temprature.max);
            ((TextView)view.findViewById(R.id.min_text)).setText(forecast.temprature.min);

            //将子项view添加到父布局
            forecastLayout.addView(view);
        }

        //写入污染指数的数据
        if(weather.aqi!=null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        //写入生活建议的数据
        comfortText.setText("舒适度："+weather.suggestion.comfort.info);
        carWashText.setText("洗车指数："+weather.suggestion.carWash.info);
        sportText.setText("运动建议："+weather.suggestion.sport.info);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 别的类使用此方法启动weatherActivity
     * @param context 原活动上下文
     * @param weatherId 城市天气编号
     */
    public static void newInstance(Context context,String weatherId){
        Intent intent=new Intent(context,WeatherActivity.class);
        intent.putExtra("weather_id",weatherId);
        context.startActivity(intent);
    }
}

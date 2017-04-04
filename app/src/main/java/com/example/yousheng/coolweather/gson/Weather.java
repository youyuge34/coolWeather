package com.example.yousheng.coolweather.gson;

/**
 * Created by yousheng on 17/4/4.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** {
 *   "HeWeather":[
 *      {
 *          "status":"ok",
 *          "basic":{}
 *          "aqi":{}
 *          "now":{}
 *          "suggestion":{}
 *          "daily_forecast":[]
 *      }
 *     ]
 *   }
 *   此为总的实例类，也是api接口直接返回的数据格式
 */
public class Weather {
    //返回ok则表示成功，失败则会返回具体的原因
    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}

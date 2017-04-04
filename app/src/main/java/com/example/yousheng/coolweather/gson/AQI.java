package com.example.yousheng.coolweather.gson;

/**
 * Created by yousheng on 17/4/4.
 */

/**
 *  "aqi":{
 *      "city":{
 *          "aqi":"44",
 *          "pm25":"13"
 *      }
 *  }
 */
public class AQI {
    public  AQICity city;

    public class AQICity {
        public String aqi;
        public String pm25;
    }
}

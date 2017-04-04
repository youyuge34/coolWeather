package com.example.yousheng.coolweather.gson;

/**
 * Created by yousheng on 17/4/4.
 */

import com.google.gson.annotations.SerializedName;

/**
 * "daily_forecast":[
 *       {
 *           "date":"2016-08-08",
 *           "cond":{
 *               "txt_d":"阵雨"
 *           },
 *           "tmp":{
 *               "max":"34"
 *               "min":"27"
 *           }
 *       },
 *
 *       {
 *           "date":"2016-08-09",
 *           "cond":{
 *               "txt_d":"多云"
 *           },
 *           "tmp":{
 *               "max":"32"
 *               "min":"29"
 *           }
 *       },
 *
 *       ......
 * ]
 *
 * 这组特殊，daily_forecast中包含了一个数组，每个数组中的每一项代表着未来一天的天气信息
 * 我们只需定义出单日天气的实体类就行，在声明实体类引用的时候使用集合类型进行声明
 */
public class Forecast {
    public String date;

    @SerializedName("cond")
    public More more;

    @SerializedName("tmp")
    public Temperature temprature;

    public class Temperature {
        public String max;
        public String min;
    }

    public class More {
        @SerializedName("txt_d")
        public String info;
    }
}

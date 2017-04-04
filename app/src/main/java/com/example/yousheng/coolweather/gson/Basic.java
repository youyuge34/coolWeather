package com.example.yousheng.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yousheng on 17/4/4.
 */

/** 解析json返回的数据，数据其中一个属性为basic
 *  "basic":{
 *     "city":"苏州",
 *     "id":"CN101190401",
 *     "update":{
 *         "loc":"2016-08-08 21:58"
 *     }
 *  }
 */

public class Basic {
    //An annotation that indicates this member should be serialized to JSON with
    // the provided name value as its field name.
    //让JSON字段和java字段之间建立映射联系
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}

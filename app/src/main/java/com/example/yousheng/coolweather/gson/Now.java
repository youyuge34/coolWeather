package com.example.yousheng.coolweather.gson;

/**
 * Created by yousheng on 17/4/4.
 */

import com.google.gson.annotations.SerializedName;

/**
 *  "now":{
 *      "temp":"29",
 *      "cond":{
 *          "txt":"阵雨"
 *      }
 *  }
 */
public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}

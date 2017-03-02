package com.zhy.smail.common.utils;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by wenliz on 2017/1/23.
 */
public class MapUtils {
    public static String getString(Map map, String key){
        if(map.get(key) == null) return null;

        return map.get(key).toString();
    }

    public static Integer getInteger(Map map, String key){
        if(map.get(key) == null) return null;

        String value =  map.get(key).toString();
        return Integer.parseInt(value);
    }

    public static Timestamp getTimeStamp(Map map, String key){
        if(map.get(key) == null) return null;

        String value =  map.get(key).toString();
        return Timestamp.valueOf(value);
    }
}

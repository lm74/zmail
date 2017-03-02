package com.zhy.smail.restful;

import com.zhy.smail.user.entity.UserInfo;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by wenliz on 2017/1/23.
 */
public class JsonOperator {
    public static ObjectMapper mapper = new ObjectMapper();

    public static String getJson(Object obj){
        StringWriter sw = new StringWriter();
        try{
            mapper.writeValue(sw, obj);

            return sw.toString();
        }
        catch(Exception e){
            return "";
        }
    }

    public static <T> T toObject(Object content, Class<T> valueType){
        String json = JsonOperator.getJson(content);
        try {
            return  mapper.readValue(json, valueType);
        }
        catch (IOException e){
            return null;
        }
    }
}

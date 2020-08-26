package com.jfeat.ext.plugin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;

import java.util.List;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/6/13
 */
public class JsonKit {
    private static ValueFilter valueFilter = (object, name, value) -> {
        if(null == value) {
            value = "";
        }

        return value instanceof Long?value.toString():value;
    };

    public JsonKit() {
    }

    public static String toJson(Object object) {
        return JSONObject.toJSONString(object, valueFilter, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }

    public static Map<String, Object> parseObject(String text) {
        return JSON.parseObject(text);
    }

    public static <T> List<T> parseArray(String jsonString, Class<T> clazz) {
        @SuppressWarnings("unchecked")
        List<T> ts = (List<T>) JSON.parseArray(jsonString, clazz);
        return ts;
    }
}

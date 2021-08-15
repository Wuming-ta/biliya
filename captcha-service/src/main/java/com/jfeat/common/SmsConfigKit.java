package com.jfeat.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2019/10/19
 */
public class SmsConfigKit {


    private static Map<String, String> map = new HashMap<>();

    public static void setTtl(String name, String value) {
        map.put(name, value);
    }

    public static String getTtl(String name) {
        return map.get(name);
    }
}

package com.jfeat.wechat.util;

/**
 * Created by kang on 2017/5/5.
 */
public class StringUtil {
    public static String join(String delimiter, String... elements) {
        if (delimiter == null || elements == null || elements.length == 0) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        result.append(elements[0]);
        for (int i = 1; i < elements.length; i++) {
            result.append(delimiter).append(elements[i]);
        }
        return result.toString().trim();
    }
}

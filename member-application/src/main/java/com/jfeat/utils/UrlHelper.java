package com.jfeat.utils;

import com.jfinal.kit.StrKit;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by kang on 2016/11/25.
 */
public class UrlHelper {
    //本地测试时，&没问题，放到服务器之后，有时 &会被转成 &amp; （这可能与TOMCAT的配置有关），所以这里要把它转回 &
    //对于中文queryString,转成urlEncode
    public static String urlDecode(String url) {
        url = StringEscapeUtils.unescapeHtml4(url);
        StringBuilder result = new StringBuilder();
        String[] params = url.split("\\?");
        if (params.length == 2) {
            result.append(params[0]);
            result.append("?");
            for (String str : params[1].split("&")) {
                String[] values = str.split("=");
                if (values.length == 2) {
                    result.append(values[0]);
                    result.append("=");
                    result.append(urlEncode(values[1]));
                    result.append("&");
                }
            }
        } else {
            result.append(params[0]);
        }
        return result.toString();
    }

    public static String urlEncode(String str) {
        if (StrKit.isBlank(str)) {
            return str;
        }
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }
}

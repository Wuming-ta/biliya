package com.jfeat.config.utils;

import com.jfeat.config.model.Config;

import java.text.MessageFormat;
import java.util.Base64;

/**
 * @author jackyhuang
 * @date 2018/9/29
 */
public class ConfigUtils {
    private ConfigUtils() {

    }

    public static String getFallbackUrl(String key, Object... args) {
        Config wxConfig = Config.dao.findByKey("wx.host");
        String wxHost = wxConfig != null ? wxConfig.getValue() : "";
        Config config = Config.dao.findByKey(key);
        String productDetailValue = config != null ? config.getValue() : "";
        String fallback = MessageFormat.format(productDetailValue, args);
        String wxUrl = wxHost + "?fallback=" + Base64.getEncoder().encodeToString(fallback.getBytes());
        return wxUrl;
    }
}

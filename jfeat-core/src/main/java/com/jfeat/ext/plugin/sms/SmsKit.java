package com.jfeat.ext.plugin.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jackyhuang
 * @date 2018/5/18
 */
public class SmsKit {

    private static final Logger logger = LoggerFactory.getLogger(SmsKit.class);

    private static Map<String, ConfigData> configDataMap = new ConcurrentHashMap<>();
    private static String defaultName = "default";

    public static void init(String appid, String appkey, String defaultTemplateId) {
        init(defaultName, appid, appkey, defaultTemplateId, null, null);
    }

    public static void init(String name, String appid, String appkey, String defaultTemplateId, String signName, String vender) {
        ConfigData configData = new ConfigData(appid, appkey, defaultTemplateId, signName, vender);
        if (configDataMap.get(name) != null) {
            throw new RuntimeException("init failure. name already registered. " + name);
        }
        configDataMap.put(name, configData);
    }

    public static void send(String message, String phone) {
        send(defaultName, message, phone);
    }

    public static void send(String name, String message, String phone) {
        List<String> messages = new ArrayList<>();
        messages.add(message);
        send(name, messages, phone);
    }

    public static void send(List<String> messages, String phone) {
        send(defaultName, messages, phone);
    }

    public static void send(String name, List<String> messages, String phone) {
        ConfigData configData = getConfigData(name);
        SmsSender smsSender = getSmsSender(configData);
        smsSender.addPhone(phone)
                .addParams(messages)
                .send();
    }

    public static void send(String name, String key, String value, String phone) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(key, value);
        send(name, paramMap, phone);
    }

    public static void send(String name, Map<String, String> paramMap, String phone) {
        ConfigData configData = getConfigData(name);
        SmsSender smsSender = getSmsSender(configData);
        smsSender.addPhone(phone)
                .addParams(paramMap)
                .send();
    }

    private static ConfigData getConfigData(String name) {
        if (name == null) {
            logger.warn("name is null, use defaultName.");
            name = defaultName;
        }
        ConfigData configData = configDataMap.get(name);
        if (configData == null) {
            logger.error("configData not found for name: {}, now trying the defaultName", name);
            configData = configDataMap.get(defaultName);
        }
        if (configData == null) {
            throw new RuntimeException("ConfigData not found for name " + name);
        }
        return configData;
    }

    private static SmsSender getSmsSender(ConfigData configData) {
        SmsSender smsSender;
        if ("Aliyun".equalsIgnoreCase(configData.getVender())) {
            smsSender = new SmsSenderAliyunImpl(configData);
        }
        else {
            smsSender = new SmsSenderQCloudImpl(configData);
        }
        return smsSender;
    }
}

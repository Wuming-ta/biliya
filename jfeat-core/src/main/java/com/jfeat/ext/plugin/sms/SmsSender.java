package com.jfeat.ext.plugin.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author jackyhuang
 * @date 2018/7/19
 */
public abstract class SmsSender {

    protected static final Logger logger = LoggerFactory.getLogger(SmsSender.class);

    protected ConfigData configData;
    protected String phone;
    protected List<String> params = new ArrayList<>();
    protected Map<String, String> map = new LinkedHashMap<>();

    public SmsSender(ConfigData configData) {
        this.configData = configData;
    }

    public SmsSender addPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public SmsSender addParam(String param) {
        this.params.add(param);
        return this;
    }

    public SmsSender addParams(List<String> params) {
        this.params = params;
        return this;
    }

    public SmsSender addParam(String key, String value) {
        this.map.put(key, value);
        return this;
    }

    public SmsSender addParams(Map<String, String> map) {
        this.map.putAll(map);
        return this;
    }

    public abstract void send();
}

package com.jfeat.ext.plugin.sms;

/**
 * @author jackyhuang
 * @date 2018/5/18
 */
public class ConfigData {
    private String signName;
    private String appid;
    private String appkey;
    private String defaultTemplateId;
    private String vender;

    public ConfigData(String appid, String appkey, String defaultTemplateId, String signName, String vender) {
        this.appid = appid;
        this.appkey = appkey;
        this.defaultTemplateId = defaultTemplateId;
        this.signName = signName;
        this.vender = vender;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getDefaultTemplateId() {
        return defaultTemplateId;
    }

    public void setDefaultTemplateId(String defaultTemplateId) {
        this.defaultTemplateId = defaultTemplateId;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getVender() {
        return vender;
    }

    public void setVender(String vender) {
        this.vender = vender;
    }
}

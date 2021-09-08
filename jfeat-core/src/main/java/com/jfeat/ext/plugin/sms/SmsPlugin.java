package com.jfeat.ext.plugin.sms;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.IPlugin;

/**
 * @author jackyhuang
 * @date 2018/5/18
 */
public class SmsPlugin implements IPlugin {

    private String name;
    private String appid;
    private String appkey;
    private String defaultTemplateId;
    private String signName;
    private String vender;

    public SmsPlugin(String appid, String appkey, String defaultTemplateId) {
        this(null, appid, appkey, defaultTemplateId, null, null);
    }

    public SmsPlugin(String name, String appid, String appkey, String defaultTemplateId, String signName, String vender) {
        this.name = name;
        this.appid = appid;
        this.appkey = appkey;
        this.defaultTemplateId = defaultTemplateId;
        this.signName = signName;
        if (vender != null && !vender.equals("")) {
            this.vender = vender;
        }
    }

    @Override
    public boolean start() {
        if (StrKit.isBlank(this.name)) {
           SmsKit.init(this.appid, this.appkey, this.defaultTemplateId);
        }
        else {
            SmsKit.init(this.name, this.appid, this.appkey, this.defaultTemplateId, this.signName, this.vender);
        }
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}

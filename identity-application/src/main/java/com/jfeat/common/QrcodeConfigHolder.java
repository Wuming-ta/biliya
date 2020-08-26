package com.jfeat.common;

import com.jfeat.config.model.Config;
import com.jfinal.kit.StrKit;

/**
 * Created by jackyhuang on 2017/8/5.
 */
public class QrcodeConfigHolder {
    private static QrcodeConfigHolder me = new QrcodeConfigHolder();
    public static QrcodeConfigHolder me() {
        return me;
    }

    private String[] contents;
    private String footer;
    private String logoUrl;
    private String infoUrl;
    private Boolean showAvatar;
    // show ext redirect link
    private Integer rdCode = 1;

    private static final String INFO_URL_KEY = "mall.qrcode.info_url";
    private static final String LOGO_URL_KEY = "mall.qrcode.logo_url";
    private static final String WX_HOST_KEY = "wx.host";

    private String getConfigValue(String key, String defaultValue) {
        Config config = Config.dao.findByKey(key);
        if (config != null && StrKit.notBlank(config.getValue())) {
            return config.getValue();
        }
        return defaultValue;
    }

    public String getWxHost() {
        return getConfigValue(WX_HOST_KEY, "");
    }

    public String getInfoUrl() {
        return getConfigValue(INFO_URL_KEY, infoUrl);
    }

    public QrcodeConfigHolder setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
        return this;
    }

    public String getLogoUrl() {
        return getConfigValue(LOGO_URL_KEY, logoUrl);
    }

    public QrcodeConfigHolder setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
        return this;
    }

    public Boolean getShowAvatar() {
        return showAvatar;
    }

    public QrcodeConfigHolder setShowAvatar(Boolean showAvatar) {
        this.showAvatar = showAvatar;
        return this;
    }

    public void setContents(String[] contents) {
        this.contents = contents;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String[] getContents() {
        return this.contents;
    }

    public String getFooter() {
        return this.footer;
    }

    public Integer getRdCode() {
        return rdCode;
    }

    public QrcodeConfigHolder setRdCode(Integer rdCode) {
        this.rdCode = rdCode;
        return this;
    }
}

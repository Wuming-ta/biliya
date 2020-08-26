/*
 *   Copyright (C) 2014-2016 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */

package com.jfeat.wechat.config;

import com.jfeat.config.model.Config;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.wxaapp.WxaConfig;

/**
 * Created by jackyhuang on 16/9/1.
 */
public class WxConfig {

    public static final String TOKEN_KEY = "wx.token";
    public static final String ENCRYPT_MESSAGE_KEY = "wx.encrypt_message";
    public static final String ENCODING_AES_KEY_KEY = "wx.encoding_aes_key";
    public static final String APP_ID_KEY = "wx.app_id";
    public static final String APP_SECRET_KEY = "wx.app_secret";
    public static final String HOST_KEY = "wx.host";
    public static final String PARTNER_ID_KEY = "wx.partner_id";
    public static final String PARTNER_KEY = "wx.partner_key";
    public static final String CERT_PATH_KEY = "wx.cert_path";
    public static final String WXA_APPID_KEY = "wx.wxa_appid";
    public static final String WXA_APP_SECRET_KEY = "wx.wxa_app_secret";
    public static final String AUTO_REG_KEY = "wx.auto_reg";
    public static final String APP_NAME_KEY = "wx.app_name";
    public static final String APP_APPID_KEY = "wx.app_appid";
    public static final String APP_APP_SECRET_KEY = "wx.app_app_secret";
    public static final String APP_APP_PARTNER_ID_KEY = "wx.app_partner_id";
    public static final String APP_APP_PARTNER_KEY = "wx.app_partner_key";
    public static final String APP_CERT_PATH_KEY = "wx.app_cert_path";

    private static Config getConfig(String key) {
        Config config = Config.dao.findByKey(key);
            if (config == null) {
                throw new RuntimeException("Weixin config " + key + " is not set.");
            }
        return config;
    }

    public static boolean hasValues() {
        return StrKit.notBlank(getToken());
    }

    public static ApiConfig getApiConfig() {
        ApiConfig ac = new ApiConfig();

        // 配置微信 API 相关常量
        ac.setToken(getToken());
        ac.setAppId(getAppId());
        ac.setAppSecret(getAppSecret());

        /**
         *  是否对消息进行加密，对应于微信平台的消息加解密方式：
         *  1：true进行加密且必须配置 encodingAesKey
         *  2：false采用明文模式，同时也支持混合模式
         */
        ac.setEncryptMessage(isEncryptMessage());
        ac.setEncodingAesKey(getEncodingAesKey());
        return ac;
    }

    public static WxaConfig getWxaConfig() {
        WxaConfig wc = new WxaConfig();

        wc.setAppId(getWxaAppId());
        wc.setAppSecret(getWxaAppSecretKey());
        return wc;
    }

    public static String getHost() {
        return getConfig(HOST_KEY).getValue();
    }

    public static String getToken() {
        return getConfig(TOKEN_KEY).getValue();
    }

    public static boolean isEncryptMessage() {
        return Boolean.parseBoolean(getConfig(ENCRYPT_MESSAGE_KEY).getValue());
    }

    public static String getEncodingAesKey() {
        return getConfig(ENCODING_AES_KEY_KEY).getValue();
    }

    public static String getAppId() {
        return getConfig(APP_ID_KEY).getValue();
    }

    public static String getAppSecret() {
        return getConfig(APP_SECRET_KEY).getValue();
    }

    public static String getPartnerId() {
        return getConfig(PARTNER_ID_KEY).getValue();
    }

    public static String getPartnerKey() {
        return getConfig(PARTNER_KEY).getValue();
    }

    public static String getCertPath() {
        return getConfig(CERT_PATH_KEY).getValue();
    }

    public static String getWxaAppId() {
        return getConfig(WXA_APPID_KEY).getValue();
    }

    public static String getWxaAppSecretKey() {
        return getConfig(WXA_APP_SECRET_KEY).getValue();
    }

    public static boolean getAutoReg() {
        return getConfig(AUTO_REG_KEY).getValueToBoolean();
    }

    public static String getAppAppId() {
        return getConfig(APP_APPID_KEY).getValue();
    }

    public static String getAppAppSecret() {
        return getConfig(APP_APP_SECRET_KEY).getValue();
    }

    public static String getAppName() {
        return getConfig(APP_NAME_KEY).getValue();
    }

    public static String getAppPartnerId() {
        return getConfig(APP_APP_PARTNER_ID_KEY).getValue();
    }

    public static String getAppPartnerKey() {
        return getConfig(APP_APP_PARTNER_KEY).getValue();
    }

    public static String getAppCertPath() {
        return getConfig(APP_CERT_PATH_KEY).getValue();
    }
}

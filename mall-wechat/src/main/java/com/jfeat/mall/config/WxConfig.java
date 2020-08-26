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

package com.jfeat.mall.config;


import com.jfeat.mall.api.SysConfigApi;
import com.jfeat.mall.api.response.SysConfigResponse;
import com.jfinal.weixin.sdk.api.ApiConfig;

/**
 * Created by jacky on 4/22/16.
 */
public class WxConfig extends BaseConfig {

    public static String getHost() {
        return getResponseData("wx.host").getValue();
    }

    public static String getToken() {
        return getResponseData("wx.token").getValue();
    }

    public static boolean isEncryptMessage() {
        return Boolean.parseBoolean(getResponseData("wx.is_encrypt_message").getValue());
    }

    public static String getEncodingAesKey() {
        return getResponseData("wx.encoding_aes_key").getValue();
    }

    public static String getAppId() {
        return getResponseData("wx.app_id").getValue();
    }

    public static String getAppSecret() {
        return getResponseData("wx.app_secret").getValue();
    }

    public static String getPartnerId() {
        return getResponseData("wx.partner_id").getValue();
    }

    public static String getPartnerKey() {
        return getResponseData("wx.partner_key").getValue();
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
}

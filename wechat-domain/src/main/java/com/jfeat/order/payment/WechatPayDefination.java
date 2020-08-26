package com.jfeat.order.payment;

import com.jfeat.identity.model.User;
import com.jfeat.wechat.config.WxConfig;
import com.jfinal.weixin.sdk.api.PaymentApi;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2019/5/30
 */
public class WechatPayDefination {

    public static final String WX_TYPE = "WX";
    public static final String WXA_TYPE = "WXA";
    public static final String NATIVE_TYPE = "NATIVE";
    public static final String APP_TYPE = "APP";

    public static final Map<String, String> TYPES = new HashMap<>();
    static {
        TYPES.put(WX_TYPE, PaymentApi.TradeType.JSAPI.name());
        TYPES.put(WXA_TYPE, PaymentApi.TradeType.JSAPI.name());
        TYPES.put(NATIVE_TYPE, PaymentApi.TradeType.NATIVE.name());
        TYPES.put(APP_TYPE, PaymentApi.TradeType.APP.name());
    }

    /**
     *        //微信公众号(Wechat public account)
     *         WPA,
     *         //小程序
     *         MINI_PROGRAM,
     *         //手机应用程序
     *         APP_ANDROID,
     *         APP_IOS,
     *         IPAD,
     *         //其他
     *         OTHER
     */
    public static final Map<String, String> ORDER_ORIGINS = new HashMap<>();
    static {
        ORDER_ORIGINS.put("WPA", PaymentApi.TradeType.JSAPI.name());
        ORDER_ORIGINS.put("APP_ANDROID", PaymentApi.TradeType.APP.name());
        ORDER_ORIGINS.put("APP_IOS", PaymentApi.TradeType.APP.name());
    }

    public static String getAppId(String type) {
        if (WXA_TYPE.equalsIgnoreCase(type)) {
            return WxConfig.getWxaAppId();
        }
        if (APP_TYPE.equalsIgnoreCase(type)) {
            return WxConfig.getAppAppId();
        }
        return WxConfig.getAppId();
    }

    public static String getPartnerId(String type) {
        if (APP_TYPE.equalsIgnoreCase(type)) {
            return WxConfig.getAppPartnerId();
        }
        return WxConfig.getPartnerId();
    }

    public static String getPartnerKey(String type) {
        if (APP_TYPE.equalsIgnoreCase(type)) {
            return WxConfig.getAppPartnerKey();
        }
        return WxConfig.getPartnerKey();
    }

    public static String getOpenid(String type, User user) {
        if (WXA_TYPE.equalsIgnoreCase(type)) {
            return user.getWxaOpenid();
        }
        if (WX_TYPE.equalsIgnoreCase(type)) {
            return user.getWeixin();
        }
        return null;
    }

    public static String getCert(String type) {
        if (APP_TYPE.equalsIgnoreCase(type)) {
            return WxConfig.getAppCertPath();
        }
        return WxConfig.getCertPath();
    }
}

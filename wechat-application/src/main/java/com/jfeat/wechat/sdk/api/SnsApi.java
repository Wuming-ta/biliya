package com.jfeat.wechat.sdk.api;

import com.jfinal.weixin.sdk.kit.ParaMap;
import com.jfinal.weixin.sdk.utils.HttpUtils;

/**
 * @author jackyhuang
 * @date 2018/7/20
 */
public class SnsApi {

    private static String validate = " https://api.weixin.qq.com/sns/auth";

    public SnsApi() {
    }

    public static AuthResult validateAccessToken(String accessToken, String openId) {
        ParaMap pm = ParaMap.create("access_token", accessToken).put("openid", openId);
        return new AuthResult(HttpUtils.get(validate, pm.getData()));
    }
}
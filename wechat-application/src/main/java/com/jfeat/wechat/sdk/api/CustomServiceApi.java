package com.jfeat.wechat.sdk.api;


import com.jfinal.weixin.sdk.api.AccessTokenApi;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.jfinal.weixin.sdk.utils.JsonUtils;

import java.io.File;
import java.util.HashMap;

/**
 * Created by kang on 2017/4/6.
 */
public class CustomServiceApi extends com.jfinal.weixin.sdk.api.CustomServiceApi {
    private static String getInviteWorkerUrl = "https://api.weixin.qq.com/customservice/kfaccount/inviteworker?access_token=";
    private static String uploadKfAccountHeadImgUrl = "https://api.weixin.qq.com/customservice/kfaccount/uploadheadimg?access_token=";

    public CustomServiceApi() {

    }

    public static ApiResult inviteWorker(String kf_account, String invite_wx) {
        String accessToken = AccessTokenApi.getAccessTokenStr();
        HashMap params = new HashMap();
        params.put("kf_account", kf_account);
        params.put("invite_wx", invite_wx);
        String jsonResult = HttpUtils.post(getInviteWorkerUrl + accessToken, JsonUtils.toJson(params));
        return new ApiResult(jsonResult);
    }

    public static ApiResult uploadKfAccountHeadImg(String kf_account, File headImg) {
        String accessToken = AccessTokenApi.getAccessTokenStr();
        String url = uploadKfAccountHeadImgUrl + accessToken + "&kf_account=" + kf_account;
        String jsonResult = HttpUtils.upload(url, headImg, (String)null);
        return new ApiResult(jsonResult);
    }

}

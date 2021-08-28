package com.jfeat.wechat.api.fix;

import com.jfeat.core.BaseController;
import com.jfeat.http.utils.HttpUtils;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import com.jfinal.wxaapp.WxaConfig;
import com.jfinal.wxaapp.WxaConfigKit;
import com.jfinal.wxaapp.api.WxaUserApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class FixWxaUserApi extends WxaUserApi {
    public static Logger logger = LoggerFactory.getLogger(FixWxaUserApi.class);
    private static String jsCode2sessionUrl = "https://api.weixin.qq.com/sns/jscode2session";

    @Override
    public ApiResult getSessionKey(String jsCode) {
        WxaConfig wc = WxaConfigKit.getWxaConfig();
        Map<String, String> params = new HashMap();
        params.put("appid", wc.getAppId());
        params.put("secret", wc.getAppSecret());
        params.put("js_code", jsCode);
        params.put("grant_type", "authorization_code");
        String para = PaymentKit.packageSign(params, false);
        String url = jsCode2sessionUrl + "?" + para;
        logger.info("FixWxaUserApi.getSessionKey url = {}", url);

        return new ApiResult(HttpUtils.get(url));
        //return super.getSessionKey(jsCode);
    }
}

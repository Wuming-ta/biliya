package com.jfeat.ext.plugin.vip;

import com.jfeat.ext.plugin.*;
import com.jfeat.http.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 成长值接口
 */
public class PointApi extends BaseApi {

    private static final Logger logger = LoggerFactory.getLogger(PointApi.class);

    private static final String UPDATE_VIP_POINT_URL = "/api/vip/accounts/point";

    public PointApi() {
        BasePlugin vipPlugin = ExtPluginHolder.me().get(VipPlugin.class);
        init(vipPlugin);
    }

    /**
     * 更新会员成长值，amount为消费金额
     *
     * @param
     * @return
     */
    public ApiResult updateAccountPoint(String account, BigDecimal amount) {
        String url = getBaseUrl() + UPDATE_VIP_POINT_URL;
        Map<String, Object> param = new HashMap<>();
        param.put("account", account);
        param.put("amount", amount);
        String data = JsonKit.toJson(param);
        logger.debug(data);
        String result = HttpUtils.post(url, data, getAuthorizationHeader());
        logger.debug("post url = {}, data = {}, result = {}", url, data, result);
        return ApiResult.create(result);
    }

}

package com.jfeat.ext.plugin.vip;

import com.jfeat.ext.plugin.*;
import com.jfeat.ext.plugin.vip.bean.CouponPrice;
import com.jfeat.ext.plugin.vip.bean.DepositPackage;
import com.jfeat.ext.plugin.vip.bean.VipAccount;
import com.jfeat.http.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2019/6/11
 */
public class CouponApi extends BaseApi {

    private static final Logger logger = LoggerFactory.getLogger(CouponApi.class);

    private static final String COUPON_CALC_PRICE_URL = "/api/vip/coupon/calc_price?couponId=%s&userId=%s&totalPrice=%s";

    private static final String COUPON_RESET_URL = "/api/vip/coupon/reset_available";

    public CouponApi() {
        BasePlugin vipPlugin = ExtPluginHolder.me().get(VipPlugin.class);
        init(vipPlugin);
    }

    public CouponPrice calcPrice(String couponId, String userId, String totalPrice) {
        String url = String.format(getBaseUrl() + COUPON_CALC_PRICE_URL, couponId, userId, totalPrice);
        String result = HttpUtils.get(url, null, getAuthorizationHeader());
        logger.debug("get url = {}, result = {}", url, result);
        ApiResult apiResult = ApiResult.create(result);
        if (!apiResult.isSucceed()) {
            throw new RuntimeException("calc coupon price error. " + apiResult.getMessage());
        }
        Map<String, Object> data = apiResult.get("data");
        CouponPrice couponPrice = JsonKit.parseObject(JsonKit.toJson(data), CouponPrice.class);
        return couponPrice;
    }

    /**
     * 订单取消时候调用，进行重置第三方优惠券
     * @param couponId
     * @param userId
     * @param orderNumber
     * @return
     */
    public ApiResult resetCoupon(String couponId, String userId, String orderNumber) {
        String url = getBaseUrl() + COUPON_RESET_URL;
        Map<String, String> param = new HashMap<>();
        param.put("couponid", couponId);
        param.put("userid", userId);
        param.put("order", orderNumber);
        String data = JsonKit.toJson(param);
        logger.debug(data);
        String result = HttpUtils.post(url, data, getAuthorizationHeader());
        logger.debug("post url = {}, data = {}, result = {}", url, data, result);
        return ApiResult.create(result);
    }
}

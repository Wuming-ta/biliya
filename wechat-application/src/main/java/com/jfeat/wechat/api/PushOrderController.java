package com.jfeat.wechat.api;

import com.jfeat.config.model.Config;
import com.jfeat.core.RestController;
import com.jfeat.core.Service;
import com.jfeat.core.ServiceContext;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.order.payment.WechatPayDefination;
import com.jfeat.service.PayService;
import com.jfeat.service.exception.RetrieveOrderException;
import com.jfeat.wechat.config.WxConfig;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.PaymentApi;
import com.jfinal.weixin.sdk.kit.IpKit;
import com.jfinal.weixin.sdk.kit.PaymentKit;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/5/19
 */
@ControllerBind(controllerKey = "/rest/wx/push_order")
public class PushOrderController extends RestController {

    private static final String PAY_SERVICE_SUFFIX = "PayService";

    /**
     * order_type: Order - 购物下单, Wallet - 充值, etc
     *
     * return:
     * {
     *     "status_code": 0,
     *     "data": {
     *         "timeStamp": "1532333992",
     *         "package": "prepay_id=wx231619522626967165fc23cb0358628552",
     *         "paySign": "1CB648CA93410FE165DEED52B220F94E",
     *         "appId": "wxdd6cdd352e99a65d",
     *         "signType": "MD5",
     *         "nonceStr": "1532333992377",
     *         "title": "aaaa",
     *         "totalFee": 12.00,
     *         "codeUrl": "wx://xxx"
     *     }
     * }
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    @Validation(rules = { "order_number = required", "type = required", "order_type = required" })
    public void save() {
        String ip = IpKit.getRealIp(getRequest());
        if (ip != null && ip.contains(",")) {
            ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
        }
        if (StrKit.isBlank(ip)) {
            ip = "127.0.0.1";
        }

        User user = getAttr("currentUser");

        Map<String, Object> maps = convertPostJsonToMap();
        String orderNumber = (String) maps.get("order_number");
        String type = (String) maps.get("type");

        if (!WechatPayDefination.TYPES.keySet().contains(type)) {
            renderFailure("invalid.type");
            return;
        }

        String orderType = (String) maps.get("order_type");
        if (StrKit.isBlank(orderType)) {
            orderType = "Order";
        }
        orderType = StrKit.firstCharToUpperCase(StrKit.toCamelCase(orderType));
        String payServiceName = orderType + PAY_SERVICE_SUFFIX;

        Service service = ServiceContext.me().getService(payServiceName);
        if (service == null) {
            logger.error("PayService not found.");
            throw new RuntimeException("PayService not found.");
        }

        PayService payService = (PayService) service;
        Map<String, Object> orderMap = null;
        try {
            orderMap = payService.retrieveToPayOrder(orderNumber);
        } catch (RetrieveOrderException ex) {
            renderFailure("retrieve.order.failure");
            return;
        }
        logger.debug("orderMap = {}", orderMap);
        String description = (String) orderMap.get("description");
        BigDecimal totalPrice = (BigDecimal) orderMap.get("total_price");

        Map<String, String> params = new HashMap<>();
        params.put("appid", WechatPayDefination.getAppId(type));
        params.put("mch_id", WechatPayDefination.getPartnerId(type));
        if (WechatPayDefination.APP_TYPE.equalsIgnoreCase(type)) {
            params.put("body", WxConfig.getAppName() + "-" + convertBody(description));
        }
        else {
            params.put("body", convertBody(description));
        }
        params.put("detail", description);
        params.put("out_trade_no", orderType + "_" + orderNumber);
        params.put("product_id", orderNumber);
        params.put("total_fee", convertPrice(totalPrice.doubleValue()));

        params.put("spbill_create_ip", ip);
        params.put("trade_type", WechatPayDefination.TYPES.get(type));
        params.put("nonce_str", System.currentTimeMillis() / 1000L + "");
        params.put("notify_url", getNotifyUrl());
        String openid = WechatPayDefination.getOpenid(type, user);
        if (StrKit.notBlank(openid)) {
            params.put("openid", openid);
        }
        String sign = PaymentKit.createSign(params, WechatPayDefination.getPartnerKey(type));
        params.put("sign", sign);
        logger.debug("push order param: {}", params);
        String xmlResult = PaymentApi.pushOrder(params);
        Map result = PaymentKit.xmlToMap(xmlResult);

        logger.debug("push order result: {}", result);

        String returnCode = (String) result.get("return_code");
        if (!StrKit.isBlank(returnCode) && "SUCCESS".equals(returnCode)) {
            String resultCode = (String) result.get("result_code");
            if (!StrKit.isBlank(resultCode) && "SUCCESS".equals(resultCode)) {
                String prepayId = (String) result.get("prepay_id");
                String codeUrl = (String) result.get("code_url");
                String nonceStr = (String) result.get("nonce_str");
                Map<String, String> packageParams = new HashMap<>();
                if (WechatPayDefination.APP_TYPE.equalsIgnoreCase(type)) {
                    // https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_12&index=2
                    packageParams.put("appid", WechatPayDefination.getAppId(type));
                    packageParams.put("timestamp", System.currentTimeMillis() / 1000L + "");
                    packageParams.put("noncestr", nonceStr);
                    packageParams.put("package", "Sign=WXPay");
                    packageParams.put("prepayid", prepayId);
                    packageParams.put("partnerid", WechatPayDefination.getPartnerId(type));
                    String packageSign = PaymentKit.createSign(packageParams, WechatPayDefination.getPartnerKey(type));
                    packageParams.put("sign", packageSign);
                }
                else {
                    packageParams.put("appId", WechatPayDefination.getAppId(type));
                    packageParams.put("timeStamp", System.currentTimeMillis() / 1000L + "");
                    packageParams.put("nonceStr", System.currentTimeMillis() + "");
                    packageParams.put("package", "prepay_id=" + prepayId);
                    packageParams.put("signType", "MD5");
                    String packageSign = PaymentKit.createSign(packageParams, WechatPayDefination.getPartnerKey(type));
                    packageParams.put("paySign", packageSign);
                }

                packageParams.put("title", convertBody(description));
                packageParams.put("totalFee", totalPrice.toPlainString());
                packageParams.put("codeUrl", codeUrl);
                renderSuccess(packageParams);
                return;
            }
        }
        renderFailure(result.get("err_code_des") == null ? result.get("return_msg") : result.get("err_code_des"));
    }

    private String getNotifyUrl() {
        String url = "/rest/pub/wx/pay_notify";
        Config config = Config.dao.findByKey("misc.api_url");
        if (config == null) {
            logger.warn("misc.api_url config not found.");
            return url;
        }
        return config.getValue() + url;
    }

    protected String convertPrice(double price) {
        return String.valueOf(BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(100)).intValue());
    }

    protected String convertBody(String body) {
        if (body == null) {
            return "PAY";
        }
        return com.jfeat.kit.StrKit.negoString(body, 32);
    }

}

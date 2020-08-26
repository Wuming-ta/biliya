package com.jfeat.alipay.api;

import com.jfeat.alipay.config.AlipayException;
import com.jfeat.alipay.config.AlipayKit;
import com.jfeat.config.model.Config;
import com.jfeat.core.RestController;
import com.jfeat.core.Service;
import com.jfeat.core.ServiceContext;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.service.PayService;
import com.jfeat.service.exception.RetrieveOrderException;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/11/2
 */
@ControllerBind(controllerKey = "/rest/ali/push_order")
public class AlipayPushOrderController extends RestController {

    private static final String PAY_SERVICE_SUFFIX = "PayService";

    private static final String QRCODE_TYPE = "QRCODE";
    private static final String APP_TYPE = "APP";

    @Override
    @Before(CurrentUserInterceptor.class)
    @Validation(rules = { "order_number = required", "type = required", "order_type = required" })
    public void save() {
        User user = getAttr("currentUser");

        Map<String, Object> maps = convertPostJsonToMap();
        String orderNumber = (String) maps.get("order_number");
        String type = (String) maps.get("type");

        if (!APP_TYPE.equalsIgnoreCase(type) && !QRCODE_TYPE.equalsIgnoreCase(type)) {
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

        String outTradeNo = orderType + "_" + orderNumber;

        try {
            if (APP_TYPE.equalsIgnoreCase(type)) {
                String orderString = AlipayKit.appPay(description, description, outTradeNo, totalPrice, getNotifyUrl());
                Map<String, String> result = new HashMap<>();
                result.put("order_string", orderString);
                renderSuccess(result);
            }
            if (QRCODE_TYPE.equalsIgnoreCase(type)) {
                Map result = AlipayKit.prePay(outTradeNo, totalPrice, description, getNotifyUrl());
                renderSuccess(result);
            }
        }
        catch (AlipayException ex) {
            renderFailure(ex.getMessage());
        }
    }


    private String getNotifyUrl() {
        if (StrKit.notBlank(AlipayKit.DEV_NOTIFY_URL)) {
            return AlipayKit.DEV_NOTIFY_URL;
        }

        String url = "/rest/pub/ali/pay_notify";
        Config config = Config.dao.findByKey("misc.api_url");
        if (config == null) {
            logger.warn("misc.api_url config not found.");
            return url;
        }
        return config.getValue() + url;
    }
}

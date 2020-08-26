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

package com.jfeat.mall.api;

import com.google.common.collect.Maps;
import com.jfeat.kit.HttpKit;
import com.jfeat.mall.api.response.OrderShareResponse;
import com.jfeat.mall.api.response.MessageResponse;
import com.jfeat.mall.api.response.OrderResponse;
import com.jfinal.kit.JsonKit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jacky on 5/3/16.
 */
public class OrderApi extends BaseApi {
    private static String orderUrl = "/sys/rest/order";
    private static String orderShareUrl = "/sys/rest/order_share";

    private String getOrderUrl() {
        return host + orderUrl;
    }

    private String getOrderShareUrl() {
        return host + orderShareUrl;
    }

    public OrderResponse getOrder(String orderNumber) {
        String url = getOrderUrl() + "/" + orderNumber;
        return HttpKit.get(url, OrderResponse.class, getHeaders());
    }

    public MessageResponse payOrder(String orderNumber) {
        String url = getOrderUrl();
        Map<String, Object> data = new HashMap<>();
        data.put("order_number", orderNumber);
        return HttpKit.post(url, JsonKit.toJson(data), MessageResponse.class, getHeaders());
    }

    public MessageResponse paidNotifyOrder(String orderNumber, String paymentType, String tradeNumber) {
        String url = getOrderUrl() + "/" + orderNumber;
        Map<String, Object> data = new HashMap<>();
        data.put("payment_type", paymentType);
        data.put("trade_number", tradeNumber);
        data.put("status", "PAID_CONFIRM_PENDING");
        return HttpKit.put(url, JsonKit.toJson(data), MessageResponse.class, getHeaders());
    }

    public OrderShareResponse share(Integer userId, String orderNumber) {
        Map<String, Object> data = Maps.newHashMap();
        data.put("order_number", orderNumber);
        data.put("user_id", userId);
        OrderShareResponse response = HttpKit.post(getOrderShareUrl(), JsonKit.toJson(data), OrderShareResponse.class, getHeaders());
        return response;
    }
}

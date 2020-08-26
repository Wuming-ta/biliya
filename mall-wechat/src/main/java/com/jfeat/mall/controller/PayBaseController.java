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

package com.jfeat.mall.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfeat.mall.api.OrderApi;
import com.jfeat.mall.api.response.OrderResponse;
import com.jfeat.mall.api.response.OrderShareResponse;
import com.jfeat.mall.config.WxConfig;
import com.jfeat.mall.controller.link.MarketingShareLink;
import com.jfeat.mall.controller.link.MarketingShareLinkHolder;
import com.jfeat.mall.util.Sign;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.JsTicket;
import com.jfinal.weixin.sdk.api.JsTicketApi;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by jackyhuang on 16/12/5.
 */
public class PayBaseController extends ApiController {

    protected static Logger logger = LoggerFactory.getLogger(PayBaseController.class);

    protected static final String CREATED_PAY_PENDING = "CREATED_PAY_PENDING";
    protected static final String PAID_CONFIRM_PENDING = "PAID_CONFIRM_PENDING";
    protected static final String CONFIRMED_DELIVER_PENDING = "CONFIRMED_DELIVER_PENDING";

    protected static final List<String> EXCLUDED_MARKETING_LIST = Lists.newArrayList(
            "WHOLESALE"
    );

    protected static final String POCKET_IMAGE = "/assets/img/pocket-480.png";

    /**
     * 微信支付要求total_fee字段是整数，单位是分
     * @param price
     * @return
     */
    protected String convertPrice(double price) {
        return String.valueOf(BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(100)).intValue());
    }

    protected String convertBody(String body) {
        if (body == null) {
            return "PAY";
        }
        int maxLength = 50;
        byte[] bodyBytes = body.getBytes();
        int length = bodyBytes.length;
        if (length > maxLength) {
            byte[] bytes = new byte[maxLength];
            System.arraycopy(bodyBytes, 0, bytes, 0, maxLength);
            return new String(bytes);
        }
        return body;
    }

    protected String getShareCode(String orderNumber) {
        Integer userId = getAttr("user_id");
        OrderApi orderApi = new OrderApi();
        OrderShareResponse orderShareResponse = orderApi.share(userId, orderNumber);
        if (orderShareResponse.isSucceed()) {
            return orderShareResponse.getData().getCode();
        }
        return null;
    }

    /**
     * 优惠券领取链接是在本项目处理的
     * @param shareCode
     * @return
     */
    protected String buildCouponShareLink(String shareCode) {
        if (StrKit.isBlank(shareCode)) {
            return null;
        }
        StringBuilder link = new StringBuilder(WxConfig.getHost());
        link.append("/app/coupon?invite_code=");
        link.append(getAttrForStr("invite_code"));
        link.append("&share_code=");
        link.append(shareCode);
        return link.toString();
    }

    /**
     * 营销活动链接是前端处理的
     * @return
     */
    protected String buildMarketingShareLink(String fallback) {
        StringBuilder link = new StringBuilder(WxConfig.getHost());
        link.append("/app?invite_code=");
        link.append(getAttrForStr("invite_code"));
        link.append("&fallback=");
        link.append(fallback);
        return link.toString();
    }

    protected boolean isMarketing(OrderResponse orderResponse) {
        if (orderResponse != null
                && StrKit.notBlank(orderResponse.getData().getMarketing())
                && orderResponse.getData().getMarketing_id() != null
                && !EXCLUDED_MARKETING_LIST.contains(orderResponse.getData().getMarketing())) {
            return true;
        }
        return false;
    }

    protected String signShareConfig() {
        Map<String, Object> jsconfig = Maps.newHashMap();
        JsTicket jsTicket = JsTicketApi.getTicket(JsTicketApi.JsApiType.jsapi);
        String url = buildUrl(getRequest().getRequestURL().toString(), getRequest().getQueryString());
        jsconfig.putAll(Sign.sign(jsTicket.getTicket(), url));
        jsconfig.put("appId", WxConfig.getAppId());
        List<String> apiList = Lists.newArrayList();
        apiList.add("onMenuShareTimeline");
        apiList.add("onMenuShareAppMessage");
        jsconfig.put("jsApiList", apiList);
        //jsconfig.put("debug", true);
        return JsonKit.toJson(jsconfig);
    }

    private String buildUrl(String url, String queryString) {
        if (StrKit.isBlank(queryString)) {
            return url;
        }
        return url + "?" + queryString;
    }
}

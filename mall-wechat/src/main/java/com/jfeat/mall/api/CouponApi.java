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

import com.jfeat.kit.HttpKit;
import com.jfeat.mall.api.response.CouponResponse;
import com.jfeat.mall.api.response.MessageResponse;
import com.jfinal.kit.JsonKit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jackyhuang on 16/11/29.
 */
public class CouponApi extends BaseApi {
    private static String couponUrl = "/sys/rest/coupon";

    private String getCouponUrl() {
        return host + couponUrl;
    }

    public CouponResponse take(Integer userId, String shareCode, boolean clearNotify) {
        String url = getCouponUrl();
        Map<String, Object> data = new HashMap<>();
        data.put("code", shareCode);
        data.put("user_id", userId);
        data.put("clear_notify", clearNotify);
        return HttpKit.post(url, JsonKit.toJson(data), CouponResponse.class, getHeaders());
    }
}

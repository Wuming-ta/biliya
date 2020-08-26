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
import com.jfeat.kit.HttpException;
import com.jfeat.kit.HttpKit;
import com.jfeat.kit.HttpNotFoundException;
import com.jfeat.mall.api.response.*;
import com.jfinal.kit.JsonKit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jackyhuang on 2018/1/9.
 */
public class MerchantApi extends BaseApi {
    private static String merchantUrl = "/sys/rest/merchant";

    private String getMerchantUrl() {
        return host + merchantUrl;
    }

    public MerchantResponse getMerchant(Integer mid) {
        String url = getMerchantUrl() + "/" + mid;
        try {
            return HttpKit.get(url, MerchantResponse.class, getHeaders());
        }
        catch (HttpNotFoundException ex) {
            MerchantResponse response = new MerchantResponse();
            response.setStatusCode(ApiResponse.FAILURE);
            response.setMessage("Not Found");
            return response;
        }
    }
}

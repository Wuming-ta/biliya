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
import com.jfeat.mall.api.response.SellerResponse;

/**
 * Created by huangjacky on 16/7/16.
 */
public class SellerApi extends BaseApi {
    private static String sellerUrl = "/sys/rest/seller";

    private String getUrl() {
        return host + sellerUrl;
    }

    public String sellerStatus(int userId) {
        SellerResponse response = HttpKit.get(getUrl() + "?userId=" + userId, SellerResponse.class, getHeaders());
        if (response.isSucceed()) {
            return response.getData().getSellerStatus();
        }
        return SellerResponse.Data.NOT_SELLER;
    }
}

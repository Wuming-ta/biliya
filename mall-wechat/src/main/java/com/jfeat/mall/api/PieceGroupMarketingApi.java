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
import com.jfeat.mall.api.response.OrderResponse;
import com.jfeat.mall.api.response.PieceGroupPurchaseResponse;

/**
 * Created by jackyhuang on 2017/5/27.
 */
public class PieceGroupMarketingApi extends BaseApi {
    private static String pieceGroupPurchaseUrl = "/sys/rest/piece_group_purchase";

    private String getPieceGroupPurchaseUr() {
        return host + pieceGroupPurchaseUrl;
    }

    public PieceGroupPurchaseResponse findByMasterId(Integer masterId) {
        String url = getPieceGroupPurchaseUr() + "?masterId=" + masterId;
        return HttpKit.get(url, PieceGroupPurchaseResponse.class, getHeaders());
    }

}

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

package com.jfeat.mall.controller.link;

import com.jfeat.mall.api.PieceGroupMarketingApi;
import com.jfeat.mall.api.response.PieceGroupPurchaseResponse;

/**
 * Created by jackyhuang on 2017/5/27.
 */
public class PieceGroupShareLink implements MarketingShareLink {
    private PieceGroupPurchaseResponse response;
    private Integer masterId;

    @Override
    public boolean init(Integer masterId) {
        this.masterId = masterId;
        PieceGroupMarketingApi api = new PieceGroupMarketingApi();
        PieceGroupPurchaseResponse response = api.findByMasterId(masterId);
        this.response = response;
        if (response != null && response.isSucceed()) {
            return true;
        }
        return false;
    }


    @Override
    public String buildLink() {
        if (response != null && response.isSucceed()) {
            return "piecegroup-" + response.getData().getId() + "-" + masterId;
        }
        return null;
    }

    @Override
    public String buildTitle() {
        if (response != null && response.isSucceed()) {
            return "我 " + response.getData().getPrice() + " 元拼了 " + response.getData().getMarketing_name();
        }
        return null;
    }

    @Override
    public String buildDescription() {
        if (response != null && response.isSucceed()) {
            return "【拼团仅剩1个名额】 赶紧和你的朋友一起参团吧！";
        }
        return null;
    }

    @Override
    public String buildCover() {
        if (response != null && response.isSucceed()) {
            return response.getData().getCover();
        }
        return null;
    }
}

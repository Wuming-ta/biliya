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

package com.jfeat.product.purchase;

import com.google.gson.annotations.SerializedName;
import com.jfeat.identity.model.User;
import com.jfinal.kit.JsonKit;

import java.util.Map;

/**
 * Created by jackyhuang on 16/10/11.
 */
public class WeixinFollowedStrategy extends DefaultPurchaseStrategy {

    private static final String message = "未关注公众号. ";

    public WeixinFollowedStrategy() {
        this.name = "purchase.strategy.weixin.followed";
        this.displayName = "关注微信公众号";
    }

    @Override
    public StrategyResult canPurchase(StrategyParameter parameter, Map<String, Object> otherParam) {
        User user = User.dao.findById(parameter.getUserId());
        if (user != null && user.getFollowed() == 0) {
            logger.debug("WeixinFollowedStrategy evaluate as true.");
            return new StrategyResult();
        }
        return new StrategyResult(message);
    }

}


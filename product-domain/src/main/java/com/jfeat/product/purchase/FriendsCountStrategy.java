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
import com.jfeat.core.Service;
import com.jfeat.core.ServiceContext;
import com.jfeat.service.FriendsCountService;
import com.jfinal.kit.JsonKit;

import java.util.Map;

/**
 * Created by jackyhuang on 16/10/13.
 */
public class FriendsCountStrategy extends DefaultPurchaseStrategy {

    private static final String message = "朋友数量不能少于%1$d个, 还差%2$d个. ";

    public FriendsCountStrategy() {
        this.name = "purchase.strategy.friends.count";
        this.displayName = "直接朋友数量";
        StrategyParameterDefinition definition = new StrategyParameterDefinition();
        definition.setName("friends_count");
        definition.setDisplayName("直接朋友数量不少于");
        definition.setType(StrategyParameterDefinition.Type.INTEGER.toString());
        this.parameters.add(definition);
    }

    @Override
    public StrategyResult canPurchase(StrategyParameter strategyParameter, Map<String, Object> otherParam) {
        logger.debug("other param = {}", otherParam);
        Parameter parameter = gson.fromJson(JsonKit.toJson(otherParam), Parameter.class);
        if (parameter.getFriendsCount() == null) {
            logger.debug("parameter.getFriendsCount() return null.");
            return new StrategyResult(defaultMessage);
        }

        Service service = ServiceContext.me().getService(FriendsCountService.class.getName());
        if (service == null) {
            logger.warn("FriendsCountService not found.");
            return new StrategyResult(defaultMessage);
        }

        FriendsCountService friendsCountService = (FriendsCountService) service;
        long friends = friendsCountService.getFriendsCount(strategyParameter.getUserId());
        logger.debug("friends_count={}, actual_friends={}", parameter.getFriendsCount(), friends);
        if (friends >= parameter.getFriendsCount()) {
            return new StrategyResult();
        }

        return new StrategyResult(String.format(message, parameter.getFriendsCount(), parameter.getFriendsCount() - friends));
    }

    public class Parameter {
        @SerializedName("friends_count")
        private Integer friendsCount;

        public Integer getFriendsCount() {
            return friendsCount;
        }

        public void setFriendsCount(Integer friendsCount) {
            this.friendsCount = friendsCount;
        }
    }

}

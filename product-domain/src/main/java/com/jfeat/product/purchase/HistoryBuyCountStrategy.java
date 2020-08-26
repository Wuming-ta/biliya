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
import com.jfeat.service.HistoryBuyCountService;
import com.jfinal.kit.JsonKit;

import java.util.Map;

/**
 * Created by jackyhuang on 16/10/14.
 */
public class HistoryBuyCountStrategy extends DefaultPurchaseStrategy {

    private static final String message = "超出购买限额, 限购%1$d件, 你过去%2$d天内已购买过%3$d件. ";

    public HistoryBuyCountStrategy() {
        this.name = "purchase.strategy.history.buy.count";
        this.displayName = "过去一段时间购买该商品数量";

        StrategyParameterDefinition lastDaysDefinition = new StrategyParameterDefinition();
        lastDaysDefinition.setName("last_days");
        lastDaysDefinition.setDisplayName("过去多少天内购买过");
        lastDaysDefinition.setType(StrategyParameterDefinition.Type.INTEGER.toString());
        this.parameters.add(lastDaysDefinition);

        StrategyParameterDefinition countDefinition = new StrategyParameterDefinition();
        countDefinition.setName("count");
        countDefinition.setDisplayName("过去已购买数量");
        countDefinition.setType(StrategyParameterDefinition.Type.INTEGER.toString());
        this.parameters.add(countDefinition);
    }

    @Override
    public StrategyResult canPurchase(StrategyParameter strategyParameter, Map<String, Object> otherParam) {
        logger.debug("other param = {}", otherParam);
        Parameter parameter = gson.fromJson(JsonKit.toJson(otherParam), Parameter.class);
        if (parameter.getCount() == null) {
            logger.debug("parameter.getCount() return null.");
            return new StrategyResult(defaultMessage);
        }
        if (parameter.getLastDays() == null) {
            logger.debug("parameter.getLastDays() return null.");
            return new StrategyResult(defaultMessage);
        }

        Service service = ServiceContext.me().getService(HistoryBuyCountService.class.getName());
        if (service == null) {
            logger.warn("HistoryBuyCountService not found.");
            return new StrategyResult(defaultMessage);
        }

        HistoryBuyCountService historyBuyCountService = (HistoryBuyCountService) service;
        long historyBuyCount = historyBuyCountService.getHistoryBuyCount(strategyParameter.getProductId(), strategyParameter.getUserId(), parameter.getLastDays());
        logger.debug("defined count = {}, history count = {}", parameter.getCount(), historyBuyCount);
        if (historyBuyCount < parameter.getCount()) {
            return new StrategyResult();
        }

        return new StrategyResult(String.format(message, parameter.getCount(), parameter.getLastDays(), historyBuyCount));
    }

    public class Parameter {
        private Integer count;
        @SerializedName("last_days")
        private Integer lastDays;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Integer getLastDays() {
            return lastDays;
        }

        public void setLastDays(Integer lastDays) {
            this.lastDays = lastDays;
        }
    }
}

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
import com.jfinal.kit.JsonKit;

import java.util.Map;

/**
 * Created by jackyhuang on 16/10/11.
 */
public class ProductQuantityStrategy extends DefaultPurchaseStrategy {

    private static final String message = "超出购买限额, 你只能购买%1$s件. ";

    public ProductQuantityStrategy() {
        StrategyParameterDefinition definition = new StrategyParameterDefinition();
        definition.setName("defined_quantity");
        definition.setDisplayName("每次购买数量不大于");
        definition.setType(StrategyParameterDefinition.Type.INTEGER.toString());
        this.parameters.add(definition);
        this.name = "purchase.strategy.product.quantity";
        this.displayName = "产品购买数量";
    }

    @Override
    public StrategyResult canPurchase(StrategyParameter strategyParameter, Map<String, Object> otherParam) {
        logger.debug("other param = {}", otherParam);
        Parameter parameter = gson.fromJson(JsonKit.toJson(otherParam), Parameter.class);
        if (strategyParameter.getQuantity() == null ||
                parameter.getDefinedQuantity() == null) {
            logger.debug("strategyParameter.getQuantity() return null or parameter.getDeinedQuantity() return null.");
            return new StrategyResult(message);
        }
        if (strategyParameter.getQuantity() > parameter.getDefinedQuantity()) {
            logger.debug("quantity = {}, defined_quantity = {}", strategyParameter.getQuantity(), parameter.getDefinedQuantity());
            return new StrategyResult(String.format(message, parameter.getDefinedQuantity()));
        }
        return new StrategyResult();
    }

    public class Parameter {
        @SerializedName("defined_quantity")
        private Integer definedQuantity;

        public Integer getDefinedQuantity() {
            return definedQuantity;
        }

        public void setDefinedQuantity(Integer definedQuantity) {
            this.definedQuantity = definedQuantity;
        }
    }
}

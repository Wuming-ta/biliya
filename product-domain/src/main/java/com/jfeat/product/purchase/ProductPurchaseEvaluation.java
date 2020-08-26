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

import com.google.common.collect.Lists;
import com.jfeat.kit.JsonKit;
import com.jfeat.product.model.ProductPurchaseStrategy;
import com.jfeat.product.model.ProductPurchaseStrategyItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by jackyhuang on 16/10/11.
 */
public class ProductPurchaseEvaluation {

    private static Logger logger = LoggerFactory.getLogger(ProductPurchaseEvaluation.class);

    public enum Operator {
        AND {
            @Override
            public boolean operator(boolean lValue, boolean rValue) {
                return lValue && rValue;
            }
        },
        OR {
            @Override
            public boolean operator(boolean lValue, boolean rValue) {
                return lValue || rValue;
            }
        };
        public abstract boolean operator(boolean lValue, boolean rValue);
    }

    private StringBuffer lastError = new StringBuffer();

    public String getLastError() {
        return lastError.toString();
    }

    public void addError(String lastError) {
        this.lastError.append(lastError);
    }

    public void clearError() {
        this.lastError.setLength(0);
    }

    public boolean evaluate(Integer productId, Integer userId, Integer quantity) {
        clearError();
        ProductPurchaseStrategy productPurchaseStrategy = ProductPurchaseStrategy.dao.findByProductId(productId);
        if (productPurchaseStrategy == null) {
            return true;
        }

        boolean canPurchase = true;
        for (ProductPurchaseStrategyItem item : productPurchaseStrategy.getItems()) {

            Map<String, Object> param = null;
            try {
                param = JsonKit.convertToMap(item.getParam());
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("convert purchase strategy parameter to map error." + e.getMessage());
                canPurchase = false;
                break;
            }

            PurchaseStrategy purchaseStrategy = ProductPurchaseHolder.me().getStrategy(item.getName());
            StrategyResult result = purchaseStrategy.canPurchase(new StrategyParameter(productId, userId, quantity), param);
            canPurchase = Operator.valueOf(item.getOperator()).operator(canPurchase, result.isSucceed());
            if (!result.isSucceed()) {
                addError(result.getErrorMessage());
            }

            logger.debug("evaluate result is {}", result);
        }

        if (canPurchase) {
            clearError();
        }

        return canPurchase;
    }

}

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
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by jackyhuang on 16/10/11.
 */
public class ProductPurchaseHolder {
    private static Logger logger = LoggerFactory.getLogger(ProductPurchaseHolder.class);
    private static ProductPurchaseHolder me = new ProductPurchaseHolder();

    private Map<String, PurchaseStrategy> strategyMap = Maps.newConcurrentMap();
    // add default implementation to prevent null pointer exception.
    private PurchaseStrategy defaultStrategy = new DefaultPurchaseStrategy();

    public static ProductPurchaseHolder me() {
        return me;
    }

    public void register(PurchaseStrategy strategy) {
        String name = strategy.getName();
        if (strategyMap.get(name) == null) {
            strategyMap.put(name, strategy);
            logger.info("register product purchase: name={}, class={}", name, strategy.getClass().getName());
        }
    }

    public PurchaseStrategy getStrategy(String name) {
        PurchaseStrategy strategy = strategyMap.get(name);
        if (strategy != null) {
            return strategy;
        }
        return defaultStrategy;
    }

    public List<PurchaseStrategy> getStrategies() {
        return Lists.newArrayList(strategyMap.values());
    }
}

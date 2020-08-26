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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jfeat.kit.JsonKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by jackyhuang on 16/10/11.
 */
public class DefaultPurchaseStrategy implements PurchaseStrategy {

    protected static Logger logger = LoggerFactory.getLogger(DefaultPurchaseStrategy.class);
    protected static final String defaultMessage = "未达到限购条件. ";

    protected List<StrategyParameterDefinition> parameters = Lists.newArrayList();
    protected String name = "purchase.strategy.default";
    protected String displayName = "默认策略";
    protected Gson gson = new GsonBuilder().create();

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public StrategyResult canPurchase(StrategyParameter parameter, Map<String, Object> otherParam) {
        return new StrategyResult(defaultMessage);
    }

    @Override
    public List<StrategyParameterDefinition> getParameterDefinition() {
        return parameters;
    }
}

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

import java.util.List;
import java.util.Map;

/**
 * Created by jackyhuang on 16/10/11.
 */
public interface PurchaseStrategy {
    String getDisplayName();
    String getName();
    StrategyResult canPurchase(StrategyParameter parameter, Map<String, Object> otherParam);
    List<StrategyParameterDefinition> getParameterDefinition();
}

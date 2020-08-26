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

package com.jfeat.settlement.service;

import com.jfeat.core.BaseService;
import com.jfeat.marketing.Marketing;
import com.jfeat.marketing.MarketingHolder;
import com.jfeat.partner.model.SettlementProportion;
import com.jfeat.product.model.Product;

import java.math.BigDecimal;

/**
 * Created by jackyhuang on 17/5/19.
 */
public class SettlementService extends BaseService {

    /**
     * 计算产品的分成, 取第一级分销分成比例计算。如果有定义营销活动,则取营销活动的价格来计算。
     * @param product
     * @param marketingType
     * @param marketingId
     * @return
     */
    public BigDecimal calcProductSettlement(Product product, String marketingType, Integer marketingId) {
        BigDecimal settle = new BigDecimal(0);
        //get level 1 seller proportion
        SettlementProportion settlementProportion = SettlementProportion.dao.findBySeller(1);
        if (settlementProportion != null) {
            BigDecimal price = product.getPrice();
            Marketing marketing = MarketingHolder.me().getMarketing(marketingType, marketingId, null, null, null, null);
            if (marketing != null) {
                price = marketing.getPrice();
            }
            SettlementProportion.Proportion proportion = settlementProportion.getProportionObject();
            if (proportion.isPercentage()) {
                BigDecimal profit = price.subtract(product.getCostPrice());
                if (SettlementProportion.SettlementType.SALES_AMOUNT.toString().equals(proportion.getSettlementtype())) {
                    profit = price;
                }
                if (profit.compareTo(new BigDecimal(0)) > 0) {
                    //最后除以1目的是为了保留2位小数。
                    settle = profit.multiply(BigDecimal.valueOf(proportion.getValue() * 1.0 / 100)).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
                }
            }
        }
        return settle;
    }
}

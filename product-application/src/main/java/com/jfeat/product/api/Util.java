package com.jfeat.product.api;

import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductSettlementProportion;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jackyhuang
 * @date 2019/10/7
 */
public class Util {

    /**
     * 计算该产品的分成。需要在product.removeSecretAttrs之前调用。因为removeSecretAttrs会清掉cost_price
     * @param product
     */
    public static void calcProductSettlement(Product product) {
        List<ProductSettlementProportion> settlementProportions = ProductSettlementProportion.dao.findByProductId(product.getId());
        settlementProportions = settlementProportions.stream().peek(p -> {
            ProductSettlementProportion.Proportion proportion = p.getProportionObject();
            if (proportion.isFixedvalue()) {
                p.put("settlementValue", proportion.getValue());
            }
            if (proportion.isPercentage()) {
                BigDecimal productProfit = product.getPrice().subtract(product.getCostPrice());
                //单品的利润 * 分成比率
                //最后除以1目的是为了保留2位小数。
                BigDecimal settlementValue = productProfit.multiply(BigDecimal.valueOf(proportion.getValue() * 1.0 / 100))
                        .divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
                p.put("settlementValue", settlementValue);
            }
            p.put("proportion", proportion);
        }).collect(Collectors.toList());
        product.put("settlementProportions", settlementProportions);
    }
}

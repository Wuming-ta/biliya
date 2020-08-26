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

package com.jfeat.settlement.api;

import com.jfeat.core.RestController;
import com.jfeat.partner.model.SettlementProportion;
import com.jfeat.product.model.Product;
import com.jfeat.settlement.service.SettlementService;
import com.jfinal.ext.route.ControllerBind;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by huangjacky on 16/7/8.
 */
@ControllerBind(controllerKey = "/rest/product_settlement")
public class ProductSettlementController extends RestController {

    private SettlementService settlementService = new SettlementService();

    /**
     * GET /rest/product_settlement?id=1&marketingType=PIECE-GROUP&marketingId=1
     * param:
     *   string id - required, the product id
     *   string marketingType - optional, the marketing type
     *   integer marketingId - optional, the marketing id
     */
    public void index() {
        Product product = Product.dao.findById(getParaToInt("id"));
        if (product == null) {
            renderFailure("invalid.product");
            return;
        }
        String marketingType = getPara("marketingType");
        Integer marketingId = getParaToInt("marketingId");
        BigDecimal settle = settlementService.calcProductSettlement(product, marketingType, marketingId);

        renderSuccess(settle);
    }
}

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

package com.jfeat.product.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.product.purchase.ProductPurchaseEvaluation;
import com.jfeat.product.purchase.StrategyResult;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;

/**
 * Created by jackyhuang on 16/10/14.
 */
@ControllerBind(controllerKey = "/rest/product_purchase_strategy")
public class ProductPurchaseStrategyController extends RestController {

    @Before(CurrentUserInterceptor.class)
    public void index() {
        User user = getAttr("currentUser");
        Integer productId = getParaToInt("productId");
        Integer quantity = getParaToInt("quantity");
        if (productId == null || quantity == null) {
            render400Rest("invalid.productId.or.quantity");
            return;
        }

        ProductPurchaseEvaluation evaluation = new ProductPurchaseEvaluation();
        boolean result = evaluation.evaluate(productId, user.getId(), quantity);
        if (result) {
            renderSuccessMessage("ok");
            return;
        }

        renderFailure(evaluation.getLastError());
    }
}

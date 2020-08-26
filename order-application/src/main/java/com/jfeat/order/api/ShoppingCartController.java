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

package com.jfeat.order.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.order.exception.QueryShoppingCartException;
import com.jfeat.order.model.ShoppingCart;
import com.jfeat.order.service.OrderService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;

import java.util.List;
import java.util.Map;

/**
 * Created by ehngjen on 1/21/2016.
 */
@ControllerBind(controllerKey = "/rest/shopping_cart")
public class ShoppingCartController extends RestController {

    private OrderService orderService = Enhancer.enhance(OrderService.class);

    /**
     * GET /shopping_cart
     */
    @Before(CurrentUserInterceptor.class)
    public void index() {
        User currentUser = getAttr("currentUser");
        try {
            renderSuccess(orderService.queryShoppingCart(currentUser.getId()));
        } catch (QueryShoppingCartException e) {
            renderFailure(e.getMessage());
        }
    }

    /**
     * POST /shopping_cart?increase=true
     * [
     * {"product_id": 1, "quantity": 2, "product_specification_id": 1},//create if not exist
     * {"product_id": 2, "quantity": 1},//update if exist
     * {"product_id": 2, "quantity": 0} //delete
     * ]
     */
    @Before(CurrentUserInterceptor.class)
    public void save() {
        boolean increase = getParaToBoolean("increase", true);
        User currentUser = getAttr("currentUser");

        Map<String, Object>[] maps = convertPostJsonToMapArray();
        Integer[] productIds = new Integer[maps.length];
        Integer[] quantities = new Integer[maps.length];
        Integer[] specificationIds = new Integer[maps.length];
        Integer[] marketingIds = new Integer[maps.length];
        String[] marketings = new String[maps.length];

        int i = 0;
        for (Map<String, Object> map : maps) {
            Integer productId = (Integer) map.get("product_id");
            Integer quantity = (Integer) map.get("quantity");
            Integer specificationId = (Integer) map.get("product_specification_id");
            productIds[i] = productId;
            quantities[i] = quantity;
            specificationIds[i] = specificationId;
            Object marketingId = map.get("marketing_id");
            marketingIds[i] = marketingId != null ? (Integer) marketingId : null;
            Object marketing = map.get("marketing");
            marketings[i] = marketing != null ? (String) marketing : null;
            i++;
        }
        orderService.updateShoppingCart(currentUser.getId(), productIds, quantities, specificationIds, increase, marketingIds, marketings);
        renderSuccess(ShoppingCart.dao.findByUserId(currentUser.getId()));
    }

    /**
     * clean all
     */
    @Before(CurrentUserInterceptor.class)
    public void delete() {
        User currentUser = getAttr("currentUser");
        Ret ret = orderService.cleanShoppingCart(currentUser.getId());
        renderSuccess(ret.get("message"));
    }
}

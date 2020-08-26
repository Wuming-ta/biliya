/*
 *   Copyright (C) 2014-2019 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */
package com.jfeat.common;

import com.jfeat.core.Module;

public class OrderDomainModelMapping {

    public static void mapping(Module module) {

        module.addModel(com.jfeat.order.model.Order.class);
        module.addModel(com.jfeat.order.model.OrderItem.class);
        module.addModel(com.jfeat.order.model.OrderProcessLog.class);
        module.addModel(com.jfeat.order.model.ShoppingCart.class);
        module.addModel(com.jfeat.order.model.Express.class);
        module.addModel(com.jfeat.order.model.OrderCustomerService.class);
        module.addModel(com.jfeat.order.model.OrderStatistic.class);
        module.addModel(com.jfeat.order.model.OrderCustomerServiceItem.class);
        module.addModel(com.jfeat.order.model.OrderExpress.class);

    }

}
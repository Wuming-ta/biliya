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

/*
 * This file is automatically generated by tools.
 * It defines the model for the table. All customize operation should 
 * be written here. Such as query/update/delete.
 * The controller calls this object.
 */
package com.jfeat.order.model;

import com.jfeat.order.model.base.OrderExpressBase;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfeat.order.model.Order;

import java.util.Date;
import java.util.List;

@TableBind(tableName = "t_order_express")
public class OrderExpress extends OrderExpressBase<OrderExpress> {

    /**
     * Only use for query.
     */
    public static OrderExpress dao = new OrderExpress();

    public List<OrderExpress> findByOrderId(Integer orderId) {
        return findByField(Fields.ORDER_ID.toString(), orderId);
    }

    public Order getOrder() {
        return Order.dao.findById(getOrderId());
    }

    @Override
    public boolean save() {
        setCreateDate(new Date());
        return super.save();
    }
}

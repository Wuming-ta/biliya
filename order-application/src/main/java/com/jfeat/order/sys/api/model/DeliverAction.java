package com.jfeat.order.sys.api.model;

import com.jfeat.order.model.Order;

/**
 * @author jackyhuang
 * @date 2019/12/8
 */
public enum DeliverAction {
    DELIVERING {
        @Override
        public Order.Status getStatus() {
            return Order.Status.DELIVERING;
        }
    },
    DELIVERED {
        @Override
        public Order.Status getStatus() {
            return Order.Status.DELIVERED_CONFIRM_PENDING;
        }
    };
    public abstract Order.Status getStatus();
}

package com.jfeat.order.api.model;

import java.math.BigDecimal;

/**
 * Created by kang on 2018/7/18.
 */
public class OrderCustomerServiceItemEntity {
    private Integer product_id;
    // 1.对于需要关联订单的退货单，不需要传递quantity，会使用其对应的order item的quantity；
    // 2.对于不需要关联订单的退货单，必须传递quantity
    // 3.对于一定要关联订单的换货单，这种单据有两个清单（退货项清单和置换项清单）。无论是退货项还是置换项，都必须指定quantity
    private Integer quantity;
    private BigDecimal refund_fee;
    private Integer Product_specification_id;

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getRefund_fee() {
        return refund_fee;
    }

    public void setRefund_fee(BigDecimal refund_fee) {
        this.refund_fee = refund_fee;
    }

    public Integer getProduct_specification_id() {
        return Product_specification_id;
    }

    public void setProduct_specification_id(Integer product_specification_id) {
        Product_specification_id = product_specification_id;
    }
}

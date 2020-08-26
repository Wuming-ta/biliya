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

package com.jfeat.product.service;

import com.jfeat.product.model.Product;

import java.math.BigDecimal;

/**
 * Created by jackyhuang on 16/8/30.
 */
public class ProductPurchasing {
    private Integer fareId;
    private Integer quantity;
    private BigDecimal price;
    private Integer weight;
    private Integer bulk;

    public ProductPurchasing(Integer fareId, Integer quantity, BigDecimal price, Integer weight, Integer bulk) {
        this.fareId = fareId;
        this.quantity = quantity;
        this.price = price;
        this.weight = weight == null ? 0 : weight;
        this.bulk = bulk == null ? 0 : bulk;
    }

    public Integer getFareId() {
        return fareId;
    }

    public void setFareId(Integer fareId) {
        this.fareId = fareId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getBulk() {
        return bulk;
    }

    public void setBulk(Integer bulk) {
        this.bulk = bulk;
    }
}

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

package com.jfeat.member.service;

import com.jfeat.member.model.Coupon;

import java.math.BigDecimal;

/**
 * Created by jackyhuang on 16/11/23.
 */
public class CouponResult {
    private Coupon coupon;
    private BigDecimal finalPrice;

    public CouponResult(Coupon coupon, Double finalPrice) {
        this.coupon = coupon;
        this.finalPrice = BigDecimal.valueOf(finalPrice).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
    }

    public CouponResult(Coupon coupon, BigDecimal finalPrice) {
        this.coupon = coupon;
        this.finalPrice = finalPrice;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = BigDecimal.valueOf(finalPrice).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
    }
}

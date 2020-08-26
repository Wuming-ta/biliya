package com.jfeat.ext.plugin.vip.bean;

import java.math.BigDecimal;

/**
 * @author jackyhuang
 * @date 2019/6/11
 */
public class CouponPrice {
    private String couponId;
    private BigDecimal couponPrice;

    public String getCouponId() {
        return couponId;
    }

    public CouponPrice setCouponId(String couponId) {
        this.couponId = couponId;
        return this;
    }

    public BigDecimal getCouponPrice() {
        return couponPrice;
    }

    public CouponPrice setCouponPrice(BigDecimal couponPrice) {
        this.couponPrice = couponPrice;
        return this;
    }
}

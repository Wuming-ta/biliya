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

package com.jfeat.member.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.member.model.Coupon;
import com.jfeat.member.service.CouponResult;
import com.jfeat.member.service.CouponService;
import com.jfeat.product.model.Product;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jackyhuang on 16/11/23.
 */
@ControllerBind(controllerKey = "/rest/coupon_calculation")
public class CouponCalculationController extends RestController {
    private static final String PRODUCT_ID = "product_id";
    private static final String PRICE = "price";
    private static final String QUANTITY = "quantity";
    private static final String COUPON_ID = "coupon_id";
    private static final String COUPON_NAME = "coupon_name";
    private static final String COUPON_DISPLAY_NAME = "coupon_display_name";
    private static final String FINAL_PRICE = "final_price";
    private static final String COUPON_TYPE = "coupon_type";
    private static final String COUPON_MONEY = "coupon_money";
    private static final String COUPON_DISCOUNT = "coupon_discount";
    private static final String COUPON_VALID_DATE = "valid_date";

    private CouponService service = new CouponService();

    /**
     * 计算优惠信息
     * 参数 phone - optional, pad端使用，为该手机对应的用户计算优惠信息。
     *
     * post /rest/coupon_calculation?phone=139000001
     * [
     * {"product_id": 1, "price": 100, "quantity": 2},
     * {"product_id": 2, "price": 59.9}
     * ]
     * <p/>
     * result:
     * <p/>
     * [
     * {
     * "coupon_id": 1,
     * "coupon_name": "全单8折",
     * "final_price": 47.92
     * },
     * {
     * "coupon_id": 2,
     * "coupon_name": "单品买立减8元",
     * "final_price": 51.9
     * }
     * ]
     */
    @Before(CurrentUserInterceptor.class)
    public void save() {
        User user = getAttr("currentUser");
        String phone = getPara("phone");
        if (StrKit.notBlank(phone)) {
            user = User.dao.findByPhone(phone);
            if (user == null) {
                renderFailure("user.not.found");
                return;
            }
        }
        Map<String, Object>[] mapList = convertPostJsonToMapArray();
        List<Map<String, Object>> resultMapList = new ArrayList<>();
        List<Integer> productIds = new ArrayList<>();
        List<Double> prices = new ArrayList<>();
        List<Double> nonCouponPrices = new ArrayList<>();
        for (int i = 0; i < mapList.length; i++) {
            Integer productId = (Integer) mapList[i].get(PRODUCT_ID);
            Number price = (Number) mapList[i].get(PRICE);
            Integer quantity = (Integer) mapList[i].get(QUANTITY);
            quantity = quantity == null ? 1 : quantity;
            Product product = Product.dao.findById(productId);
            if (product != null && product.getAllowCoupon() == Product.AllowCoupon.YES.getValue()) {
                productIds.add(productId);
                prices.add(price.doubleValue() * quantity);
            }
            if (product != null && product.getAllowCoupon() == Product.AllowCoupon.NO.getValue()) {
                nonCouponPrices.add(price.doubleValue() * quantity);
            }
        }
        if (productIds.isEmpty()) {
            renderSuccess(resultMapList);
            return;
        }
        BigDecimal nonCouponTotalPrice = BigDecimal.ZERO;
        if (!nonCouponPrices.isEmpty()) {
            nonCouponTotalPrice = BigDecimal.valueOf(nonCouponPrices.stream().mapToDouble(item -> item).sum());
        }

        List<CouponResult> list = service.couponCalc(user.getId(), productIds.toArray(new Integer[0]), prices.toArray(new Double[0]));
        for (CouponResult couponResult : list) {
            Map<String, Object> map = new HashMap<>();
            Coupon coupon = couponResult.getCoupon();
            map.put(COUPON_ID, coupon.getId());
            map.put(COUPON_NAME, coupon.getName());
            map.put(COUPON_DISPLAY_NAME, coupon.getDisplayName());
            map.put(FINAL_PRICE, couponResult.getFinalPrice().add(nonCouponTotalPrice));
            map.put(COUPON_TYPE, coupon.getType());
            map.put(COUPON_MONEY, coupon.getMoney());
            map.put(COUPON_DISCOUNT, coupon.getDiscount());
            map.put(COUPON_VALID_DATE, coupon.getValidDate());
            resultMapList.add(map);
        }
        renderSuccess(resultMapList);
    }
}

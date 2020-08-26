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

package com.jfeat.ut;

import com.jfeat.AbstractTestCase;
import com.jfeat.core.BaseService;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfeat.member.model.*;
import com.jfeat.member.service.CouponResult;
import com.jfeat.member.service.CouponService;
import com.jfeat.member.service.CouponStrategyService;
import com.jfinal.ext.kit.RandomKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by jackyhuang on 16/11/23.
 */
public class CouponServiceTest extends AbstractTestCase {

    private User user;
    private CouponType orderCouponType;
    private CouponType productCouponType;
    private CouponType marketingCouponType;
    private CouponService service = new CouponService();

    @Before
    public void before() {
        addUser();
        addOrderCouponType();
        addProductCouponType();
        addMarketingCouponType();
    }

    public void addUser() {
        user = new User();
        user.setName("testuser");
        user.setStatus(User.Status.NORMAL.toString());
        user.setPassword("testuser");
        user.setLoginName("testuser");
        user.setFollowed(User.INFOLLOW_SUBSCRIBE);
        user.setAppUser(User.APP_USER);
        user.save();
    }

    private void addMarketingCouponType() {
        CouponTemplate couponTemplate = CouponTemplate.dao.findFirstByField(CouponTemplate.Fields.NAME.toString(), "拼团活动免单券");
        marketingCouponType = new CouponType();
        marketingCouponType.setValidDays(3);
        marketingCouponType.setType(CouponType.Type.MARKETING_PIECE_GROUP.toString());
        marketingCouponType.setName(RandomKit.randomStr());
        marketingCouponType.setCond(couponTemplate.getCond());
        marketingCouponType.save();
    }

    private void addOrderCouponType() {
        CouponTemplate template = CouponTemplate.dao.findById(6);//无限制型订单代金券
        orderCouponType = new CouponType();
        orderCouponType.setValidDays(30);
        orderCouponType.setType(CouponType.Type.ORDER.toString());
        orderCouponType.setName("order money");
        orderCouponType.setMoney(10);
        String condition = replaceTemplate(template.getCond(),
                orderCouponType.getProductId() != null ? String.valueOf(orderCouponType.getProductId()) : "",
                orderCouponType.getDiscount(),
                orderCouponType.getMoney(),
                orderCouponType.getUpTo());
        orderCouponType.setCond(condition);
        orderCouponType.save();
    }

    private void addProductCouponType() {
        CouponTemplate template = CouponTemplate.dao.findById(2);//无限制型产品代金券
        productCouponType = new CouponType();
        productCouponType.setValidDays(30);
        productCouponType.setType(CouponType.Type.PRODUCT.toString());
        productCouponType.setName("product money");
        productCouponType.setMoney(10);
        productCouponType.setProductId(1);
        String condition = replaceTemplate(template.getCond(),
                productCouponType.getProductId() != null ? String.valueOf(productCouponType.getProductId()) : "",
                productCouponType.getDiscount(),
                productCouponType.getMoney(),
                productCouponType.getUpTo());
        productCouponType.setCond(condition);
        productCouponType.save();
    }

    @After
    public void after() {
        user.delete();
        orderCouponType.delete();
        productCouponType.delete();
        List<Coupon> coupons = Coupon.dao.findAll();
        for (Coupon coupon : coupons) {
            coupon.delete();
        }
    }


    private String replaceTemplate(String condition, String productId, Integer discount, Integer money, Integer limit) {
        if (StrKit.notBlank(condition)) {
            condition = condition.replace("#id#", productId);
            if (discount != null) {
                condition = condition.replace("#discount#", discount.toString());
            }
            if (money != null) {
                condition = condition.replace("#money#", money.toString());
            }
            if (limit != null) {
                condition = condition.replace("#totalPrice#", limit.toString());
            }
        }
        return condition;
    }

    @Test
    public void testCalcWithNoCoupon() {
        Integer[] productIds = new Integer[]{1, 2, 3, 4};
        Double[] prices = new Double[]{100d, 200d, 300d, 400d};
        List<CouponResult> resultList = service.couponCalc(user.getId(), productIds, prices);
        assertNotNull(resultList);
        assertEquals(0, resultList.size());
    }

    @Test
    public void testCalcWithOrderCoupon() {
        Integer[] productIds = new Integer[]{1, 2, 3, 4};
        Double[] prices = new Double[]{100d, 200d, 300d, 400d};
        Coupon coupon = service.createCoupon(user.getId(), orderCouponType, Coupon.Source.SYSTEM);
        assertNotNull(coupon);
        coupon.setStatus(Coupon.Status.ACTIVATION.toString());
        coupon.update();
        coupon = service.createCoupon(user.getId(), productCouponType, Coupon.Source.SYSTEM);
        assertNotNull(coupon);
        coupon.setStatus(Coupon.Status.ACTIVATION.toString());
        coupon.update();
        List<CouponResult> resultList = service.couponCalc(user.getId(), productIds, prices);
        assertNotNull(resultList);
        assertEquals(2, resultList.size());
        for (CouponResult couponResult : resultList) {
            assertEquals(990, couponResult.getFinalPrice().intValue());
        }
    }

    @Test
    public void testProductCouponCalc() {
        Coupon coupon = service.createCoupon(user.getId(), productCouponType, Coupon.Source.SYSTEM);
        CouponResult couponResult = service.productCouponCalc(coupon, 1, 100);
        assertEquals(90, couponResult.getFinalPrice().intValue());
    }

    @Test
    public void testMarketingCouponCalc() {
        Coupon coupon = service.createCoupon(user.getId(), marketingCouponType, Coupon.Source.SYSTEM);
        CouponResult couponResult = service.marketingCouponCalc(coupon, 200);
        assertEquals(0, couponResult.getFinalPrice().intValue());
    }

}

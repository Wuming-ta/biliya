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

package com.jfeat.api;

import com.jfeat.config.model.Config;
import com.jfeat.config.model.ConfigGroup;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfeat.member.model.Coupon;
import com.jfeat.member.model.CouponShare;
import com.jfeat.member.model.CouponTemplate;
import com.jfeat.member.model.CouponType;
import com.jfeat.member.service.CouponService;
import com.jfeat.member.service.CouponStrategyService;
import com.jfinal.kit.StrKit;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by jackyhuang on 16/12/1.
 */
public class CouponShareApiTest extends ApiTestBase {
    private String url = baseUrl + "rest/coupon_share";

    @Before
    public void before() {
        User user = User.dao.findByLoginName(testUserName);

        CouponTemplate couponTemplate = CouponTemplate.dao.findById(1);
        CouponType couponType = new CouponType();
        couponType.setName("test");
        couponType.setMoney(10);
        couponType.setValidDays(3);
        String condition = replaceTemplate(couponTemplate.getCond(),
                couponType.getProductId() != null ? String.valueOf(couponType.getProductId()) : "",
                couponType.getDiscount(),
                couponType.getMoney(),
                couponType.getUpTo());
        couponType.setCond(condition);
        couponType.save();
        CouponService couponService = new CouponService();
        Coupon coupon = couponService.createCoupon(user.getId(), couponType, Coupon.Source.LINK);
        CouponShare couponShare = new CouponShare();
        couponShare.setOrderNumber("12345");
        couponShare.setUserId(user.getId());
        couponShare.setValidDate(DateKit.daysLater(5));
        couponShare.save();

        ConfigGroup configGroup = new ConfigGroup();
        configGroup.setName("test");
        configGroup.save();
        Config config = new Config();
        config.setName("wx.host");
        config.setKeyName("wx.host");
        config.setValueType("String");
        config.setGroupId(configGroup.getId());
        config.setValue("http://www.kequandian.net/app");
        config.save();
    }

    @Test
    public void test() throws IOException {
        get(url);
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
}

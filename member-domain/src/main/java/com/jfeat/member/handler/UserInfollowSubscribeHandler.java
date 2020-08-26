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

package com.jfeat.member.handler;

import com.jfeat.identity.model.User;
import com.jfeat.member.model.Coupon;
import com.jfeat.member.service.CouponService;
import com.jfeat.member.service.CouponStrategyService;
import com.jfeat.observer.Observer;
import com.jfeat.observer.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 用户关注公众号后, 把他的未激活优惠券进行激活。
 * Created by jackyhuang on 16/12/1.
 */
public class UserInfollowSubscribeHandler implements Observer {
    private static Logger logger = LoggerFactory.getLogger(UserInfollowSubscribeHandler.class);
    private CouponService couponService = new CouponService();
    private CouponStrategyService strategyService = new CouponStrategyService();

    @Override
    public void invoke(Subject subject, int event, Object param) {
        if (subject instanceof User && event == User.EVENT_USER_INFOLLOW_SUBSCRIBE) {
            User user = (User) subject;
            logger.debug("user {} infollow subscribe.", user.getName());
            try {
                List<Coupon> coupons = couponService.findNonActivationCoupon(user.getId());
                for (Coupon coupon : coupons) {
                    couponService.activateCoupon(coupon);
                }
                logger.debug("activate {} coupons.", coupons.size());
                if (coupons.size() > 0) {
                    strategyService.resetCouponNotify(user.getId(), coupons);
                    logger.debug("found non-activation coupons, activate them and reset coupon notify.");
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                ex.printStackTrace();
                for (StackTraceElement element : ex.getStackTrace()) {
                    logger.error("    {}:{} - {}:{}", element.getFileName(), element.getLineNumber(), element.getClassName(), element.getMethodName());
                }
            }
        }

    }
}

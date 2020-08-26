package com.jfeat.member.service;

import com.jfeat.ext.plugin.async.AsyncTaskKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异步赠送优惠券
 * @author jackyhuang
 * @date 2018/10/24
 */
public class DispatchCouponStrategyTrigger {

    private static Logger logger = LoggerFactory.getLogger(DispatchCouponStrategyTrigger.class);

    private DispatchCouponStrategyTrigger() {

    }

    private static DispatchCouponStrategyTrigger me = new DispatchCouponStrategyTrigger();
    private CouponStrategyService couponStrategyService = new CouponStrategyService();

    public static DispatchCouponStrategyTrigger me() {
        return me;
    }

    public void trigger(Integer userId) {
        logger.debug("sending a dispatch coupon task for user {}", userId);
        DispatchCouponStrategyTask task = new DispatchCouponStrategyTask(couponStrategyService, userId);
        AsyncTaskKit.submit(task);
    }

    /**
     * async task
     */
    class DispatchCouponStrategyTask implements Runnable {

        private CouponStrategyService couponStrategyService;
        private Integer userId;
        public DispatchCouponStrategyTask(CouponStrategyService couponStrategyService, Integer userId) {
            this.couponStrategyService = couponStrategyService;
            this.userId = userId;
        }

        @Override
        public void run() {
            logger.debug("handling dispatch coupon task for user {}", userId);
            couponStrategyService.dispatchPrecisionMarketingCouponStrategy(userId);
        }
    }
}

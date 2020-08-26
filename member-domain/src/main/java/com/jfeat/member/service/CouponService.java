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

import com.google.common.collect.Lists;
import com.jfeat.common.Constants;
import com.jfeat.config.model.Config;
import com.jfeat.core.BaseService;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfeat.member.model.Coupon;
import com.jfeat.member.model.CouponOverdue;
import com.jfeat.member.model.CouponType;
import com.jfeat.member.model.UserCouponNotify;
import com.jfeat.member.notification.CouponOverdueNotification;
import com.jfeat.ruleengine.Context;
import com.jfeat.ruleengine.MvelContext;
import com.jfeat.ruleengine.RuleEngineProcessor;
import com.jfeat.wechat.config.WxConfig;
import com.jfinal.kit.StrKit;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by jackyhuang on 16/11/23.
 */
public class CouponService extends BaseService {

    private static final String RULE_ATTR_COUPON_TYPE = "type";
    private static final String RULE_ATTR_PRODUCT_ID = "productId";
    private static final String RULE_ATTR_FINAL_PRICE = "finalPrice";
    private static final String RULE_ATTR_TOTAL_PRICE = "totalPrice";
    private static final String RULE_ATTR_GET_FINAL_PRICE = "getFinalPrice";
    private static final String OVERDUE_TIME_INTERVAL = "coupon.overdue_time_interval";

    public CouponResult productCouponCalc(Coupon coupon, int productId, double totalPrice) {
        if (coupon == null || !coupon.getType().equals(Coupon.Type.PRODUCT.toString())) {
            return null;
        }
        Double finalPrice = totalPrice;
        RuleEngineProcessor processor = new RuleEngineProcessor();
        processor.init(coupon.getCond());
        Context context = new MvelContext();
        context.put(RULE_ATTR_PRODUCT_ID, productId);
        context.put(RULE_ATTR_FINAL_PRICE, totalPrice);
        context.put(RULE_ATTR_TOTAL_PRICE, totalPrice);
        processor.process(context, RULE_ATTR_GET_FINAL_PRICE);
        Double tempFinalPrice = context.get(RULE_ATTR_FINAL_PRICE);
        if (tempFinalPrice < finalPrice) {
            finalPrice = tempFinalPrice < 0d ? 0d : tempFinalPrice;
        }
        return new CouponResult(coupon, finalPrice);
    }

    /**
     * 计算使用产品型优惠劵后的该单品的价格
     *
     * @param couponId
     * @param productId
     * @param totalPrice
     * @return
     */
    public CouponResult productCouponCalc(int couponId, int productId, double totalPrice) {
        Coupon coupon = Coupon.dao.findById(couponId);
        if (coupon == null) {
            return null;
        }
        return productCouponCalc(coupon, productId, totalPrice);
    }

    public CouponResult orderCouponCalc(Coupon coupon, double totalPrice) {
        if (coupon == null || !coupon.getType().equals(Coupon.Type.ORDER.toString())) {
            return null;
        }
        Double finalPrice = totalPrice;
        RuleEngineProcessor processor = new RuleEngineProcessor();
        processor.init(coupon.getCond());
        Context context = new MvelContext();
        context.put(RULE_ATTR_FINAL_PRICE, totalPrice);
        context.put(RULE_ATTR_TOTAL_PRICE, totalPrice);
        processor.process(context, RULE_ATTR_GET_FINAL_PRICE);
        Double tempFinalPrice = context.get(RULE_ATTR_FINAL_PRICE);
        if (tempFinalPrice < finalPrice) {
            finalPrice = tempFinalPrice < 0d ? 0d : tempFinalPrice;
        }
        return new CouponResult(coupon, finalPrice);
    }

    /**
     * 计算使用订单型优惠劵后整单的价格
     *
     * @param couponId
     * @param totalPrice
     * @return
     */
    public CouponResult orderCouponCalc(int couponId, double totalPrice) {
        Coupon coupon = Coupon.dao.findById(couponId);
        if (coupon == null) {
            return null;
        }
        return orderCouponCalc(coupon, totalPrice);
    }

    public CouponResult marketingCouponCalc(Coupon coupon, double totalPrice) {
        if (coupon == null || !coupon.getType().startsWith("MARKETING_")) {
            return null;
        }
        Double finalPrice = totalPrice;
        RuleEngineProcessor processor = new RuleEngineProcessor();
        processor.init(coupon.getCond());
        Context context = new MvelContext();
        context.put(RULE_ATTR_COUPON_TYPE, coupon.getType());
        context.put(RULE_ATTR_FINAL_PRICE, totalPrice);
        context.put(RULE_ATTR_TOTAL_PRICE, totalPrice);
        processor.process(context, RULE_ATTR_GET_FINAL_PRICE);
        Double tempFinalPrice = context.get(RULE_ATTR_FINAL_PRICE);
        if (tempFinalPrice < finalPrice) {
            finalPrice = tempFinalPrice < 0d ? 0d : tempFinalPrice;
        }
        return new CouponResult(coupon, finalPrice);
    }

    /**
     * 计算使用营销活动优惠券后的价格
     * @param couponId
     * @param totalPrice
     * @return
     */
    public CouponResult marketingCouponCalc(int couponId, double totalPrice) {
        Coupon coupon = Coupon.dao.findById(couponId);
        if (coupon == null) {
            return null;
        }
        return marketingCouponCalc(coupon, totalPrice);
    }

    /**
     * 计算某用户购买产品时的优惠情况
     *
     * @param userId
     * @param productIds
     * @param prices
     * @return
     */
    public List<CouponResult> couponCalc(int userId, Integer[] productIds, Double[] prices) {
        if (productIds == null || prices == null) {
            throw new RuntimeException("productIds or prices is null.");
        }
        if (productIds.length != prices.length) {
            throw new RuntimeException("productIds.length != prices.length");
        }

        List<CouponResult> results = Lists.newArrayList();
        Double totalPrice = 0d;
        for (double price : prices) {
            totalPrice += price;
        }

        for (Coupon coupon : Coupon.dao.find(userId, Coupon.Type.PRODUCT, Coupon.Status.ACTIVATION)) {
            for (int i = 0; i < productIds.length; i++) {
                int productId = productIds[i];
                double price = prices[i];
                CouponResult result = productCouponCalc(coupon, productId, price);
                if (result != null && result.getFinalPrice().compareTo(BigDecimal.valueOf(price)) < 0) {
                    double finalPrice = totalPrice - (price - result.getFinalPrice().doubleValue());
                    result.setFinalPrice(finalPrice);
                    results.add(result);
                }
            }
        }
        for (Coupon coupon : Coupon.dao.find(userId, Coupon.Type.ORDER, Coupon.Status.ACTIVATION)) {
            CouponResult result = orderCouponCalc(coupon, totalPrice);
            if (result != null && result.getFinalPrice().compareTo(BigDecimal.valueOf(totalPrice)) < 0) {
                results.add(result);
            }
        }
        for (Coupon coupon : Coupon.dao.find(userId, Coupon.Type.MARKETING_PIECE_GROUP, Coupon.Status.ACTIVATION)) {
            CouponResult result = marketingCouponCalc(coupon, totalPrice);
            if (result != null && result.getFinalPrice().compareTo(BigDecimal.valueOf(totalPrice)) < 0) {
                results.add(result);
            }
        }

        return results;
    }

    public List<Coupon> findNonActivationCoupon(int userId) {
        return Coupon.dao.find(userId, null, Coupon.Status.NON_ACTIVATION);
    }

    public List<Coupon> findActivationCoupon(int userId) {
        return Coupon.dao.find(userId, null, Coupon.Status.ACTIVATION);
    }

    public Coupon createCoupon(Integer userId, CouponType couponType, Coupon.Source source) {
        return createCoupon(userId, couponType, source, false, null);
    }

    /**
     * 给用户添加一张优惠券
     *
     * @param userId
     * @param couponType
     * @return
     */
    public Coupon createCoupon(Integer userId, CouponType couponType, Coupon.Source source, boolean activation, Date validDate) {
        User user = User.dao.findById(userId);
        if (user == null || couponType == null) {
            return null;
        }
        if (validDate == null) {
            validDate = DateKit.daysLater(couponType.getValidDays());
        }
        Coupon coupon = new Coupon();
        coupon.setUserId(userId);
        coupon.addAttribute(Coupon.AttributeNames.SOURCE.toString(), source.toString());
        Coupon.Status status = activation || user.getFollowed() == User.INFOLLOW_SUBSCRIBE ? Coupon.Status.ACTIVATION : Coupon.Status.NON_ACTIVATION;
        coupon.setStatus(status.toString());
        coupon.setName(couponType.getName());
        coupon.setType(couponType.getType());
        coupon.setCond(couponType.getCond());
        coupon.setCreatedDate(new Date());
        coupon.setMoney(couponType.getMoney());
        coupon.setDiscount(couponType.getDiscount());
        coupon.setValidDate(validDate);
        coupon.setDisplayName(couponType.getDisplayName());
        coupon.setDescription(couponType.getDescription());
        coupon.save();

        CouponOverdue couponOverdue = new CouponOverdue();
        couponOverdue.setUserId(coupon.getUserId());
        couponOverdue.setCouponId(coupon.getId());
        couponOverdue.setEndTime(coupon.getValidDate());
        couponOverdue.save();

        return coupon;
    }

    /**
     * 激活优惠券,如果已过期就设为overdue状态
     *
     * @param coupon
     * @return
     */
    public boolean activateCoupon(Coupon coupon) {
        if (coupon == null) {
            return false;
        }
        if (new Date().getTime() < coupon.getValidDate().getTime()) {
            coupon.setStatus(Coupon.Status.ACTIVATION.toString());
            return coupon.update();
        }
        logger.debug("coupon code={} already overdue.", coupon.getCode());
        coupon.setStatus(Coupon.Status.OVERDUE.toString());
        return coupon.update();
    }

    /**
     * 删除优惠券
     *
     * @param coupon
     * @return
     */
    public boolean deleteCoupon(Coupon coupon) {
        if (coupon == null) {
            return false;
        }
        return coupon.delete();
    }

    public void useCoupon(Integer userId, Coupon coupon, String orderNumber, BigDecimal benefit) throws Exception {
        if (coupon != null) {
            coupon.setStatus(Coupon.Status.USED.toString());
            coupon.setUserId(userId);
            coupon.addAttribute(Coupon.AttributeNames.ORDER_NUMBER.toString(), orderNumber);
            coupon.addAttribute(Coupon.AttributeNames.BENEFIT.toString(), benefit);
            coupon.addAttribute(Coupon.AttributeNames.USED_DATE.toString(), DateKit.today(Constants.DATE_TIME_FORMAT));
            coupon.update();
        }
        resetCouponUnread(userId);
    }

    /**
     * 当用户进入'优惠券'页面后, 表示用户已经看过拿到的优惠券了, 前端就可以不用红点提示用户了。
     *
     * @param userId
     */
    public void resetCouponUnread(Integer userId) {
        UserCouponNotify userCouponNotify = UserCouponNotify.dao.findFirstByUserId(userId);
        if (userCouponNotify != null) {
            userCouponNotify.setCouponCount(0);
            userCouponNotify.setCouponValue(0);
            userCouponNotify.update();
        }
    }

    public void overdueCoupons() {
        overdueCoupons(null);
    }

    public void overdueCoupons(String date) {
        if (StrKit.isBlank(date)) {
            date = DateKit.today(Constants.DATE_TIME_FORMAT);
        }
        List<Coupon> coupons = Coupon.dao.findCouponsShouldBeOverdue(date);
        new Coupon().overdueCoupons(coupons);
    }

    public void notifyCouponsWillOverdue() {
        //从现在算起，若hours小时后优惠券就过期，则通知用户
        Config config = Config.dao.findByKey(OVERDUE_TIME_INTERVAL);
        Integer hours = 24;
        if (config != null && config.getValueToInt() != null) {
            hours = config.getValueToInt();
        }
        String dateTime = DateKit.hoursAgoStr(-hours, Constants.DATE_TIME_FORMAT);
        //找出 有将要过期优惠券的用户
        List<Integer> userIds = CouponOverdue.dao.findWillOverdue(dateTime);
        for (Integer userId : userIds) {
            User user = User.dao.findById(userId);
            List<CouponOverdue> couponOverdues = CouponOverdue.dao.findByUserId(userId);
            new CouponOverdueNotification(user.getWeixin())
                    .param(CouponOverdueNotification.MESSAGE, String.format(CouponOverdueNotification.MESSAGE_VAL, couponOverdues.size()))
                    .param(CouponOverdueNotification.OVERDUE_DATE, new SimpleDateFormat(Constants.DATE_TIME_FORMAT).format(couponOverdues.get(0).getEndTime()))
                    .setUrl(WxConfig.getHost() + "/app")
                    .send();
        }
        CouponOverdue.dao.deleteWillOverdue(dateTime);
    }
}

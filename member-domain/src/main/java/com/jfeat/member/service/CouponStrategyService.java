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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jfeat.common.Constants;
import com.jfeat.config.model.Config;
import com.jfeat.core.BaseService;
import com.jfeat.identity.model.User;
import com.jfeat.identity.model.param.UserParam;
import com.jfeat.kit.DateKit;
import com.jfeat.member.bean.CouponStrategyData;
import com.jfeat.member.bean.CouponStrategyTarget;
import com.jfeat.member.model.*;
import com.jfeat.member.notification.CouponDispatchedNotification;
import com.jfeat.wechat.config.WxConfig;
import com.jfinal.aop.Before;
import com.jfinal.ext.kit.RandomKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jackyhuang on 16/11/25.
 */
public class CouponStrategyService extends BaseService {

    private static final String COUPON_SHARE_LINK_VALID_DAYS_KEY = "coupon.share_link_valid_days";
    private static final int COUPON_SHARE_LINK_VALID_DAYS_DEFAULT = 30;

    private static final String COUPON_POCKET_TIME_KEY = "coupon.pocket_time";
    private static final int COUPON_POCKET_TIME_DEFAULT = 24;
    private static final String COUPON_POCKET_ORDER_COUNT_KEY = "coupon.pocket_order_count";
    private static final int COUPON_POCKET_ORDER_COUNT_DEFAULT = 2;

    private CouponService couponService = new CouponService();
    private WalletService walletService = new WalletService();

    @Before(Tx.class)
    public Ret createStrategy(CouponStrategy strategy, Integer... couponTypeIds) {
        strategy.save();
        strategy.updateCouponTypes(couponTypeIds);
        return success();
    }

    @Before(Tx.class)
    public Ret updateStrategy(CouponStrategy couponStrategy, Integer... couponTypeIds) {
        if (couponStrategy == null) {
            return failure();
        }
        couponStrategy.update();
        if (couponTypeIds != null) {
            couponStrategy.updateCouponTypes(couponTypeIds);
        }
        return success();
    }

    public Ret dispatchCoupon(CouponStrategy couponStrategy, int userId, Coupon.Source source) {
        return dispatchCoupon(couponStrategy, userId, source, false, null);
    }

    /**
     * 根据某策略赠送优惠券给用户
     *
     * @param couponStrategy
     * @param userId
     * @return
     */
    public Ret dispatchCoupon(CouponStrategy couponStrategy, int userId, Coupon.Source source, boolean activation, Date validDate) {
        if (couponStrategy == null) {
            return failure("invalid.coupon.strategy");
        }
        List<CouponType> couponTypes = couponStrategy.getCouponTypes();
        if (couponTypes.size() == 0) {
            return failure("empty.coupon.types");
        }
        List<Coupon> coupons = Lists.newArrayList();
        if (couponStrategy.getRandomNumber() <= 0) {
            for (CouponType couponType : couponTypes) {
                Coupon coupon = couponService.createCoupon(userId, couponType, source, activation, validDate);
                coupons.add(coupon);
            }
        } else {
            for (int i = 0; i < couponStrategy.getRandomNumber(); ++i) {
                int index = RandomKit.random(0, couponTypes.size());
                CouponType couponType = couponTypes.get(index);
                Coupon coupon = couponService.createCoupon(userId, couponType, source, activation, validDate);
                coupons.add(coupon);
            }
        }
        //OK, coupon dispatched. now check the notification
        if (!coupons.isEmpty()) {
            resetCouponNotify(userId, coupons);
        }

        return success("coupon.dispatched").put("coupons", coupons);
    }

    private String getCouponShareBeginDate() {
        Config config = Config.dao.findByKey(COUPON_POCKET_TIME_KEY);
        int beginDate = COUPON_POCKET_TIME_DEFAULT;
        if (config != null && config.getValueToInt() > 0) {
            beginDate = config.getValueToInt();
        }
        return DateKit.hoursAgoStr(beginDate, "yyyy-MM-dd HH:mm:ss");
    }

    private int getCouponPocketOrderCount() {
        Config config = Config.dao.findByKey(COUPON_POCKET_ORDER_COUNT_KEY);
        if (config != null && config.getValueToInt() > 0) {
            return config.getValueToInt();
        }
        return COUPON_POCKET_ORDER_COUNT_DEFAULT;
    }

    private Date getCouponShareLinkValidDate() {
        Config config = Config.dao.findByKey(COUPON_SHARE_LINK_VALID_DAYS_KEY);
        Date validDate = DateKit.daysLater(COUPON_SHARE_LINK_VALID_DAYS_DEFAULT);
        if (config != null && config.getValueToInt() > 0) {
            validDate = DateKit.daysLater(config.getValueToInt());
        }
        return validDate;
    }


    /**
     * 对订单生成分享码。晒单时调用该方法。
     *
     * @param orderNumber
     * @return the CouponShare object.
     * 如果不满足分享条件(M小时内N单)则返回null
     */
    public CouponShare generateCouponShare(int userId, String orderNumber) {
        int orderCount = CouponShare.dao.countSharedOrder(userId, getCouponShareBeginDate());
        int definedOrderCount = getCouponPocketOrderCount();
        if (orderCount >= definedOrderCount) {
            logger.debug("share failed. userId = {}, orderCount = {}, definedOrderCount = {}",
                    userId, orderCount, definedOrderCount);
            return null;
        }
        // 1. 根据order_number 看t_coupon_share是否有记录, 有就返回,没有, 就生成一个
        CouponShare couponShare = CouponShare.dao.findFirstByOrderNumber(orderNumber);
        if (couponShare == null) {
            couponShare = new CouponShare();
            couponShare.setUserId(userId);
            couponShare.setOrderNumber(orderNumber);
            couponShare.setType(CouponShare.Type.ORDER.toString());
            couponShare.setValidDate(getCouponShareLinkValidDate());
            couponShare.save();
        }
        return couponShare;
    }

    /**
     * 生成系统赠送类型的分享码
     *
     * @param operatorId
     * @return
     */
    public CouponShare generateSystemCouponShare(int operatorId) {
        CouponShare couponShare = new CouponShare();
        couponShare.setUserId(operatorId);
        couponShare.setOrderNumber(RandomKit.randomMD5Str());
        couponShare.setType(CouponShare.Type.SYSTEM.toString());
        couponShare.setValidDate(getCouponShareLinkValidDate());
        couponShare.save();
        return couponShare;
    }

    public List<CouponShare> findSystemCouponShare() {
        return CouponShare.dao.findByType(CouponShare.Type.SYSTEM);
    }

    public List<CouponShare> findCouponShare(int userId) {
        return CouponShare.dao.findValidByUserId(userId);
    }

    @Before(Tx.class)
    public Ret userTakeCouponByShareCode(String code, Integer userId) {
        //通过code查t_coupon_share
        CouponShare couponShare = CouponShare.dao.findFirstByCode(code);
        if (couponShare == null) {
            return failure("coupon.share.not.found");
        }
        if (System.currentTimeMillis() > couponShare.getValidDate().getTime()) {
            return failure("coupon.share.code.overdue");
        }
        //再看看t_coupon_taken_record 是否有该用户的记录
        //如果没有,就CouponStrategyService.dispatchCoupon
        if (CouponTakenRecord.dao.findByUserIdAndShareId(userId, couponShare.getId()) != null) {
            return failure("user.already.take.coupon");
        }

        CouponShare.Type couponShareType = CouponShare.Type.valueOf(couponShare.getType());
        List<CouponStrategy> couponStrategies = CouponStrategy.dao.findByType(couponShareType.mapCouponStrategyType());
        if (couponStrategies.isEmpty()) {
            return failure("not.coupon.strategy.found");
        }
        int couponValue = 0;
        List<Coupon> coupons = Lists.newArrayList();
        for (CouponStrategy couponStrategy : couponStrategies) {
            Ret ret = dispatchCoupon(couponStrategy, userId, couponShareType.mapCouponSource());
            if (BaseService.isSucceed(ret)) {
                List<Coupon> couponList = ret.get("coupons");
                for (Coupon coupon : couponList) {
                    couponValue += coupon.getMoney();
                }
                coupons.addAll(couponList);
            }
        }
        CouponTakenRecord couponTakenRecord = new CouponTakenRecord();
        couponTakenRecord.setUserId(userId);
        couponTakenRecord.setShareId(couponShare.getId());
        couponTakenRecord.setCouponValue(couponValue);
        couponTakenRecord.setCreatedDate(new Date());
        couponTakenRecord.setMessage(CouponMessage.randomMessage());
        couponTakenRecord.save();
        return success().put("coupons", coupons).put("coupon_taken_record", couponTakenRecord);
    }

    /**
     * 查找领取该晒单优惠券的用户
     *
     * @param shareCode
     * @return
     */
    public List<CouponTakenRecord> findCouponTakenRecord(String shareCode) {
        CouponShare couponShare = CouponShare.dao.findFirstByCode(shareCode);
        if (couponShare != null) {
            return CouponTakenRecord.dao.findByShareId(couponShare.getId());
        }
        return Lists.newArrayList();
    }

    /**
     * 当用户有新的未激活优惠券添加时, 用户的优惠券通知状态设置为未通知
     */
    public void resetCouponNotify(Integer userId, List<Coupon> coupons) {
        if (userId == null || coupons.size() == 0) {
            return;
        }
        int couponValue = 0;
        for (Coupon coupon : coupons) {
            couponValue += coupon.getMoney();
        }
        UserCouponNotify userCouponNotify = UserCouponNotify.dao.findFirstByUserId(userId);
        if (userCouponNotify != null) {
            userCouponNotify.setIsNotified(UserCouponNotify.UNNOTIFIED);
            userCouponNotify.setCouponValue(couponValue);
            userCouponNotify.setCouponCount(coupons.size());
            userCouponNotify.update();
        } else {
            userCouponNotify = new UserCouponNotify();
            userCouponNotify.setUserId(userId);
            userCouponNotify.setIsNotified(UserCouponNotify.UNNOTIFIED);
            userCouponNotify.setCouponValue(couponValue);
            userCouponNotify.setCouponCount(coupons.size());
            userCouponNotify.save();
        }
        new CouponDispatchedNotification(User.dao.findById(userId).getWeixin(), coupons.get(0).getMoney(), 1)
                .param(CouponDispatchedNotification.VALID_DATE, new SimpleDateFormat(Constants.DATE_TIME_FORMAT).format(coupons.get(0).getValidDate()))
                .setUrl(WxConfig.getHost() + "/app").send();
    }

    /**
     * 通知用户有新红包入袋
     *
     * @param userId
     * @return
     */
    public UserCouponNotify doCouponNotify(Integer userId) {
        UserCouponNotify userCouponNotify = UserCouponNotify.dao.findFirstByUserId(userId);
        if (userCouponNotify == null) {
            userCouponNotify = new UserCouponNotify();
            userCouponNotify.setUserId(userId);
            userCouponNotify.setIsNotified(UserCouponNotify.NOTIFIED);
            userCouponNotify.setNotifyDate(new Date());
            userCouponNotify.save();
        }

        if (userCouponNotify.shouldNotify()) {
            userCouponNotify.setIsNotified(UserCouponNotify.NOTIFIED);
            userCouponNotify.setNotifyDate(new Date());
            userCouponNotify.update();
        }

        return userCouponNotify;
    }

    /**
     * 精准营销
     */
    public void dispatchPrecisionMarketingCouponStrategy(int userId) {
        User user = User.dao.findById(userId);
        if (user == null) {
            logger.debug("user not found. {}", userId);
            return;
        }

        List<CouponStrategy> strategies = CouponStrategy.dao.findByType(CouponStrategy.Type.PRECISION_MARKETING);
        for (CouponStrategy strategy : strategies) {
            if (strategy.getEndTime() != null && System.currentTimeMillis() - strategy.getEndTime().getTime() > 0) {
                strategy.setStatus(CouponStrategy.Status.FINISHED.toString());
                strategy.update();
                logger.debug("strategy {} has finished. endTime = {}, currentTime = {}", strategy.getName(), strategy.getEndTime(), new Date());
                continue;
            }
            if (strategy.getStartTime() != null && System.currentTimeMillis() - strategy.getStartTime().getTime() < 0) {
                logger.debug("strategy {} not started. startTime = {}, currentTime = {}", strategy.getName(), strategy.getStartTime(), new Date());
                continue;
            }
            if (CouponStrategyTakenRecord.dao.isTaken(strategy.getId(), user.getId(), strategy.getVersion())) {
                logger.debug("user {} already taken coupon {}-{}", userId, strategy.getId(), strategy.getName());
                continue;
            }

            CouponStrategyData couponStrategyData = new CouponStrategyData();
            couponStrategyData.setUserType(StrKit.notBlank(user.getPhone()) ? CouponStrategyTarget.USER_TYPE_MEMBER : CouponStrategyTarget.USER_TYPE_CUSTOMER);
            couponStrategyData.setSex(user.getSex() != null && user.getSex() == User.Sex.MALE.getValue() ? CouponStrategyTarget.SEX_MALE : CouponStrategyTarget.SEX_FEMALE);
            couponStrategyData.setBirthday(user.getBirthday());
            couponStrategyData.setBeCustomerTime(user.getRegisterDate());

            Wallet wallet = walletService.getWallet(userId);
            couponStrategyData.setWalletAmount(wallet.getAccumulativeAmount().intValue());
            couponStrategyData.setWalletBalance(wallet.getBalance().intValue());
            //累计储值消费
            int walletConsumeAmount = wallet.getAccumulativeAmount().intValue() - wallet.getBalance().intValue();
            couponStrategyData.setWalletConsumeAmount(walletConsumeAmount);

            MemberExt memberExt = MemberExt.dao.findByUserId(userId);
            couponStrategyData.setBeMemberTime(memberExt.getBeMemberTime());
            couponStrategyData.setLastConsumeTime(memberExt.getLastConsumeTime());
            couponStrategyData.setConsumeAmount(memberExt.getConsumeAmount());
            couponStrategyData.setConsumeCount(memberExt.getConsumeCount());
            int averageAmount = 0;
            if (memberExt.getConsumeCount() > 0) {
                averageAmount = memberExt.getConsumeAmount() / memberExt.getConsumeCount();
            }
            couponStrategyData.setConsumeAverageAmount(averageAmount);
            couponStrategyData.setCreditAmount(memberExt.getTotalCredit());
            couponStrategyData.setCreditBalance(memberExt.getCredit());
            //累计非储值消费
            int creditConsumeAmount = memberExt.getTotalCredit() - memberExt.getCredit();
            couponStrategyData.setCreditConsumeAmount(creditConsumeAmount);


            CouponStrategyTarget couponStrategyTarget = JSONObject.parseObject(strategy.getTargetCondition(), CouponStrategyTarget.class);
            if (couponStrategyTarget != null && !couponStrategyTarget.canDispatchCoupon(couponStrategyData)) {
                logger.debug("coupon strategy {} can't dispatch coupon to user {}", strategy.getName(), userId);
                continue;
            }

            Ret ret = dispatchCoupon(strategy, user.getId(), Coupon.Source.SYSTEM, true, strategy.getEndTime());
            logger.debug("dispatch coupon result = {}", ret.getData());
            CouponStrategyTakenRecord record = new CouponStrategyTakenRecord();
            record.setStrategyId(strategy.getId());
            record.setVersion(strategy.getVersion());
            record.setUserId(user.getId());
            record.save();
        }
    }
}

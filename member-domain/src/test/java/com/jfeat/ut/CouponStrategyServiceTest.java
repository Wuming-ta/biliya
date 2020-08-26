package com.jfeat.ut;

import com.jfeat.AbstractTestCase;
import com.jfeat.core.BaseService;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfeat.member.model.*;
import com.jfeat.member.service.CouponStrategyService;
import com.jfinal.ext.kit.RandomKit;
import com.jfinal.kit.Ret;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by kang on 2016/11/28.
 */
public class CouponStrategyServiceTest extends AbstractTestCase {
    private static CouponStrategyService couponStrategyService = new CouponStrategyService();
    private User user;

    @Before
    public void before() {
        user = new User();
        user.setName("name1");
        user.setPassword("password1");
        user.setAppUser(User.APP_USER);
        user.save();
    }

    @After
    public void after() {
        if (user != null) {
            user.delete();
        }
    }


//    @Test
//    public void testWriteDownNotification() {
//        //该user未存在UserCouponNotify记录的时候
//        couponStrategyService.resetCouponNotify(user.getId(), 44, 1);
//        assertEquals(Integer.valueOf(0), UserCouponNotify.dao.findFirstByUserId(user.getId()).getIsNotified());
//        //该user已存在UserCouponNotify记录的时候
//        couponStrategyService.resetCouponNotify(user.getId(), 44, 1);
//        assertEquals(Integer.valueOf(0), UserCouponNotify.dao.findFirstByUserId(user.getId()).getIsNotified());
//    }

    @Test
    public void testUpdateNotification() {
        UserCouponNotify userCouponNotify = UserCouponNotify.dao.findFirstByUserId(user.getId());
        if (userCouponNotify != null && Integer.valueOf(0).equals(userCouponNotify.getIsNotified())) {
            couponStrategyService.doCouponNotify(user.getId());
            assertEquals(Integer.valueOf(1), userCouponNotify.getIsNotified());
        }
    }


    @Test
    public void testUserTakeCouponByShareCode() {
        User user = new User();
        user.setLoginName("abc");
        user.setPassword("abc");
        user.setName("abc");
        user.setAppUser(User.APP_USER);
        user.save();

        CouponShare couponShare = new CouponShare();
        couponShare.setOrderNumber("12345");
        couponShare.setUserId(user.getId());
        couponShare.setValidDate(DateKit.daysLater(2));
        couponShare.setType(CouponShare.Type.ORDER.toString());
        couponShare.save();

        CouponStrategy strategy = new CouponStrategy();
        strategy.setName("strategy");
        strategy.setStatus(CouponStrategy.Status.EXECUTING.toString());
        strategy.setType(CouponStrategy.Type.SHARE_LINK.toString());

        CouponType couponType = new CouponType();
        couponType.setName("coupontype");
        couponType.setType(CouponType.Type.ORDER.toString());
        couponType.setMoney(3);
        couponType.setValidDays(3);
        couponType.save();
        couponStrategyService.createStrategy(strategy, couponType.getId());

        CouponTakenRecord couponTakenRecord = CouponTakenRecord.dao.findByUserIdAndShareId(user.getId(), couponShare.getId());
        assertNull(couponTakenRecord);

        Ret ret = couponStrategyService.userTakeCouponByShareCode(couponShare.getCode(), user.getId());
        logger.debug("Ret = {}", ret.getData());
        assertEquals(true, BaseService.isSucceed(ret));

        couponTakenRecord = CouponTakenRecord.dao.findByUserIdAndShareId(user.getId(), couponShare.getId());
        assertNotNull(couponTakenRecord);
        logger.debug(couponTakenRecord.toJson());

        // already taken
        ret = couponStrategyService.userTakeCouponByShareCode(couponShare.getCode(), user.getId());
        logger.debug("Ret = {}", ret.getData());
        assertEquals(false, BaseService.isSucceed(ret));
    }

    @Test
    public void testGenerateCouponShare() {
        CouponShare couponShare = couponStrategyService.generateCouponShare(user.getId(), RandomKit.randomMD5Str());
        assertNotNull(couponShare);
        couponShare = couponStrategyService.generateCouponShare(user.getId(), RandomKit.randomMD5Str());
        assertNotNull(couponShare);
        couponShare = couponStrategyService.generateCouponShare(user.getId(), RandomKit.randomMD5Str());
        assertNull(couponShare);
    }

}

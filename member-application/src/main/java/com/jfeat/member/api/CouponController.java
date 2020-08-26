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

import com.google.common.collect.Maps;
import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.member.model.Coupon;
import com.jfeat.member.model.CouponTakenRecord;
import com.jfeat.member.model.param.CouponParam;
import com.jfeat.member.service.CouponService;
import com.jfeat.member.service.CouponStrategyService;
import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.shiro.ShiroMethod;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ehngjen on 1/13/2016.
 */
@ControllerBind(controllerKey = "/rest/coupon")
public class CouponController extends RestController {

    private CouponService couponService = new CouponService();
    CouponStrategyService strategyService = new CouponStrategyService();

    /**
     * List my coupons
     * GET/rst/coupon?phone=xxx  管理员可以通过phone查看会员的优惠券
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    public void index() {
        String phone = getPara("phone");
        User currentUser = getAttr("currentUser");
        Integer userId = currentUser.getId();
        if (StrKit.notBlank(phone)) {
            User targetUser = User.dao.findByPhone(phone);
            if (targetUser == null) {
                renderFailure("user.not.found");
                return;
            }
            if (ShiroMethod.lacksPermission("member.edit")) {
                renderFailure("lack.of.permission");
                return;
            }
            userId = targetUser.getId();
        }
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 30);
        String status = getPara("status", Coupon.Status.ACTIVATION.toString());
        Map<String, Object> result = new HashMap<>();
        CouponParam couponParam = new CouponParam(pageNumber, pageSize);
        couponParam.setStatus(status);
        couponParam.setUserId(userId);
        Page<Coupon> couponPage = Coupon.dao.paginate(couponParam);
        List<Coupon> coupons = couponPage.getList();
        result.put("totalPage", couponPage.getTotalPage());
        result.put("totalRow", couponPage.getTotalRow());
        result.put("pageNumber", couponPage.getPageNumber());
        result.put("pageSize", couponPage.getPageSize());
        result.put("coupons", coupons);
        for (Coupon.Status s : Coupon.Status.values()) {
            result.put(s.toString(), Coupon.dao.countUserCouponByStatus(userId, s.toString()));
        }

        if (userId.equals(currentUser.getId())) {
            couponService.resetCouponUnread(userId);
        }
        renderSuccess(result);
    }

    /**
     * PUT /rest/coupon/id
     * {
     *     "status": "ACTIVATION"
     * }
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    public void update() {
        User currentUser = getAttr("currentUser");
        Coupon coupon = Coupon.dao.findById(getParaToInt());
        if (coupon == null || !coupon.getUserId().equals(currentUser.getId())) {
            renderFailure("invalid.coupon");
            return;
        }
        if (currentUser.getFollowed() == User.INFOLLOW_UNSUBSCRIBE) {
            renderFailure("user.must.be.followed");
            return;
        }

        boolean result = couponService.activateCoupon(coupon);
        if (!result) {
            renderFailure("activate.failure");
            return;
        }
        renderSuccessMessage("activate.success");
    }

    @Override
    @Before(CurrentUserInterceptor.class)
    public void delete() {
        User currentUser = getAttr("currentUser");
        Coupon coupon = Coupon.dao.findById(getParaToInt());
        if (coupon == null || !coupon.getUserId().equals(currentUser.getId())) {
            renderFailure("invalid.coupon");
            return;
        }

        boolean result = couponService.deleteCoupon(coupon);
        if (!result) {
            renderFailure("delete.failure");
            return;
        }
        renderSuccessMessage("delete.success");
    }



    /**
     * 根据分享代码领取优惠券
     * POST /rest/coupon
     * <p/>
     * {
     * "code": "fsfefaf"
     * }
     * <p/>
     * return:
     {
     "status_code": 0,
         "data": {
             "coupons": [
                 {
                 "code": "1982dbcf-442a-4111-923f-6b20b67eb31e",
                 "description": null,
                 "type": "ORDER",
                 "display_name": "式",
                 "valid_date": "2016-12-01",
                 "money": 4,
                 "user_id": 1,
                 "name": "aaaa",
                 "created_date": "2016-11-28",
                 "id": 1,
                 "status": "ACTIVATION"
                 },
                 {
                 "code": "0d0cf6c8-58c4-4df7-91e3-a14e74046b97",
                 "description": null,
                 "type": "ORDER",
                 "display_name": "33",
                 "valid_date": "2016-12-02",
                 "money": 2,
                 "user_id": 1,
                 "name": "发啊发",
                 "created_date": "2016-11-28",
                 "id": 2,
                 "status": "ACTIVATION"
                 }
             ],
            "coupon_value": 6
         }
     }
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    public void save() {
        User user = getAttr("currentUser");
        Map<String, Object> map = convertPostJsonToMap();
        String shareCode = (String) map.get("code");
        if (StrKit.isBlank(shareCode)) {
            renderFailure("code.is.required");
            return;
        }

        Ret ret = strategyService.userTakeCouponByShareCode(shareCode, user.getId());
        logger.debug("ret = {}", ret.getData());
        Map<String, Object> result = Maps.newHashMap();
        if (BaseService.isSucceed(ret)) {
            List<Coupon> coupons = ret.get("coupons");
            for (Coupon coupon : coupons) {
                coupon.remove(Coupon.Fields.COND.toString());
                coupon.remove(Coupon.Fields.ATTRIBUTE.toString());
            }
            result.put("coupons", coupons);
            CouponTakenRecord couponTakenRecord = ret.get("coupon_taken_record");
            result.put("coupon_value", couponTakenRecord.getCouponValue());
            renderSuccess(result);
            return;
        }

        renderFailure(BaseService.getMessage(ret));
    }
}

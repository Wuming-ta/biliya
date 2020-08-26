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

package com.jfeat.member.controller;

import com.google.common.collect.Lists;
import com.jfeat.core.BaseController;
import com.jfeat.flash.Flash;
import com.jfeat.kit.JsonKit;
import com.jfeat.member.model.Coupon;
import com.jfeat.member.model.CouponType;
import com.jfeat.member.model.MemberExt;
import com.jfeat.member.model.param.CouponParam;
import com.jfeat.member.service.CouponService;
import com.jfeat.member.service.CouponStrategyService;
import com.jfeat.utils.UrlHelper;
import com.jfinal.aop.Before;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ehngjen on 1/8/2016.
 */
public class CouponController extends BaseController {
    private CouponService couponService = new CouponService();
    private CouponStrategyService strategyService = new CouponStrategyService();

    @Override
    @Before(Flash.class)
    @RequiresPermissions(value = { "coupon.edit", "coupon.menu" }, logical = Logical.OR)
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        String code = getPara("code");
        String name = getPara("name");
        String status = getPara("status");
        String user = getPara("user");
        CouponParam couponParam = new CouponParam(pageNumber, pageSize);
        couponParam.setUser(user).setCode(code).setStatus(status).setName(name);
        setAttr("coupons", Coupon.dao.paginate(couponParam));
        setAttr("couponTypes", CouponType.dao.findEnabled());
        setAttr("statuses", Coupon.Status.values());
        keepPara();
    }

    @RequiresPermissions(value = { "coupon.edit", "coupon.menu" }, logical = Logical.OR)
    public void detail() {
        Coupon coupon = Coupon.dao.findById(getParaToInt());
        if (coupon == null) {
            renderError(404);
            return;
        }
        try {
            coupon.put("attributeMap", JsonKit.convertToMap(coupon.getAttribute()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        setAttr("coupon", coupon);
    }

    @RequiresPermissions("coupon.edit")
    public void activate() {
        String returnUrl = getPara("returnUrl", "/coupon");
        Coupon coupon = Coupon.dao.findById(getParaToInt());
        coupon.setStatus(Coupon.Status.ACTIVATION.toString());
        coupon.update();
        setFlash("message", getRes().get("member.activate.coupon.success"));
        redirect(returnUrl);
    }

    @Override
    @RequiresPermissions("coupon.edit")
    public void delete() {
        String returnUrl = UrlHelper.urlDecode(getPara("returnUrl", "/coupon"));
        Coupon.dao.deleteById(getParaToInt());
        setFlash("message", getRes().get("member.delete.coupon.success"));
        redirect(returnUrl);
    }

    @RequiresPermissions("coupon.edit")
    public void give() {
        CouponType couponType = CouponType.dao.findById(getParaToInt("couponTypeId"));
        String returnUrl = UrlHelper.urlDecode(getPara("returnUrl", "/coupon"));
        String[] userIdStrArray = getPara("userId").split("-");
        List<Coupon> couponList = Lists.newLinkedList();
        for (String userIdStr : userIdStrArray) {
            MemberExt memberExt = MemberExt.dao.findByUserId(Integer.parseInt(userIdStr));
            if (couponType == null || memberExt == null) {
                break;
            }
            Coupon coupon = couponService.createCoupon(memberExt.getUserId(), couponType, Coupon.Source.SYSTEM);
            couponList.add(coupon);
            strategyService.resetCouponNotify(memberExt.getUserId(), couponList);
            couponList.clear();
        }
        setFlash("message", getRes().get("member.give.coupon.success"));
        redirect(returnUrl);
    }
}

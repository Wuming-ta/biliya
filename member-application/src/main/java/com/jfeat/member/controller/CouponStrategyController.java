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

import com.jfeat.common.CouponConfigHolder;
import com.jfeat.config.model.Config;
import com.jfeat.core.BaseController;
import com.jfeat.ext.plugin.JsonKit;
import com.jfeat.flash.Flash;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.member.bean.CouponStrategyTarget;
import com.jfeat.member.model.CouponShare;
import com.jfeat.member.model.CouponStrategy;
import com.jfeat.member.model.CouponType;
import com.jfeat.member.service.CouponStrategyService;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jackyhuang on 16/11/25.
 */
public class CouponStrategyController extends BaseController {

    private CouponStrategyService strategyService = new CouponStrategyService();

    @RequiresPermissions(value = { "coupon.edit", "coupon_strategy.menu" }, logical = Logical.OR)
    @Before(Flash.class)
    @Override
    public void index() {
        String name = getPara("name");
        String status = getPara("status");
        String type = getPara("type");
        String startDate = getPara("startDate");
        String endDate = getPara("endDate");
        if (StrKit.notBlank(startDate)) {
            startDate += " 00:00:00";
        }
        if (StrKit.notBlank(endDate)) {
            endDate += " 23:59:59";
        }
        setAttr("strategies", CouponStrategy.dao.find(name, type, status, startDate, endDate));
        setAttr("statuses", CouponStrategy.Status.values());
        setAttr("types", Arrays.stream(CouponStrategy.Type.values()).map(Enum::toString)
                .filter(item -> !CouponConfigHolder.me().getExcludedStrategyTypes().contains(item)).toArray());
        keepPara();
    }

    @RequiresPermissions("coupon.edit")
    @Override
    public void add() {
        setAttr("types", Arrays.stream(CouponStrategy.Type.values()).map(Enum::toString)
                .filter(item -> !CouponConfigHolder.me().getExcludedStrategyTypes().contains(item)).toArray());
        setAttr("couponTypes", CouponType.dao.findEnabled());
        setAttr("couponStrategy", new CouponStrategy());
        setAttr("targetCondition", new HashMap<>());
    }

    @RequiresPermissions("coupon.edit")
    @Override
    public void save() {
        CouponStrategy strategy = getModel(CouponStrategy.class);
        CouponStrategyTarget target = getBean(CouponStrategyTarget.class);
        strategy.setTargetCondition("{}");
        if (target != null && CouponStrategyTarget.TYPE_SOME.equalsIgnoreCase(target.getType())) {
            strategy.setTargetCondition(JsonKit.toJson(target));
            strategy.setTargetType(CouponStrategy.TARGET_TYPE_SOME);
        }
        strategyService.createStrategy(strategy, getParaValuesToInt("couponTypeId"));
        redirect("/coupon_strategy");
    }

    @RequiresPermissions("coupon.edit")
    @Override
    public void edit() {
        CouponStrategy couponStrategy = CouponStrategy.dao.findById(getParaToInt());
        setAttr("types", Arrays.stream(CouponStrategy.Type.values()).map(Enum::toString)
                .filter(item -> !CouponConfigHolder.me().getExcludedStrategyTypes().contains(item)).toArray());
        setAttr("couponTypes", CouponType.dao.findEnabled());
        setAttr("couponStrategy", couponStrategy);
        setAttr("targetCondition", StrKit.notBlank(couponStrategy.getTargetCondition()) ? JsonKit.parseObject(couponStrategy.getTargetCondition()) : new HashMap<>());
    }

    @RequiresPermissions("coupon.edit")
    @Override
    public void update() {
        CouponStrategy strategy = getModel(CouponStrategy.class);
        CouponStrategyTarget target = getBean(CouponStrategyTarget.class);
        strategy.setTargetCondition("{}");
        strategy.setTargetType(CouponStrategy.TARGET_TYPE_ALL);
        if (target != null && CouponStrategyTarget.TYPE_SOME.equalsIgnoreCase(target.getType())) {
            strategy.setTargetCondition(JsonKit.toJson(target));
            strategy.setTargetType(CouponStrategy.TARGET_TYPE_SOME);
        }
        strategyService.updateStrategy(strategy, getParaValuesToInt("couponTypeId"));
        redirect("/coupon_strategy");
    }

    @RequiresPermissions("coupon.edit")
    @Override
    public void delete() {
        CouponStrategy couponStrategy = CouponStrategy.dao.findById(getParaToInt());
        couponStrategy.delete();
        redirect("/coupon_strategy");
    }

    @RequiresPermissions("coupon.edit")
    public void publish() {
        CouponStrategy couponStrategy = CouponStrategy.dao.findById(getParaToInt());
        couponStrategy.setStatus(CouponStrategy.Status.EXECUTING.toString());
        couponStrategy.setVersion(couponStrategy.getVersion() + 1);
        couponStrategy.update();
        redirect("/coupon_strategy");
    }

    @RequiresPermissions("coupon.edit")
    public void suspended() {
        CouponStrategy couponStrategy = CouponStrategy.dao.findById(getParaToInt());
        couponStrategy.setStatus(CouponStrategy.Status.SUSPENDED.toString());
        couponStrategy.update();
        redirect("/coupon_strategy");
    }

    @RequiresPermissions("coupon.edit")
    public void resume() {
        CouponStrategy couponStrategy = CouponStrategy.dao.findById(getParaToInt());
        couponStrategy.setStatus(CouponStrategy.Status.EXECUTING.toString());
        couponStrategy.update();
        redirect("/coupon_strategy");
    }

    @RequiresPermissions("coupon.edit")
    public void finish() {
        CouponStrategy couponStrategy = CouponStrategy.dao.findById(getParaToInt());
        couponStrategy.setStatus(CouponStrategy.Status.FINISHED.toString());
        couponStrategy.update();
        redirect("/coupon_strategy");
    }

    @RequiresPermissions("coupon.edit")
    @Before(CurrentUserInterceptor.class)
    public void genShareLink() {
        User user = getAttr("currentUser");
        strategyService.generateSystemCouponShare(user.getId());
        redirect("/coupon_strategy/viewShareLink");
    }

    @RequiresPermissions("coupon.edit")
    public void viewShareLink() {
        List<CouponShare> list = strategyService.findSystemCouponShare();
        Config config = Config.dao.findByKey("wx.host");
        String host = "";
        if (config != null) {
            host = config.getValueToStr();
        }
        for (CouponShare couponShare : list) {
            User user = couponShare.getUser();
            StringBuilder link = new StringBuilder(host);
            link.append("/app/coupon?invite_code=");
            link.append(user.getInvitationCode());
            link.append("&share_code=");
            link.append(couponShare.getCode());
            couponShare.put("link", link.toString());
            boolean valid = couponShare.getValidDate().getTime() > System.currentTimeMillis();
            couponShare.put("valid", valid);
        }
        setAttr("couponShareList", list);
    }

    @RequiresPermissions("coupon.edit")
    public void deleteCouponShare() {
        CouponShare couponShare = CouponShare.dao.findById(getParaToInt());
        if (couponShare != null) {
            couponShare.delete();
        }

        redirect("/coupon_strategy/viewShareLink");
    }
}

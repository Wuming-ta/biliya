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

import com.jfeat.core.BaseController;
import com.jfeat.flash.Flash;
import com.jfeat.identity.model.User;
import com.jfeat.member.model.*;
import com.jfeat.member.model.param.WalletHistoryParam;
import com.jfeat.member.service.WalletService;
import com.jfinal.aop.Before;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jacky on 12/24/15.
 */
public class MemberController extends BaseController {

    private WalletService walletService = new WalletService();

    @Override
    @Before(Flash.class)
    @RequiresPermissions(value = { "member.edit", "vip.manage.menu" }, logical = Logical.OR)
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 30);
        Integer levelId = getParaToInt("levelId");
        String user = getPara("user");
        setAttr("members", Member.dao.paginate(pageNumber, pageSize, levelId, user));
        setAttr("levels", MemberLevel.dao.findAll());
        setAttr("couponTypes", CouponType.dao.findEnabled());
        keepPara();
    }

    @Before(Flash.class)
    @RequiresPermissions(value = { "member.edit", "vip.manage.menu" }, logical = Logical.OR)
    public void detail() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        String startTime = getPara("startTime");
        String endTime = getPara("endTime");
        Integer couponPageNumber = getParaToInt("couponPageNumber", 1);
        Integer couponPageSize = getParaToInt("couponPageSize", 50);
        User user = User.dao.findById(getParaToInt());
        MemberExt memberExt = MemberExt.dao.findByUserId(user.getId());
        Wallet wallet = walletService.getWallet(user.getId());
        setAttr("user", user);
        setAttr("memberExt", memberExt);
        setAttr("memberPointHistory", MemberPointHistory.dao.paginate(pageNumber, pageSize, memberExt.getId(), startTime, endTime));
        setAttr("coupons", Coupon.dao.paginate(couponPageNumber, couponPageSize, user.getId()));
        setAttr("wallet", wallet);
        WalletHistoryParam param = new WalletHistoryParam(pageNumber, pageSize);
        param.setWalletId(wallet.getId());
        setAttr("walletHistory", WalletHistory.dao.paginate(param));
        keepPara();
    }
}

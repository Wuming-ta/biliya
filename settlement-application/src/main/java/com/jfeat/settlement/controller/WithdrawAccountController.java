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

package com.jfeat.settlement.controller;

import com.jfeat.core.BaseController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.settlement.model.WithdrawAccount;
import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.shiro.ShiroMethod;
import com.jfinal.kit.StrKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * Created by jacky on 4/27/16.
 */
@Before(CurrentUserInterceptor.class)
public class WithdrawAccountController extends BaseController {

    @Override
    @RequiresPermissions(value = { "SettlementApplication.view", "settlement.withdraw_account.menu" }, logical = Logical.OR)
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 30);
        String userName = getPara("id");
        Integer userId = null;
        if (StrKit.notBlank(userName) && StringUtils.isNumeric(userName)) {
            userId = Integer.parseInt(userName);
        }
        setAttr("accounts", WithdrawAccount.dao.paginateGroupByUserId(pageNumber, pageSize, userId, userName));
        keepPara();
    }

    @RequiresPermissions(value = { "SettlementApplication.view", "settlement.withdraw_account.menu" }, logical = Logical.OR)
    public void detail() {
        Integer userId = getParaToInt();
        setAttr("user", User.dao.findById(userId));
        setAttr("accounts", WithdrawAccount.dao.findByUserId(userId));
    }
}

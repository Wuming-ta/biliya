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

package com.jfeat.settlement.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.settlement.api.model.WithdrawAccountEntity;
import com.jfeat.settlement.model.WithdrawAccount;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;

/**
 * Created by jacky on 4/27/16.
 */
@ControllerBind(controllerKey = "/rest/withdraw_account")
public class WithdrawAccountController extends RestController {

    @Before(CurrentUserInterceptor.class)
    public void index() {
        User currentUser = getAttr("currentUser");
        renderSuccess(WithdrawAccount.dao.findByUserId(currentUser.getId()));
    }

    /**
     * POST /rest/withdraw_account
     * {
     *     "owner_name":"Mr.A",
     *     "type":"ALIPAY",
     *     "account":"234234234324",
     *     "bank_name":"ICBC"
     * }
     */
    @Before(CurrentUserInterceptor.class)
    public void save() {
        User currentUser = getAttr("currentUser");
        WithdrawAccountEntity entity = getPostJson(WithdrawAccountEntity.class);
        try {
            WithdrawAccount.Type.valueOf(entity.getType());
        }
        catch (IllegalArgumentException ex) {
            renderFailure("invalid.type");
            return;
        }

        WithdrawAccount account = new WithdrawAccount();
        account.setUserId(currentUser.getId());
        account.setAccount(entity.getAccount());
        account.setBankName(entity.getBank_name());
        account.setType(entity.getType());
        account.setOwnerName(entity.getOwner_name());
        account.save();
        renderSuccessMessage("withdraw.account.created");
    }

    @Before(CurrentUserInterceptor.class)
    public void delete() {
        User currentUser = getAttr("currentUser");
        WithdrawAccount account = WithdrawAccount.dao.findById(getParaToInt());
        if (account == null || !account.getUserId().equals(currentUser.getId())) {
            renderFailure("invalid.withdraw.account");
            return;
        }
        account.delete();
        renderSuccessMessage("withdraw.account.deleted");
    }
}

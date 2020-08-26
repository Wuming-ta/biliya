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

package com.jfeat.settlement.api.model;

import com.jfeat.settlement.model.WithdrawAccount;

/**
 * Created by jacky on 4/28/16.
 */
public class WithdrawCashEntity {

    /**
     * withdraw_type: WECHAT
     * withdraw_account_id : 1
     * withdraw_cash : 23.02
     */
    private String withdraw_type = WithdrawAccount.Type.WECHAT.toString();
    private Integer withdraw_account_id;
    private double withdraw_cash;

    public String getWithdraw_type() {
        return withdraw_type;
    }

    public WithdrawCashEntity setWithdraw_type(String withdraw_type) {
        this.withdraw_type = withdraw_type;
        return this;
    }

    public void setWithdraw_account_id(Integer withdraw_account_id) {
        this.withdraw_account_id = withdraw_account_id;
    }

    public void setWithdraw_cash(double withdraw_cash) {
        this.withdraw_cash = withdraw_cash;
    }

    public Integer getWithdraw_account_id() {
        return withdraw_account_id;
    }

    public double getWithdraw_cash() {
        return withdraw_cash;
    }
}

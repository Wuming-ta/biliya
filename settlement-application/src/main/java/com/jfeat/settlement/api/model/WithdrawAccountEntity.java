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

/**
 * Created by jacky on 4/27/16.
 */
public class WithdrawAccountEntity {

    /**
     * owner_name : admin
     * type : ALIPAY
     * bank_name : ICBC
     * account : 324241
     */

    private String owner_name;
    private String type;
    private String bank_name;
    private String account;

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public String getType() {
        return type;
    }

    public String getBank_name() {
        return bank_name;
    }

    public String getAccount() {
        return account;
    }
}

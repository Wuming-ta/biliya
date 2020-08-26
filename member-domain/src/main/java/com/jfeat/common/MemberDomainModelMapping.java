/*
 *   Copyright (C) 2014-2018 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */
package com.jfeat.common;

import com.jfeat.core.Module;

public class MemberDomainModelMapping {

    public static void mapping(Module module) {

        module.addModel(com.jfeat.member.model.MemberLevel.class);
        module.addModel(com.jfeat.member.model.MemberExt.class);
        module.addModel(com.jfeat.member.model.MemberPointHistory.class);
        module.addModel(com.jfeat.member.model.CouponType.class);
        module.addModel(com.jfeat.member.model.Coupon.class);
        module.addModel(com.jfeat.member.model.CouponStrategy.class);
        module.addModel(com.jfeat.member.model.CouponStrategyTakenRecord.class);
        module.addModel(com.jfeat.member.model.Contact.class);
        module.addModel(com.jfeat.member.model.CouponTemplate.class);
        module.addModel(com.jfeat.member.model.CouponShare.class);
        module.addModel(com.jfeat.member.model.CouponTakenRecord.class);
        module.addModel(com.jfeat.member.model.UserCouponNotify.class);
        module.addModel(com.jfeat.member.model.CouponOverdue.class);
        module.addModel(com.jfeat.member.model.CouponStatistic.class);
        module.addModel(com.jfeat.member.model.Wallet.class);
        module.addModel(com.jfeat.member.model.WalletCharge.class);
        module.addModel(com.jfeat.member.model.WalletHistory.class);

    }

}
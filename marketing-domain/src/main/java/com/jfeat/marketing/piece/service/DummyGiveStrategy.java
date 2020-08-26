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

package com.jfeat.marketing.piece.service;

import com.google.common.collect.Lists;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMember;

import java.util.List;

/**
 * Created by jackyhuang on 2017/5/31.
 */
public class DummyGiveStrategy implements CouponGiveStrategy {

    @Override
    public List<Integer> getUsers(List<PieceGroupPurchaseMember> paidMembers) {
        return Lists.newArrayList();
    }
}

package com.jfeat.marketing.piece.service;


import com.google.common.collect.Lists;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMember;

import java.util.List;
import java.util.Random;

/**
 * Created by kang on 2017/5/23.
 */
public class RandomCouponGiveStrategy implements CouponGiveStrategy {

    public static final String NAME = "RandomCouponGiveStrategy";

    /**
     * 随机赠送免单优惠券给其中1个成员
     *
     * @param paidMembers
     * @return
     */
    @Override
    public List<Integer> getUsers(List<PieceGroupPurchaseMember> paidMembers) {
        List<Integer> userIds = Lists.newLinkedList();
        if (paidMembers == null || paidMembers.size() == 0) {
            return userIds;
        }
        Random r = new Random();
        userIds.add(paidMembers.get(r.nextInt(paidMembers.size())).getUserId());
        return userIds;
    }
}

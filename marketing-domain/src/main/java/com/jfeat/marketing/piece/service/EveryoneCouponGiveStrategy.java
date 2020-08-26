package com.jfeat.marketing.piece.service;

import com.google.common.collect.Lists;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMember;

import java.util.List;

/**
 * Created by kang on 2017/5/23.
 */
public class EveryoneCouponGiveStrategy implements CouponGiveStrategy {

    public static final String NAME = "EveryoneCouponGiveStrategy";

    /**
     * 赠送免单优惠券给每一个成员
     *
     * @param paidMembers
     * @return
     */
    public List<Integer> getUsers(List<PieceGroupPurchaseMember> paidMembers) {
        List<Integer> userIds = Lists.newLinkedList();
        if (paidMembers == null || paidMembers.size() == 0) {
            return userIds;
        }
        for (PieceGroupPurchaseMember pieceGroupPurchaseMember : paidMembers) {
            userIds.add(pieceGroupPurchaseMember.getUserId());
        }
        return userIds;
    }

}

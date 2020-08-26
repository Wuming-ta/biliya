package com.jfeat.marketing.piece.service;

import com.google.common.collect.Lists;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMaster;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMember;

import java.util.List;
import java.util.Random;

/**
 * Created by kang on 2017/5/23.
 */
public class RandomUnmasterCouponGiveStrategy implements CouponGiveStrategy {

    public static final String NAME = "RandomUnmasterCouponGiveStrategy";

    /**
     * 随机赠送免单优惠券给从来未当过团长的一个团员
     *
     * @param paidMembers
     * @return
     */
    public List<Integer> getUsers(List<PieceGroupPurchaseMember> paidMembers) {
        List<Integer> resultList = Lists.newLinkedList();
        List<Integer> userIds = Lists.newArrayList();
        if (paidMembers == null || paidMembers.size() == 0) {
            return resultList;
        }
        for (PieceGroupPurchaseMember pieceGroupPurchaseMember : paidMembers) {
            if (PieceGroupPurchaseMaster.dao.findByUserIdAndStatus(pieceGroupPurchaseMember.getUserId(), PieceGroupPurchaseMaster.Status.DEAL.toString()).size() == 0) {
                userIds.add(pieceGroupPurchaseMember.getUserId());
            }
        }
        if (userIds.size() > 0) {
            resultList.add(userIds.get(new Random().nextInt(userIds.size())));
        }
        return resultList;
    }
}

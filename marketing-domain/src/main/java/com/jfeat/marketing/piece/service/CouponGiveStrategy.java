package com.jfeat.marketing.piece.service;

import com.jfeat.marketing.piece.model.PieceGroupPurchase;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMember;

import java.util.List;

/**
 * Created by kang on 2017/5/23.
 */
public interface CouponGiveStrategy {

    public List<Integer> getUsers(List<PieceGroupPurchaseMember> paidMembers);

}

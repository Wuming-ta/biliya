package com.jfeat.member.service;

import com.jfeat.core.BaseService;
import com.jfeat.member.model.MemberExt;

import java.util.Date;

/**
 * @author jackyhuang
 * @date 2018/10/24
 */
public class MemberService extends BaseService {

    /**
     * 订单模块更新会员的消费信息
     * @param userId
     * @param consumeAmount
     * @param consumeCount
     * @param lastConsumeTime
     */
    public void updateConsumeInfo(Integer userId, Integer consumeAmount, Integer consumeCount, Date lastConsumeTime) {
        MemberExt memberExt = MemberExt.dao.findByUserId(userId);
        memberExt.setConsumeAmount(consumeAmount);
        memberExt.setConsumeCount(consumeCount);
        memberExt.setLastConsumeTime(lastConsumeTime);
        memberExt.update();

        DispatchCouponStrategyTrigger.me().trigger(userId);
    }
}

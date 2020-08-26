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

package com.jfeat.settlement.service;

import com.jfeat.core.BaseService;
import com.jfeat.settlement.model.OrderItemReward;

import java.math.BigDecimal;


/**
 * Created by jingfei on 2016/3/18.
 */
public class OrderItemRewardService extends BaseService {

    /**
     * 查询用户所有已结算拥金总额
     * @param id
     * @return
     */
    public BigDecimal querySettledReward(Integer id) {
        return OrderItemReward.dao.queryRewardByState(id, OrderItemReward.State.SETTLED);
    }

    /**
     * 查询用户所有待结算拥金总额
     * @param id
     * @return
     */
    public BigDecimal queryPendingReward(Integer id) {
        return OrderItemReward.dao.queryRewardByState(id, OrderItemReward.State.PENDING_SETTLEMENT);
    }
}

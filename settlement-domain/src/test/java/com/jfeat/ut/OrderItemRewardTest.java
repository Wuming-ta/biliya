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

package com.jfeat.ut;

import com.jfeat.AbstractTestCase;
import com.jfeat.settlement.model.OrderItemReward;
import com.jfeat.settlement.service.OrderItemRewardService;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by jingfei on 2016/3/17.
 */
public class OrderItemRewardTest extends AbstractTestCase{

    OrderItemRewardService ors ;

    @Before
    public void init(){
        ors = new OrderItemRewardService();
    }

    @Test
    public void testQueryRewardByStatus(){
//        Double money = OrderItemReward.dao.queryRewardByState(222,"Y");
//        System.out.print(money);
    }

    @Test
    public void testQueryRewardByTime(){
//        Double orsReward = ors.queryRewardCurrentYearNow(7);
//        System.out.print(orsReward);
    }

    @Test
    public void testQueryRewardDetails(){
//        List<OrderItemReward> list = OrderItemReward.dao.queryRewardDetails(7);
//        System.out.print(list.size());
    }

    @Test
    public void testQueryRewardDetailsByTimestamp(){
//        List<OrderItemReward> list = ors.queryRewardDetailsCurrentMon(7);
//        System.out.print(list.size());
    }

}

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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfeat.AbstractTestCase;
import com.jfeat.identity.model.User;
import com.jfeat.product.purchase.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by jackyhuang on 16/10/11.
 */
public class PurchaseStrategyTest extends AbstractTestCase {

    private User user;

    @Before
    public void setup() {
        user = new User();
        user.setName("abc");
        user.setLoginName("abc");
        user.setPassword("abc");
        user.setFollowed(0);
        user.save();
    }

    @After
    public void cleanup() {
        user.delete();
    }

    @Test
    public void testWeixinFollowed() {
        Map<String, Object> para = Maps.newHashMap();
        para.put("user_id", user.getId());
        PurchaseStrategy strategy = ProductPurchaseHolder.me().getStrategy("purchase.strategy.weixin.followed");
        StrategyResult result = strategy.canPurchase(new StrategyParameter(null, user.getId(), 1), para);
        assertEquals(true, result.getResult());
    }

    @Test
    public void testDefaultStrategy() {
        PurchaseStrategy strategy = ProductPurchaseHolder.me().getStrategy("xxxxxxxxxxxxxxxxx");
        StrategyResult result = strategy.canPurchase(new StrategyParameter(), null);
        assertEquals(false, result.getResult());
    }

}

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
import com.jfeat.identity.model.User;
import com.jfeat.settlement.model.OwnerBalance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Created by jacky on 4/27/16.
 */
public class OwnerBalanceTest extends AbstractTestCase {

    private User user;

    @Before
    public void before() {
        user = new User();
        user.setLoginName("abc");
        user.setPassword("abc");
        user.save();
        OwnerBalance ownerBalance = new OwnerBalance();
        ownerBalance.setUserId(user.getId());
        ownerBalance.setBalance(new BigDecimal(100));
        ownerBalance.setVersion(Integer.MAX_VALUE - 3);
        ownerBalance.save();
    }

    @After
    public void after() {
        user.delete();
    }

    @Test
    public void testLuckyLockUpdateOverflow() {
        OwnerBalance current = OwnerBalance.dao.findByUserId(user.getId());
        System.out.println(current);
        for (int i = 1; i <= 5; i++) {
            System.out.println("start============== " + i);
            BigDecimal balance = current.getBalance().add(new BigDecimal(10));
            boolean result = OwnerBalance.luckyLockUpdate(balance, current.getVersion(), current.getUserId());
            System.out.println("result = " + result);
            current = OwnerBalance.dao.findByUserId(user.getId());
            System.out.println(current);
            System.out.println("end============== " + i);
        }
    }

    @Test
    public void testLuckyLockUpdateConflict() {
        OwnerBalance current = OwnerBalance.dao.findByUserId(user.getId());
        System.out.println(current);
        BigDecimal balance = current.getBalance().add(new BigDecimal(10));

        OwnerBalance other = OwnerBalance.dao.findByUserId(user.getId());
        System.out.println(other);
        boolean result = OwnerBalance.luckyLockUpdate(balance, other.getVersion(), other.getUserId());
        System.out.println("result = " + result);
        other = OwnerBalance.dao.findByUserId(user.getId());
        System.out.println(other);

        result = OwnerBalance.luckyLockUpdate(balance, current.getVersion(), current.getUserId());
        System.out.println("result = " + result);




    }
}

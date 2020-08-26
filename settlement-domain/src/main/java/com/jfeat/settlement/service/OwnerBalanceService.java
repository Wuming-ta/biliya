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
import com.jfeat.settlement.model.OwnerBalance;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * Created by jacky on 4/27/16.
 */
public class OwnerBalanceService extends BaseService {

    private enum OperateType {
        ADD,
        SUBTRACT
    }

    /**
     * 查询用户余额信息
     * 如果没有记录则新建一条。
     * @param userId
     * @return
     */
    public OwnerBalance queryBalance(int userId) {
        OwnerBalance ownerBalance = OwnerBalance.dao.findByUserId(userId);
        if (ownerBalance == null) {
            ownerBalance = new OwnerBalance();
            ownerBalance.setBalance(new BigDecimal(0));
            ownerBalance.setUserId(userId);
            ownerBalance.setVersion(0);
            ownerBalance.save();
        }
        return ownerBalance;
    }

    @Before(Tx.class)
    public boolean addReward(int userId, BigDecimal reward) {
        return operateReward(userId, reward, OperateType.ADD);
    }

    @Before(Tx.class)
    public boolean subtractReward(int userId, BigDecimal reward) {
        return operateReward(userId, reward, OperateType.SUBTRACT);
    }

    /**
     * 更新余额表，使用乐观锁，重试机制。
     * @param userId
     * @param reward
     * @return
     */
    private boolean operateReward(int userId, BigDecimal reward, OperateType type) {
        int retry = 0;
        int RETRY_COUNT = 5;
        long SLEEP_MILLISECONDS = 500;

        do {
            OwnerBalance ownerBalance = queryBalance(userId);
            Integer version = ownerBalance.getVersion();
            BigDecimal balance = ownerBalance.getBalance();
            if (type == OperateType.ADD) {
                balance = balance.add(reward);
            }
            else {
                if (balance.compareTo(reward) == -1) {
                    logger.error("OwnerBalance operate failed. balance {} is less than reward {}.", balance, reward);
                    return false;
                }
                balance = balance.subtract(reward);
            }
            if (OwnerBalance.luckyLockUpdate(balance, version, userId)) {
                logger.debug("Successful update OwnerBalance balance={},version={},user_id={}", balance, version, userId);
                break;
            }

            retry++;
            try {
                TimeUnit.MILLISECONDS.sleep(SLEEP_MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.warn("update OwnerBalance failed. attempt {}/{} retry after {} ms.", retry, RETRY_COUNT, SLEEP_MILLISECONDS);
        } while (retry < RETRY_COUNT);

        if (retry == RETRY_COUNT) {
            logger.error("Fail to update OwnerBalance after retry. reward={},user_id={}", reward, userId);
            return false;
        }

        return true;
    }
}

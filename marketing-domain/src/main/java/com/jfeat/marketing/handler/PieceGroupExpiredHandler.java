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

package com.jfeat.marketing.handler;

import com.jfeat.marketing.piece.model.PieceGroupPurchase;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMaster;
import com.jfeat.marketing.piece.service.PieceGroupPurchaseService;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.redis.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 把拼团记录放到redis里, 设置过期时间为拼团结束时间,这样当到时间的时候, redis会发出过期事件, ExpiredHandler就可以处理了。
 * 要使用这个feature, 需要设置redis服务端的配置为: notify-keyspace-events Ex
 * Created by jackyhuang on 17/5/4.
 */
public class PieceGroupExpiredHandler implements ExpiredHandler {

    private static final Logger logger = LoggerFactory.getLogger(PieceGroupExpiredHandler.class);
    public static final String NAME = "PIECEGROUP";

    private static Cache CACHE;

    private PieceGroupPurchaseService pieceGroupPurchaseService = Enhancer.enhance(PieceGroupPurchaseService.class);

    @Override
    public Ret handle(int id) {
        return pieceGroupPurchaseService.handlePieceGroupMasterExpired(id);
    }

    public static void init(Cache cache, boolean cronEnabled) {
        CACHE = cache;
        if (!cronEnabled) {
            logger.debug("cronEnabled is false, just init cache.");
            return;
        }

        PieceGroupPurchaseService ps=new PieceGroupPurchaseService();
        for (PieceGroupPurchaseMaster master : PieceGroupPurchaseMaster.dao.findByStatus(PieceGroupPurchaseMaster.Status.OPENING.toString())) {
            Long expiredTime = master.getEndTime().getTime() / 1000; //unit: second
            if(expiredTime <= System.currentTimeMillis() / 1000){
                ps.handlePieceGroupMasterExpired(master.getPieceGroupPurchaseId());
                continue;
            }
            StringBuilder message = new StringBuilder();
            message.append(Constants.PREFIX);
            message.append(Constants.SEPARATOR);
            message.append(NAME);
            message.append(Constants.SEPARATOR);
            message.append(master.getId());
            String res = CACHE.set(message.toString(), master.getId());
            logger.debug("Redis set - key:{}, res:{}", message.toString(), res);
            Long result = CACHE.expireAt(message.toString(), expiredTime);
            logger.debug("Redis expired - key:{}, expired:{}, res:{}", message.toString(), expiredTime, result);
        }
    }

    public static void add(int masterId) {
        if (CACHE == null) {
            throw new RuntimeException("CACHE is null, must call init to initial the cache when system is booting.");
        }
        PieceGroupPurchaseMaster master = PieceGroupPurchaseMaster.dao.findById(masterId);
        Long expiredTime = master.getEndTime().getTime() / 1000; //unit: second
        if(expiredTime <= System.currentTimeMillis() / 1000) {
            new PieceGroupPurchaseService().handlePieceGroupMasterExpired(master.getPieceGroupPurchaseId());
            return;
        }
        StringBuilder message = new StringBuilder();
        message.append(Constants.PREFIX);
        message.append(Constants.SEPARATOR);
        message.append(NAME);
        message.append(Constants.SEPARATOR);
        message.append(master.getId());
        String res = CACHE.set(message.toString(), master.getId());
        logger.debug("Redis set - key:{}, res:{}", message.toString(), res);
        Long result = CACHE.expireAt(message.toString(), expiredTime);
        logger.debug("Redis expired - key:{}, expired:{}, res:{}", message.toString(), expiredTime, result);
    }
}

package com.jfeat.partner.handler;

import com.jfeat.config.model.Config;
import com.jfeat.kit.DateKit;
import com.jfeat.partner.model.Alliance;
import com.jfeat.partner.model.Apply;
import com.jfeat.partner.model.Seller;
import com.jfeat.partner.model.base.AllianceBase;
import com.jfeat.partner.service.AllianceService;
import com.jfeat.partner.service.PhysicalSellerService;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.redis.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 临时盟友记录 设置过期时间,这样当到时间的时候, redis会发出过期事件, ExpiredHandler就可以处理了。
 * 要使用这个feature, 需要设置redis服务端的配置为: notify-keyspace-events Ex
 * Created by kang on 2017/6/3.
 */
public class AllianceShipExpiredHandler implements ExpiredHandler {
    private static final Logger logger = LoggerFactory.getLogger(AllianceShipExpiredHandler.class);
    public static final String NAME = "ALLIANCE_SHIP";

    public static Cache CACHE;

    private AllianceService allianceService = Enhancer.enhance(AllianceService.class);

    @Override
    public Ret handle(int allianceId) {
        return allianceService.handleAllianceShipExpired(allianceId);
    }

    /**
     * 系统启动时候，找出 “临时盟友，把他们的过期时间，放进redis里
     */
    public static void init(Cache cache, boolean cronEnabled) {
        CACHE = cache;
        if (!cronEnabled) {
            logger.debug("cronEnabled is false, just init cache.");
            return;
        }
        AllianceService allianceService = new AllianceService();
        List<Alliance> alliances = Alliance.dao.findByField(
                Alliance.Fields.ALLIANCE_SHIP.toString(),
                Alliance.AllianceShip.TEMP.getValue());
        alliances.forEach(alliance -> {
            if (new Date().compareTo(alliance.getTempAllianceExpiryTime()) > 0) {
                allianceService.handleAllianceShipExpired(alliance.getId());
            } else {
                Long expiredTime = (alliance.getTempAllianceExpiryTime().getTime() / 1000);
                String message = buildMessage(alliance.getId());
                String res = CACHE.set(message, alliance.getId());
                logger.debug("Redis set - key:{}, res:{}", message.toString(), res);
                Long result = CACHE.expireAt(message, expiredTime);
                logger.debug("Redis expired - key:{}, expired:{}, res:{}", message, expiredTime, result);
            }
        });
    }

    public static void add(int allianceId) {
        if (CACHE == null) {
            throw new RuntimeException("CACHE is null, must call init to initial the cache when system is booting.");
        }

        Alliance alliance = Alliance.dao.findById(allianceId);
        Long expiredTime = alliance.getTempAllianceExpiryTime().getTime() / 1000;   //unit:second
        String message = buildMessage(alliance.getId());
        String res = CACHE.set(message, alliance.getId());
        logger.debug("Redis set - key:{}, res:{}", message, res);
        Long result = CACHE.expireAt(message, expiredTime);
        logger.debug("Redis expired - key:{}, expired:{}, res:{}", message, expiredTime, result);
    }

    public static String buildMessage(int sellerId) {
        StringBuilder message = new StringBuilder();
        message.append(Constants.PREFIX);
        message.append(Constants.SEPARATOR);
        message.append(NAME);
        message.append(Constants.SEPARATOR);
        message.append(sellerId);
        return message.toString();
    }
}

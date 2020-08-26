package com.jfeat.partner.handler;

import com.jfeat.config.model.Config;
import com.jfeat.partner.model.Apply;
import com.jfeat.partner.model.Seller;
import com.jfeat.partner.service.PhysicalSellerService;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.redis.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 把皇冠商记录放到redis里, 设置过期时间,这样当到时间的时候, redis会发出过期事件, ExpiredHandler就可以处理了。
 * 要使用这个feature, 需要设置redis服务端的配置为: notify-keyspace-events Ex
 * Created by kang on 2017/6/3.
 */
public class PhysicalCrownShipExpiredHandler implements ExpiredHandler {
    private static final Logger logger = LoggerFactory.getLogger(PhysicalCrownShipExpiredHandler.class);
    public static final String NAME = "PHYSICAL_CROWN_SHIP";
    private static final String WHOLESALE_TIME = "partner.new_physical_seller_wholesale_time";

    public static Cache CACHE;

    private PhysicalSellerService physicalSellerService = Enhancer.enhance(PhysicalSellerService.class);

    @Override
    public Ret handle(int sellerId) {
        return physicalSellerService.handlePhysicalCrownShipExpired(sellerId);
    }

    /**
     * 找出 “N小时之前~现在” 这段时间内 被批准的皇冠商，计算出他们的过期时间，放进redis里
     */
    public static void init(Cache cache, boolean cronEnabled) {
        CACHE = cache;
        if (!cronEnabled) {
            logger.debug("cronEnabled is false, just init cache.");
            return;
        }

        List<Apply> applies = Apply.dao.findByTypeStatus(Apply.Type.CROWN.toString(), Apply.Status.APPROVE.toString());
        Date now = new Date();
        Config config = Config.dao.findByKey(WHOLESALE_TIME);
        Integer temp = null;
        Integer interval = (config != null && (temp = config.getValueToInt()) != null) ? temp : 2;
        PhysicalSellerService pss = new PhysicalSellerService();
        for (Apply apply : applies) {
            if ((apply.getApproveDate().getTime() / 1000) < (now.getTime() / 1000 - interval * 3600)) {
                pss.handlePhysicalCrownShipExpired(Seller.dao.findByUserId(apply.getUserId()).getId());
                continue;
            }
            Seller seller = Seller.dao.findByUserId(apply.getUserId());

            //审核通过时间+interval
            Long expiredTime = (apply.getApproveDate().getTime() / 1000) + (interval * 3600);
            String message = buildMessage(seller.getId());
            String res = CACHE.set(message, seller.getId());
            logger.debug("Redis set - key:{}, res:{}", message.toString(), res);
            Long result = CACHE.expireAt(message, expiredTime);
            logger.debug("Redis expired - key:{}, expired:{}, res:{}", message, expiredTime, result);
        }
    }

    public static void add(int sellerId) {
        if (CACHE == null) {
            throw new RuntimeException("CACHE is null, must call init to initial the cache when system is booting.");
        }
        Config config = Config.dao.findByKey(WHOLESALE_TIME);
        Integer temp = null;
        Integer interval = (config != null && (temp = config.getValueToInt()) != null) ? temp : 2;
        Seller seller = Seller.dao.findById(sellerId);
        Long expiredTime = System.currentTimeMillis() / 1000 + interval * 3600;   //unit:second
        String message = buildMessage(seller.getId());
        String res = CACHE.set(message, seller.getId());
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

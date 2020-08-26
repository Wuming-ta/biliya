package com.jfeat.marketing.piece.service;

import com.google.common.collect.Maps;
import com.jfinal.kit.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by kang on 2017/5/23.
 */
public class CouponGiveStrategyHolder {

    private static Logger logger = LoggerFactory.getLogger(CouponGiveStrategyHolder.class);
    private static CouponGiveStrategyHolder me = new CouponGiveStrategyHolder();
    private final Map<String, Class<? extends CouponGiveStrategy>> serviceMap = Maps.newLinkedHashMap();
    private CouponGiveStrategy dummyStrategy = new DummyGiveStrategy();

    private CouponGiveStrategyHolder() {
        register(EveryoneCouponGiveStrategy.NAME, EveryoneCouponGiveStrategy.class);
        register(RandomCouponGiveStrategy.NAME, RandomCouponGiveStrategy.class);
        register(RandomUnmasterCouponGiveStrategy.NAME, RandomUnmasterCouponGiveStrategy.class);
    }

    public static CouponGiveStrategyHolder me() {
        return me;
    }

    public void register(String key, Class<? extends CouponGiveStrategy> cls) {
        logger.debug("registering CouponGiveStrategyService, key={},class={}", key, cls.getClass().getName());
        if (StrKit.notBlank(key) && cls != null) {
            serviceMap.put(key, cls);
        }
    }

    public Map<String, Class<? extends CouponGiveStrategy>> getServiceMap() {
        return serviceMap;
    }

    public CouponGiveStrategy getCouponStrategy(String key) {
        if (serviceMap.get(key) != null) {
            try {
                return serviceMap.get(key).newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return dummyStrategy;
            }
        }
        return dummyStrategy;
    }
}

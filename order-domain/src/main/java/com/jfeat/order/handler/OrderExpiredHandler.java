package com.jfeat.order.handler;


import com.jfeat.config.model.Config;
import com.jfeat.order.model.Order;
import com.jfeat.order.service.OrderService;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.redis.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by kang on 2017/6/10.
 */
public class OrderExpiredHandler implements ExpiredHandler {
    private static final Logger logger = LoggerFactory.getLogger(OrderExpiredHandler.class);
    public static final String NAME = "CREATED_PAY_PENDING";


    private static Cache CACHE;

    private OrderService orderService = Enhancer.enhance(OrderService.class);

    @Override
    public Ret handle(int orderId) {
        return orderService.handleOrderExpired(orderId);
    }

    /**
     * 初始化cache，如果cronEnabled为true，则对取出待支付的订单列表加入redis
     * @param cache
     * @param cronEnabled
     */
    public static void init(Cache cache, boolean cronEnabled) {
        CACHE = cache;
        if (!cronEnabled) {
            logger.debug("cronEnabled is false, just init cache.");
            return;
        }

        OrderService os = new OrderService();
        int payTimeout = os.getPayTimeout();
        for (Order order : Order.dao.findByField(Order.Fields.STATUS.toString(), Order.Status.CREATED_PAY_PENDING.toString())) {
            Long expiredTime = order.getCreatedDate().getTime() / 1000 + payTimeout * 60; //unit: second
            //服务器启动的时候，若取出订单后，计算出它的支付过期时间<=现在，则不需要设置redis的过期事件，而是立刻处理此订单的过期事件
            if (expiredTime <= System.currentTimeMillis() / 1000) {
                os.handleOrderExpired(order.getId());
                continue;
            }
            StringBuilder message = new StringBuilder();
            message.append(Constants.PREFIX);
            message.append(Constants.SEPARATOR);
            message.append(NAME);
            message.append(Constants.SEPARATOR);
            message.append(order.getId());
            String res = CACHE.set(message.toString(), order.getId());
            logger.debug("Redis set - key:{}, res:{}", message.toString(), res);
            Long result = CACHE.expireAt(message.toString(), expiredTime);
            logger.debug("Redis expired - key:{}, expired:{}, res:{}", message.toString(), expiredTime, result);
        }
    }

    public static void add(int orderId) {
        if (CACHE == null) {
            throw new RuntimeException("CACHE is null, must call init to initial the cache when system is booting.");
        }
        Order order = Order.dao.findById(orderId);
        if (order == null) {
            logger.error("order(id: {}) not found.", orderId);
            return;
        }
        OrderService os = new OrderService();
        int payTimeout = os.getPayTimeout();
        Long expiredTime = order.getCreatedDate().getTime() / 1000 + payTimeout * 60; //unit: second
        if (expiredTime <= System.currentTimeMillis() / 1000) {
            new OrderService().handleOrderExpired(order.getId());
            return;
        }
        StringBuilder message = new StringBuilder();
        message.append(Constants.PREFIX);
        message.append(Constants.SEPARATOR);
        message.append(NAME);
        message.append(Constants.SEPARATOR);
        message.append(order.getId());
        String res = CACHE.set(message.toString(), order.getId());
        logger.debug("Redis set - key:{}, res:{}", message.toString(), res);
        Long result = CACHE.expireAt(message.toString(), expiredTime);
        logger.debug("Redis expired - key:{}, expired:{}, res:{}", message.toString(), expiredTime, result);
    }

}

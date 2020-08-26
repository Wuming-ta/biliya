package com.jfeat.order.handler;

import com.google.common.collect.Maps;
import com.jfeat.ext.plugin.redis.RedisSubscriber;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.redis.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 响应redis过期事件
 * Created by kang on 2017/6/10.
 */
public class OrderSubscriber extends RedisSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(OrderSubscriber.class);

    private Cache cache;
    private Map<String, ExpiredHandler> handlers = Maps.newHashMap();


    public OrderSubscriber(Cache cache, String channel) {
        super(cache, channel);
        this.cache = cache;
        this.handlers.put(OrderExpiredHandler.NAME, new OrderExpiredHandler());
    }

    @Override
    public void onMessage(String channel, String message) {
        super.onMessage(channel, message);
        if (StrKit.isBlank(message) || !message.startsWith(Constants.PREFIX)) {
            logger.warn("unknown message {}", message);
            return;
        }
        try {
            String[] strings = message.split(Constants.SEPARATOR);
            if (strings.length != 3 || !strings[0].equals(Constants.PREFIX)) {
                return;
            }
            String type = strings[1];
            Integer id = Integer.parseInt(strings[2]);
            ExpiredHandler handler = handlers.get(type);
            if (handler == null) {
                logger.error("handler not found for type: {}", type);
                return;
            }

            Ret ret = handler.handle(id);
            logger.debug("handle id:{} ret = {}", id, ret.getData());

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage());
            logger.error(ex.toString());
            for (StackTraceElement element : ex.getStackTrace()) {
                logger.error("    {}:{} {}", element.getFileName(), element.getLineNumber(), element.getMethodName());
            }
        }
    }
}
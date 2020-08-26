package com.jfeat.ext.plugin.wms;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jfeat.ext.plugin.rabbitmq.QueueConsumer;
import com.jfeat.ext.plugin.rabbitmq.RabbitMQKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * WMS 服务更新了商品信息后，监听消息队列消息，更新商城的SKU数据
 * @author jackyhuang
 * @date 2018/12/18
 */
public class WmsUpdatedHandler {

    private static final Logger logger = LoggerFactory.getLogger(WmsUpdatedHandler.class);

    private static WmsUpdatedHandler me = new WmsUpdatedHandler();

    private WmsUpdatedHandler() {

    }

    public static WmsUpdatedHandler me() {
        return me;
    }

    private String endpointName = "wms-update-queue";
    private Map<WmsType, WmsHandler> handlers = new HashMap<>();


    public WmsUpdatedHandler setEndpointName(String endpointName) {
        this.endpointName = endpointName;
        return this;
    }

    public WmsUpdatedHandler registerHandler(WmsType wmsType, WmsHandler handler) {
        this.handlers.put(wmsType, handler);
        return this;
    }

    public void init() {
        if (!RabbitMQKit.isInited()) {
            throw new RuntimeException("rabbitMQ plugin is not started. Please init it first.");
        }

        try {
            WmsUpdatedConsumer consumer = new WmsUpdatedConsumer(endpointName);
            handlers.forEach(consumer::addHandler);

            ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("wms-updated-consumer-pool-%d").build();
            ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

            singleThreadPool.execute(consumer);
        }
        catch (TimeoutException | IOException e) {
            logger.error("init error. {}", e.getMessage());
        }
    }
}

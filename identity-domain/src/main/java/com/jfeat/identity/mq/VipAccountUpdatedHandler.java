package com.jfeat.identity.mq;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jfeat.ext.plugin.rabbitmq.RabbitMQKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author jackyhuang
 * @date 2019/1/22
 */
public class VipAccountUpdatedHandler {


    private static final Logger logger = LoggerFactory.getLogger(VipAccountUpdatedHandler.class);

    private static VipAccountUpdatedHandler me = new VipAccountUpdatedHandler();

    private VipAccountUpdatedHandler() {

    }

    public static VipAccountUpdatedHandler me() {
        return me;
    }

    private String endpointName = "vip-account-update-queue";


    public VipAccountUpdatedHandler setEndpointName(String endpointName) {
        this.endpointName = endpointName;
        return this;
    }

    public void init() {
        if (!RabbitMQKit.isInited()) {
            throw new RuntimeException("rabbitMQ plugin is not started. Please init it first.");
        }

        try {
            VipAccountUpdatedQueueConsumer consumer = new VipAccountUpdatedQueueConsumer(endpointName);

            ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("vip-account-updated-consumer-pool-%d").build();
            ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

            singleThreadPool.execute(consumer);
        }
        catch (TimeoutException | IOException ex) {
            logger.error("init error. {}", ex.getMessage());
            logger.error(ex.getMessage());
            logger.error(ex.toString());
            for (StackTraceElement element : ex.getStackTrace()) {
                logger.error("    {}:{} {}", element.getFileName(), element.getLineNumber(), element.getMethodName());
            }
        }
    }
}

package com.jfeat.plugintest.rabbitmq;

import com.jfeat.ext.plugin.rabbitmq.Producer;
import com.jfeat.ext.plugin.rabbitmq.QueueConsumer;
import com.jfeat.ext.plugin.rabbitmq.RabbitMQPlugin;
import com.jfinal.kit.JsonKit;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by jackyhuang on 2017/8/16.
 */
public class RabbitmqTest {

    @Test
    @Ignore
    public void test() throws IOException, TimeoutException, InterruptedException {
        RabbitMQPlugin plugin = new RabbitMQPlugin("localhost", 5672, "guest", "guest");
        plugin.start();

//        QueueConsumer consumer = new QueueConsumer("queue");
//        Thread consumerThread = new Thread(consumer);
//        consumerThread.start();

        Producer producer = new Producer("wms-update-queue");
        HashMap message = new HashMap();
        message.put("type", "SKU");
        HashMap data = new HashMap();
        data.put("id", 32);
        data.put("skuName", "黄");
        data.put("skuCode", "66666666666");
        data.put("barCode", "22444455566");
        message.put("data", data);
        String dataStr = JsonKit.toJson(message);
        producer.sendMessage(dataStr);

//        for (int i = 0; i < 2; i++) {
//            HashMap message = new HashMap();
//            message.put("number", i);
//            String data = JsonKit.toJson(message);
//            producer.sendMessage(data);
//            System.out.println("Message Number "+ i +" sent." + data);
//        }

        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    @Ignore
    public void testConsume() throws IOException, TimeoutException, InterruptedException {
        RabbitMQPlugin plugin = new RabbitMQPlugin("112.74.26.228", 5672, "jfeat", "jfeat");
        plugin.start();


        QueueConsumer consumer = new TestQueueConsumer("wms-update-queue");
        Thread consumerThread = new Thread(consumer);
        consumerThread.start();

        TimeUnit.SECONDS.sleep(60);
    }
}

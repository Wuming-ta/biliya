package com.jfeat.identity.mq;

import com.jfeat.ext.plugin.rabbitmq.Producer;
import com.jfeat.identity.model.User;
import com.jfinal.kit.JsonKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户更新后通知其他系统
 * @author jackyhuang
 * @date 2019/3/16
 */
public class UserUpdatedNotifier {

    private static final Logger logger = LoggerFactory.getLogger(UserUpdatedNotifier.class);

    private static String QUEUE_NAME = "user-increasement-queue";
    private static boolean NOTIFY_ENABLED = false;

    public enum Type {
        REGISTER,
        SUBSCRIBED,
        UNSUBSCRIBED,
        PHONE_UPDATED,
        INVITER_UPDATED,
        STORE_UPDATED,
        DELETED
    }

    public static void init(String queueName) {
        NOTIFY_ENABLED = true;
        QUEUE_NAME = queueName;
    }

    public static void sendUserUpdatedNotify(User user, Type type) {
        logger.debug("sendUserCreatedNotify for userId {}: notify enabled = {}", user.getId(), NOTIFY_ENABLED);
        if (NOTIFY_ENABLED) {
            try {
                Producer producer = new Producer(QUEUE_NAME);
                Map<String, Object> res = new HashMap<>();
                res.put("type", type.toString());
                res.put("data", user);
                String data = JsonKit.toJson(res);
                logger.debug("sending user data to rabbitmq queue: {}", data);

                producer.sendMessage(data);
            } catch (Exception ex) {
                logger.error("sendNotify to RabbitMQ error. {}", ex.toString());
                for (StackTraceElement element : ex.getStackTrace()) {
                    logger.error("    {}:{} {}", element.getFileName(), element.getLineNumber(), element.getMethodName());
                }
            }
        }
    }
}

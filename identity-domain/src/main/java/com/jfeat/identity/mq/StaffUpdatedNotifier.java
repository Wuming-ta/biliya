package com.jfeat.identity.mq;

import com.jfeat.ext.plugin.rabbitmq.Producer;
import com.jfeat.identity.model.User;
import com.jfinal.kit.JsonKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 员工信息更新时通知外部系统（如门店的店员信息）
 *
 * @author jackyhuang
 * @date 2019/1/18
 */
public class StaffUpdatedNotifier {
    private static final Logger logger = LoggerFactory.getLogger(StaffUpdatedNotifier.class);

    private static String QUEUE_NAME = "staff-updated-queue";
    private static boolean NOTIFY_ENABLED = false;

    public static void init(String queueName) {
        NOTIFY_ENABLED = true;
        QUEUE_NAME = queueName;
    }

    public static void sendStaffUpdatedNotify(User user) {
        logger.debug("sendStaffUpdatedNotify for userId {}: notify enabled = {}", user.getId(), NOTIFY_ENABLED);
        if (NOTIFY_ENABLED) {
            try {
                Producer producer = new Producer(QUEUE_NAME);
                String data = JsonKit.toJson(user);
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

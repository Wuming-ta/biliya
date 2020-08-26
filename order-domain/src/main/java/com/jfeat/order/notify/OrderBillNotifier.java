package com.jfeat.order.notify;

import com.jfeat.ext.plugin.rabbitmq.Producer;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderCustomerService;
import com.jfinal.kit.JsonKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 订单支付/退款时生成账单通知其他外部系统（如提拥系统）
 *
 * @author jackyhuang
 * @date 2018/11/14
 */
public class OrderBillNotifier {
    private static final Logger logger = LoggerFactory.getLogger(OrderBillNotifier.class);

    private static String QUEUE_NAME = "order-bill-queue";
    private static boolean NOTIFY_ENABLED = false;

    private static final String BILL_TYPE_ORDER = "ORDER";
    private static final String BILL_TYPE_ORDER_REFUND = "ORDER_REFUND";


    public static void init(String queueName) {
        NOTIFY_ENABLED = true;
        QUEUE_NAME = queueName;
    }

    public static void sendPaidOrderNotify(Order order) {
        logger.debug("sendPaidOrderNotify for order {}: notify enabled = {}", order.getOrderNumber(), NOTIFY_ENABLED);
        if (NOTIFY_ENABLED) {
            if (!order.getStatus().equalsIgnoreCase(Order.Status.PAID_CONFIRM_PENDING.toString())) {
                logger.error("order {} status is not PAID_CONFIRM_PENDING", order.getOrderNumber());
                return;
            }
            try {
                Producer producer = new Producer(QUEUE_NAME);
                Bill bill = new Bill();
                bill.setBill_id(order.getId().toString());
                bill.setBill_type(BILL_TYPE_ORDER);
                bill.setAmount(order.getTotalPrice().toPlainString());
                bill.setStatus(BillStatus.PAID.toString());
                bill.setBill_number(order.getOrderNumber());
                bill.setAssistant_id(order.getStoreGuideUserId());
                bill.setBilling_time(formatDate(order.getPayDate()));
                bill.setPayer_id(order.getUserId().toString());
                bill.setVendor_id(order.getStoreId());
                bill.setLocation(order.getAddress());

                String data = JsonKit.toJson(bill);
                logger.debug("sending bill data to rabbitmq queue: {}", data);

                producer.sendMessage(data);
            } catch (Exception ex) {
                logger.error("sendNotify to RabbitMQ error. {}", ex.toString());
                for (StackTraceElement element : ex.getStackTrace()) {
                    logger.error("    {}:{} {}", element.getFileName(), element.getLineNumber(), element.getMethodName());
                }
            }
        }
    }

    public static void sendRefundedOrderNotify(OrderCustomerService orderCustomerService) {
        logger.debug("sendRefundedOrderNotify for order {}: notify enabled = {}", orderCustomerService.getServiceNumber(), NOTIFY_ENABLED);
        if (NOTIFY_ENABLED) {
            if (!orderCustomerService.getStatus().equalsIgnoreCase(OrderCustomerService.Status.REFUNDED.toString())) {
                logger.error("orderCustomerService {} status is not REFUNDED", orderCustomerService.getServiceNumber());
                return;
            }

            try {
                Order order = orderCustomerService.getOrder();
                Producer producer = new Producer(QUEUE_NAME);
                Bill bill = new Bill();
                bill.setBill_type(BILL_TYPE_ORDER_REFUND);
                bill.setAmount(orderCustomerService.getRefundFee().toPlainString());
                bill.setStatus(BillStatus.REFUNDED.toString());
                bill.setBill_number(order.getOrderNumber());
                bill.setAssistant_id(order.getStoreGuideUserId());
                bill.setBilling_time(formatDate(orderCustomerService.getCreatedDate()));
                bill.setPayer_id(order.getUserId().toString());
                bill.setVendor_id(order.getStoreId());
                bill.setLocation(order.getAddress());

                String data = JsonKit.toJson(bill);
                logger.debug("sending bill data to rabbitmq queue: {}", data);

                producer.sendMessage(data);
            } catch (Exception ex) {
                logger.error("sendNotify to RabbitMQ error. {}", ex.toString());
                for (StackTraceElement element : ex.getStackTrace()) {
                    logger.error("    {}:{} {}", element.getFileName(), element.getLineNumber(), element.getMethodName());
                }
            }
        }
    }

    private static String formatDate(Date date) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(date.getTime());
    }
}

package com.jfeat.order.service;

import com.jfeat.core.BaseService;
import com.jfeat.order.model.OrderCustomerService;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jackyhuang
 * @date 2019/12/8
 */
public class AsyncRefundCustomerServiceOrderTask implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(AsyncRefundCustomerServiceOrderTask.class);

    private OrderService orderService;
    private OrderCustomerService orderCustomerService;
    public AsyncRefundCustomerServiceOrderTask(OrderService orderService, OrderCustomerService orderCustomerService) {
        this.orderService = orderService;
        this.orderCustomerService = orderCustomerService;
    }

    @Override
    public void run() {
        try {
            logger.debug("checking if auto refund. orderCustomerService = {}", JsonKit.toJson(this.orderCustomerService));
            OrderCustomerService.ServiceType serviceType = OrderCustomerService.ServiceType.valueOf(orderCustomerService.getServiceType());
            if (serviceType == OrderCustomerService.ServiceType.REFUND) {
                logger.info("type is REFUND, attempting auto agree/refund.");
                Ret retRes = orderService.agreeCustomerService(this.orderCustomerService, "System");
                logger.debug("agree result = ", retRes.getData());
                if (BaseService.isSucceed(retRes)) {
                    orderService.refundOrder(this.orderCustomerService, false);
                }
            }
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

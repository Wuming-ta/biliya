package com.jfeat.order.sys.api;

import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.order.model.Express;
import com.jfeat.order.model.Order;
import com.jfeat.order.service.OrderService;
import com.jfeat.order.sys.api.model.DeliverAction;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;

import java.util.Map;

/**
 * 后台发货处理
 * @author jackyhuang
 * @date 2019/12/1
 */
@ControllerBind(controllerKey = "/sys/rest/order/deliver")
public class OrderDeliverController extends RestController {

    private OrderService orderService = Enhancer.enhance(OrderService.class);

    @Override
    public void index() {

    }

    /**
     * PUT /sys/rest/order/deliver/:ordernumber
     * {
     *     "action": "DELIVERING", // or DELIVERED
     *     "express_code": "SF",
     *     "express_number": "12345"
     * }
     */
    @Override
    @Validation(rules = {
            "action = required"
    })
    public void update() {
        Map<String, Object> map = convertPostJsonToMap();
        if (map == null) {
            renderError(400);
            return;
        }
        String orderNumber = getPara();
        DeliverAction action = DeliverAction.valueOf((String) map.get("action"));
        String expressCode = (String) map.get("express_code");
        String expressNumber = (String) map.get("express_number");

        Order order = Order.dao.findByOrderNumber(orderNumber);
        if (order == null) {
            logger.error("invalid order. {}", orderNumber);
            renderFailure("invalid.order");
            return;
        }

        Express express = Express.dao.findByCode(expressCode);
        if (express != null) {
            order.setExpressCode(express.getCode());
            order.setExpressCompany(express.getName());
        }
        if (expressNumber != null) {
            order.setExpressNumber(expressNumber);
        }

        Order.Status targetStatus = action.getStatus();
        order.setStatus(targetStatus.toString());
        Ret ret = orderService.updateOrder(order);
        logger.debug("result: {}", ret.getData());

        renderSuccessMessage("order.delivered");
    }
}

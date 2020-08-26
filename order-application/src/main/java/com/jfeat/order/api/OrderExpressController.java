package com.jfeat.order.api;

import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.identity.model.User;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderExpress;
import com.jfeat.order.service.ExpressInfo;
import com.jfeat.order.service.ExpressServiceHolder;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;

/**
 * @author jackyhuang
 * @date 2019/12/25
 */
@ControllerBind(controllerKey = "/rest/order_express")
public class OrderExpressController extends RestController {

    @Override
    public void index() {

    }

    /**
     * 根据订单的物流单id查询物流轨迹信息
     * GET /rest/order_express/:id
     */
    @Override
    public void show() {
        Integer id = getParaToInt();
        User currentUser = getAttr("currentUser");
        OrderExpress orderExpress = OrderExpress.dao.findById(id);
        if (orderExpress == null) {
            render404Rest("express.not.found");
            return;
        }
        Order order = orderExpress.getOrder();
        if (!order.getUserId().equals(currentUser.getId())) {
            render400Rest("not.your.express");
            return;
        }
        ExpressInfo expressInfo = ExpressServiceHolder.me().getExpressService().queryExpress(orderExpress.getExpressCode(), orderExpress.getExpressNumber());
        if (expressInfo.isSucceed()) {
            renderSuccess(expressInfo);
            return;
        }
        renderFailure("cannot.find.express.info");
    }
}

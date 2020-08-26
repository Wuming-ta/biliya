package com.jfeat.order.api;

import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.order.model.Order;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/8/31
 */
@ControllerBind(controllerKey = "/rest/order_comment")
public class OrderCommentController extends RestController {

    @Override
    @Before(CurrentUserInterceptor.class)
    @Validation(rules = { "comment_id = required" })
    public void update() {
        Map<String, Object> map = convertPostJsonToMap();
        User currentUser = getAttr("currentUser");
        String orderNumber = getPara();
        Order order = Order.dao.findByOrderNumber(orderNumber);
        if (order == null) {
            renderFailure("order.not.found");
            return;
        }
        if (!currentUser.getId().equals(order.getUserId())) {
            renderFailure("permission.denied");
            return;
        }
        if (!order.getStatus().equals(Order.Status.CLOSED_CONFIRMED.toString())) {
            renderFailure("invalid.status");
            return;
        }
        if (StrKit.notBlank(order.getCommentId())) {
            renderFailure("already.commentted");
            return;
        }

        order.setCommentId(map.get("comment_id").toString());
        order.update();
        renderSuccessMessage("ok");
    }
}

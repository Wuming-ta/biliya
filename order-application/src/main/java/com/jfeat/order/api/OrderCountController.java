package com.jfeat.order.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.param.OrderParam;
import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.shiro.ShiroMethod;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/9/7
 */
@ControllerBind(controllerKey = "/rest/order_count")
public class OrderCountController extends RestController {

    @Override
    @Before(CurrentUserInterceptor.class)
    public void index() {
        Boolean queryMarketing = getParaToBoolean("queryMarketing", true);
        String phone = getPara("phone");
        String storeId = null;
        Boolean showDeleted = null;
        User currentUser = getAttr("currentUser");
        Integer userId = currentUser.getId();
        if (StrKit.notBlank(phone)) {
            User targetUser = User.dao.findByPhone(phone);
            if (targetUser == null) {
                renderFailure("user.not.found");
                return;
            }
            if (ShiroMethod.lacksPermission("order.view")) {
                renderFailure("lack.of.permission");
                return;
            }
            userId = targetUser.getId();
            storeId = getPara("storeId");
            showDeleted = true;
        }
        Map<String, Object> result = new HashMap<>();
        OrderParam orderParam = new OrderParam();
        orderParam.setUserId(userId).setQueryMarketing(queryMarketing).setStoreId(storeId).setShowDeleted(showDeleted);

        orderParam.setStatuses(new String[] {
                Order.Status.PAID_CONFIRM_PENDING.toString()
                , Order.Status.CONFIRMED_DELIVER_PENDING.toString()
                , Order.Status.CONFIRMED_PICK_PENDING.toString()
                , Order.Status.DELIVERING.toString()
                , Order.Status.CANCELED_RETURN_PENDING.toString()
                , Order.Status.CLOSED_CONFIRMED.toString()
                , Order.Status.CANCELED_REFUND_PENDING.toString()
                , Order.Status.CLOSED_REFUNDED.toString()
                , Order.Status.CLOSED_CANCELED.toString()
                , Order.Status.DELIVERED_CONFIRM_PENDING.toString()
        });
        result.put("total", Order.dao.countOrderByCond(orderParam));
        result.put("totalPrice", Order.dao.queryOrderTotalPrice(orderParam));

        orderParam.setStatuses(new String[] {
                Order.Status.CREATED_PAY_PENDING.toString()
        });
        result.put("payPending", Order.dao.countOrderByCond(orderParam));

        orderParam.setStatuses(new String[] {
                Order.Status.PAID_CONFIRM_PENDING.toString()
                , Order.Status.CONFIRMED_DELIVER_PENDING.toString()
                , Order.Status.DELIVERING.toString()
        });
        result.put("delivering", Order.dao.countOrderByCond(orderParam));

        orderParam.setStatuses(new String[] {
                Order.Status.DELIVERED_CONFIRM_PENDING.toString(),
                Order.Status.CANCELED_RETURN_PENDING.toString(),
                Order.Status.CANCELED_REFUND_PENDING.toString(),
                Order.Status.CONFIRMED_PICK_PENDING.toString()
        });
        result.put("delivered", Order.dao.countOrderByCond(orderParam));

        orderParam.setStatuses(new String[] {
                Order.Status.CLOSED_CONFIRMED.toString()
        }).setCommented(false);
        result.put("commentPending", Order.dao.countOrderByCond(orderParam));
        renderSuccess(result);
    }
}

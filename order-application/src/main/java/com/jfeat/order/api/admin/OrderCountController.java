package com.jfeat.order.api.admin;

import com.jfeat.core.RestController;
import com.jfeat.identity.model.User;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.param.OrderParam;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/9/7
 */
@ControllerBind(controllerKey = "/rest/admin/order_count")
public class OrderCountController extends RestController {

    @Override
    @RequiresPermissions("order.view")
    public void index() {
        String phone = getPara("phone");
        if (StrKit.isBlank(phone)) {
            renderFailure("phone.is.required");
            return;
        }
        User user = User.dao.findByPhone(phone);
        if (user == null) {
            renderFailure("user.not.found");
            return;
        }
        Map<String, Object> result = new HashMap<>();
        OrderParam orderParam = new OrderParam();
        orderParam.setUserId(user.getId()).setShowDeleted(true);

        orderParam.setStatuses(new String[] {
                Order.Status.CLOSED_CONFIRMED.toString()
        });
        result.put("totalPrice", Order.dao.queryOrderTotalPrice(orderParam));

        orderParam.setStatuses(new String[] {
                Order.Status.CREATED_PAY_PENDING.toString()
        });
        result.put("payPending", Order.dao.countOrderByCond(orderParam));

        orderParam.setStatuses(new String[] {
                Order.Status.CONFIRMED_DELIVER_PENDING.toString(), Order.Status.DELIVERING.toString()
        });
        result.put("delivering", Order.dao.countOrderByCond(orderParam));

        orderParam.setStatuses(new String[] {
                Order.Status.DELIVERED_CONFIRM_PENDING.toString()
        });
        result.put("delivered", Order.dao.countOrderByCond(orderParam));

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
        });
        result.put("total", Order.dao.countOrderByCond(orderParam));

        orderParam.setType(Order.Type.ORDER.toString());
        result.put("onlineOrder", Order.dao.countOrderByCond(orderParam));

        orderParam.setType(Order.Type.STORE_ORDER.toString());
        result.put("storeOrder", Order.dao.countOrderByCond(orderParam));

        orderParam.setStatuses(new String[] {
                Order.Status.CLOSED_CONFIRMED.toString()
        }).setCommented(false).setType(null);
        result.put("commentPending", Order.dao.countOrderByCond(orderParam));


        renderSuccess(result);
    }
}

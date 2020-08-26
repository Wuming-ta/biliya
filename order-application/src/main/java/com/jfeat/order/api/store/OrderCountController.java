package com.jfeat.order.api.store;

import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.param.OrderParam;
import com.jfeat.order.service.StoreUtil;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/9/7
 */
@ControllerBind(controllerKey = "/rest/store/order_count")
public class OrderCountController extends RestController {

    /**
     * GET /rest/store/order_count?storeId=1
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    public void index() {
        String storeId = getPara("storeId");
        if (StrKit.isBlank(storeId)) {
            renderFailure("查找失败 - 必须指定店铺id");
            return;
        }
        String type = getPara("type");

        String verifyResult = StoreUtil.verify(this, storeId);
        if (StrKit.notBlank(verifyResult)) {
            renderFailure(verifyResult);
            return;
        }

        String[] statuses = getParaValues("status");
        String paymentType = getPara("paymentType");
        String contactUser = getPara("contactUser");
        String phone = getPara("phone");
        String search = getPara("search");
        String[] createTime = getParaValues("createTime");
        String startTime = null, endTime = null;
        if (createTime != null && createTime.length == 2) {
            startTime = StrKit.notBlank(createTime[0]) ? createTime[0] + " 00:00:00" : null;
            endTime = StrKit.notBlank(createTime[1]) ? createTime[1] + " 23:59:59" : null;
        }
        String deliveryType = getPara("deliveryType");

        OrderParam orderParam = new OrderParam();
        orderParam.setType(type)
                .setContactUser(contactUser)
                .setPhone(phone)
                .setPaymentType(paymentType)
                .setSearch(search)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setDeliveryType(deliveryType)
                .setStoreId(storeId)
                .setShowDeleted(true);

        String[] totalStatuses = {
                Order.Status.PAID_CONFIRM_PENDING.toString()
                , Order.Status.CONFIRMED_DELIVER_PENDING.toString()
                , Order.Status.CONFIRMED_PICK_PENDING.toString()
                , Order.Status.DELIVERING.toString()
                , Order.Status.CANCELED_RETURN_PENDING.toString()
                , Order.Status.CLOSED_CONFIRMED.toString()
                , Order.Status.CANCELED_REFUND_PENDING.toString()
                , Order.Status.CLOSED_REFUNDED.toString()
                , Order.Status.CLOSED_CANCELED.toString()
        };
        if (statuses != null && statuses.length > 0) {
            totalStatuses = new String[statuses.length];
            for (int i = 0; i < statuses.length; i++) {
                totalStatuses[i] = statuses[i];
            }
        }

        Map<String, Object> result = new HashMap<>();
        orderParam.setStatuses(totalStatuses);
        result.put("totalPrice", Order.dao.queryOrderTotalPrice(orderParam));

        orderParam.setStatuses(totalStatuses);
        result.put("total", Order.dao.countOrderByCond(orderParam));

        orderParam.setStatuses(new String[]{
                Order.Status.PAID_CONFIRM_PENDING.toString()
        });
        result.put("handlePending", Order.dao.countOrderByCond(orderParam));

        orderParam.setStatuses(new String[]{
                Order.Status.CONFIRMED_DELIVER_PENDING.toString()
        });
        result.put("deliverPending", Order.dao.countOrderByCond(orderParam));

        orderParam.setStatuses(new String[]{
                Order.Status.CONFIRMED_PICK_PENDING.toString()
        });
        result.put("pickPending", Order.dao.countOrderByCond(orderParam));

        orderParam.setStatuses(new String[]{
                Order.Status.DELIVERING.toString(),
                Order.Status.DELIVERED_CONFIRM_PENDING.toString()
        });
        result.put("delivering", Order.dao.countOrderByCond(orderParam));

        orderParam.setStatuses(new String[]{
                Order.Status.CLOSED_CONFIRMED.toString()
        });
        result.put("closed", Order.dao.countOrderByCond(orderParam));

        orderParam.setStatuses(new String[]{
                Order.Status.CLOSED_CANCELED.toString()
        });
        result.put("cancled", Order.dao.countOrderByCond(orderParam));

        renderSuccess(result);
    }
}

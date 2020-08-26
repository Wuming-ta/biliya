package com.jfeat.order.api.store;

import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.store.bean.Assistant;
import com.jfeat.ext.plugin.store.bean.Store;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.member.model.Coupon;
import com.jfeat.order.OrderStoreUtil;
import com.jfeat.order.api.interceptor.OrderCreatedEnableInterceptor;
import com.jfeat.order.api.model.Action;
import com.jfeat.order.api.model.OrderEntity;
import com.jfeat.order.api.validator.OrderValidator;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderItem;
import com.jfeat.order.model.param.OrderParam;
import com.jfeat.order.service.OrderService;
import com.jfeat.order.service.StoreUtil;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/6/26
 */

/**
 * 订单API（店员用）
 * 店员A若是店铺B，C的员工，则店员A在ipad端以店铺C登录之后，只要是店铺C的单都可以看到（即使这个单是店铺C的其他店员D的单也如此）
 */
@ControllerBind(controllerKey = "/rest/store/order")
public class OrderController extends RestController {

    private OrderService orderService = Enhancer.enhance(OrderService.class);

    /**
     * type：(1)STORE_ORDER：线下订单（由店员下的单） （ipad端收银 -> 历史订单页面需传type=STORE_ORDER）
     * (2)ORDER：线上订单（由终端用户下的单）（分两种：1.配送方式为“快递”的单  2.配送方式为“自提”或“极速送达”的单，这样的单在下单时是关联了一个店铺的。
     * 由于本api必须传递storeId，因此返回的订单只可能是“线下订单”或者“配送方式为自提或极速送达的线上订单” （ipad端 -> 线上待处理订单 -> 全部订单需传type=ORDER）
     */
    @Before(CurrentUserInterceptor.class)
    @Override
    public void index() {
        String storeId = getPara("storeId");
        if (StrKit.isBlank(storeId)) {
            renderFailure("查找失败 - 必须指定店铺id");
            return;
        }

        String verifyResult = StoreUtil.verify(this, storeId);
        if (StrKit.notBlank(verifyResult)) {
            renderFailure(verifyResult);
            return;
        }

        //STORE_ORDER-ipad端历史订单页面  ORDER-ipad端线上待处理订单页面
        String type = getPara("type");
        String paymentType = getPara("paymentType");
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 30);
        String search = getPara("search");
        String contactUser = getPara("contactUser");
        String phone = getPara("phone");
        String[] statuses = getParaValues("status");
        String orderNumber = getPara("orderNumber");
        String[] createTime = getParaValues("createTime");
        String startTime = null, endTime = null;
        if (createTime != null && createTime.length == 2) {
            startTime = StrKit.notBlank(createTime[0]) ? createTime[0] + " 00:00:00" : null;
            endTime = StrKit.notBlank(createTime[1]) ? createTime[1] + " 23:59:59" : null;
        }
        String deliveryType = getPara("deliveryType");
        String storeUserCode = getPara("storeUserCode");
        String month = getPara("month");
        if (StrKit.notBlank(month)) {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String[] d = month.split("-");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.parseInt(d[0]));
            // month 从0开始的，所以要减1
            calendar.set(Calendar.MONTH, Integer.parseInt(d[1]) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            startTime = sf.format(calendar.getTime());

            // 当月最后一天
            int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, lastDay);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            endTime = sf.format(calendar.getTime());
        }

        OrderParam orderParam = new OrderParam(pageNumber, pageSize);
        orderParam.setType(type)
                .setContactUser(contactUser)
                .setPhone(phone)
                .setOrderNumber(orderNumber)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setStatuses(statuses)
                .setDeliveryType(deliveryType)
                .setStoreId(storeId)
                .setPaymentType(paymentType)
                .setSearch(search)
                .setStoreUserCode(storeUserCode)
                .setShowDeleted(true);
        Page<Order> result = Order.dao.paginate(orderParam);
        renderSuccess(result);

    }

    /**
     * GET /rest/store/order/<order_number>
     * <p/>
     * Return:
     */
    @Before(CurrentUserInterceptor.class)
    @Override
    public void show() {
        Order order = Order.dao.findByOrderNumber(getPara());
        if (order == null || StrKit.isBlank(order.getStoreId())) {
            renderError(404);
            return;
        }

        String verifyResult = StoreUtil.verify(this, order.getStoreId());
        if (StrKit.notBlank(verifyResult)) {
            renderFailure(verifyResult);
            return;
        }

        order.put("order_items", order.getOrderItems());
        order.put("order_customer_services", order.getOrderCustomerService());
        renderSuccess(order);
    }


    /**
     * 线下订单（店员下的单）
     */
    @Before({CurrentUserInterceptor.class, OrderCreatedEnableInterceptor.class, OrderValidator.class})
    @Override
    public void save() {
        OrderEntity orderEntity = getPostJson(OrderEntity.class);
        if (StrKit.isBlank(orderEntity.getStore_id())) {
            renderFailure("新建订单失败 - 必须指定门店");
            return;
        }
        String origin = StrKit.notBlank(orderEntity.getOrigin()) ? orderEntity.getOrigin() : Order.Origin.OTHER.toString();
        Order.Origin originEnum = null;
        try {
            originEnum = Order.Origin.valueOf(origin);
        } catch (Exception e) {
            renderFailure("新建订单失败 - 不支持的来源");
            return;
        }

        String verifyResult = StoreUtil.verify(this, orderEntity.getStore_id());
        if (StrKit.notBlank(verifyResult)) {
            renderFailure(verifyResult);
            return;
        }

        Store store = getAttr("store");
        Assistant assistant = getAttr("assistant");
        User currentUser = getAttr("currentUser");

        List<OrderEntity.OrderItemsEntity> orderItemsEntityList = orderEntity.getOrder_items();
        //对于店员下单，delivery_type为自提，运费为0（理论上也可以极速送达，目前先默认店员下的单必为自提，以后可扩展）
        boolean freeShipping = true;
        String deliveryType = Order.DeliveryType.SELF_PICK.toString();
        Integer credit = orderEntity.getPay_credit();
        Coupon coupon = getAttr("coupon");

        Order order = new Order();
        order.setOrigin(originEnum.toString());
        order.setUserId(currentUser.getId());
        order.setRemark(orderEntity.getRemark());
        order.setInvoice(orderEntity.getInvoice());
        order.setInvoiceTitle(orderEntity.getInvoice_title());
        order.setReceivingTime(orderEntity.getReceiving_time());
        order.setPaymentType(orderEntity.getPayment_type());
        order.setType(Order.Type.STORE_ORDER.toString());

        order.setMid(orderEntity.getMid());
        order.setMname(orderEntity.getMname());

        order.setStoreUserId(String.valueOf(assistant.getUserId()));
        order.setStoreUserName(assistant.getName());
        order.setStoreUserCode(assistant.getCode());

        order.setStoreId(orderEntity.getStore_id());
        order.setStoreName(store.getName());
        order.setStoreCode(store.getCode());
        order.setStoreCover(store.getAvatar());
        order.setStoreAddress(store.getFormattedAddress());

        order.setDeliveryType(deliveryType);

        OrderEntity.ContactEntity contactEntity = orderEntity.getContact();
        if (contactEntity != null) {
            order.setContactUser(contactEntity.getContact_user());
            order.setPhone(contactEntity.getPhone());
            order.setProvince(contactEntity.getProvince());
            order.setCity(contactEntity.getCity());
            order.setDistrict(contactEntity.getDistrict());
            order.setStreet(contactEntity.getStreet());
            order.setZip(contactEntity.getZip());
            order.setDetail(contactEntity.getDetail());

            if (contactEntity.getPhone() != null) {
                User customer = User.dao.findByPhone(contactEntity.getPhone());
                if (customer != null) {
                    //若联系人的电话在user表中有相应记录，则把此订单挂到此用户上
                    order.setUserId(customer.getId());
                    OrderStoreUtil.updateStore(customer, order);
                }
            }
        }

        for (OrderEntity.OrderItemsEntity itemEntity : orderItemsEntityList) {
            OrderItem item = new OrderItem();
            item.setProductId(itemEntity.getProduct_id());
            item.setQuantity(itemEntity.getQuantity());
            item.setProductSpecificationId(itemEntity.getProduct_specification_id());
            order.getOrderItems().add(item);
        }

        try {
            Long warehouseId = getAttr("warehouseId");
            Ret ret = orderService.createOrder(order, coupon, false, credit, freeShipping, warehouseId);
            logger.debug("Ret = {}", ret.getData());
            if (BaseService.isSucceed(ret)) {
                renderSuccess(order);
            } else {
                logger.error("Create order error. Ret={}", ret);
                renderFailure("新建订单失败 - " + BaseService.getMessage(ret));
            }
        } catch (Exception e) {
            e.printStackTrace();
            renderFailure("新建订单失败 - 库存不足");
        }
    }

    /**
     * PUT { action: 'COMPLETE' }
     * ipad端收银->历史订单页面 和 ipad端线上待处理订单页面 都用到此api
     * 1.对于ipad端收银->历史订单页面，店员下单(POST API)后是CREATED_PAY_PENDING（未支付）状态，
     * 稍后店员会再调用本API来直接把CREATED_PAY_PENDING的订单置为CLOSED_CONFIRMED（已确认收货）
     **/
    @Override
    @Before({CurrentUserInterceptor.class, OrderValidator.class})
    @Validation(rules = {"action = required"})
    public void update() {
        Map<String, Object> map = convertPostJsonToMap();
        String action = (String) map.get("action");
        String expressCompany = (String) map.get("express_company");
        String expressNumber = (String) map.get("express_number");

        Order order = Order.dao.findByOrderNumber(getPara());
        if (order == null) {
            renderError(404);
            return;
        }

        String verifyResult = StoreUtil.verify(this, order.getStoreId());
        if (StrKit.notBlank(verifyResult)) {
            renderFailure(verifyResult);
            return;
        }

        Store store = getAttr("store");
        Assistant assistant = getAttr("assistant");
        User currentUser = getAttr("currentUser");

        //线下订单 只能'完成/取消'
        if (Order.Type.STORE_ORDER.toString().equals(order.getType())) {
            Order.Status originalStatus = Order.Status.valueOf(order.getStatus());
            Ret ret = null;
            if (Action.COMPLETE.equalsIgnoreCase(action)) {
                if (originalStatus == Order.Status.CREATED_PAY_PENDING) {
                    order.setStatus(Order.Status.PAID_CONFIRM_PENDING.toString());
                    ret = orderService.updateOrder(order);
                    logger.debug("store order {}: update status: {} -> {}, ret = {}",
                            order.getOrderNumber(),
                            Order.Status.CREATED_PAY_PENDING.toString(),
                            Order.Status.PAID_CONFIRM_PENDING.toString(),
                            ret.getData());
                }

                order.setStatus(Order.Status.DELIVERING.toString());
                ret = orderService.updateOrder(order);
                logger.debug("store order {}: update status: {} -> {}, ret = {}",
                        order.getOrderNumber(),
                        Order.Status.PAID_CONFIRM_PENDING.toString(),
                        Order.Status.DELIVERING.toString(),
                        ret.getData());

                order.setStatus(Order.Status.DELIVERED_CONFIRM_PENDING.toString());
                ret = orderService.updateOrder(order);
                logger.debug("store order {}: update status: {} -> {}, ret = {}",
                        order.getOrderNumber(),
                        Order.Status.DELIVERING.toString(),
                        Order.Status.DELIVERED_CONFIRM_PENDING.toString(),
                        ret.getData());

                order.setStatus(Order.Status.CLOSED_CONFIRMED.toString());
                ret = orderService.updateOrder(order);
                logger.debug("store order {}: update status: {} -> {}, ret = {}",
                        order.getOrderNumber(),
                        Order.Status.DELIVERED_CONFIRM_PENDING.toString(),
                        Order.Status.CLOSED_CONFIRMED.toString(),
                        ret.getData());
            }
            else if (Action.CANCEL.equalsIgnoreCase(action)) {
                order.setStatus(Order.Status.CLOSED_CANCELED.toString());
                ret = orderService.updateOrder(order);
                logger.debug("store order {}: update status: {} -> {}, ret = {}",
                        order.getOrderNumber(),
                        originalStatus,
                        Order.Status.CLOSED_CANCELED.toString(),
                        ret.getData());
            }
            else {
                renderFailure("更新订单失败 - 非法的操作");
                return;
            }

            renderSuccessMessage("更新订单成功！");
            return;
        }

        // 线上自提订单，只能接受和完成
        if (Order.Type.ORDER.toString().equals(order.getType())
                && order.getDeliveryType().equals(Order.DeliveryType.SELF_PICK.toString())
                && (Action.ACCEPT.equalsIgnoreCase(action) || Action.COMPLETE.equalsIgnoreCase(action))) {
            Ret ret = null;
            if (Action.ACCEPT.equals(action)) {
                String targetStatus = Order.Status.CONFIRMED_PICK_PENDING.toString();
                order.setStatus(targetStatus);
                order.setStoreUserId(String.valueOf(assistant.getUserId()));
                order.setStoreUserName(assistant.getName());
                order.setStoreUserCode(assistant.getCode());
                ret = orderService.updateOrder(order);
            }
            else if (Action.COMPLETE.equals(action)) {
                order.setStatus(Order.Status.DELIVERING.toString());
                ret = orderService.updateOrder(order);
                logger.debug("store order {}: update status: {} -> {}, ret = {}",
                        order.getOrderNumber(),
                        Order.Status.CONFIRMED_PICK_PENDING.toString(),
                        Order.Status.DELIVERING.toString(),
                        ret.getData());

                order.setStatus(Order.Status.DELIVERED_CONFIRM_PENDING.toString());
                ret = orderService.updateOrder(order);
                logger.debug("store order {}: update status: {} -> {}, ret = {}",
                        order.getOrderNumber(),
                        Order.Status.DELIVERING.toString(),
                        Order.Status.DELIVERED_CONFIRM_PENDING.toString(),
                        ret.getData());

                order.setStatus(Order.Status.CLOSED_CONFIRMED.toString());
                ret = orderService.updateOrder(order);
                logger.debug("store order {}: update status: {} -> {}, ret = {}",
                        order.getOrderNumber(),
                        Order.Status.DELIVERED_CONFIRM_PENDING.toString(),
                        Order.Status.CLOSED_CONFIRMED.toString(),
                        ret.getData());
            }
            else {
                renderFailure("更新订单失败 - 非法的操作");
                return;
            }

            renderSuccessMessage("更新订单成功！");
            return;
        }

        //// 线上同城快递订单, 可以接受和拒绝,完成
        if (Order.Type.ORDER.toString().equals(order.getType())
                && order.getDeliveryType().equals(Order.DeliveryType.FLASH.toString())
                && (Action.ACCEPT.equalsIgnoreCase(action)
                    || Action.DELIVERING.equalsIgnoreCase(action)
                    || Action.COMPLETE.equalsIgnoreCase(action)
                    || Action.REJECT.equalsIgnoreCase(action))) {
            Ret ret = null;
            if (Action.ACCEPT.equals(action)) {
                String targetStatus = Order.Status.CONFIRMED_DELIVER_PENDING.toString();
                order.setStatus(targetStatus);
                order.setStoreUserId(String.valueOf(assistant.getUserId()));
                order.setStoreUserName(assistant.getName());
                order.setStoreUserCode(assistant.getCode());
                ret = orderService.updateOrder(order);
            }
            else if (Action.REJECT.equalsIgnoreCase(action)) {
                order.setStoreUserId(null);
                order.setStoreUserName(null);
                order.setStoreUserCode(null);
                order.setStoreId(null);
                order.setStoreName(null);
                order.setStoreCover(null);
                order.setStoreAddress(null);
                order.setStoreCode(null);
                ret = orderService.updateOrder(order);
            }

            //代表我们的送货员正在送货了，货物还未送达客户手上
            else if (Action.DELIVERING.equals(action)) {
                //总之最终都让其状态成为DELIVERED_CONFIRM_PENDING
                order.setStatus(Order.Status.DELIVERING.toString());
                ret = orderService.updateOrder(order);
                logger.debug("store order {}: update status: {} -> {}, ret = {}",
                        order.getOrderNumber(),
                        Order.Status.CONFIRMED_PICK_PENDING.toString(),
                        Order.Status.DELIVERING.toString(),
                        ret.getData());
                ret = orderService.updateOrder(order);

                order.setStatus(Order.Status.DELIVERED_CONFIRM_PENDING.toString());
                order.setExpressCompany(expressCompany);
                order.setExpressNumber(expressNumber);
                ret = orderService.updateOrder(order);
            }

            //代表我们的送货员已经把货物交到客户手上了，此时不用客户确认，我们知道此货物已经送达，由店员手动按完成
            else if (Action.COMPLETE.equals(action)) {
                String targetStatus = Order.Status.CLOSED_CONFIRMED.toString();
                order.setStatus(targetStatus);
                ret = orderService.updateOrder(order);
            }
            else {
                renderFailure("更新订单失败 - 非法的操作");
                return;
            }

            renderSuccessMessage("更新订单成功！");
            return;

        }

        renderFailure("更新订单失败 - 非法的订单类型");
    }

}

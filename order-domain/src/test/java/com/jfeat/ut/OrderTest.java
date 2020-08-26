/*
 *   Copyright (C) 2014-2016 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */

package com.jfeat.ut;

import com.jfeat.AbstractTestCase;
import com.jfeat.ext.plugin.rabbitmq.RabbitMQPlugin;
import com.jfeat.identity.model.User;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderItem;
import com.jfeat.order.notify.OrderBillNotifier;
import com.jfeat.order.service.OrderService;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductCategory;
import com.jfinal.aop.Duang;
import com.jfinal.kit.Ret;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by jacky on 3/10/16.
 */
public class OrderTest extends AbstractTestCase {

    private User user;
    private ProductCategory category;
    private OrderService orderService = Duang.duang(OrderService.class);

    @Before
    public void setup() {
        user = User.dao.findByLoginName("abc");
        if (user == null) {
            user = new User();
            user.setLoginName("abc");
            user.setPassword("abc");
            user.setAppUser(User.APP_USER);
            user.save();
        }

        category = new ProductCategory();
        category.setName("c");
        category.save();
        for (int i = 0; i < 10; i++) {
            Product product = new Product();
            product.setCategoryId(category.getId());
            product.setName("p" + i);
            product.setShortName("p" + i);
            product.setPrice(new BigDecimal(50 + i));
            product.setCostPrice(new BigDecimal(40 + i));
            product.setStatus(Product.Status.ONSELL.toString());
            product.setStockBalance(100);
            product.save();
        }
    }

    @After
    public void tearDown() {
        user.delete();
        category.delete();
        for (Order order : Order.dao.findAll()) {
            order.delete();
        }
    }

    private Order prepareOrder() {
        List<OrderItem> orderItems = new ArrayList<>();
        List<Product> products = Product.dao.findByCategoryId(category.getId());
        for (int i = 0; i < 5; i++) {
            OrderItem orderItem = new OrderItem();
            int index = new Random().nextInt(products.size());
            orderItem.setProductId(products.get(index).getId());
            orderItem.setQuantity(new Random().nextInt(10));
            orderItems.add(orderItem);
        }
        Order order = new Order();
        order.setUserId(user.getId());
        order.setOrderItems(orderItems);
        order.setDeliveryType(Order.DeliveryType.EXPRESS.toString());
        return order;
    }

    @Test
    public void testCreateFakeOrder() {
        Order order = orderService.createFakeOrder(user.getId());
        logger.debug(order.toJson());
    }

    @Test
    public void testCreateOrder() throws Exception {
        Order order = prepareOrder();
        Ret ret = orderService.createOrder(order, null, false,0,false, null);
        assertEquals(OrderService.SUCCESS, ret.get(OrderService.RESULT));
        Order returnedOrder = ret.get(OrderService.DATA);
        logger.debug("returnedOrder=" + returnedOrder.toJson());
        assertNotNull(returnedOrder);
        assertEquals(user.getId(), returnedOrder.getUserId());
        Order verifyOrder = Order.dao.findById(order.getId());
        logger.debug("verifyOrder=" + verifyOrder.toJson());
        assertNotNull(verifyOrder);
        assertEquals(user.getId(), verifyOrder.getUserId());
    }

    @Test
    public void testUpdateOrder() throws Exception {
        Order order = prepareOrder();
        orderService.createOrder(order, null, false,0, false, null);
        Order updateOrder = Order.dao.findById(order.getId());
        updateOrder.setStatus(Order.Status.PAID_CONFIRM_PENDING.toString());
        Ret ret = orderService.updateOrder(updateOrder);
        assertEquals(OrderService.SUCCESS, ret.get(OrderService.RESULT));

        //sleep 5 second to wait for the observer update order status:
        //direct change status to CONFIRMED_DELIVER_PENDING after paid.
        TimeUnit.SECONDS.sleep(5);
        Order verifyOrder = Order.dao.findById(order.getId());
        assertEquals(Order.Status.DELIVERED_CONFIRM_PENDING.toString(), verifyOrder.getStatus());
    }

    @Test
    @Ignore
    public void testSendOrderPaidNotify() throws Exception {
        RabbitMQPlugin rabbitMQPlugin = new RabbitMQPlugin("localhost", 5672, "guest", "guest");
        OrderBillNotifier.init("order-bill-queue");
        rabbitMQPlugin.start();

        Order order = prepareOrder();
        orderService.createOrder(order, null, false,0, false, null);
        Order updateOrder = Order.dao.findById(order.getId());
        updateOrder.setStatus(Order.Status.PAID_CONFIRM_PENDING.toString());
        Ret ret = orderService.updateOrder(updateOrder);
        assertEquals(OrderService.SUCCESS, ret.get(OrderService.RESULT));

        TimeUnit.SECONDS.sleep(5);
    }
}

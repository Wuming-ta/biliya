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

package com.jfeat.api.test;

import com.jfeat.api.ApiTestBase;
import com.jfeat.api.Response;
import com.jfeat.config.model.Config;
import com.jfeat.order.api.model.OrderEntity;
import com.jfeat.order.model.Order;
import com.jfeat.order.service.OrderService;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductCategory;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.JsonKit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by jacky on 4/14/16.
 */
public class OrderTest extends ApiTestBase {
    private String url = baseUrl + "rest/order";

    private Product product;
    private ProductCategory productCategory;
    private Order order;

    @Before
    public void before() {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setName("category");
        productCategory.save();

        this.productCategory = productCategory;

        Product product = new Product();
        product.setCategoryId(productCategory.getId());
        product.setName("p1");
        product.setShortName("p1");
        product.setStockBalance(100);
        product.setStatus(Product.Status.ONSELL.toString());
        product.setPrice(new BigDecimal(145));
        product.save();

        this.product = product;

        Order order = new Order();
        order.setUserId(1);
        order.setProvince("GD");
        order.save();

        this.order = order;
    }

    @After
    public void after() {
        for (Order order : Order.dao.findAll()) {
            order.delete();
        }

        productCategory.delete();
    }

    @Test
    public void testGetOrderList() throws IOException {
        Response response = get(url, Response.class);
        assertEquals(SUCCESS, response.getStatusCode());
    }

    @Test
    public void testGetOrder() throws IOException {
        Response response = get(url + "/" + order.getOrderNumber(), Response.class);
        assertEquals(SUCCESS, response.getStatusCode());
    }

    @Test
    public void testCreateOrderDisabled() throws IOException {
        switchOrderCreated(false);
        Response response = createOrder();
        assertEquals(FAILURE, response.getStatusCode());
    }

    @Test
    public void testCreateOrderEnabled() throws IOException {
        switchOrderCreated(true);
        Response response = createOrder();
        assertEquals(SUCCESS, response.getStatusCode());
    }

    private Response createOrder() throws IOException {
        OrderEntity.ContactEntity contact = new OrderEntity.ContactEntity();
        contact.setProvince("GD");
        contact.setCity("GZ");
        contact.setDistrict("LW");
        contact.setContact_user("ABC");
        contact.setPhone("1390000000");
        contact.setStreet("AX");
        contact.setZip("510000");

        List<OrderEntity.OrderItemsEntity> orderItems = new ArrayList<>();
        OrderEntity.OrderItemsEntity orderItemsEntity = new OrderEntity.OrderItemsEntity();
        orderItemsEntity.setProduct_id(product.getId());
        orderItemsEntity.setQuantity(2);
        orderItems.add(orderItemsEntity);

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setContact(contact);
        orderEntity.setOrder_items(orderItems);

        return post(url, JsonKit.toJson(orderEntity), Response.class);
    }

    private void switchOrderCreated(boolean enabled) {
        Config config = Config.dao.findByKey("mall.order_created_enable");
        if (config == null) {
            config = new Config();
            config.setName("create-order-enable");
            config.setKeyName("mall.order_created_enable");
            config.setValueType("boolean");
            config.save();
        }
        config.setValue(String.valueOf(enabled));
        config.update();
    }
}

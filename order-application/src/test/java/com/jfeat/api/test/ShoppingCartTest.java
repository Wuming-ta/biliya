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

import com.google.common.collect.Lists;
import com.jfeat.api.ApiTestBase;
import com.jfeat.api.Response;
import com.jfeat.identity.model.User;
import com.jfeat.order.model.ShoppingCart;
import com.jfeat.product.model.*;
import com.jfeat.product.service.PostageService;
import com.jfinal.kit.JsonKit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by jacky on 4/14/16.
 */
public class ShoppingCartTest extends ApiTestBase {
    private String url = baseUrl + "rest/shopping_cart";

    private List<Product> products = new ArrayList<>();
    private FareTemplate fareTemplate;

    @Before
    public void before() {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setName("category");
        productCategory.save();

        fareTemplate = new FareTemplate();
        fareTemplate.setName("Template");
        fareTemplate.setIsInclPostage(FareTemplate.InclPostage.YES.getValue());
        fareTemplate.setDispatchTime("24");
        fareTemplate.setValuationModel(FareTemplate.ValuationModel.PIECE.getValue());
        CarryMode carryMode = new CarryMode();
        carryMode.setFareId(fareTemplate.getId());
        carryMode.setCarryWay(CarryMode.CarryWay.EXPRESS.getValue());
        carryMode.setFirstAmount(BigDecimal.ZERO);
        carryMode.setSecondPiece(1);
        carryMode.setSecondAmount(BigDecimal.ZERO);
        carryMode.setFirstPiece(1);
        carryMode.setIsDefault(1);
        List<CarryMode> carryModes = Lists.newArrayList();
        carryModes.add(carryMode);
        PostageService postageService = new PostageService();
        postageService.createFareTemplate(fareTemplate, carryModes, Lists.<InclPostageProviso>newArrayList());

        for (int i = 1; i < 5; i++) {
            Product product = new Product();
            product.setCategoryId(productCategory.getId());
            product.setName("p" + i);
            product.setShortName("p" + i);
            product.setStatus(Product.Status.ONSELL.toString());
            product.setPrice(new BigDecimal(145 + i));
            product.setFareId(fareTemplate.getId());
            product.save();
            products.add(product);
        }

        User user = User.dao.findByLoginName(testUserName);

        Product product = products.get(1);
        ShoppingCart cart = new ShoppingCart();
        cart.setProductName(product.getName());
        cart.setProductId(product.getId());
        cart.setPrice(product.getPrice());
        cart.setCover(product.getCover());
        cart.setQuantity(1);
        cart.setFareId(fareTemplate.getId());
        cart.setUserId(user.getId());
        cart.save();
    }

    @After
    public void after() {
        fareTemplate.delete();
        for (ProductCategory category : ProductCategory.dao.findAll()) {
            category.delete();
        }
        for (ShoppingCart cart : ShoppingCart.dao.findAll()) {
            cart.delete();
        }
    }

    @Test
    public void testAddShoppingCart() throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("product_id", products.get(0).getId());
        map.put("quantity", 4);
        list.add(map);
        Response response = post(url, JsonKit.toJson(list), Response.class);
        assertEquals(SUCCESS, response.getStatusCode());
    }

    @Test
    public void testModifyShoppingCart() throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("product_id", products.get(0).getId());
        map.put("quantity", 4);
        list.add(map);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("product_id", products.get(1).getId());
        map2.put("quantity", 0);
        list.add(map2);
        Response response = post(url, JsonKit.toJson(list), Response.class);
        assertEquals(SUCCESS, response.getStatusCode());
    }

    @Test
    public void testGetShoppingCart() throws IOException {
        Response response = get(url, Response.class);
        assertEquals(SUCCESS, response.getStatusCode());
    }

    @Test
    public void testCleanupShoppingCart() throws IOException {
        Response response = delete(url, Response.class);
        assertEquals(SUCCESS, response.getStatusCode());
    }

}

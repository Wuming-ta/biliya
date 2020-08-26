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

import com.google.common.collect.Lists;
import com.jfeat.AbstractTestCase;
import com.jfeat.core.BaseService;
import com.jfeat.identity.model.User;
import com.jfeat.order.model.ShoppingCart;
import com.jfeat.order.service.OrderService;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductCategory;
import com.jfinal.kit.Ret;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by jackyhuang on 16/11/8.
 */
public class ShoppingCartTest extends AbstractTestCase {

    private OrderService orderService = new OrderService();

    private ProductCategory category;
    private List<Product> products = Lists.newArrayList();
    private User user;

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
        category.setName("category");
        category.save();

        for (int i = 0; i < 6; i++) {
            Product product = new Product();
            product.setName("p" + i);
            product.setShortName("p" + i);
            product.setPrice(new BigDecimal(20));
            product.setStatus(Product.Status.ONSELL.toString());
            product.setCategoryId(category.getId());
            product.save();
            products.add(product);
        }
    }

    @Test
    public void addShoppingCart() {
        Ret ret = orderService.updateShoppingCart(user.getId(),
                new Integer[]{products.get(0).getId(), products.get(1).getId(), products.get(2).getId(), products.get(3).getId()},
                new Integer[]{2, 4, 5, 6},
                new Integer[]{null, null, null, null},
                true,
                null, null);
        System.out.println(ret.getData());
        assertEquals(true, BaseService.isSucceed(ret));

        List<ShoppingCart> carts = orderService.queryShoppingCart(user.getId());
        assertEquals(4, carts.size());

        // test offsell
        Product product = products.get(1);
        product.setStatus(Product.Status.OFFSELL.toString());
        product.update();

        carts = orderService.queryShoppingCart(user.getId());
        assertEquals(3, carts.size());


        // add more
        ret = orderService.updateShoppingCart(user.getId(),
                new Integer[]{products.get(4).getId(), products.get(5).getId()},
                new Integer[]{8, 9},
                new Integer[]{null, null},
                true, null, null);
        System.out.println(ret.getData());
        assertEquals(true, BaseService.isSucceed(ret));

        carts = orderService.queryShoppingCart(user.getId());
        assertEquals(5, carts.size());
    }
}

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
import com.google.common.collect.Maps;
import com.jfeat.AbstractTestCase;
import com.jfeat.identity.model.User;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductCategory;
import com.jfeat.product.model.ProductPurchaseStrategy;
import com.jfeat.product.model.ProductPurchaseStrategyItem;
import com.jfeat.product.purchase.*;
import com.jfinal.kit.JsonKit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by jackyhuang on 16/10/12.
 */
public class ProductPurchaseEvaluationTest extends AbstractTestCase {

    private Product productWithoutPurchaseStrategy;
    private Product productWithPurchaseStrategy;
    private ProductCategory category;
    private User user;
    private ProductPurchaseStrategy strategy;

    @Before
    public void setup() {
        user = new User();
        user.setName("abc");
        user.setLoginName("abc");
        user.setPassword("abc");
        user.setFollowed(0);
        user.save();

        category = new ProductCategory();
        category.setName("c");
        category.save();

        productWithoutPurchaseStrategy = new Product();
        productWithoutPurchaseStrategy.setCategoryId(category.getId());
        productWithoutPurchaseStrategy.setName("p");
        productWithoutPurchaseStrategy.setShortName("p");
        productWithoutPurchaseStrategy.setPrice(new BigDecimal(10));
        productWithoutPurchaseStrategy.setStatus(Product.Status.ONSELL.toString());
        productWithoutPurchaseStrategy.save();

        productWithPurchaseStrategy = new Product();
        productWithPurchaseStrategy.setCategoryId(category.getId());
        productWithPurchaseStrategy.setName("p");
        productWithPurchaseStrategy.setShortName("p");
        productWithPurchaseStrategy.setPrice(new BigDecimal(10));
        productWithPurchaseStrategy.setStatus(Product.Status.ONSELL.toString());
        productWithPurchaseStrategy.save();


        strategy = new ProductPurchaseStrategy();
        strategy.setName("test1");
        strategy.save();
        List<ProductPurchaseStrategyItem> items = Lists.newArrayList();
        PurchaseStrategy weixinFollowedStrategy = new WeixinFollowedStrategy();
        ProductPurchaseStrategyItem item = new ProductPurchaseStrategyItem();
        item.setStrategyId(strategy.getId());
        item.setName(weixinFollowedStrategy.getName());
        item.setOperator(ProductPurchaseEvaluation.Operator.AND.toString());
        Map<String, Object> param = Maps.newHashMap();
        item.setParam(JsonKit.toJson(param));
        items.add(item);
        PurchaseStrategy productQuantityStrategy = new ProductQuantityStrategy();
        ProductPurchaseStrategyItem item2 = new ProductPurchaseStrategyItem();
        item2.setStrategyId(strategy.getId());
        item2.setName(productQuantityStrategy.getName());
        item2.setOperator(ProductPurchaseEvaluation.Operator.AND.toString());
        Map<String, Object> param2 = Maps.newHashMap();
        param2.put("defined_quantity", 2);
        item2.setParam(JsonKit.toJson(param2));
        items.add(item2);

        strategy.updateItems(items);

        ProductPurchaseStrategy.dao.updateProductStrategy(productWithPurchaseStrategy.getId(), strategy.getId());
    }

    @After
    public void cleanup() {
        category.delete();
        user.delete();
        strategy.delete();
    }


    @Test
    public void testEvaluationWithoutPurchaseStrategy() {
        ProductPurchaseEvaluation evaluation = new ProductPurchaseEvaluation();
        boolean result = evaluation.evaluate(productWithoutPurchaseStrategy.getId(), null, null);
        assertEquals(true, result);
    }

    @Test
    public void testEvaluationWithPurchaseStrategy() {
        ProductPurchaseEvaluation evaluation = new ProductPurchaseEvaluation();
        boolean result = evaluation.evaluate(productWithPurchaseStrategy.getId(), user.getId(), 3);
        assertEquals(false, result);
    }
}

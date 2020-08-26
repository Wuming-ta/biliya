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

package com.jfeat.order.handler;

import com.jfeat.observer.Observer;
import com.jfeat.observer.Subject;
import com.jfeat.order.model.ShoppingCart;
import com.jfeat.order.service.OrderService;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductSpecification;
import com.jfinal.plugin.activerecord.Db;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by jackyhuang on 16/12/7.
 */
public class ProductUpdatedHandler implements Observer {
    private static final Logger logger = LoggerFactory.getLogger(ProductUpdatedHandler.class);

    private OrderService orderService = new OrderService();

    @Override
    public void invoke(Subject subject, int event, Object param) {
        try {
            if (subject instanceof Product && event == Product.EVENT_PRICE_UPDATE) {
                Product product = (Product) subject;
                logger.info("Product [id={}] price updated. updating the shopping cart.", product.getId());
                if (param == null) {
                    List<ShoppingCart> shoppingCartList = orderService.queryShoppingCartByProductId(product.getId());
                    for (ShoppingCart cart : shoppingCartList) {
                        cart.setPrice(product.getPrice());
                    }
                    Db.batchUpdate(shoppingCartList, 100);
                } else {
                    List<ProductSpecification> specifications = (List<ProductSpecification>) param;
                    for (ProductSpecification specification : specifications) {
                        List<ShoppingCart> shoppingCartList = orderService.queryShoppingCartByProductSpecificationId(specification.getId());
                        for (ShoppingCart cart : shoppingCartList) {
                            cart.setPrice(specification.getPrice());
                        }
                        Db.batchUpdate(shoppingCartList, 100);
                    }
                }
            }

            if (subject instanceof Product && event == Product.EVENT_COVER_UPDATE) {
                Product product = (Product) subject;
                logger.info("Product [id={}] cover updated. updating the shopping cart.", product.getId());
                List<ShoppingCart> shoppingCartList = orderService.queryShoppingCartByProductId(product.getId());
                for (ShoppingCart cart : shoppingCartList) {
                    cart.setCover(product.getCover());
                }
                Db.batchUpdate(shoppingCartList, 100);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage());
            logger.error(ex.toString());
            for (StackTraceElement element : ex.getStackTrace()) {
                logger.error("    {}:{} {}", element.getFileName(), element.getLineNumber(), element.getMethodName());
            }
        }
    }
}

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

package com.jfeat.api;

import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductCategory;
import com.jfinal.ext.kit.RandomKit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by jingfei on 2016/4/8.
 */
public class ProductCategoryApiTest extends ApiTestBase {

    private String url = baseUrl + "rest/product_category";

    private List<ProductCategory> productCategories = new LinkedList<>();

    ProductCategory productCategory0 = new ProductCategory();

    @Before
    public void init() {
        productCategory0.setName("productCategory0");
        productCategory0.save();
        Integer parentId = productCategory0.getId();
        for (int i = 1; i <= 10; i++) {
            ProductCategory productCategory = new ProductCategory();
            productCategory.setName("category " + i);
            productCategory.setParentId(parentId);
            productCategory.save();
            parentId = productCategory.getId();
            productCategories.add(productCategory);
        }
        for (int i = 1; i <= 3; i++) {
            Product product = new Product();
            product.setStatus(Product.Status.ONSELL.toString());
            product.setName("product " + i);
            product.setShortName("product" + i);
            product.setCategoryId(productCategory0.getId());
            product.setPrice(new BigDecimal("34.00"));
            if (i % 2 == 0) {
                product.setPromoted(Product.Promoted.YES.getValue());
            }
            if (i % 3 == 0) {
                product.setStatus(Product.Status.DRAFT.toString());
            }
            product.save();
            for (int j = 0; j < 2; j++) {
                product.addImage("http://localhost:8080/images/" + RandomKit.randomStr() + ".jpg");
            }
        }
    }

    @After
    public void clean() {
        for(ProductCategory category : ProductCategory.dao.findAll()) {
            category.delete();
        }
        for (Product product : Product.dao.findAll()){
            product.delete();
        }
    }

    @Test
    public void testIndex() throws IOException {
        Response response = get(url, Response.class);
        assertEquals(SUCCESS, response.getStatusCode());
    }

//    @Test
//    public void testShow() throws IOException {
//        Response response = get(url + "/" + productCategory0.getId(), Response.class);
//        assertEquals(SUCCESS, response.getStatusCode());
//    }
}

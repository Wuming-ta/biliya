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
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductCategory;
import com.jfeat.product.model.param.ProductParam;
import com.jfinal.plugin.activerecord.Page;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by jacky on 3/9/16.
 */
public class ProductTest extends AbstractTestCase {

    private ProductCategory category;
    private ProductCategory subCategory;

    @Before
    public void setup() {
        category = new ProductCategory();
        category.setName("c1");
        category.save();
        subCategory = new ProductCategory();
        subCategory.setParentId(category.getId());
        subCategory.setName("subc1");
        subCategory.save();
        for (int i = 0; i < 10; i++) {
            Product product = new Product();
            product.setName("p" + i);
            product.setShortName("p" + i);
            product.setCategoryId(subCategory.getId());
            product.setPrice(new BigDecimal(50));
            product.setStatus(Product.Status.ONSELL.toString());
            product.save();
        }
    }

    @After
    public void tearDown() {
        for (ProductCategory category : ProductCategory.dao.findAll()) {
            category.delete();
        }
    }

    @Test
    public void testGetProductCategory() {
        ProductCategory category = ProductCategory.dao.findByName("c1");
        assertNotNull(category);
        assertEquals("c1", category.getName());

        category = ProductCategory.dao.findByName("xxxx");
        assertNull(category);
    }

    @Test
    public void testListProductCategory() {
        List<ProductCategory> list = ProductCategory.dao.findAllRoot();
        assertEquals(1, list.size());

        list = ProductCategory.dao.findAllRecursively();
        assertEquals(1, list.size());
        List<ProductCategory> children = list.get(0).get("sub_categories");
        assertNotNull(children);
        assertEquals(1, children.size());
    }

    @Test
    public void testProduct() {
        ProductParam parm = new ProductParam(1, 2).setCategoryId(subCategory.getId());
        Page<Product> products = Product.dao.paginate(parm);
        assertNotNull(products);
        assertEquals(2, products.getList().size());
    }
}

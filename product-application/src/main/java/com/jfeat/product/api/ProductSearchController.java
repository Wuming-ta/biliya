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

package com.jfeat.product.api;

import com.jfeat.core.RestController;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductCategory;
import com.jfeat.product.model.param.ProductParam;
import com.jfeat.product.service.ProductService;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * Created by huangjacky on 16/6/13.
 */
@ControllerBind(controllerKey = "/rest/product_search")
public class ProductSearchController extends RestController {

    private ProductService productService = Enhancer.enhance(ProductService.class);

    /**
     * GET /rest/product_search?pageNumber=1&pageSize=20&name=abc&orderByDesc=view_count&orderBy=price&orderBy=sales
     */
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        String name = getPara("name");
        String barCode = getPara("barCode");
        Integer categoryId = getParaToInt("categoryId");
        String[] orderByList = getParaValues("orderBy");
        String[] orderByDescList = getParaValues("orderByDesc");
        ProductParam param = new ProductParam(pageNumber, pageSize);
        param.setName(name)
                .setWholesale(ProductCategory.RETAIL)
                .setBarCode(barCode)
                .setCategoryId(categoryId)
                .setOrderByList(orderByList).setOrderByDescList(orderByDescList);
        List<Product> products = Product.dao.paginate(param).getList();
        if (StrKit.notBlank(name) && !products.isEmpty()) {
            productService.updateHitWord(name);
        }
        products = products.stream().peek(p -> {
            Util.calcProductSettlement(p);
            p.removeSecretAttrs();
        }).collect(Collectors.toList());
        renderSuccess(products);
    }
}

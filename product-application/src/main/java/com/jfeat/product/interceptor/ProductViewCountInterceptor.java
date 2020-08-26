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

package com.jfeat.product.interceptor;

import com.jfeat.product.service.ProductService;
import com.jfinal.aop.Enhancer;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * Created by huangjacky on 16/7/7.
 */
public class ProductViewCountInterceptor implements Interceptor {

    private ProductService productService = Enhancer.enhance(ProductService.class);

    @Override
    public void intercept(Invocation invocation) {
        invocation.invoke();
        productService.increaseProductViewCount(invocation.getController().getParaToInt());
    }
}

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
import com.jfeat.product.model.ProductHitWord;
import com.jfinal.ext.route.ControllerBind;

/**
 * Created by huangjacky on 16/6/24.
 */
@ControllerBind(controllerKey = "/rest/product_hit_word")
public class ProductHitWordController extends RestController {

    /**
     * GET /rest/product_hit_word?count=10
     */
    public void index() {
        Integer count = getParaToInt("count", 10);
        renderSuccess(ProductHitWord.dao.findTop(count));
    }
}

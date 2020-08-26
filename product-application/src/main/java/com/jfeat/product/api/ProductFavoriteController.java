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
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.product.model.ProductFavorite;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by jingfei on 2016/5/18.
 */
@ControllerBind(controllerKey = "/rest/product_favorite")
public class ProductFavoriteController extends RestController {

    @Override
    @Before(CurrentUserInterceptor.class)
    public void index() {
        User currentUser = getAttr("currentUser");
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        Page<Record> productFavorites = ProductFavorite.dao.productPaginate(pageNumber, pageSize, currentUser.getId());
        renderSuccess(productFavorites);
    }

    /**
     * POST /rest/product_favorite
     *
     * Data:
     * {
     *     "product_id":1
     * }
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    public void save() {
        Map<String, Object> map = convertPostJsonToMap();
        Integer productId = (Integer) map.get("product_id");
        if (null == productId) {
            renderFailure("invalid.input.json");
            return;
        }

        User currentUser = getAttr("currentUser");
        ProductFavorite oldFavorite = ProductFavorite.dao.find(currentUser.getId(), productId);
        if (oldFavorite == null) {
            ProductFavorite productFavorite = new ProductFavorite();
            productFavorite.setUserId(currentUser.getId());
            productFavorite.setProductId(productId);
            productFavorite.setCollectDate(new Date());
            productFavorite.save();
        }
        renderSuccessMessage("product.favorite.created");
    }

    /**
     *
     * DELETE /rest/product_favorite/<product_id>
     *     {"ids": [ "1", "2" ] }
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    public void delete() {
        User currentUser = getAttr("currentUser");
        Integer productId = getParaToInt();
        ProductFavorite favorite = new ProductFavorite();
        favorite.delete(currentUser.getId(), productId);
        Map<String, Object> map = convertPostJsonToMap();
        if (map != null) {
            List<Integer> ids = (List<Integer>) map.get("ids");
            if (ids != null && !ids.isEmpty()) {
                ids.forEach(id -> favorite.delete(currentUser.getId(), id));
            }
        }

        renderSuccessMessage("product.favorite.deleted");
    }
}

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

package com.jfeat.partner.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.model.User;
import com.jfeat.partner.model.Seller;
import com.jfeat.partner.service.SellerService;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;

/**
 * Created by jingfei on 2016/4/1.
 */
@ControllerBind(controllerKey = "/rest/seller_level")
public class SellerLevelController extends RestController {

    private SellerService sellerService = Enhancer.enhance(SellerService.class);

    public void index() {
        User currentUser = getAttr("currentUser");
        Seller currentSeller = Seller.dao.findByUserId(currentUser.getId());
        Ret ret = sellerService.queryLevelCount(currentSeller.getId());
        renderSuccess(ret.getData());
    }

}

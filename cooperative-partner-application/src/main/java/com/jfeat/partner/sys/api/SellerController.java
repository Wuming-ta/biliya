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

package com.jfeat.partner.sys.api;

import com.jfeat.core.RestController;
import com.jfeat.partner.model.Apply;
import com.jfeat.partner.model.Seller;
import com.jfinal.ext.route.ControllerBind;

/**
 * Created by huangjacky on 16/7/16.
 */
@ControllerBind(controllerKey = "/sys/rest/seller")
public class SellerController extends RestController {

    /**
     * 返回用户的销售商信息, 是否是销售商,是否正在申请中
     */
    public void index() {
        Integer userId = getParaToInt("userId");
        if (userId == null) {
            renderFailure("invalid.userid");
            return;
        }
        Seller seller = Seller.dao.findByUserId(userId);
        Apply apply = Apply.dao.findByUserId(userId);
        int isApplying = 0;
        if (apply != null && apply.getStatus().equals(Apply.Status.INIT.toString())) {
            isApplying = 1;
        }
        seller.put("is_applying", isApplying);
        renderSuccess(seller);
    }
}

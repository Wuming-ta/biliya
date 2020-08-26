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

package com.jfeat.merchant.sys.api;

import com.jfeat.core.RestController;
import com.jfeat.merchant.model.SettledMerchant;
import com.jfinal.ext.route.ControllerBind;

/**
 * Created by jackyhuang on 2018/1/9.
 */
@ControllerBind(controllerKey = "/sys/rest/merchant")
public class MerchantController extends RestController {

    public void show() {
        Integer mid = getParaToInt();
        SettledMerchant settledMerchant = SettledMerchant.dao.findById(mid);
        if (settledMerchant == null) {
            renderFailure("merchant.not.found");
            return;
        }
        renderSuccess(settledMerchant);
    }
}

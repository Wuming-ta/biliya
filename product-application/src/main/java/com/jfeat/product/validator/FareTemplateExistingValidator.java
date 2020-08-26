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

package com.jfeat.product.validator;

import com.jfeat.core.BaseController;
import com.jfeat.product.model.FareTemplate;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
 * Created by jackyhuang on 16/8/26.
 */
public class FareTemplateExistingValidator extends Validator {
    @Override
    protected void validate(Controller controller) {
        FareTemplate fareTemplate = FareTemplate.dao.findById(controller.getParaToInt());
        if (fareTemplate == null) {
            addError("message", "fare_template.not.found");
        }
    }

    @Override
    protected void handleError(Controller controller) {
        BaseController baseController = (BaseController) controller;
        baseController.renderError(404);
    }
}

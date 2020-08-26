package com.jfeat.validator;

import com.jfeat.core.BaseController;
import com.jfeat.identity.model.User;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
 * Created by kang on 2017/2/28.
 */
public class PwdValidator extends Validator {
    @Override
    protected void validate(Controller controller) {
        BaseController baseController = (BaseController) controller;
        User currentUser = baseController.getAttr("currentUser");
        if (!currentUser.verifyPassword(baseController.getPara("pwd"))) {
            addError("message", "密码错误");
        }
    }

    @Override
    protected void handleError(Controller controller) {
        BaseController baseController = (BaseController) controller;
        baseController.setFlash("message", controller.getAttr("message"));
        baseController.redirect(controller.getPara("returnUrl"));
    }
}

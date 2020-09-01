package validator;

import com.jfeat.config.model.Config;
import com.jfeat.config.model.ConfigGroup;
import com.jfeat.core.BaseController;
import com.jfeat.identity.model.User;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;

import java.util.List;

/**
 * Created by kang on 2017/2/28.
 */
public class PwdValidator extends Validator {

    @Override
    protected void validate(Controller controller) {
        BaseController baseController = (BaseController) controller;
        User currentUser = baseController.getAttr("currentUser");
        List<Config> configs = baseController.getModels(Config.class);
        if (ConfigGroup.dao.findById(Config.dao.findById(configs.get(0).getId()).getGroupId()).isProtected()) {
            String pwd = baseController.getPara("pwd");
            if (StrKit.isBlank(pwd)) {
                addError("message", "请输入密码");
            } else if (!currentUser.verifyPassword(baseController.getPara("pwd"))) {
                addError("message", "密码错误");
            }
        }
    }

    @Override
    protected void handleError(Controller controller) {
        BaseController baseController = (BaseController) controller;
        baseController.setFlash("message", controller.getAttr("message"));
        baseController.redirect(controller.getPara("returnUrl"));
    }
}

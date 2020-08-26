package com.jfeat.identity.api;

import com.jfeat.captcha.CaptchaKit;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.model.User;
import com.jfinal.ext.route.ControllerBind;

import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/6/26
 */
@ControllerBind(controllerKey = "/rest/pub/forget_password")
public class ForgetPasswordController extends RestController {

    @Override
    @Validation(rules = { "phone = required", "captcha = required", "password = required" })
    public void save() {
        Map<String, Object> map = convertPostJsonToMap();
        String phone = (String) map.get("phone");
        String captcha = (String) map.get("captcha");
        String password = (String) map.get("password");

        User user = User.dao.findByPhone(phone);
        if (user == null) {
            renderFailure("user.not.found");
            return;
        }

        if (!CaptchaKit.verifyCode(phone, captcha)) {
            renderFailure("captcha.invalid");
            return;
        }

        user.setPassword(password);
        user.update();

        renderSuccess("password.reset");
    }
}

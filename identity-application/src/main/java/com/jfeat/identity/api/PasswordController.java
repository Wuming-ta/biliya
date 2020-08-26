package com.jfeat.identity.api;

import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.identity.service.UserService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;

import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/6/26
 */
@ControllerBind(controllerKey = "/rest/password")
public class PasswordController extends RestController {

    private UserService userService = Enhancer.enhance(UserService.class);

    @Override
    @Validation(rules = { "old_password = required", "new_password = required" })
    @Before(CurrentUserInterceptor.class)
    public void save() {
        User currentUser = getAttr("currentUser");
        Map<String, Object> map = convertPostJsonToMap();
        String oldPassword = (String) map.get("old_password");
        String newPassword = (String) map.get("new_password");
        if (currentUser.verifyPassword(oldPassword)) {
            currentUser.setPassword(newPassword);
            currentUser.update();
            renderSuccess("password.changed");
            return;
        }
        renderFailure("incorrect.old.password");
    }
}

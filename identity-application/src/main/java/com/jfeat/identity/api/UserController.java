package com.jfeat.identity.api;

import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.identity.model.param.UserParam;
import com.jfeat.identity.service.UserService;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/7/11
 */
@ControllerBind(controllerKey = "/rest/user")
public class UserController extends RestController {

    @Override
    @Before(CurrentUserInterceptor.class)
    @RequiresPermissions(value = "identity.view")
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 30);
        String phone = getPara("phone");
        UserParam param = new UserParam(pageNumber, pageSize);
        param.setAppUser(User.APP_USER).setStatus(User.Status.NORMAL.toString()).setPhone(phone);
        Page<User> page = User.dao.paginate(param);
        renderSuccess(page);
    }
}

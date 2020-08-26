package com.jfeat.identity.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.model.Role;
import com.jfinal.ext.route.ControllerBind;

/**
 * @author jackyhuang
 * @date 2018/8/17
 */
@ControllerBind(controllerKey = "/rest/role")
public class RoleController extends RestController {

    @Override
    public void index() {
        renderSuccess(Role.dao.findAll());
    }
}

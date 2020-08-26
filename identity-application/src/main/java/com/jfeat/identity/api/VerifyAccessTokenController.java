package com.jfeat.identity.api;

import com.jfeat.core.RestController;
import com.jfinal.ext.route.ControllerBind;

/**
 * @author jackyhuang
 * @date 2018/7/2
 */
@ControllerBind(controllerKey = "/rest/verify_access_token")
public class VerifyAccessTokenController extends RestController {

    /**
     * authc filter 会进行校验，如果校验不过不会进入该方法
     */
    @Override
    public void save() {
        renderSuccessMessage("ok");
    }
}

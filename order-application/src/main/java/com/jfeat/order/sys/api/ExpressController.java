package com.jfeat.order.sys.api;

import com.jfeat.core.RestController;
import com.jfeat.order.model.Express;
import com.jfinal.ext.route.ControllerBind;

/**
 * @author jackyhuang
 * @date 2019/12/13
 */
@ControllerBind(controllerKey = "/sys/rest/express")
public class ExpressController extends RestController {

    @Override
    public void index() {
        renderSuccess(Express.dao.findAllEnabled());
    }
}

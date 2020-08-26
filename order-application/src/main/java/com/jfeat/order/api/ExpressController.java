package com.jfeat.order.api;

import com.jfeat.core.RestController;
import com.jfeat.order.model.Express;
import com.jfinal.ext.route.ControllerBind;

/**
 * 查询快递100的物流公司信息
 * @author jackyhuang
 * @date 2019/12/14
 */
@ControllerBind(controllerKey = "/rest/express")
public class ExpressController extends RestController {

    @Override
    public void index() {
        renderSuccess(Express.dao.findAllEnabled());
    }

    /**
     * get /rest/express/:code
     */
    @Override
    public void show() {
        String code = getPara();
        Express express = Express.dao.findByCode(code);
        if (express == null) {
            renderError(404);
        }
        renderSuccess(express);
    }
}


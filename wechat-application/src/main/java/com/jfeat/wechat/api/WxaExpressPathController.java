package com.jfeat.wechat.api;

import com.jfeat.config.model.Config;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.wechat.sdk.api.ExpressApi;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.weixin.sdk.api.ApiResult;

import java.util.Map;

/**
 * 小程序下单的运单查询
 * @author jackyhuang
 * @date 2019/11/9
 */
@ControllerBind(controllerKey = "/rest/wxa/express/path")
public class WxaExpressPathController extends RestController {

    @Override
    @Before(CurrentUserInterceptor.class)
    @Validation(rules = { "order_number = required", "waybill_id = required" })
    public void index() {
        if (!Config.dao.findByKey("wx.express.enabled").getValueToBoolean()) {
            renderFailure("wx.express.disabled");
            return;
        }

        User currentUser = getAttr("currentUser");
        String orderNumber = getPara("order_number");
        String waybillId = getPara("waybill_id");
        ApiResult result = ExpressApi.getPath(orderNumber,
                currentUser.getWxaOpenid(),
                Config.dao.findByKey("wx.express.delivery_id").getValueToStr(),
                waybillId);
        if (!result.isSucceed()) {
            renderFailure(result.getErrorMsg());
            return;
        }
        Map<String, Object> map = result.getAttrs();
        map.put("delivery_name", Config.dao.findByKey("wx.express.delivery_name").getValueToStr());
        renderSuccess(map);
    }
}

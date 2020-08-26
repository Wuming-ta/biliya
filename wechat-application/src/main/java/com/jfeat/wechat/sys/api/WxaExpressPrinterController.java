package com.jfeat.wechat.sys.api;

import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.wechat.sdk.api.ExpressApi;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.weixin.sdk.api.ApiResult;

import java.util.Map;

/**
 * @author jackyhuang
 * @date 2019/11/20
 */
@ControllerBind(controllerKey = "/sys/rest/wxa/express/printer")
public class WxaExpressPrinterController extends RestController {

    /**
     * get all printer
     */
    @Override
    public void index() {
        ApiResult result = ExpressApi.getPrinter();
        renderSuccess(result);
    }

    @Override
    @Validation(rules = {
            "openid = required",
            "update_type = required"
    })
    public void save() {
        Map<String, Object> maps = convertPostJsonToMap();
        String openid = (String) maps.get("openid");
        String updateType = (String) maps.get("update_type");
        ApiResult result = ExpressApi.updatePrinter(openid, updateType);
        renderSuccess(result);
    }
}

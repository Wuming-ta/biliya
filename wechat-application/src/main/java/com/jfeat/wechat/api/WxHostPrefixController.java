package com.jfeat.wechat.api;

import com.jfeat.core.RestController;
import com.jfeat.wechat.config.WxConfig;
import com.jfinal.ext.route.ControllerBind;

/**
 * @author jackyhuang
 * @date 2018/8/31
 */
@ControllerBind(controllerKey = "/rest/wx/host_prefix")
public class WxHostPrefixController extends RestController {

    @Override
    public void index() {
        renderSuccess(WxConfig.getHost());
    }
}

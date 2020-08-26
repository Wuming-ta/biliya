package com.jfeat.wechat.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfeat.core.BaseController;
import com.jfeat.flash.Flash;
import com.jfinal.aop.Before;
import com.jfinal.kit.JsonKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.MenuApi;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.Map;

/**
 * Created by sing on 2016/8/26.
 */
public class WechatMenuController extends BaseController {

    @Override
    @Before(Flash.class)
    @RequiresPermissions(value = { "wechat.view", "sys.wechat_menu.menu" }, logical = Logical.OR)
    public void index() {
        Map map = Maps.newHashMap();
        map.put("button", Lists.newArrayList());
        ApiResult apiResult = MenuApi.getMenu();
        logger.debug("getMenu result: {}", apiResult.getJson());
        if (apiResult.isSucceed()) {
            map = apiResult.getMap("menu");
        }
        else if (apiResult.getErrorCode() != 46003) {
            //46003 : menu not exist
            renderError(500);
            return;
        }

        String menuValue= JsonKit.toJson(map);
        setAttr("menuValue",menuValue);
    }

    @Override
    @RequiresPermissions("wechat.edit")
    public void update() {
        String json = getRequest().getParameter("data");
        ApiResult apiResult = MenuApi.createMenu(json);
        logger.debug("createMenu result: {}", apiResult.getJson());
        if (apiResult.isSucceed()) {
            setFlash("message", getRes().get("wechat.menu.update.success"));
        }
        else {
            setFlash("message", getRes().get("wechat.menu.update.failure") + "ErrorMsg: " + apiResult.getErrorMsg());
        }
        redirect("/wechat_menu");
    }
}

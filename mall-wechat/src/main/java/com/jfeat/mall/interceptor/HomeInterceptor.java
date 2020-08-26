/*
 *   Copyright (C) 2014-2016 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */

package com.jfeat.mall.interceptor;

import com.jfeat.mall.config.WxConfig;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.weixin.sdk.api.ApiConfigKit;

/**
 * Created by jacky on 5/16/16.
 */
public class HomeInterceptor implements Interceptor {

    private String mallName;
    private String weuiCss;
    private String angularJs;
    private String angularUiRouterJs;
    private String ocLazyLoadJs;
    private String extension;

    public HomeInterceptor(String mallName) {
        this.mallName = mallName;
    }

    public HomeInterceptor(String mallName,
                           String weuiCss, String angularJs, String angularUiRouterJs, String ocLazyLoadJs,
                           String extension) {
        this(mallName);
        this.weuiCss = weuiCss;
        this.angularJs = angularJs;
        this.angularUiRouterJs = angularUiRouterJs;
        this.ocLazyLoadJs = ocLazyLoadJs;
        this.extension = extension;
    }

    @Override
    public void intercept(Invocation inv) {

        ApiConfigKit.putApiConfig(WxConfig.getApiConfig());

        inv.getController().setAttr("mall_name", mallName);
        inv.getController().setAttr("weuiCss", weuiCss);
        inv.getController().setAttr("angularJs", angularJs);
        inv.getController().setAttr("angularUiRouterJs", angularUiRouterJs);
        inv.getController().setAttr("ocLazyLoadJs", ocLazyLoadJs);
        inv.getController().setAttr("extension", extension);
        if (inv.getControllerKey().equals("/") || inv.getControllerKey().equals("/index")) {
            inv.getController().forwardAction("/app");
            return;
        }
        inv.invoke();
    }
}

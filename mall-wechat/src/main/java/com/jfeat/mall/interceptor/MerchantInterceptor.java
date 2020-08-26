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

import com.jfeat.mall.api.MerchantApi;
import com.jfeat.mall.api.response.MerchantResponse;
import com.jfeat.mall.config.SysConfig;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jackyhuang on 2018/1/9.
 */
public class MerchantInterceptor implements Interceptor {

    private static Logger logger = LoggerFactory.getLogger(MerchantInterceptor.class);

    @Override
    public void intercept(Invocation inv) {
        Controller controller = inv.getController();

        //根据是否允许平台零售和是否带mid决定
        Integer mid = controller.getParaToInt("mid");
        logger.debug("mid from para = {}", mid);
        if (mid == null) {
            mid = controller.getSessionAttr("mid");
            controller.setSessionAttr("mid", mid);
            logger.debug("mid from session = {}", mid);
            if (mid != null) {
                redirectWithMid(controller, mid);
                return;
            }
        }

        controller.setSessionAttr("mid", mid);

        if (!SysConfig.allowPlatformSale()) {
            String merchant = "MID: " + mid;
            //不允许平台零售，指定了商家时 MID: 1234 或者 没指定商家 MID: null
            controller.setAttr("merchant", merchant);
        }
        if (mid != null) {
            //MID: 1234
            String merchant = "MID: " + mid;
            MerchantApi merchantApi = new MerchantApi();
            MerchantResponse merchantResponse = merchantApi.getMerchant(mid);
            if (merchantResponse.isSucceed()) {
                merchant += ", MNAME: '" + merchantResponse.getData().getName() + "'";
            }
            controller.setAttr("merchant", merchant);
            logger.debug("merchant = {}", merchant);
        }

        inv.invoke();
    }

    private void redirectWithMid(Controller controller, Integer mid) {
        StringBuffer url = controller.getRequest().getRequestURL();
        Map<String, String[]> queryString = new HashMap<>(controller.getParaMap());
        queryString.put("mid", new String[] {String.valueOf(mid)});
        String finalUrl = UrlUtil.buildUrlWithQueryString(url.toString(), queryString);
        logger.debug("redirect to url: {}", finalUrl);
        controller.redirect(finalUrl);
    }
}

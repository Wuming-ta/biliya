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

package com.jfeat.mall.api;

import com.jfeat.kit.HttpKit;
import com.jfeat.mall.api.response.SysConfigResponse;
import com.jfinal.plugin.ehcache.CacheKit;

/**
 * Created by jackyhuang on 16/9/2.
 */
public class SysConfigApi extends BaseApi {

    private static String sysConfigUrl = "/sys/rest/sys_config";

    private String getUrl() {
        return host + sysConfigUrl;
    }

    public SysConfigResponse getSysConfig() {
        SysConfigResponse response = CacheKit.get("sys_config", "sys_config");
        if (response == null) {
            response = HttpKit.get(getUrl(), SysConfigResponse.class, getHeaders());
            if (response != null && response.getStatusCode() == 0) {
                CacheKit.put("sys_config", "sys_config", response);
            }
        }
        return response;
    }
}

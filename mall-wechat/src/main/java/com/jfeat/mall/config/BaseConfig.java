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

package com.jfeat.mall.config;

import com.jfeat.mall.api.SysConfigApi;
import com.jfeat.mall.api.response.SysConfigResponse;

/**
 * Created by jackyhuang on 16/9/2.
 */
public abstract class BaseConfig {

    public static SysConfigResponse.Data getResponseData(String key) {
        SysConfigResponse response = new SysConfigApi().getSysConfig();
        if (response.getStatusCode() == 0) {
            for (SysConfigResponse.Data data : response.getData()) {
                if (key.equals(data.getKeyName())) {
                    return data;
                }
            }
        }
        return new SysConfigResponse.Data();
    }
}

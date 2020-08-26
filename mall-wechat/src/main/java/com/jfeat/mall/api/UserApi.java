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

import com.google.gson.Gson;
import com.jfeat.kit.HttpKit;
import com.jfeat.mall.api.response.CreateUserResponse;
import com.jfeat.mall.api.response.UserResponse;
import com.jfinal.kit.JsonKit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jacky on 5/3/16.
 */
public class UserApi extends BaseApi {
    private static String userUrl = "/sys/rest/user";

    private String getUrl() {
        return host + userUrl;
    }

    public UserResponse getUser(String weixin) {
        return HttpKit.get(getUrl() + "/" + weixin, UserResponse.class, getHeaders());
    }

    /**
     *
     * @param name
     * @param loginName
     * @param weixin
     * @param password
     * @param inviteCode
     * @param avatar
     * @param sex
     * @param qrcode APP URL to create qrcode url.
     * @return
     */
    public CreateUserResponse createUser(String name, String loginName, String weixin, String password,
                                         String inviteCode, String avatar, Integer sex, String qrcode) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("login_name", loginName);
        map.put("weixin", weixin);
        map.put("password", password);
        map.put("invite_code", inviteCode);
        map.put("avatar", avatar);
        map.put("sex", sex);
        map.put("invitation_qrcode", qrcode);
        String data = JsonKit.toJson(map);
        logger.debug("data = {}", data);
        return HttpKit.post(getUrl(), data, CreateUserResponse.class, getHeaders());
    }
}

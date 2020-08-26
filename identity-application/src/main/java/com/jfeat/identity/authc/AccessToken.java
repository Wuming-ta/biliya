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

package com.jfeat.identity.authc;

import com.jfeat.identity.model.User;
import com.jfeat.kit.Digests;
import com.jfeat.kit.Encodes;
import com.jfinal.kit.JsonKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ehngjen on 9/2/2015.
 */
public class AccessToken {
    private static Logger logger = LoggerFactory.getLogger(AccessToken.class);
    public static final String TOKEN = "token";

    public static String getAccessToken(User user) {
        Map<String, String> data = new HashMap<>();
        data.put(User.Fields.LOGIN_NAME.toString(), user.getLoginName());
        data.put(User.Fields.ID.toString(), user.getId().toString());
        byte[] salt = Encodes.decodeHex(user.getTokenSalt());
        byte[] hashToken = Digests.sha1(user.getPassword().getBytes(), salt, User.HASH_INTERATIONS);
        data.put(TOKEN, Encodes.encodeHex(hashToken));
        return Encodes.encodeBase64(JsonKit.toJson(data).getBytes());
    }

    public static Map<String, Object> extractAccessToken(String accessToken) {
        String str = new String(Encodes.decodeBase64(accessToken));
        logger.debug(str);
        Map<String, Object> map = null;
        try {
            map = com.jfeat.kit.JsonKit.convertToMap(str);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

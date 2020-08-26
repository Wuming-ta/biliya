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

import com.google.common.primitives.Bytes;
import com.jfeat.kit.Encodes;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.util.ByteSource;

import java.util.Map;

/**
 * Created by ehngjen on 9/2/2015.
 */
public class AccessTokenToken implements AuthenticationToken {

    private String username;
    private String token;

    public AccessTokenToken(String username, String token) {
        this.username = username;
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public Object getCredentials() {
        return ByteSource.Util.bytes(Encodes.decodeHex(token));
    }
}
